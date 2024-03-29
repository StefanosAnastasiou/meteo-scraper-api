package com.emperium;

import com.emperium.DAO.CityDAO;
import com.emperium.DAO.DayDAO;
import com.emperium.DAO.MeasurementDAO;
import com.emperium.domain.Day;
import com.emperium.domain.Measurement;
import com.emperium.model.City;
import com.emperium.scraper.MeteoScraper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeteoScraperTest {

    @InjectMocks
    private MeteoScraper scraper = new MeteoScraper();

    @Mock
    private CityDAO cityDAO;

    @Mock
    private DayDAO dayDAO;

    @Mock
    private MeasurementDAO measurementDAO;

    private Day domainDay;

    private com.emperium.model.Day modelDay;

    private List<com.emperium.model.Day> modelDays;

    @Before
    public void init() {
        this.domainDay = new Day(LocalDate.of(2021,07,07));
        this.domainDay.setMeasurements(new ArrayList<>());

        Measurement measurement1  = new Measurement();
        measurement1.setEventTime(LocalTime.of(03, 00));
        measurement1.setHumidity(56);
        measurement1.setPhenomeno("ΣΥΝΝΕΦΙΑ");
        measurement1.setWind("2Μπφ");
        measurement1.setTemperature(25);

        Measurement measurement2 = new Measurement();
        measurement2.setEventTime(LocalTime.of(06, 00));
        measurement2.setHumidity(60);
        measurement2.setPhenomeno("ΚΑΘΑΡΟΣ ΟΥΡΑΝΟΣ");
        measurement2.setWind("1Μπφ");
        measurement2.setTemperature(24);

        this.domainDay.getMeasurements().add(measurement1);
        this.domainDay.getMeasurements().add(measurement2);

        com.emperium.model.Measurement mMeasurement = new com.emperium.model.Measurement();
        mMeasurement.setHumidity(25);
        mMeasurement.setWind("2Μπφ");
        mMeasurement.setTemperature(25);
        mMeasurement.setPhenomeno("ΣΥΝΝΕΦΙΑ");
        mMeasurement.setTime(LocalTime.of(03, 00));

        List<com.emperium.model.Measurement> modelMeasurements = new ArrayList<>();
        modelMeasurements.add(mMeasurement);

        this.modelDay = new com.emperium.model.Day();
        this.modelDay.setMeasurements(modelMeasurements);

        this.modelDays = new ArrayList<>();
        modelDays.add(modelDay);
    }

    @Test
    public void test_city_day_measurements_Not_set() {
        Mockito.when(cityDAO.cityIsSet("ΤΡΙΚΑΛΑ")).thenReturn(false);
        boolean cityIsSet = scraper.cityIsSet.test("ΤΡΙΚΑΛΑ");

        Assert.assertFalse("City should not be set.", cityIsSet);

        City city = new City();
        city.setName("ΤΡΙΚΑΛΑ");

        city.setDays(this.modelDays);

        doNothing().when(cityDAO).saveCity(city);
        cityDAO.saveCity(city);
        verify(cityDAO, times(1)).saveCity(city);
    }

    @Test
    public void test_city_Set_day_measurements_Not_set() {
        Mockito.when(cityDAO.cityIsSet("ΤΡΙΚΑΛΑ")).thenReturn(true);
        Mockito.when(dayDAO.dayIsSet(LocalDate.of(2021, 07,07), 54)).thenReturn(false);

        boolean cityIsSet = scraper.cityIsSet.test("ΤΡΙΚΑΛΑ");
        boolean dayIsSet = scraper.dayIsSet.test(LocalDate.of(2021, 07, 07), 54);

        Assert.assertTrue("City should be set.", cityIsSet);
        Assert.assertFalse("Day should not be set.", dayIsSet);

        doNothing().when(measurementDAO).setDailyMeasurements(this.domainDay.getMeasurements(), 54);
        measurementDAO.setDailyMeasurements(this.domainDay.getMeasurements(), 54);
        verify(measurementDAO, times(1)).setDailyMeasurements(this.domainDay.getMeasurements(), 54);
    }

    @Test
    public void test_city_day_measurements_set() {
        Mockito.when(cityDAO.cityIsSet("ΘΕΣΣΑΛΟΝΙΚΗ")).thenReturn(true);
        Mockito.when(dayDAO.dayIsSet(LocalDate.of(2021, 07, 07), 1)).thenReturn(true);
        Mockito.when(measurementDAO.measurementsAreSet(1)).thenReturn(true);

        boolean cityExists = scraper.cityIsSet.test("ΘΕΣΣΑΛΟΝΙΚΗ");
        boolean dayExists = scraper.dayIsSet.test(LocalDate.of(2021, 07, 07), 1);
        boolean dailyMeasurementsAreSet = scraper.dailyMeasurementsAreSet.test(1);

        Assert.assertTrue("City should be set.", cityExists);
        Assert.assertTrue("Day should be set.", dayExists);
        Assert.assertTrue("Daily measurements should be set.", dailyMeasurementsAreSet);

        doNothing().when(measurementDAO).checkAndUpdateDailyMeasurement(1, this.domainDay.getMeasurements());
        measurementDAO.checkAndUpdateDailyMeasurement(1, this.domainDay.getMeasurements());
        verify(measurementDAO, times(1)).checkAndUpdateDailyMeasurement(1, this.domainDay.getMeasurements());
    }

    @Test
    public void test_city_day_set_measurements_not_set() {
        Mockito.when(cityDAO.cityIsSet("ΚΑΒΑΛΑ")).thenReturn(true);
        Mockito.when(dayDAO.dayIsSet(LocalDate.of(2021, 07, 07), 2)).thenReturn(true);
        Mockito.when(measurementDAO.measurementsAreSet(2)).thenReturn(false);

        boolean cityExists = scraper.cityIsSet.test("ΚΑΒΑΛΑ");
        boolean dayExists = scraper.dayIsSet.test(LocalDate.of(2021, 07, 07), 2);
        boolean dailyMeasurementsAreSet = scraper.dailyMeasurementsAreSet.test(2);

        Assert.assertTrue("City should be set.", cityExists);
        Assert.assertTrue("Day should be set.", dayExists);
        Assert.assertFalse("Measurements should not be set.", dailyMeasurementsAreSet);

        doNothing().when(measurementDAO).setDailyMeasurements(this.domainDay.getMeasurements(), 2);
        measurementDAO.setDailyMeasurements(this.domainDay.getMeasurements(), 2);
        verify(measurementDAO, times(1)).setDailyMeasurements(this.domainDay.getMeasurements(), 2);
    }
}