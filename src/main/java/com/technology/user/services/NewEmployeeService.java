package com.technology.user.services;

import com.technology.role.enums.Role;
import com.technology.role.errors.RoleNotFoundException;
import com.technology.shift.repositories.FileRepository;
import com.technology.user.brokers.CustomMessage;
import com.technology.user.brokers.MessagePublisher;
import com.technology.user.exceptions.FileAlreadyUploadedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Slf4j
@Service
@RequiredArgsConstructor
public class NewEmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(NewEmployeeService.class);
    private final JdbcTemplate jdbcTemplate;
    private final MessagePublisher messagePublisher;
    private final FileRepository fileRepository;
    public void uploadNewEmployeesData(MultipartFile file) {
        fileRepository.findFileByFileName(file.getOriginalFilename())
                .ifPresent((fileName) -> {
                    throw new FileAlreadyUploadedException("The file you submitted is already uploaded");
                });
        Pair<String, String> cleanedContentAndHash = null;
        try {
            cleanedContentAndHash = purifyContentAndCalculateHash(file);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NO SUCH ALGORITHM occurred: ", e);
        } catch (IOException e) {
            logger.error("RUNTIME occurred: ", e);
        }
        if (cleanedContentAndHash != null) {
            //notify user with response 200
            fileRepository.findFileByFileHash(cleanedContentAndHash.getSecond())
                    .ifPresent((hash) -> {
                        throw new FileAlreadyUploadedException("The file you submitted is already uploaded");
                    });
            /*messagePublisher.publishMessage(new CustomMessage(
                    cleanedContentAndHash.getFirst(),
                    cleanedContentAndHash.getSecond()));*/
        }
    }

    //TODO CHANGE TO USE THE EMPLOYEE AND FILE REPOSITORIES AND ENTITIES
    @Async
    @RabbitListener(queues = "${rabbitmq.message.queue}")
    public CompletableFuture<Comparable<Void>> saveNewEmployees(CustomMessage message) {
        if (message != null) {
            List<Object[]> employees = createNewEmployees(Pair.of(message.getContent(), message.getFileHash()));
            String sql = """
                    INSERT INTO employee(email,role) VALUES(?,?)\s
                    ON CONFLICT(email) DO UPDATE SET role = ?
                     """;
            jdbcTemplate.batchUpdate(sql, employees);
        }
        return CompletableFuture.completedFuture(null);
    }


    private List<Object[]> createNewEmployees(Pair<String, String> data) {
        List<Object[]> employees = new ArrayList<>();
        String cleanedContent = data.getFirst();

        Pattern pattern = Pattern.compile("username\\s(\\S+)\\srole\\s(\\S+)");

        Matcher matcher = pattern.matcher(cleanedContent);
        while (matcher.find()) {
            Role role = Role.contains(matcher.group(2))
                    .orElseThrow(() -> new RoleNotFoundException("Role " + matcher.group(2) + " does not exist"));
            Object[] employeeData = {
                    matcher.group(1),
                    role.name()};
            employees.add(employeeData);
        }
        return employees;
    }

    private Pair<String, String> purifyContentAndCalculateHash(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        Pattern pattern = Pattern.compile("[^a-zA-z0-9 ]");
        Pattern multipleSpaces = Pattern.compile(" +");
        StringBuilder cleanedContent = new StringBuilder();
        StringBuilder hexString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            String intermediate = matcher.replaceAll("");
            Matcher spaceMatcher = multipleSpaces.matcher(intermediate);
            String cleanedLine = spaceMatcher.replaceAll(" ").toLowerCase();
            cleanedContent.append(cleanedLine);

            digest.update(cleanedLine.getBytes());
            byte[] hash = digest.digest();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }


        }
        return Pair.of(cleanedContent.toString(), hexString.toString());
    }
}
