package com.technology.shift.services;

import com.technology.shift.repositories.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)

class ShiftServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ShiftService shiftService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        shiftService = new ShiftService(fileRepository, jdbcTemplate);
    }

    @Test
    public void testGetDateIntervalsHyphenSeparatedInterval() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String hyphenSeparatedIntervalInputDDMMYYY = "01-01-2023-10-01-2023";
        String hyphenSeparatedSingleInputDDMMYYY = "01-01-2023";
        String slashSeparatedIntervalInputDDMMYYY = "01/01/2023-10/01/2023";
        String slashSeparatedSingleInputDDMMYYY = "01/01/2023";

        String hyphenSeparatedIntervalInputMMDDYYY = "01-01-2023-01-10-2023";
        String hyphenSeparatedSingleInputMMDDYYY = "01-01-2023";
        String slashSeparatedIntervalInputMMDDYYY = "01/01/2023-01/10/2023";
        String slashSeparatedSingleInputMMDDYYY = "01/01/2023";



        String hyphenSeparatedIntervalInputYYYMMDD = "2023-01-01-2023-01-10-2023";
        String hyphenSeparatedSingleInputYYYMMDD = "2023-01-01";
        String slashSeparatedIntervalInputYYYMMDD = "2023/01/01-2023/01/10";
        String slashSeparatedSingleInputYYYMMDD = "2023/01/01";
        Method privateMethod = ShiftService.class.getDeclaredMethod("getDateIntervals", String.class);
        privateMethod.setAccessible(true);

        List<Pair<LocalDate, LocalDate>> result =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        hyphenSeparatedIntervalInputDDMMYYY);

        // Assert the result based on your expectations
        // For example, you can check the size of the list or specific date values
        assertEquals(1, result.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePair = result.get(0);
        LocalDate startDate = datePair.getFirst();
        LocalDate endDate = datePair.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 1), startDate);
        assertEquals(LocalDate.of(2023, 1, 10), endDate);

    }


    @Test
    public void testGetDateIntervalsHyphenSeparatedSingle() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String hyphenSeparatedSingleInputDDMMYYY = "13-01-2023";
        /*String hyphenSeparatedSingleInputMMDDYYY = "01-13-2023";
        String hyphenSeparatedSingleInputYYYMMDD = "2023-01-13";*/
        Method privateMethod = ShiftService.class.getDeclaredMethod("getDateIntervals", String.class);
        privateMethod.setAccessible(true);

        List<Pair<LocalDate, LocalDate>> resultSingleInputDDMMYYY =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        hyphenSeparatedSingleInputDDMMYYY);

        // Assert the result based on your expectations
        // For example, you can check the size of the list or specific date values
        assertEquals(1, resultSingleInputDDMMYYY.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePairSingleInputDDMMYYY = resultSingleInputDDMMYYY.get(0);
        LocalDate startDateSingleInputDDMMYYY = datePairSingleInputDDMMYYY.getFirst();
        LocalDate endDateSingleInputDDMMYYY = datePairSingleInputDDMMYYY.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 13), startDateSingleInputDDMMYYY);
        assertEquals(LocalDate.of(2023, 1, 13), endDateSingleInputDDMMYYY);

    }

    @Test
    public void testGetDateIntervalsDotsSeparatedInterval() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String dotSeparatedIntervalInputDDMMYYY = "13.01.2023-21.01.2023";
        Method privateMethod = ShiftService.class.getDeclaredMethod("getDateIntervals", String.class);
        privateMethod.setAccessible(true);

        List<Pair<LocalDate, LocalDate>> resultIntervalInputDDMMYYY =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        dotSeparatedIntervalInputDDMMYYY);

        // Assert the result based on your expectations
        // For example, you can check the size of the list or specific date values
        assertEquals(1, resultIntervalInputDDMMYYY.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePairIntervalInputDDMMYYY = resultIntervalInputDDMMYYY.get(0);
        LocalDate startDateIntervalInputDDMMYYY = datePairIntervalInputDDMMYYY.getFirst();
        LocalDate endDateIntervalInputDDMMYYY = datePairIntervalInputDDMMYYY.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 13), startDateIntervalInputDDMMYYY);
        assertEquals(LocalDate.of(2023, 1, 21), endDateIntervalInputDDMMYYY);


        String dotSeparatedIntervalInputMMDDYYY = "01.13.2023-01.21.2023";

        List<Pair<LocalDate, LocalDate>> resultIntervalInputMMDDYYY =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        dotSeparatedIntervalInputMMDDYYY);

        // Assert the result based on your expectations
        // For example, you can check the size of the list or specific date values
        assertEquals(1, resultIntervalInputMMDDYYY.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePairIntervalInputMMDDYYY = resultIntervalInputMMDDYYY.get(0);
        LocalDate startDateIntervalInputMMDDYYY = datePairIntervalInputMMDDYYY.getFirst();
        LocalDate endDateIntervalInputMMDDYYY = datePairIntervalInputMMDDYYY.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 13), startDateIntervalInputMMDDYYY);
        assertEquals(LocalDate.of(2023, 1, 21), endDateIntervalInputMMDDYYY);



        String dotSeparatedIntervalInputYYYMMDD = "2023.01.13-2023.01.21";

        List<Pair<LocalDate, LocalDate>> resultIntervalInputYYYMMDD =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        dotSeparatedIntervalInputYYYMMDD);

        // Assert the result based on your expectations
        // For example, you can check the size of the list or specific date values
        assertEquals(1, resultIntervalInputYYYMMDD.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePairIntervalInputYYYMMDD = resultIntervalInputYYYMMDD.get(0);
        LocalDate startDateIntervalInputYYYMMDD = datePairIntervalInputYYYMMDD.getFirst();
        LocalDate endDateIntervalInputYYYMMDD = datePairIntervalInputYYYMMDD.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 13), startDateIntervalInputYYYMMDD);
        assertEquals(LocalDate.of(2023, 1, 21), endDateIntervalInputYYYMMDD);

    }

    @Test
    public void testGetDateIntervalsDotsSeparatedSingle() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String dotSeparatedSingleInputDDMMYYY = "21.01.2023";
        String dotSeparatedSingleInputMMDDYYY = "01.21.2023";
        String dotSeparatedSingleInputYYYMMDD = "2023.01.21";
        Method privateMethod = ShiftService.class.getDeclaredMethod("getDateIntervals", String.class);
        privateMethod.setAccessible(true);

        List<Pair<LocalDate, LocalDate>> resultSingleInputDDMMYYY =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        dotSeparatedSingleInputDDMMYYY);

        List<Pair<LocalDate, LocalDate>> resultSingleInputMMDDYYY =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        dotSeparatedSingleInputMMDDYYY);

        List<Pair<LocalDate, LocalDate>> resultSingleInputYYYMMDD =
                (List<Pair<LocalDate, LocalDate>>) privateMethod.invoke(shiftService,
                        dotSeparatedSingleInputYYYMMDD);

        // Assert the result based on your expectations
        assertEquals(1, resultSingleInputDDMMYYY.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePairSingleInputDDMMYYY = resultSingleInputDDMMYYY.get(0);
        LocalDate startDateSingleInputDDMMYYY = datePairSingleInputDDMMYYY.getFirst();
        LocalDate endDateSingleInputDDMMYYY = datePairSingleInputDDMMYYY.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 21), startDateSingleInputDDMMYYY);
        assertEquals(LocalDate.of(2023, 1, 21), endDateSingleInputDDMMYYY);



        // Assert the result based on your expectations
        assertEquals(1, resultSingleInputMMDDYYY.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePairSingleInputMMDDYYY = resultSingleInputMMDDYYY.get(0);
        LocalDate startDateSingleInputMMDDYYY = datePairSingleInputMMDDYYY.getFirst();
        LocalDate endDateSingleInputMMDDYYY = datePairSingleInputMMDDYYY.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 21), startDateSingleInputMMDDYYY);
        assertEquals(LocalDate.of(2023, 1, 21), endDateSingleInputMMDDYYY);


        // Assert the result based on your expectations
        assertEquals(1, resultSingleInputMMDDYYY.size());
        // Access the elements of the Pair and make assertions
        Pair<LocalDate, LocalDate> datePairSingleInputYYYMMDD = resultSingleInputYYYMMDD.get(0);
        LocalDate startDateSingleInputYYYMMDD = datePairSingleInputYYYMMDD.getFirst();
        LocalDate endDateSingleInputYYYMMDD = datePairSingleInputYYYMMDD.getSecond();

        // Example assertions, replace with your actual expectations
        assertEquals(LocalDate.of(2023, 1, 21), startDateSingleInputYYYMMDD);
        assertEquals(LocalDate.of(2023, 1, 21), endDateSingleInputYYYMMDD);

    }
}