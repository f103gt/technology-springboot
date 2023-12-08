package com.technology.shift.services;

import com.technology.shift.models.File;
import com.technology.shift.repositories.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.technology.user.services.FileHelper.calculateHash;

@Service
@RequiredArgsConstructor
public class ShiftServiceV2 {
    //TODO  ADD FILE HASH CHECK UP

    //TODO HANDLE THE CASE WHEN THE USERNAME IS PROVIDED INCORRECTLY
    private static final Logger logger = LoggerFactory.getLogger(ShiftServiceV2.class);
    private final FileRepository fileRepository;
    private final JdbcTemplate jdbcTemplate;

    public void parseCSVFile(MultipartFile multipartFile) {
        fileRepository.findFileByFileName(multipartFile.getOriginalFilename())
                .ifPresent(fileName -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The provided file data is already uploaded.");
                });

        String hash = calculateHash(multipartFile);

        if (!hash.isEmpty()) {
            fileRepository.findFileByFileHash(hash)
                    .ifPresent(fileHash -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "The provided file data is already uploaded.");
                    });
        }
        try {
            List<Object[]> employeeShifts = parseFile(multipartFile);
            if (!employeeShifts.isEmpty()) {
                saveAllTheShifts(employeeShifts);
            }
        } catch (IOException e) {
            logger.error("UNABLE TO PARSE THE FILE");
        }
        fileRepository.save(File.builder()
                .fileName(multipartFile.getOriginalFilename())
                .fileHash(hash)
                .build());
    }

    private List<Object[]> parseFile(MultipartFile multipartFile) throws IOException {
        List<Object[]> employeeShifts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader().setSkipHeaderRecord(true).build().parse(reader);

            // Extract column names (dates)
            List<String> columnNames = new ArrayList<>(csvParser.getHeaderNames());
            columnNames.remove(0);

            for (CSVRecord csvRecord : csvParser) {
                // Assuming the first column represents the employee email
                String employeeEmail = csvRecord.get(0);

                // Process date columns
                for (String columnName : columnNames) {
                    String timeInterval = csvRecord.get(columnName);

                    // Check if the time interval is present (not blank)
                    LocalDateTime shiftStartDateTime = null;
                    LocalDateTime shiftEndDateTime = null;
                    if (!timeInterval.isEmpty()) {
                        Optional<Pair<LocalTime, LocalTime>> intervals = parseTimeInterval(timeInterval);
                        if (intervals.isPresent()) {
                            Optional<LocalDate> date = parseDate(columnName);
                            if (date.isPresent()) {
                                shiftStartDateTime = LocalDateTime.of(date.get(), intervals.get().getFirst());
                                shiftEndDateTime = LocalDateTime.of(date.get(), intervals.get().getSecond());
                            }
                        }
                        employeeShifts.add(new Object[]{employeeEmail, shiftStartDateTime, shiftEndDateTime});
                    }
                }
            }

        }
        return employeeShifts;
    }

    //TODO implement batch insert using jdbc

    @Transactional
    public void saveAllTheShifts(List<Object[]> employeeShifts) {
        String selectShiftIdSql = "SELECT id FROM shift WHERE start_time = ? AND end_time = ?";
        String insertShiftSql = "INSERT INTO shift(start_time, end_time) VALUES (?, ?)";
        String selectEmployeeIdSql = "SELECT id FROM employee WHERE email = ?";
        String insertEmployeeShiftSql = "INSERT INTO employee_shift(employee_id, shift_id) VALUES (?, ?)";

        for (Object[] employeeShift : employeeShifts) {
            String email = (String) employeeShift[0];
            LocalDateTime startTime = (LocalDateTime) employeeShift[1];
            LocalDateTime endTime = (LocalDateTime) employeeShift[2];

            Integer shiftId = jdbcTemplate.query(selectShiftIdSql,
                    (ResultSet rs) -> rs.next() ? rs.getInt(1) : null,
                    startTime, endTime);

            if (shiftId == null) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(insertShiftSql, new String[]{"id"});
                    ps.setTimestamp(1, Timestamp.valueOf(startTime));
                    ps.setTimestamp(2, Timestamp.valueOf(endTime));
                    return ps;
                }, keyHolder);

                if (keyHolder.getKey() != null) {
                    shiftId = keyHolder.getKey().intValue();
                } else {
                    throw new IllegalStateException("Failed to retrieve shift ID after insertion");
                }
            }

            Integer employeeId = jdbcTemplate.queryForObject(selectEmployeeIdSql, Integer.class, email);

            // Insert the relation between employee and shift
            jdbcTemplate.update(insertEmployeeShiftSql, employeeId, shiftId);
        }
    }


    private Optional<Pair<LocalTime, LocalTime>> parseTimeInterval(String timeInterval) {

        List<DateTimeFormatter> formatters24 = Arrays.asList(
                DateTimeFormatter.ofPattern("HH:mm"),
                DateTimeFormatter.ofPattern("kk:mm"),
                DateTimeFormatter.ofPattern("H:mm")
        );

        List<DateTimeFormatter> formatters12 = Arrays.asList(
                DateTimeFormatter.ofPattern("hh:mm a"),
                DateTimeFormatter.ofPattern("K:mm a")
        );

        List<DateTimeFormatter> formatters = timeInterval.toUpperCase().contains("AM") ||
                timeInterval.toUpperCase().contains("PM") ? formatters12 : formatters24;

        for (DateTimeFormatter formatter : formatters) {
            try {
                String[] times = timeInterval.split("-");
                LocalTime startTime = LocalTime.parse(times[0].trim(), formatter);
                LocalTime endTime = LocalTime.parse(times[1].trim(), formatter);
                return Optional.of(Pair.of(startTime, endTime));
            } catch (DateTimeParseException e) {
                logger.debug("Failed to parse time interval using formatter {}: {}", formatter, e.getMessage());
            }
        }
        return Optional.empty();
    }


    private Optional<LocalDate> parseDate(String dateString) {
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("MM.dd.yyyy"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),

                DateTimeFormatter.ofPattern("d.M.yyyy"),
                DateTimeFormatter.ofPattern("M.d.yyyy"),
                DateTimeFormatter.ofPattern("yyyy.M.d"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("M-d-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-M-d"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/M/d")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate parsedDate = LocalDate.parse(dateString, formatter);
                return Optional.of(parsedDate);
            } catch (Exception e) {
                logger.error("DATE TIME PARSE EXCEPTION OCCURRED IN parseDate()");
            }
        }
        return Optional.empty();
    }

}
