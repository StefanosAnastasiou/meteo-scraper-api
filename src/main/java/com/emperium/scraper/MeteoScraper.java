package com.emperium.scraper;

import com.emperium.dao.db.*;
import com.emperium.model.City;
import com.emperium.model.Day;
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
 *
 * @author Stefanos Anastasiou
 */
public class MeteoScraper implements Job {

    private Logger logger = Logger.getLogger(MeteoScraper.class);
    private CityDAO cityDAO = new CityDAOImpl();
    private DayDbDAO dayDbDAO = new DayDbDAOImpl();
    private PredictionsDAO measurementsDAO = new PredictionsDaoImpl();
    private City modelCity;

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

             modelCity = cityScraper.getCity();
            /* The first time ever the application is run, City table will be empty.
            * from the second time and onwards it will never be empty. We check here in order
            * only to move to predictions*/
            if(cityIsSet.test(modelCity.getName())) {
                city_id = cityDAO.getCityId(modelCity.getName());

                modelCity.getDays().forEach(day -> {

                    if(dayIsSet.test(day.getDay(), city_id)) {
                        int day_id = dayDbDAO.getDayId(day.getDay(), city_id);

                        if (dailyMeasurementsAreSet.test(day_id)) {
                            measurementsDAO.checkAndUpdateDailyPredictions(day_id, day.getPredictions());
                        } else {
                            measurementsDAO.setDailyPredictions(day.getPredictions(), day_id);
                        }
                    } else {
                        insertRecords(true, day);
                    }
                });
                deletePreviousMeasurements.accept(city_id);
                deletePreviousDays.accept(city_id);
            } else {
                insertRecords(false, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Predicate<String> cityIsSet = city -> cityDAO.isCitySet(city);

    public BiPredicate<LocalDate, Integer> dayIsSet = (date, city_id) -> dayDbDAO.isDaySet(date, city_id);

    public Predicate<Integer> dailyMeasurementsAreSet = day_id -> measurementsDAO.isPredictionsSet(day_id);

    private Consumer<Integer> deletePreviousMeasurements = city_id -> measurementsDAO.deleteByCityId(city_id);

    private Consumer<Integer> deletePreviousDays = city_id -> dayDbDAO.deleteById(city_id);

    private void insertRecords(boolean cityExists, Day day) {
        if(cityExists) {
        City city = cityDAO.getCityById(city_id);
            dayDbDAO.insertRecords(day, city);
        } else {
            cityDAO.saveCity(modelCity);
        }
    }

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
//                                initiate();
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