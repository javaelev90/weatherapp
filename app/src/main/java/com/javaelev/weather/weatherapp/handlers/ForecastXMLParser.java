package com.javaelev.weather.weatherapp.handlers;

import com.javaelev.weather.weatherapp.model.ForecastItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
            NodeList list = doc.getElementsByTagName("tabular");
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
        float precipitationMin;
        float precipitationMax;
        Node tabular = list.item(0);
        NodeList timeList = tabular.getChildNodes();
        System.out.println(timeList.toString());
        for(int i = 0; i < timeList.getLength(); i++){
            if(!(timeList.item(i).getNodeType() == Node.ELEMENT_NODE)) continue;
            Element time =  (Element)timeList.item(i);
            dateTime = LocalDateTime.parse(time.getAttribute("from"));
            Element symbol = (Element) time.getElementsByTagName("symbol").item(0);
            symbolMeaning = symbol.getAttribute("name");
            symbolCode = symbol.getAttribute("var");
            Element windDir = (Element) time.getElementsByTagName("windDirection").item(0);
            windDirection = windDir.getAttribute("name");
            Element windSpd = (Element) time.getElementsByTagName("windSpeed").item(0);
            windSpeed = Float.valueOf(windSpd.getAttribute("mps"));
            Element temperature = (Element) time.getElementsByTagName("temperature").item(0);
            Element precipitation = (Element) time.getElementsByTagName("precipitation").item(0);

            float precipitationAvg = Float.valueOf(precipitation.getAttribute("value"));
            String maxValue = precipitation.getAttribute("maxvalue");
            String minValue = precipitation.getAttribute("minvalue");

            if(precipitationAvg == 0.0f || (maxValue.equals("") && minValue.equals(""))){
                precipitationMax = precipitationAvg;
                precipitationMin = precipitationAvg;
            } else {
                precipitationMax = Float.valueOf(precipitation.getAttribute("maxvalue"));
                precipitationMin = Float.valueOf(precipitation.getAttribute("minvalue"));
            }

            degreesCelsius = Float.valueOf(temperature.getAttribute("value"));
            forecastItem = new ForecastItem(dateTime, windDirection, degreesCelsius, windSpeed, symbolCode, symbolMeaning, precipitationMin, precipitationMax);
            forecast.add(forecastItem);

        }
        return forecast;
    }

}
