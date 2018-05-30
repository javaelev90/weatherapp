package com.javaelev.weather.weatherapp.handlers;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.javaelev.weather.weatherapp.R;
import com.javaelev.weather.weatherapp.model.CityInfo;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


// YR forecast url: "https://www.yr.no/place/Sweden/Norrbotten/Lule%C3%A5/forecast.xml"
//
//    !!!!!! ALL FIELDS ARE TAB-DELIMITED !!!!!!
//
//    The main 'geoname' table has the following fields :
//            ---------------------------------------------------
//    0 geonameid         : integer id of record in geonames database
//    1 name              : name of geographical point (utf8) varchar(200)
//    2 asciiname         : name of geographical point in plain ascii characters, varchar(200)
//    3 alternatenames    : alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
//    4 latitude          : latitude in decimal degrees (wgs84)
//    5 longitude         : longitude in decimal degrees (wgs84)
//    6 feature class     : see http://www.geonames.org/export/codes.html, char(1)
//    7 feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
//    8 country code      : ISO-3166 2-letter country code, 2 characters
//    9 cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
//    10 admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
//    11 admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80)
//    12 admin3 code       : code for third level administrative division, varchar(20)
//    13 admin4 code       : code for fourth level administrative division, varchar(20)
//    14 population        : bigint (8 byte int)
//    15 elevation         : in meters, integer
//    16 dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
//    17 timezone          : the iana timezone id (see file timeZone.txt) varchar(40)
//    18 modification date : date of last modification in yyyy-MM-dd format

public class FileLoader {

    public HashMap<String, String> makeCountryCodesHashMap(Resources resources){

        HashMap<String, String> codeToCountryMapping = new HashMap<>();
        InputStream inputStream = resources.openRawResource(R.raw.data_csv);
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            //First line is just a description i.e. Name,Code
            reader.readLine();

            //First actual (country, country code) line
            String line = reader.readLine();
            int index;
            while(line != null){
                index = line.lastIndexOf(",");
                //String[] countryAndCode = {line.substring(0, index), line.substring(index+1)};

//                System.out.format("Country: %-50s Code: %-2s%n",countryAndCode[0] , countryAndCode[1]);
                codeToCountryMapping.put(line.substring(0, index), line.substring(index+1));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Country to code mapping done.");
        return codeToCountryMapping;
    }

    public HashMap<String, CityInfo> makeCityToCityInfoMapping(Resources resources){

        HashMap<String, CityInfo> cityToCountryCodeMapping = new HashMap<>();
        InputStream inputStream = resources.openRawResource(R.raw.cities1000);
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            //First line is just a description i.e. Name,Code
            reader.readLine();

            //First actual (country, country code) line
            String line = reader.readLine();

            while(line != null){
                //line.replaceAll("[\t]+", "\t");
                String[] cityAndCountryCode = line.split("\t");


                cityToCountryCodeMapping.put(cityAndCountryCode[1], new CityInfo(cityAndCountryCode[1],cityAndCountryCode[10],cityAndCountryCode[8]));
//                System.out.format("Country: %-50s Code: %-10s%n",cityAndCountryCode[1].trim() , cityAndCountryCode[8].trim());

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Reading all cities done.");
        return cityToCountryCodeMapping;
    }

    public HashMap<String, String> makeCityAreaCodeToCountryAreaMapping(Resources resources){

        HashMap<String, String> countryAreaCodeToCountryAreaMapping = new HashMap<>();
        InputStream inputStream = resources.openRawResource(R.raw.admin1codesascii);
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            //First line is just a description i.e. Name,Code
            reader.readLine();

            //First actual (country, country code) line
            String line = reader.readLine();

            while(line != null){
                //line.replaceAll("[\t]+", "\t");
                String[] cityAndCountryCode = line.split("\t");

                countryAreaCodeToCountryAreaMapping.put(cityAndCountryCode[0],cityAndCountryCode[1]);
//                System.out.format("Country: %-50s Code: %-10s%n",cityAndCountryCode[1].trim() , cityAndCountryCode[8].trim());

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Reading all cities done.");
        return countryAreaCodeToCountryAreaMapping;
    }

}
