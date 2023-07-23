package com.javaelev.weather.weatherapp.handlers;

import com.javaelev.weather.weatherapp.model.ForecastItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// API
// https://api.met.no/weatherapi/locationforecast/2.0/documentation
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
            NodeList list = doc.getElementsByTagName("product");
            return getForeCastItemsFromNodeList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ForecastItem> getForeCastItemsFromNodeList(NodeList list){
        List<ForecastItem> forecast = new ArrayList<>();
        ForecastItem forecastItem;

        LocalDateTime dateTimeFrom;
        LocalDateTime dateTimeTo;
        String windDirection = "";
        String symbolMeaning = "";
        String symbolCode = "";
        float degreesCelsius = 0;
        float windSpeed = 0;
        float precipitationMin = 0;
        float precipitationMax = 0;
        float cloudiness = 0;
        Node tabular = list.item(0);
        NodeList timeList = tabular.getChildNodes();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        System.out.println(timeList.toString());
        for(int i = 0; i < timeList.getLength(); i++) {
            if (!(timeList.item(i).getNodeType() == Node.ELEMENT_NODE)) continue;
            Element time = (Element) timeList.item(i);

            dateTimeFrom = LocalDateTime.parse(time.getAttribute("from"), formatter);
            dateTimeTo = LocalDateTime.parse(time.getAttribute("to"), formatter);
            if (time.hasChildNodes()) {
                NodeList locationList = time.getChildNodes();
                for(int j = 0; j < locationList.getLength(); j++) {
                    if (!(timeList.item(j).getNodeType() == Node.ELEMENT_NODE)) continue;
                    windDirection = "";
                    symbolMeaning = "";
                    symbolCode = "";
                    degreesCelsius = 0;
                    windSpeed = 0;
                    precipitationMin = 0;
                    precipitationMax = 0;
                    cloudiness = 0;

                    for(int k = 0; k < locationList.getLength(); k++) {
                        if (!(timeList.item(k).getNodeType() == Node.ELEMENT_NODE)) continue;
                        Element locationChild = (Element) locationList.item(k);
                        Element symbol = getAttribute("symbol", locationChild);

                        if(symbol != null){
                            symbolMeaning = symbol.getAttribute("id");
                            symbolCode = symbol.getAttribute("code");
                            Element precipitation = getAttribute("precipitation", locationChild);
                            float precipitationAvg = Float.parseFloat(precipitation.getAttribute("value"));
                            String maxValue = precipitation.getAttribute("maxvalue");
                            String minValue = precipitation.getAttribute("minvalue");

                            if (precipitationAvg == 0.0f || (maxValue.equals("") && minValue.equals(""))) {
                                precipitationMax = precipitationAvg;
                                precipitationMin = precipitationAvg;
                            } else {
                                precipitationMax = Float.parseFloat(precipitation.getAttribute("maxvalue"));
                                precipitationMin = Float.parseFloat(precipitation.getAttribute("minvalue"));
                            }
                        } else {
                            Element windDir = getAttribute("windDirection", locationChild);
                            windDirection = windDir != null ? windDir.getAttribute("name") : "";
                            Element windSpd = getAttribute("windSpeed", locationChild);;
                            windSpeed = windSpd != null ? Float.parseFloat(windSpd.getAttribute("mps")) : 0f;
                            Element temperature = getAttribute("temperature", locationChild);
                            degreesCelsius = temperature != null ? Float.parseFloat(temperature.getAttribute("value")) : 0f;
                            Element clouds = getAttribute("cloudiness", locationChild);;
                            cloudiness = clouds != null ? Float.parseFloat(clouds.getAttribute("percent")) : 0f;
                        }
                    }
                    forecastItem = new ForecastItem(dateTimeFrom, dateTimeTo, windDirection, degreesCelsius, windSpeed, symbolCode, symbolMeaning, precipitationMin, precipitationMax, cloudiness);
                    forecast.add(forecastItem);
                }
            }
        }

        return mergeForecastItems(forecast);
    }

    private Element getAttribute(String name, Element element){
        return (Element) element.getElementsByTagName(name).item(0);
    }

    private List<ForecastItem> mergeForecastItems(List<ForecastItem> items){
        items.sort(Comparator.comparing(ForecastItem::getForecastTimeTo));
        List<ForecastItem> mergedList = new ArrayList<>();
        for(int i = 0; i < items.size() && i+1 < items.size(); i+=2){
            ForecastItem item1 = items.get(i);
            ForecastItem item2 = items.get(i+1);

            if (item1.getForecastTimeFrom().equals(item1.getForecastTimeTo())){
                mergedList.add(mergeForecastPair(item1, item2));
            } else {
                mergedList.add(mergeForecastPair(item2, item1));
            }
        }
        return mergedList;
    }
    private ForecastItem mergeForecastPair(ForecastItem item1, ForecastItem item2){
        ForecastItem merged = new ForecastItem();
        merged.setForecastTimeFrom(item1.getForecastTimeFrom());
        merged.setForecastTimeTo(item1.getForecastTimeTo());
        merged.setForecastTimeFrom(item1.getForecastTimeFrom());
        merged.setForecastTimeTo(item1.getForecastTimeTo());
        merged.setCloudiness(item1.getCloudiness());
        merged.setWindDirection(item1.getWindDirection());
        merged.setWindSpeed(item1.getWindSpeed());
        merged.setDegreesCelsius(item1.getDegreesCelsius());

        merged.setSymbolCode(item2.getSymbolCode());
        merged.setSymbolMeaning(item2.getSymbolMeaning());
        merged.setPrecipitationMin(item2.getPrecipitationMin());
        merged.setPrecipitationMax(item2.getPrecipitationMax());
        return merged;
    }
}
