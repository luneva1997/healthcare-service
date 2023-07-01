package ru.netology.patient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static java.lang.System.out;
import static org.mockito.ArgumentMatchers.isA;

public class MedicalServiceImplTest {

    private static SendAlertServiceImpl alert = Mockito.mock(SendAlertServiceImpl.class);
    private static PatientInfoFileRepository info = Mockito.mock(PatientInfoFileRepository.class);
    private static MedicalServiceImpl medical = new MedicalServiceImpl(info, alert);
    private static ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    final String id = "123";
    final BigDecimal temprature = new BigDecimal("36.5");
    final BloodPressure bloodPressure = new BloodPressure(135, 70);

    @BeforeAll
    public static void before(){

        Mockito.doCallRealMethod().when(alert).send(isA(String.class));

        Mockito.when(info.getById("123"))
                .thenReturn(new PatientInfo("123", "Василий", "Иванович",
                        LocalDate.of(1996, 6, 16),
                        new HealthInfo(new BigDecimal("39.5"), new BloodPressure(125, 60))));

    }

    @Test
    public void checkBloodPressure(){
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        medical.checkBloodPressure(id, bloodPressure);

        Assertions.assertEquals("Warning, patient with id: 123, need help", output.toString().replaceAll("\n", "").replaceAll("\r", ""));
    }

    @Test
    public void checkTemperature(){
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        medical.checkTemperature(id, temprature);

        Assertions.assertEquals("Warning, patient with id: 123, need help", output.toString().replaceAll("\n", "").replaceAll("\r", ""));
    }

    @Test
    public void test(){

        BigDecimal temprature = new BigDecimal("39.5");
        BloodPressure bloodPressure = new BloodPressure(125, 60);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        medical.checkBloodPressure(id, bloodPressure);
        medical.checkTemperature(id, temprature);

        Assertions.assertEquals("", output.toString().replaceAll("\n", "").replaceAll("\r", ""));
    }

    @AfterAll
    public static void after(){
        Mockito.verify(alert, Mockito.atLeast(1)).send(argument.capture());

        Assertions.assertEquals("Warning, patient with id: 123, need help", argument.getValue());
    }
}
