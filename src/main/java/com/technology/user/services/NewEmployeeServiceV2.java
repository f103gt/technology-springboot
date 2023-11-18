package com.technology.user.services;

import com.technology.role.enums.Role;
import com.technology.shift.models.File;
import com.technology.shift.repositories.FileRepository;
import com.technology.user.brokers.MessagePublisher;
import com.technology.user.exceptions.FileAlreadyUploadedException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

@Service
@RequiredArgsConstructor
public class NewEmployeeServiceV2 {
    //private static final Logger logger = LoggerFactory.getLogger(NewEmployeeServiceV2.class);
    private final FileRepository fileRepository;
    private final JdbcTemplate jdbcTemplate;
    private final MessagePublisher messagePublisher;

    public void parseFile(MultipartFile file) {
        fileRepository.findFileByFileName(file.getOriginalFilename())
                .ifPresent(fileName -> {
                    throw new FileAlreadyUploadedException("The file" + fileName + " has already been uploaded");
                });

        String hash = calculateHash(file);

        if (!hash.isEmpty()) {
            fileRepository.findFileByFileHash(hash)
                    .ifPresent(fileHash -> {
                        throw new FileAlreadyUploadedException("The provided file content has already been uploaded");
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
                    Object[] employeeData = {email, role.name(),role.name()};
                    employees.add(employeeData);
                }
            }
            messagePublisher.publishMessage(employees);

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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
