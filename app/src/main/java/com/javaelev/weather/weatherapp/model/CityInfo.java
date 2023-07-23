package com.javaelev.weather.weatherapp.model;

public class CityInfo {

    private String city;
    private String cityAreaCode;
    private String countryCode;
    private float lat;
    private float lon;

    public CityInfo(String city,String cityAreaCode, String countryCode, float lat, float lon){
        this.city = city;
        this.cityAreaCode = cityAreaCode;
        this.countryCode = countryCode;
        this.lat = lat;
        this.lon = lon;
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

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
