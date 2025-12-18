package com.emperium.utils;

import com.emperium.config.HibernateAnnotationUtil;
import com.emperium.model.City;
import com.emperium.model.Day;
import com.emperium.model.Predictions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Class that holds all SQL operations.
 * @author Stefanos Anastasiou
 */
public class SQL {

    private static SQL instance;

    private SQL() {
    }

    public static SQL getInstance() {
        if (instance == null) {
            instance = new SQL();
        }
        return instance;
    }

    private static final String INSERT_INTO_PREDICTIONS = "INSERT INTO Predictions(time, temperature, humidity, wind, phenomeno, day_id) VALUES" +
            " (:time, :temperature, :humidity, :wind, :phenomeno, :day_id)";

    private final String UPDATE_TEMPERATURE = "UPDATE Predictions SET temperature=:temperature WHERE id=:id";

    private final String UPDATE_HUMIDITY = "UPDATE Predictions SET humidity=:humidity WHERE id=:id";

    private final String UPDATE_WIND = "UPDATE Predictions SET wind=:wind WHERE id=:id";

    private final String UPDATE_PHENOMENO = "UPDATE Predictions SET phenomeno=:phenomeno WHERE id=:id";

    private final String SELECT_CITY_PREDICTIONS_BY_DAY = "SELECT C.city, D.day, M.time, M.temperature, M.wind, M.humidity, M.phenomeno FROM Predictions AS M" +
            " JOIN Day AS D on M.day_id = D.id" +
            " JOIN City C on D.city_id = C.id where D.day=:day AND C.city=:city";

    private final String SELECT_CITY_PREDICTIONS_BY_TIME = "SELECT C.city, D.day, M.time, M.temperature, M.wind, M.humidity, M.phenomeno FROM Predictions AS M" +
            " JOIN Day AS D on M.day_id = D.id" +
            " JOIN City C on D.city_id = C.id where D.day=:day AND C.city=:city AND M.time=:time";

    private final String SELECT_CITY_PREDICTIONS = "SELECT C.city, D.day, M.time, M.temperature, M.wind, M.humidity, M.phenomeno FROM Predictions AS M" +
            " JOIN Day AS D on M.day_id = D.id" +
            " JOIN City AS C on D.city_id = C.id where C.city=:city" +
            " AND D.day >= current_date ";
//            "and M.id >= (SELECT PredictionId(city))";

    private static final String TEMPERATURE = "temperature";
    private static final String HUMIDITY = "humidity";
    private static final String WIND = "wind";
    private static final String PHENOMENO = "phenomeno";
    private static final String DAY = "day";
    private static final String TIME = "time";
    private static final String CITY = "city";


    private SessionFactory sessionFactory() {
        SessionFactory sessionFactory = HibernateAnnotationUtil.getSessionFactory();

        return sessionFactory;
    }

    /**
     * Gets the id of a specified city.
     *
     * @param city
     * @return the id
     */
    public int getCityId(String city) {
        Session session = sessionFactory().openSession();
        Query query = session.createSQLQuery("SELECT id FROM City WHERE city=:city");
        query.setParameter(CITY, city);
        List<Object> results = query.getResultList();
        session.close();

        return (int) results.get(0);
    }

    /**
     * Checks if predictions for a specified day are set
     *
     * @param day_id the day id
     * @return true if predictions are set
     */
    public boolean isDailyPredictionsSet(int day_id) {
        Session session = sessionFactory().openSession();
        String hql = "SELECT * FROM Predictions WHERE day_id =:day_id";
        Query query = session.createSQLQuery(hql);
        query.setParameter("day_id", day_id);

        List<Object[]> results = query.getResultList();
        session.close();

        return results.isEmpty() ? false : true;
    }

    /**
     * Gets the id of a specified day that already exists in the database.
     *
     * @param day the day
     * @return the id of the day.
     */
    public int getDayId(LocalDate day, int city_id) {
        String hql = "SELECT id FROM Day WHERE day=:day AND city_id=:city_id";
        Session session = sessionFactory().openSession();
        Query query = session.createSQLQuery(hql);
        query.setParameter(DAY, day);
        query.setParameter("city_id", city_id);

        List<Object> result = query.getResultList();
        session.close();

        return (int) result.get(0);
    }

    /**
     * Checks if the date for a specified city is already set.
     *
     * @param day     the day
     * @param city_id city id
     *
     * @return true if the date already exists in the database.
     */
    public boolean dayIsSet(LocalDate day, int city_id) {
        Session session = sessionFactory().openSession();
        String hql = "SELECT day FROM Day WHERE day=:day AND city_id=:city_id";

        Query query = session.createSQLQuery(hql);
        query.setParameter(DAY, day);
        query.setParameter("city_id", city_id);

        List<Object> result = query.getResultList();
        session.close();

        return !result.isEmpty() ? true : false;
    }

    /**
     * Inserts weather predictions for a specified day.
     *
     * @param predictions a List of {@link Predictions}
     * @param dayId        day id
     */
    public void selectDailyPredictions(List<Predictions> predictions, int dayId) {
        for (Predictions prediction : predictions) {
            Session session = sessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createSQLQuery(INSERT_INTO_PREDICTIONS);
            query.setParameter(TIME, prediction.getTime());
            query.setParameter(TEMPERATURE, prediction.getTemperature());
            query.setParameter(HUMIDITY, prediction.getHumidity());
            query.setParameter(WIND, prediction.getWind());
            query.setParameter(PHENOMENO, prediction.getPhenomeno());
            query.setParameter("day_id", dayId);
            query.executeUpdate();

            commitTransaction(tx);
            session.close();
        }
    }

    /**
     * Checks if weather predictions are the same and updates id necessary.
     *
     * @param day_id       the day id
     * @param predictions a list of {@link Predictions}
     */
    public void checkAndUpdateDailyPredictions(int day_id, List<Predictions> predictions) {
        Session session = sessionFactory().openSession();
        String sqlQuery = "SELECT * FROM " +
                "(SELECT * FROM Predictions WHERE day_id = " + day_id + " ORDER BY id DESC LIMIT " + predictions.size() + ") " +
                "a ORDER BY id;";

        Query query = session.createSQLQuery(sqlQuery);
        List<Object[]> result = query.getResultList();
        session.close();

        int i = 0;
        for (Object[] data : result) {
            if (data[2].equals(predictions.get(i).getTemperature())) {
                assert true;
            } else {
                updatePrediction(UPDATE_TEMPERATURE, predictions.get(i), data[0], TEMPERATURE);
            }

            if (data[3].equals(predictions.get(i).getHumidity())) {
                assert true;
            } else {
                updatePrediction(UPDATE_HUMIDITY, predictions.get(i), data[0], HUMIDITY);
            }

            if (data[4].equals(predictions.get(i).getWind())) {
                assert true;
            } else {
                updatePrediction(UPDATE_WIND, predictions.get(i), data[0], WIND);
            }

            if (data[5].equals(predictions.get(i).getPhenomeno())) {
                assert true;
            } else {
                updatePrediction(UPDATE_PHENOMENO, predictions.get(i), data[0], PHENOMENO);
            }
            i++;
        }


        if (i < 8) {
            for (int k = i; k < predictions.size(); k++) {
                Session sess = sessionFactory().openSession();
                Transaction tx = sess.beginTransaction();
                Query ms = sess.createSQLQuery(INSERT_INTO_PREDICTIONS);
                ms.setParameter(TIME, predictions.get(k).getTime());
                ms.setParameter(TEMPERATURE, predictions.get(k).getTemperature());
                ms.setParameter(HUMIDITY, predictions.get(k).getHumidity());
                ms.setParameter(WIND, predictions.get(k).getWind());
                ms.setParameter(PHENOMENO, predictions.get(k).getPhenomeno());
                ms.setParameter("day_id", day_id);
                ms.executeUpdate();

                commitTransaction(tx);
                sess.close();
            }
        }
    }

    /**
     * Update any of the measurements specified in the parameter if they differ
     *
     * @param hql         the hql
     * @param predictions the {@link Predictions}
     * @param id          id the id
     * @param record      the record
     */
    private void updatePrediction(String hql, Predictions predictions, Object id, String record) {
        Session session = sessionFactory().openSession();
        Object val = switch (record) {
            case TEMPERATURE -> predictions.getTemperature();
            case HUMIDITY -> predictions.getHumidity();
            case WIND -> predictions.getWind();
            default -> predictions.getPhenomeno();
        };

        Transaction tx = session.beginTransaction();
        Query query = session.createSQLQuery(hql);
        query.setParameter(record, val);
        query.setParameter("id", id);
        query.executeUpdate();

        commitTransaction(tx);
        session.close();
    }

    /**
     * Checks if a specified city is already set in the database.
     *
     * @param city city name
     * @return true if a specified city is set
     */
    public boolean cityIsSet(String city) {
        Session session = sessionFactory().openSession();
        Query query = session.createSQLQuery("SELECT city FROM City WHERE city=:city");
        query.setParameter(CITY, city);
        List<Object> results = query.getResultList();
        session.close();

        return results.isEmpty() ? false : true;
    }

    /**
     * Deletes the measurements of the previous days.
     *
     * @param city_id city id used to query and delete previous measurements
     */
    public void deletePredictionsByDayId(int city_id) {
        Session session = sessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String hql = "DELETE FROM Predictions WHERE day_id BETWEEN (SELECT id FROM Day WHERE city_id=:city_id AND day < current_date ORDER BY id ASC LIMIT 1)" +
                    " AND (SELECT id FROM Day WHERE city_id=:city_id AND day < current_date ORDER BY id DESC LIMIT 1)";
            Query query = session.createSQLQuery(hql);
            query.setParameter("city_id", city_id);
            query.executeUpdate();
            commitTransaction(tx);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Deletes days which are earlier than the current day for a specified city
     *
     * @param city_id the city id for which days are deleted
     */
    public void deleteDays(int city_id) {
        Session session = sessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "DELETE from Day where city_id=:city_id AND day < current_date";
            Query query = session.createSQLQuery(sql);
            query.setParameter("city_id", city_id);
            query.executeUpdate();
            commitTransaction(tx);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Saves a city to database.
     *
     * @param city city name
     */
    public void saveCity(City city) {
        Session session = sessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(city);

        commitTransaction(tx);
        session.close();
    }

    /**
     * Inserts new days and measurement into an existing City.
     *
     * @param day  the {@link Day}
     * @param city the {@link City}
     */
    public void insertByCityId(Day day, City city) {
        Session session = sessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            day.setCity(city);
            session.save(day);
            commitTransaction(tx);
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }

    }

    /**
     * Gets a City entity by id.
     *
     * @param city_id the city id
     * @return the {@link City}
     */
    public City getCityById(int city_id) {
        Session session = sessionFactory().openSession();
        City city = null;
        try {
            session = sessionFactory().openSession();
            city = session.load(City.class, city_id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return city;
    }

    /**
     * Gets the predictions for a day for a specified city.
     *
     * @param city city
     * @param date date
     * @return result list
     */
    public List<Object[]> getCityDailyPredictions(String city, String date) {
        Session session = sessionFactory().openSession();
        Query cityQuery = session.createSQLQuery(SELECT_CITY_PREDICTIONS_BY_DAY);
        cityQuery.setParameter(CITY, city);
        cityQuery.setParameter(DAY, date);

        List<Object[]> result = cityQuery.list();
        session.close();

        return result;
    }

    /**
     * Gets the predictions for a specified time of a day, for a specified city.
     *
     * @param city city
     * @param date date
     * @param time time
     * @return result list
     */
    public List<Object[]> getCityPredictionsPerHour(String city, String date, String time) {
        Session session = sessionFactory().openSession();
        Query query = session.createSQLQuery(SELECT_CITY_PREDICTIONS_BY_TIME);
        query.setParameter(CITY, city);
        query.setParameter(DAY, date);
        query.setParameter(TIME, time);

        List<Object[]> result = query.list();
        session.close();

        return result;
    }

    /**
     * Gets all the predictions for a specified city, from the time requested and onwards.
     *
     * @param city city
     * @return result list
     */
    public List<Object[]> getCityPredictions(String city) {
        Session session = sessionFactory().openSession();
        Query query = session.createSQLQuery(SELECT_CITY_PREDICTIONS);
        query.setParameter(CITY, city);

        List<Object[]> result = query.getResultList();
        session.close();

        return result;
    }

    /**
     * Commits a transaction.
     *
     * @param tx transaction object
     */
    private static void commitTransaction(Transaction tx) {
        if (tx.getStatus().equals(TransactionStatus.ACTIVE)) {
            tx.commit();
        }
    }
}