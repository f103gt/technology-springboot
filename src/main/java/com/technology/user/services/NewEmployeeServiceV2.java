package com.technology.user.services;

import com.technology.role.enums.Role;
import com.technology.shift.models.File;
import com.technology.shift.repositories.FileRepository;
import com.technology.user.brokers.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.technology.user.services.FileHelper.calculateHash;

@Log4j2
@Service
@RequiredArgsConstructor
public class NewEmployeeServiceV2 {
    private final FileRepository fileRepository;
    private final JdbcTemplate jdbcTemplate;
    private final MessagePublisher messagePublisher;

    public void parseFile(MultipartFile file) {
        fileRepository.findFileByFileName(file.getOriginalFilename())
                .ifPresent(fileName -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The provided file data is already uploaded.");
                });

        String hash = calculateHash(file);

        if (!hash.isEmpty()) {
            fileRepository.findFileByFileHash(hash)
                    .ifPresent(fileHash -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "The provided file data is already uploaded.");
                    });
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            InputStream is = new DigestInputStream(file.getInputStream(), md);
            Reader reader = new InputStreamReader(is);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader().setSkipHeaderRecord(true).build().parse(reader);


            List<Object[]> employees = new ArrayList<>();

            for (CSVRecord record : records) {
                String email = "";
                Role role = null;
                for (String column : record.toMap().keySet()) {
                    String value = record.get(column);
                    if (isEmail(value)) {
                        email = value;
                    } else {
                        role = rolePositionResolver(value);
                    }
                }
                if (!email.isEmpty() && role != null) {
                    Object[] employeeData = {email, role.name(), role.name()};
                    employees.add(employeeData);
                }
            }
            messagePublisher.publishMessage(employees);

        } catch (NoSuchAlgorithmException ex) {
            log.error("NoSuchAlgorithmException OCCURRED IN parseFile() IN NewEmployeeServiceV2.class");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while saving new employees.\n" +
                            "Please,try again latter.", ex);
        } catch (IOException ex) {
            log.error("IOException OCCURRED IN parseFile() IN NewEmployeeServiceV2.class");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while saving new employees.\n" +
                            "Please,try again latter.", ex);
        }
        fileRepository.save(File.builder()
                .fileName(file.getOriginalFilename())
                .fileHash(hash)
                .build());
    }

    @Async
    @RabbitListener(queues = "${rabbitmq.message.queue}")
    public CompletableFuture<Comparable<Void>> saveNewEmployees(List<Object[]> employees) {
        if (employees != null) {
            String sql = """
                    INSERT INTO employee(email,role) VALUES(?,?)
                    ON CONFLICT(email) DO UPDATE SET role = ?
                     """;
            jdbcTemplate.batchUpdate(sql, employees);
        }
        return CompletableFuture.completedFuture(null);
    }

    private boolean isEmail(String value) {
        return value.contains("@gmail.com");
    }


    private Role rolePositionResolver(String position) {
        position = position.toLowerCase().trim();
        if (position.contains("manager")) {
            return Role.MANAGER;
        } else if (position.contains("admin")) {
            return Role.ADMIN;
        } else if (position.contains("staff") ||
                position.contains("packer")) {
            return Role.STAFF;
        } else {
            return Role.USER;
        }
    }

}
