package com.emperium.scraper;

import com.emperium.dao.db.*;
import com.emperium.model.City;
import com.emperium.scheduler.ScrapeScheduler;
import com.emperium.utils.Mappings;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import java.time.LocalDate;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Class responsible for taking all values scraped and adds them to database accordingly
 * @author Stefanos Anastasiou
 */
public class MeteoScraper implements Job {

    private Logger logger = Logger.getLogger(MeteoScraper.class);
    private CityDAO cityDAO = new CityDAOImpl();
    private DayDAO dayDAO = new DayDAOImpl();
    private PredictionsDAO measurementsDAO = new PredictionsDaoImpl();
    private City city;

    private int city_id;

    public void init() throws SchedulerException {
        loggerConfig();
        startJob();
    }

    private void initiate() {
        Mappings.cityMappings.keySet()
                .forEach(this::scrapeAndSave);
    }

    private void scrapeAndSave(int ck) {
        try {
            CityScraper cityScraper = new CityScraper(ck);
            cityScraper.scrapeCity();

            city = cityScraper.getCity();
            /* The first time ever the application is run, City table will be empty.
             * from the second time and onwards it will never be empty. We check here in order
             * only to move to predictions*/
            if (isCitySet.test(city.getName())) {
                city_id = cityDAO.getCityId(city.getName());

                city.getDays().forEach(day -> {

                    if (isDaySet.test(day.getDay(), city_id)) {
                        int day_id = dayDAO.getDayId(day.getDay(), city_id);

                        if (dailyMeasurementsAreSet.test(day_id)) {
                            measurementsDAO.checkAndUpdateDailyPredictions(day_id, day.getPredictions());
                        } else {
                            measurementsDAO.setDailyPredictions(day.getPredictions(), day_id);
                        }
                    } else {
                        City city = cityDAO.getCityById(city_id);
                        dayDAO.insertRecords(day, city);
                    }
                });
                deletePreviousMeasurements.accept(city_id);
                deletePreviousDays.accept(city_id);
            } else {

                city.getDays().forEach(day ->
                        day.getPredictions().forEach(prediction ->
                                prediction.setDay(day))
                );
                city.getDays().forEach(day ->
                        day.setCity(city)
                );
                cityDAO.saveCity(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Predicate<String> isCitySet = city -> cityDAO.isCitySet(city);

    public BiPredicate<LocalDate, Integer> isDaySet = (date, city_id) -> dayDAO.isDaySet(date, city_id);

    public Predicate<Integer> dailyMeasurementsAreSet = day_id -> measurementsDAO.isPredictionsSet(day_id);

    private Consumer<Integer> deletePreviousMeasurements = city_id -> measurementsDAO.deleteByCityId(city_id);

    private Consumer<Integer> deletePreviousDays = city_id -> dayDAO.deleteById(city_id);

//    private void insertRecords(boolean cityExists, Day day) {
//        if(cityExists) {
//        City city = cityDAO.getCityById(city_id);
//            dayDAO.insertRecords(day, city);
//        } else {
//            cityDAO.saveCity(city);
//        }
//    }

    private void startJob() throws SchedulerException {
        ScrapeScheduler
                .newInstance()
                .getScraperSchedulerFactory()
                .getScraperScheduler()
                .createJob()
                .createTrigger()
                .setScheduler()
                .start();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.debug("Scheduler started successfully");
        try {
            ScrapeScheduler.scheduler
                    .getCurrentlyExecutingJobs()
                    .forEach(job -> {
                        if (job.getJobDetail().getKey().getName().equals(ScrapeScheduler.SCRAPE_CITY_JOB)) {
                            try {
                                initiate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            logger.error("No such job available");
                        }
                    });
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void loggerConfig() {
        /* Disable HtmlUnit logging */
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }
}