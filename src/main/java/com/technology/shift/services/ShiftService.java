package com.technology.shift.services;

import com.technology.shift.models.File;
import com.technology.shift.repositories.FileRepository;
import com.technology.user.exceptions.FileAlreadyUploadedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private static final Logger logger = LoggerFactory.getLogger(ShiftService.class);
    private final FileRepository fileRepository;
    private final JdbcTemplate jdbcTemplate;

    public void parseFile(MultipartFile file) {
        fileRepository.findFileByFileName(file.getName())
                .ifPresent(fileName -> {
                    throw new FileAlreadyUploadedException("The file" + fileName + " has already been uploaded");
                });

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            InputStream is = new DigestInputStream(file.getInputStream(), md);
            Reader reader = new InputStreamReader(is);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader().setSkipHeaderRecord(true).build().parse(reader);


            String hash = calculateHash(file);

            if (!hash.isEmpty()) {
                fileRepository.findFileByFileHash(hash)
                        .ifPresent(fileHash -> {
                            throw new FileAlreadyUploadedException("The provided file content has already been uploaded");
                        });
            }

            List<Object[]> employeeShifts = new ArrayList<>();

            //List<Object[]> fileS
            for (CSVRecord record : records) {
                String email = record.get(0);
                List<Pair<LocalDate, LocalDate>> dates = null;
                Duration timeInterval = null;
                for (String column : record.toMap().keySet()) {
                    String value = record.get(column);
                    if (isEmail(value)) {
                        email = value;
                    } else if (!getDateIntervals(value).isEmpty()) {
                        dates = getDateIntervals(value);
                    } else if (getDuration(value) != Duration.ZERO) {
                        timeInterval = getDuration(value);
                    }
                }
                //handle the case if the date is represented in form of interval

                // Now you have the email, date, and time for this record.
                // You can store them in the appropriate database columns.
                if (timeInterval != null && dates != null && email != null) {
                    List<Pair<LocalDateTime, LocalDateTime>> shifts = fillDatesWithShiftTimes(timeInterval, dates);
                    employeeShifts = getEmployeeShiftObjects(shifts, email);
                }

            }

            fileRepository.save(File.builder()
                    .fileName(file.getName())
                    .fileHash(hash)
                    .build());
            saveAllTheShifts(employeeShifts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public void saveAllTheShifts(List<Object[]> employeeShifts) {
        String sql = """
                INSERT INTO shift(employee_id,start_time,end_time) VALUES(
                (SELECT id FROM employee WHERE email = ?),?,?) 
                ON CONFLICT (employee_id) DO UPDATE SET 
                start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time;
                """;
        jdbcTemplate.batchUpdate(sql, employeeShifts);
    }

    private List<Pair<LocalDateTime, LocalDateTime>> fillDatesWithShiftTimes(Duration shiftDuration, List<Pair<LocalDate, LocalDate>> dateIntervals) {
        List<Pair<LocalDateTime, LocalDateTime>> dateTimePairs = new ArrayList<>();

        for (Pair<LocalDate, LocalDate> dateInterval : dateIntervals) {
            LocalDate startDate = dateInterval.getFirst();
            LocalDate endDate = dateInterval.getSecond();

            LocalDateTime startDateTime = startDate.atTime(LocalTime.MIDNIGHT);
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MIDNIGHT).plusDays(1); // Adding 1 day to include the end day

            while (!startDateTime.isAfter(endDateTime.minus(shiftDuration))) {
                LocalDateTime endShiftDateTime = startDateTime.plus(shiftDuration);

                dateTimePairs.add(Pair.of(startDateTime, endShiftDateTime));

                startDateTime = startDateTime.plus(shiftDuration);
            }
        }

        return dateTimePairs;
    }

    private Duration getDuration(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String[] parts = value.split("-");

        try {
            LocalTime startTime = LocalTime.parse(parts[0].trim(), formatter);
            LocalTime endTime = LocalTime.parse(parts[1].trim(), formatter);


            return Duration.between(startTime, endTime);
        } catch (DateTimeParseException e) {
            return Duration.ZERO;
        }
    }

    private List<Object[]> getEmployeeShiftObjects(List<Pair<LocalDateTime, LocalDateTime>> shifts, String email) {
        return shifts.stream().map(
                        localDateTime -> new Object[]{localDateTime.getFirst(),
                                localDateTime.getSecond(),
                                email})
                .collect(Collectors.toList());
    }

    private String calculateHash(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest();
            StringBuilder hash = new StringBuilder();
            for (byte b : digest) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("NO SUCH ALGORITHM EXCEPTION OCCURRED DURING HASH CALCULATION");
        }
        return "";
    }

    private boolean isEmail(String value) {
        return value.contains("@");
    }

    private List<Pair<LocalDate, LocalDate>> getDateIntervals(String value) {
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("MM.dd.yyyy"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        );

        String[] parts = value.split("-");
        List<Pair<LocalDate, LocalDate>> dateIntervals = new ArrayList<>();

        for (int i = 0; i < parts.length; i += 2) {
            String startPart = parts[i].trim();
            String endPart = (i + 1 < parts.length) ? parts[i + 1].trim() : startPart;

            LocalDate startDate = null;
            LocalDate endDate = null;

            for (DateTimeFormatter formatter : formatters) {
                try {
                    startDate = LocalDate.parse(startPart, formatter);
                    endDate = LocalDate.parse(endPart, formatter);
                    break;  // Break out of the loop if parsing is successful
                } catch (DateTimeParseException e) {
                    logger.error("DATE TIME PARSE EXCEPTION OCCURRED IN getDateIntervals()");
                }
            }

            if (startDate != null && endDate != null) {
                dateIntervals.add(Pair.of(startDate, endDate));
            }
        }

        return dateIntervals;
    }


}
