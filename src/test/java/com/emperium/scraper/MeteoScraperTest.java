package com.emperium.scraper;

import com.emperium.dao.db.CityDAO;
import com.emperium.dao.db.DayDbDAO;
import com.emperium.dao.db.PredictionsDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MeteoScraperTest {

    @InjectMocks
    private MeteoScraper scraper = new MeteoScraper();

    @Mock
    private CityDAO cityDAO;

    @Mock
    private DayDbDAO dayDbDAO;

    @Mock
    private PredictionsDAO predictionsDAO;

//    private DayDTO domainDayDTO;

    private com.emperium.model.Day modelDay;

    private List<com.emperium.model.Day> modelDays;

    @Before
    public void init() {
//        this.domainDayDTO = new DayDTO(LocalDate.of(2021,07,07));
//        this.domainDayDTO.setMeasurementDTOS(new ArrayList<>());

//        MeasurementDTO measurementDTO1 = new MeasurementDTO();
//        measurementDTO1.setTime(LocalTime.of(03, 00));
//        measurementDTO1.setHumidity(56);
//        measurementDTO1.setPhenomeno("ΣΥΝΝΕΦΙΑ");
//        measurementDTO1.setWind("2Μπφ");
//        measurementDTO1.setTemperature(25);
//
//        MeasurementDTO measurementDTO2 = new MeasurementDTO();
//        measurementDTO2.setTime(LocalTime.of(06, 00));
//        measurementDTO2.setHumidity(60);
//        measurementDTO2.setPhenomeno("ΚΑΘΑΡΟΣ ΟΥΡΑΝΟΣ");
//        measurementDTO2.setWind("1Μπφ");
//        measurementDTO2.setTemperature(24);
//
//        this.domainDayDTO.getMeasurements().add(measurementDTO1);
//        this.domainDayDTO.getMeasurements().add(measurementDTO2);
//
//        com.emperium.model.Measurement mMeasurement = new com.emperium.model.Measurement();
//        mMeasurement.setHumidity(25);
//        mMeasurement.setWind("2Μπφ");
//        mMeasurement.setTemperature(25);
//        mMeasurement.setPhenomeno("ΣΥΝΝΕΦΙΑ");
//        mMeasurement.setTime(LocalTime.of(03, 00));
//
//        List<com.emperium.model.Measurement> modelMeasurements = new ArrayList<>();
//        modelMeasurements.add(mMeasurement);
//
//        this.modelDay = new com.emperium.model.Day();
//        this.modelDay.setMeasurements(modelMeasurements);
//
//        this.modelDays = new ArrayList<>();
//        modelDays.add(modelDay);
    }

    @Test
    public void test_city_day_measurements_Not_set() {
//        Mockito.when(cityDAO.cityIsSet("ΤΡΙΚΑΛΑ")).thenReturn(false);
//        boolean cityIsSet = scraper.cityIsSet.test("ΤΡΙΚΑΛΑ");
//
//        Assert.assertFalse("CityDTO should not be set.", cityIsSet);
//
//        City city = new City();
//        city.setName("ΤΡΙΚΑΛΑ");
//
//        city.setDays(this.modelDays);
//
//        doNothing().when(cityDAO).saveCity(city);
//        cityDAO.saveCity(city);
//        verify(cityDAO, times(1)).saveCity(city);
    }

    @Test
    public void test_city_Set_day_measurements_Not_set() {
//        Mockito.when(cityDAO.cityIsSet("ΤΡΙΚΑΛΑ")).thenReturn(true);
//        Mockito.when(dayDAO.dayIsSet(LocalDate.of(2021, 07,07), 54)).thenReturn(false);
//
//        boolean cityIsSet = scraper.cityIsSet.test("ΤΡΙΚΑΛΑ");
//        boolean dayIsSet = scraper.dayIsSet.test(LocalDate.of(2021, 07, 07), 54);
//
//        Assert.assertTrue("CityDTO should be set.", cityIsSet);
//        Assert.assertFalse("DayDTO should not be set.", dayIsSet);
//
//        doNothing().when(measurementDAO).setDailyMeasurements(this.domainDayDTO.getMeasurementDTOS(), 54);
//        measurementDAO.setDailyMeasurements(this.domainDayDTO.getMeasurementDTOS(), 54);
//        verify(measurementDAO, times(1)).setDailyMeasurements(this.domainDayDTO.getMeasurementDTOS(), 54);
    }

    @Test
    public void test_city_day_measurements_set() {
//        Mockito.when(cityDAO.cityIsSet("ΘΕΣΣΑΛΟΝΙΚΗ")).thenReturn(true);
//        Mockito.when(dayDAO.dayIsSet(LocalDate.of(2021, 07, 07), 1)).thenReturn(true);
//        Mockito.when(measurementDAO.measurementsAreSet(1)).thenReturn(true);
//
//        boolean cityExists = scraper.cityIsSet.test("ΘΕΣΣΑΛΟΝΙΚΗ");
//        boolean dayExists = scraper.dayIsSet.test(LocalDate.of(2021, 07, 07), 1);
//        boolean dailyMeasurementsAreSet = scraper.dailyMeasurementsAreSet.test(1);
//
//        Assert.assertTrue("CityDTO should be set.", cityExists);
//        Assert.assertTrue("DayDTO should be set.", dayExists);
//        Assert.assertTrue("Daily measurementDTOS should be set.", dailyMeasurementsAreSet);
//
//        doNothing().when(measurementDAO).checkAndUpdateDailyMeasurement(1, this.domainDayDTO.getMeasurementDTOS());
//        measurementDAO.checkAndUpdateDailyMeasurement(1, this.domainDayDTO.getMeasurementDTOS());
//        verify(measurementDAO, times(1)).checkAndUpdateDailyMeasurement(1, this.domainDayDTO.getMeasurementDTOS());
    }

    @Test
    public void test_city_day_set_measurements_not_set() {
//        Mockito.when(cityDAO.cityIsSet("ΚΑΒΑΛΑ")).thenReturn(true);
//        Mockito.when(dayDAO.dayIsSet(LocalDate.of(2021, 07, 07), 2)).thenReturn(true);
//        Mockito.when(measurementDAO.measurementsAreSet(2)).thenReturn(false);
//
//        boolean cityExists = scraper.cityIsSet.test("ΚΑΒΑΛΑ");
//        boolean dayExists = scraper.dayIsSet.test(LocalDate.of(2021, 07, 07), 2);
//        boolean dailyMeasurementsAreSet = scraper.dailyMeasurementsAreSet.test(2);
//
//        Assert.assertTrue("CityDTO should be set.", cityExists);
//        Assert.assertTrue("DayDTO should be set.", dayExists);
//        Assert.assertFalse("Measurements should not be set.", dailyMeasurementsAreSet);
//
//        doNothing().when(measurementDAO).setDailyMeasurements(this.domainDayDTO.getMeasurementDTOS(), 2);
//        measurementDAO.setDailyMeasurements(this.domainDayDTO.getMeasurementDTOS(), 2);
//        verify(measurementDAO, times(1)).setDailyMeasurements(this.domainDayDTO.getMeasurementDTOS(), 2);
    }
}