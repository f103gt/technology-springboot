package com.technology.shift.services;

import com.mongodb.internal.connection.tlschannel.util.Util;
import com.technology.shift.repositories.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ShiftServiceV2Test {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ShiftServiceV2 shiftService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        shiftService = new ShiftServiceV2(fileRepository, jdbcTemplate);
    }

    //TODO add more test on the formatting
    //TODO testing of insertion of the objects into the database
    @Test
    public void testParseFile() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // Prepare a CSV file content as an InputStream
        String csvContent = "Email,2023-01-01,2023-01-02\n" +
                "john@example.com,8:00-16:00,9:00-17:00\n" +
                "jane@example.com,10:00-18:00,,";
        // Create an InputStream from the CSV content
        InputStream csvInputStream = new ByteArrayInputStream(
                csvContent.getBytes(StandardCharsets.UTF_8));

        // MockMultipartFile does not require a real file to be present
        MultipartFile multipartFile =
                new MockMultipartFile("file", "test.csv", "text/csv",
                        csvInputStream);

        Method privateMethod = ShiftServiceV2.class.getDeclaredMethod("parseFile", MultipartFile.class);
        privateMethod.setAccessible(true);
        List<Object[]> result =
                (List<Object[]>) privateMethod.invoke(shiftService,multipartFile);
        // Call the method to be tested
        //List<Object[]> result = shiftService.parseFile(multipartFile);

        // Assert the result based on your expectations
        assertEquals(3, result.size());

        // Assuming the first record
        Object[] firstRecord = result.get(0);
        assertEquals("john@example.com", firstRecord[0]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        LocalDateTime johnShift1Start = LocalDateTime.of(
                LocalDate.parse("2023-01-01"), LocalTime.parse("8:00", formatter));
        formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime johnShift1End = LocalDateTime.of(LocalDate.parse(
                "2023-01-01"), LocalTime.parse("16:00",formatter));

        assertEquals(johnShift1Start, firstRecord[1]);
        assertEquals(johnShift1End, firstRecord[2]);

        Object[] secondRecord = result.get(1);
        assertEquals("john@example.com", secondRecord[0]);

        formatter = DateTimeFormatter.ofPattern("H:mm");
        LocalDateTime johnShift2Start = LocalDateTime.of(
                LocalDate.parse("2023-01-02"), LocalTime.parse("9:00", formatter));
        formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime johnShift2End = LocalDateTime.of(LocalDate.parse(
                "2023-01-02"), LocalTime.parse("17:00",formatter));


        Object[] thirdRecord = result.get(2);
        assertEquals("jane@example.com", thirdRecord[0]);

        LocalDateTime janeShift1Start = LocalDateTime.of(
                LocalDate.parse("2023-01-01"), LocalTime.parse("10:00", formatter));
        LocalDateTime janeShift1End = LocalDateTime.of(LocalDate.parse(
                "2023-01-01"), LocalTime.parse("18:00",formatter));

        assertEquals(janeShift1Start, thirdRecord[1]);
        assertEquals(janeShift1End, thirdRecord[2]);
    }
    @Test
    public void testParseTimeInterval() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Given
        //String inputTimeInterval = "14:30-16:45"; // Replace with your desired input
        String inputTimeInterval = "8:00-16:00";

        // Use reflection to access the private method
        Method privateMethod = ShiftServiceV2.class.getDeclaredMethod("parseTimeInterval", String.class);
        privateMethod.setAccessible(true);

        // When
        Optional<Pair<LocalTime, LocalTime>> result = (Optional<Pair<LocalTime, LocalTime>>)
                privateMethod.invoke(shiftService, inputTimeInterval);

        // Then
        assertTrue(result.isPresent());
        Pair<LocalTime, LocalTime> timePair = result.get();
        // Add assertions based on your expectations
    }
}