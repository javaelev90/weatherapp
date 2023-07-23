package com.javaelev.weather.weatherapp.model;

import java.time.LocalDateTime;

public class ForecastItem {

    private LocalDateTime forecastTimeFrom;
    private LocalDateTime forecastTimeTo;
    private String windDirection;
    private String symbolMeaning;
    private String symbolCode;
    private float precipitationMin;
    private float precipitationMax;
    private float degreesCelsius;
    private float windSpeed;
    private float cloudiness;
    public ForecastItem(){
    }
    public ForecastItem(LocalDateTime forecastTimeFrom, LocalDateTime forecastTimeTo, String windDirection,
                        float degreesCelsius, float windSpeed, String symbolCode,
                        String symbolMeaning, float precipitationMin, float precipitationMax, float cloudiness){
        this.forecastTimeFrom = forecastTimeFrom;
        this.forecastTimeTo = forecastTimeTo;
        this.windDirection = windDirection;
        this.degreesCelsius = degreesCelsius;
        this.windSpeed = windSpeed;
        this.symbolCode = symbolCode;
        this.symbolMeaning = symbolMeaning;
        this.precipitationMax = precipitationMax;
        this.precipitationMin = precipitationMin;
        this.cloudiness = cloudiness;
    }

    public float getPrecipitationMin() {
        return precipitationMin;
    }

    public float getPrecipitationMax() {
        return precipitationMax;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public float getDegreesCelsius() {
        return degreesCelsius;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public String getSymbolMeaning() {
        return symbolMeaning;
    }

    public String getSymbolCode() {
        return symbolCode;
    }

    public LocalDateTime getForecastTimeFrom() {
        return forecastTimeFrom;
    }
    public LocalDateTime getForecastTimeTo() {
        return forecastTimeTo;
    }
    public float getCloudiness() { return cloudiness; }


    public void setForecastTimeFrom(LocalDateTime forecastTimeFrom) {
        this.forecastTimeFrom = forecastTimeFrom;
    }

    public void setForecastTimeTo(LocalDateTime forecastTimeTo) {
        this.forecastTimeTo = forecastTimeTo;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setSymbolMeaning(String symbolMeaning) {
        this.symbolMeaning = symbolMeaning;
    }

    public void setSymbolCode(String symbolCode) {
        this.symbolCode = symbolCode;
    }

    public void setPrecipitationMin(float precipitationMin) {
        this.precipitationMin = precipitationMin;
    }

    public void setPrecipitationMax(float precipitationMax) {
        this.precipitationMax = precipitationMax;
    }

    public void setDegreesCelsius(float degreesCelsius) {
        this.degreesCelsius = degreesCelsius;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setCloudiness(float cloudiness) {
        this.cloudiness = cloudiness;
    }
}
