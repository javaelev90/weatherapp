package com.javaelev.weather.weatherapp.model;

import java.time.LocalDateTime;

public class ForecastItem {

    private LocalDateTime forecastTime;
    private String windDirection;
    private String symbolMeaning;
    private String symbolCode;
    private float degreesCelsius;
    private float windSpeed;

    public ForecastItem(LocalDateTime forecastTime, String windDirection, float degreesCelsius, float windSpeed, String symbolCode, String symbolMeaning){
        this.forecastTime = forecastTime;
        this.windDirection = windDirection;
        this.degreesCelsius = degreesCelsius;
        this.windSpeed = windSpeed;
        this.symbolCode = symbolCode;
        this.symbolMeaning = symbolMeaning;
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

    public LocalDateTime getForecastTime() {
        return forecastTime;
    }
}
