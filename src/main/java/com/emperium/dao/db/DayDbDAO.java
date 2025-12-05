package com.emperium.dao.db;

import com.emperium.model.City;
import com.emperium.model.Day;

import java.time.LocalDate;

public interface DayDbDAO {

    boolean isDaySet(LocalDate date, int city);

    int getDayId(LocalDate day, int city_id);

    void deleteById(int city_id);

    void insertRecords(Day day, City city);
}
