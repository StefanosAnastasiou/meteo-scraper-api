package com.emperium.scraper;

import com.emperium.enums.DomElementsEnum;
import com.emperium.model.City;
import com.emperium.model.Day;
import com.emperium.model.Predictions;
import com.emperium.utils.Mappings;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.log4j.Logger;

import com.emperium.scheduler.ScrapeScheduler;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that holds all scraping logic.
 * It is responsible for scraping elements from {@link <a href="https://www.meteo.gr/">Meteo</a>}
 * and sets the {@link City} model accordingly.
 *
 * @author Steafanos Anastasiou
 */
public class CityScraper {

    private City city;
    private final Logger logger = Logger.getLogger(CityScraper.class);
    private final Calendar now;
    private static final String SERVICE_URL = "https://www.meteo.gr/cf.cfm?city_id=";
    private final int cityKey;

    public CityScraper(int cityKey) {
        this.cityKey = cityKey;
        this.now = Calendar.getInstance();
        this.now.add(Calendar.MONTH, 1);
    }

    /**
     * Scrapes the city. This only works as long as {@link <a href="https://www.meteo.gr/">Meteo</a>} keeps these
     * specific names in the DOM elements !!! If elements change in {@link <a href="https://www.meteo.gr/">Meteo</a>},
     * update {@link DomElementsEnum}
     */
    public void scrapeCity() {
        try {
            WebClient client = new WebClient();

            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);

            HtmlPage scrapedCity = client.getPage(SERVICE_URL + cityKey);

            List<HtmlElement> datesEvent = scrapedCity.getByXPath(DomElementsEnum.DATE.getElement());
//            List<HtmlElement> months =  city.getByXPath("//span[@class='monthNumbercf']");
            List<HtmlElement> months = scrapedCity.getByXPath(DomElementsEnum.MONTH.getElement());
            List<HtmlElement> holidayEventDate = scrapedCity.getByXPath("//span[@class='monthNumbercf']");
            //table[@class="info"]//td[2]//text()
//            List<HtmlElement> eventTime = city.getByXPath("//table[@id='outerTable']//td");
            List<HtmlElement> eventTime = scrapedCity.getByXPath(DomElementsEnum.TIME.getElement());
            List<HtmlElement> eventTemp = scrapedCity.getByXPath(DomElementsEnum.TEMPERATURE.getElement());
            List<HtmlElement> eventExtremeTemp = scrapedCity.getByXPath(DomElementsEnum.EXTREME_TEMPERATURE.getElement());
            List<HtmlElement> eventHumidity = scrapedCity.getByXPath(DomElementsEnum.HUMIDITY.getElement());
            List<HtmlElement> eventWind = scrapedCity.getByXPath(DomElementsEnum.WIND.getElement());
            List<HtmlElement> phenomeno = scrapedCity.getByXPath(DomElementsEnum.PHONOMENON.getElement());


            List<String> datesList = daysToList(datesEvent);
            List<String> monthsList = monthsToList(months);
//            List<LocalDate> holidaysList = holidaysToList(holidayEventDate);
            List<LocalDate> dates = zipDaymonthToLocaDate(datesList, monthsList);

            List<LocalTime> timeList = timeToList(eventTime);

            List<Integer> temperatureList = new ArrayList<>();
            if (!eventTemp.isEmpty()) {
                temperatureList = tempToList(eventTemp, 0);
            } else {
                temperatureList = tempToList(eventExtremeTemp, 0);
            }

            List<Integer> humidityList = humidityToList(eventHumidity, 1);
            List<String> windList = windToList(eventWind);
            List<String> phenomenonList = phenomenoToList(phenomeno);

//            if (holidaysList.size() != 0) {
//                dates.addAll(holidaysList);
//                Collections.sort(dates);
//
//            setDailyMeasurements(timeList, temperatureList, humidityList, windList, phenomenonList);
            //            setCityDays(dates);
//            measurements = getMeasurementsPerDay(timeList, temperatureList, humidityList, windList, phenomenonList);

            city = new City();
            city.setName(Mappings.cityMappings.get(cityKey));
            List<Day> days = setDailyMeasurements(timeList, temperatureList, humidityList, windList, phenomenonList, dates);

            city.setDays(days);

            logger.info("Scrape for city " + city.getName() + " finished successfully.");
        } catch (Exception e) {
            logger.error("Scrape for city " + city.getName() + " failed due to " + e.getMessage());
        }
    }

    public List<LocalDate> zipDaymonthToLocaDate(List<String> datesList, List<String> monthsList) {
        final String firstDate = monthsList.get(0);
        final int nextYear = now.get(Calendar.YEAR);

        return IntStream
                .range(0, Math.min(datesList.size(), monthsList.size()))
                .mapToObj(i -> datesList.get(i) + "-" + Mappings.monthMappings.get(monthsList.get(i).trim()))
                .map(stringDate -> {
                    /** If length is 4 then we know that the days are in the range of the first 10 days of the month */
                    if (stringDate.length() == 4) {
                        stringDate = "0" + stringDate;
                        if (checkIfNextMonthIsNextYear(stringDate, 2, 4, firstDate)) {
                            stringDate += "-" + nextYear;
                            return formatStringToLocalDate(stringDate);
                        }
                    }
                    if (stringDate.length() == 5) {
                        if (checkIfNextMonthIsNextYear(stringDate, 3, 5, firstDate))
                            return formatStringToLocalDate(stringDate);
                    }

                    int year = now.getInstance().get(Calendar.YEAR);
                    stringDate += "-" + year;

                    return formatStringToLocalDate(stringDate);
                }).collect(Collectors.toList());
    }

    private LocalDate formatStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return LocalDate.parse(date, formatter);
    }

    public List<String> daysToList(List<HtmlElement> datesEvent) {
        return datesEvent
                .stream()
//                .map(d -> d.getNodeName().replaceAll("[^\\d]", ""))
                .map(d -> ((DomText) d.getFirstChild()).getData())
//                .map(dt -> {
//                    /** Remove sunset and sunrise digits and keep only date **/
//                    if (dt.length() == 10) {
//                        return dt.substring(0, 2);
//                    } else if (dt.length() == 2){
//                        return dt;
//                    }
//                    return "0" + dt.substring(0, 1);
//                })
                .collect(Collectors.toList());
    }

    public List<LocalDate> holidaysToList(List<HtmlElement> eventHoliday) {
        return eventHoliday
                .stream()
                .map(hd -> ((DomText) hd.getFirstChild()).getData().replaceAll("[Α-Ω]", ""))
                .map(h -> h.replace("Ϊ", ""))
                .filter(day -> !day.equals(""))
                .map(d -> {
                    try {
                        return stringToLocalDate(d.replace("/", "-"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public List<String> monthsToList(List<HtmlElement> months) {
        return months.stream()
                .map(m -> formatMonth.apply(((DomText) m.getFirstChild()).getData().trim()))
                .collect(Collectors.toList());
    }

    private Function<String, String> formatMonth = (String month) -> {
        if (month.contains("/")) {
            return Mappings.monthMappings
                    .entrySet()
                    .stream()
                    .filter(m -> m.getValue().equals(month.substring(3, 5)))
                    .findFirst()
                    .get()
                    .getKey();
        }
        return month.trim();
    };

    public City getCity() {
        return city;
    }


    /**
     * Compares the first value of foreCastTimeValues method parameter with all the values of {@link Mappings#summerTimeMappings}
     * and {@link Mappings#winterTimeMappings}. The first value that is matched, will determine if the scraping occurs
     * during summer or winter and this method will return the index of the matched value.
     * <br></br>
     * Example 1: user makes {@link ScrapeScheduler} start 2:30 AM during winter. This means that the first measurement
     * {@link Mappings#winterTimeMappings}
     * at 02:00 AM is not scraped. So for this day only, there will be only 7 predictions scraped, while for the rest
     * of the days, 8 predictions per day will be scraped.
     * <br></br>
     * Example 2: user makes {@link ScrapeScheduler} start at 06:20 AM during summer. This means that the measurements
     * at 03:00 AM and 06:00 are not scraped. So for this day only there will be 6 predictions scraped and for the rest
     * of the other days, 8 predictions per day will be scraped.
     * <br></br>
     * If scraping begins before the start of the day we split the measurements into sets
     * of 8 measurements (since in each day, 8 measurements occur).
     * Otherwise we only keep the measurement at the index returned in this method until the
     * last index of the first day and then split the rest of the values into sets of 8 values.
     * This implementation gives the ability to the user to set the scraping scheduler
     * at any time desired or even multiple times a day.
     *
     * @param foreCastTimeValues the time values the scraping occurs
     * @return The index of the first forecast time value
     */
    private int getTimeMappingIndex(List<LocalTime> foreCastTimeValues) {
        Optional<Integer> summerMapping = Mappings.summerTimeMappings.stream()
                .filter(tm -> tm.compareTo(foreCastTimeValues.get(0)) == 0)
                .findFirst()
                .map(val -> Mappings.summerTimeMappings.indexOf(val))
                .or(() -> Optional.of(8));

        Optional<Integer> winterMapping = Mappings.winterTimeMappings.stream()
                .filter(tm -> tm.compareTo(foreCastTimeValues.get(0)) == 0)
                .findFirst()
                .map(val -> Mappings.winterTimeMappings.indexOf(val))
                .or(() -> Optional.of(8));

        return summerMapping.get() != 8 ? summerMapping.get() : winterMapping.get();
    }

    public List<Day> setDailyMeasurements(List<LocalTime> foreCastTimeValues, List<Integer> forecastTempValues,
                                          List<Integer> forecastHumidValues, List<String> forecastWindValues,
                                          List<String> forecastPhenomenonValues, List<LocalDate> days) {

        List<Day> days1 = new ArrayList<>();

        /*
         * ScrapeScheduler is designed so that the scraping starts at any time of the day.
         *
         * If scraping starts before the first measurement of the day.
         * e.g. for summer before 3:00 (not inclusive) and after 00:00 (not inclusive)
         * and for winter before 02:00 (not inclusive) and after 23:00
         */
        if (getTimeMappingIndex(foreCastTimeValues) == 0) {
            int index = 0;
            for (int i = 0; i < days.size(); i++) {
                List<Predictions> predictions = new ArrayList<>();
                Day day = new Day();
                day.setDay(days.get(i));

                for (int k = 0; k < Mappings.summerTimeMappings.size(); k++) {
                    if (index == foreCastTimeValues.size()) {
                        break;
                    } else {
                        Predictions prediction = setPredictions(foreCastTimeValues.get(index),
                                forecastTempValues.get(index), forecastHumidValues.get(index),
                                forecastWindValues.get(index),
                                forecastPhenomenonValues.get(index));
                        predictions.add(prediction);
                        index++;
                    }
                }
                day.setPredictions(predictions);
                days1.add(day);
            }
            return days1;
            /*
             * If scraping starts after the first predictions of the day.
             * e.g. for summer scrape starts after 03:00 AM and for winter after 02:00 AM
             */
        } else {
            List<Predictions> firstDayPredictions = new ArrayList<>();

            Day firstDay = new Day();
            firstDay.setDay(days.get(0));
            for (int i = 0; i < Mappings.summerTimeMappings.size() - getTimeMappingIndex(foreCastTimeValues); i++) {
                Predictions predictions = setPredictions(foreCastTimeValues.get(i), forecastTempValues.get(i),
                        forecastHumidValues.get(i), forecastWindValues.get(i),
                        forecastPhenomenonValues.get(i));

                firstDayPredictions.add(predictions);
            }

            firstDay.setPredictions(firstDayPredictions);
            days1.add(firstDay);

            int nextDayPredictionIndex = Mappings.summerTimeMappings.size() - getTimeMappingIndex(foreCastTimeValues);
            for (int i = 1; i < days.size(); i++) {
                Day day = new Day();
                day.setDay(days.get(i));
                List<Predictions> nextDaysPredictions = new ArrayList<>();
                /** We already know that each day has 8 measurements */
                for (int j = 0; j < 8; j++) {
                    if (nextDayPredictionIndex == foreCastTimeValues.size()) {
                        break;
                    } else {
                        Predictions predictions = setPredictions(foreCastTimeValues.get(nextDayPredictionIndex), forecastTempValues.get(nextDayPredictionIndex),
                                forecastHumidValues.get(nextDayPredictionIndex), forecastWindValues.get(nextDayPredictionIndex),
                                forecastPhenomenonValues.get(nextDayPredictionIndex));

                        nextDaysPredictions.add(predictions);
                        nextDayPredictionIndex++;
                    }

                }
                day.setPredictions(nextDaysPredictions);
                days1.add(day);
            }
        }
        return days1;
    }

    private Predictions setPredictions(LocalTime time, Integer temperature,
                                       Integer humidity, String wind, String phenomeno) {
        Predictions predictions = new Predictions();
        predictions.setPhenomeno(phenomeno);
        predictions.setWind(wind);
        predictions.setHumidity(humidity);
        predictions.setTemperature(temperature);
        predictions.setTime(time);

        return predictions;
    }

    private LocalDate stringToLocalDate(String day) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return LocalDate.parse(day, formatter);
    }

    public List<LocalTime> timeToList(List<HtmlElement> eventTime) {
        return eventTime
                .stream()
                .map(tl -> tl.asNormalizedText().trim() + ":00")
                .map(LocalTime::parse)
                .toList();
    }

    public List<Integer> tempToList(List<HtmlElement> temperature, int element) {
        return splitTempHumidity(temperature, element);
    }

    private List<Integer> humidityToList(List<HtmlElement> humidity, int element) {
        return splitTempHumidity(humidity, element);
    }

    public List<String> windToList(List<HtmlElement> wind) {
        return wind
                .stream()
                .map(DomNode::asNormalizedText)
                .map(wd -> {
                    if (wd.matches("[a-zA-Z]+")) {
                        return wd;
                    }
                    String[] event = wd.split("\\R");
                    return event[0].trim();
                })
                .toList();
    }

    public List<String> phenomenoToList(List<HtmlElement> phenomeno) {
        return phenomeno
                .stream()
                .map(ph -> ph.asNormalizedText().trim())
                .collect(Collectors.toList());
    }

    /**
     * Temperature element comes with a small hidden element underneath representing the humidity and sometimes
     * with a "feels like" extra temperature value. All these are scraped in two lines and when the "feels like"
     * temperature is present then they are scraped in three. We only keep the values for temperature and humidity.
     *
     * @param event the event scraped
     * @return the event input parameter as a List
     */
    private List<Integer> splitTempHumidity(List<HtmlElement> event, int element) {
        return event
                .stream()
                .map(DomNode::asNormalizedText)
                .map(t -> {
                    String[] items = t.split("\\R");
                    if (items.length == 3 && element == 1) {
                        return items[2].replaceAll("[^0-9]", "");
                    }
                    /** If temperature is minus degrees Celcius */
                    if (items[element].startsWith("-")) {
                        String temp = items[element].replaceAll("[^0-9]", "");
                        return "-" + temp;
                    }
                    return items[element].replaceAll("[^0-9]", "");
                })
                .map(Integer::parseInt)
                .toList();
    }

    /**
     * This method checks if one of the measurements belongs to the next year. In this case we need to concatenate
     * the next year instead of the current in the place where this method is called i.e. (see {@link #zipDaymonthToLocaDate(List, List)})
     * e.g. if one measurement is 31-12 and the next is 01-12 then in the concatenation of the year we will need to add the next year.
     *
     * @param stringDate the date as String that needs to be checked
     * @param startIndex the position of the starting index of the String checked.
     * @param endIndex   the position of ending index of the String checked
     * @param firstDate  the date of the year
     * @return true if one of measurements belongs to new years eve false otherwise
     */
    private boolean checkIfNextMonthIsNextYear(String stringDate, int startIndex, int endIndex, String firstDate) {
        return stringDate.substring(startIndex, endIndex).equals("01") && firstDate.equals(firstDate);
    }
}