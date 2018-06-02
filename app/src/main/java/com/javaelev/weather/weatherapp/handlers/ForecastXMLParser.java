package com.javaelev.weather.weatherapp.handlers;

import com.javaelev.weather.weatherapp.model.ForecastItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ForecastXMLParser {

    public List<ForecastItem> getForecast(String xml) {
        return convertXMLStringToList(xml);
    }

    private List<ForecastItem> convertXMLStringToList(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            NodeList list = doc.getElementsByTagName("time");
            return getForeCastItemsFromNodeList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ForecastItem> getForeCastItemsFromNodeList(NodeList list){
        List<ForecastItem> forecast = new ArrayList<>();
        ForecastItem forecastItem;

        LocalDateTime dateTime;
        String windDirection;
        String symbolMeaning;
        String symbolCode;
        float degreesCelsius;
        float windSpeed;

        for(int i = 0; i < list.getLength(); i++){
            Element time = (Element) list.item(i);
            dateTime = LocalDateTime.parse(time.getAttribute("from"));
            Element symbol = (Element) time.getElementsByTagName("symbol").item(0);
            symbolMeaning = symbol.getAttribute("name");
            symbolCode = symbol.getAttribute("var");
            Element windDir = (Element) time.getElementsByTagName("windDirection").item(0);
            windDirection = windDir.getAttribute("name");
            Element windSpd = (Element) time.getElementsByTagName("windSpeed").item(0);
            windSpeed = Float.parseFloat(windSpd.getAttribute("mps"));
            Element temperature = (Element) time.getElementsByTagName("temperature").item(0);
            degreesCelsius = (float)Integer.parseInt(temperature.getAttribute("value"));
            forecastItem = new ForecastItem(dateTime, windDirection, degreesCelsius, windSpeed, symbolCode, symbolMeaning);
            forecast.add(forecastItem);

        }
        return forecast;
    }

}
