package com.technology.shift.services;

import com.technology.shift.repositories.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftServiceV2 {
    //TODO  ADD FILE HASH CHECK UP

    private static final Logger logger = LoggerFactory.getLogger(ShiftServiceV2.class);
    private final FileRepository fileRepository;
    private final JdbcTemplate jdbcTemplate;

    public void parseCSVFile(MultipartFile multipartFile){
        try {
            List<Object[]> employeeShifts = parseFile(multipartFile);
            if(!employeeShifts.isEmpty()){
                saveAllTheShifts(employeeShifts);
            }
        } catch (IOException e) {
            logger.error("UNABLE TO PARSE THE FILE");
        }
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
                        Optional<Pair<LocalTime,LocalTime>> intervals = parseTimeInterval(timeInterval);
                        if(intervals.isPresent()){
                            Optional<LocalDate> date = parseDate(columnName);
                            if(date.isPresent()){
                                shiftStartDateTime = LocalDateTime.of(date.get(),intervals.get().getFirst());
                                shiftEndDateTime = LocalDateTime.of(date.get(),intervals.get().getSecond());
                            }
                        }
                        employeeShifts.add(new Object[]{employeeEmail,shiftStartDateTime,shiftEndDateTime});
                    }
                }
            }

        }
        return employeeShifts;
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
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
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
