package com.emperium.dao.db;

import com.emperium.model.City;

public interface CityDAO {

    boolean isCitySet(String city);

    int getCityId(String city);

    void saveCity(City city);

    City getCityById(int id);
}
