package com.technology.user.services;

import com.technology.role.enums.Role;
import com.technology.role.errors.RoleNotFoundException;
import com.technology.user.exceptions.FileAlreadyUploadedException;
import com.technology.user.repositories.NewEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Slf4j
@Service
@RequiredArgsConstructor
public class NewEmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(NewEmployeeService.class);
    private final NewEmployeeRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public void uploadNewEmployeesData(MultipartFile file) {
        Pair<String, String> cleanedContentAndHash = null;
        try {
            cleanedContentAndHash = purifyContentAndCalculateHash(file);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NO SUCH ALGORITHM occurred: ", e);
        } catch (IOException e) {
            logger.error("RUNTIME occurred: ", e);
        }
        if (cleanedContentAndHash != null) {
            repository.findFileHash(cleanedContentAndHash.getSecond())
                    .ifPresent((hash) -> {
                        throw new FileAlreadyUploadedException("The file you submitted is already uploaded");
                    });
            List<Object[]> employees = createNewEmployees(cleanedContentAndHash);
            String sql = """
                     INSERT INTO new_employee(email,role,file_hast) VALUES(?,?,?)\s
                     ON CONFLICT(email) DO UPDATE SET role = ?, file_hash = ?
                     """;
            jdbcTemplate.batchUpdate(sql,employees);
        }
    }



    private List<Object[]> createNewEmployees(Pair<String, String> data){
        List<Object[]> employees = new ArrayList<>();
        String cleanedContent = data.getFirst();
        String fileHash = data.getSecond();

        Pattern pattern = Pattern.compile("username\\s(\\S+)\\srole\\s(\\S+)");

        Matcher matcher = pattern.matcher(cleanedContent);
        while (matcher.find()){
            Role role = Role.contains(matcher.group(2))
                    .orElseThrow(()->new RoleNotFoundException("Role "+ matcher.group(2) + " does not exist"));
            Object[] employeeData= {
                    matcher.group(1),
                    role.name(),
                    fileHash};
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
