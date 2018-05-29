package com.javaelev.weather.weatherapp.model;

public class CityInfo {

    private String city;
    private String cityAreaCode;
    private String countryCode;

    public CityInfo(String city,String cityAreaCode, String countryCode){
        this.city = city;
        this.cityAreaCode = cityAreaCode;
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public String getCityAreaCode() {
        return cityAreaCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
