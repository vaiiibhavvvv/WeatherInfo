package com.example.weatherInfo.service;

import com.example.weatherInfo.dto.GeoCordApiResponse;
import com.example.weatherInfo.dto.WeatherApiResponse;
import com.example.weatherInfo.entity.PincodeLocation;
import com.example.weatherInfo.entity.WeatherInfo;
import com.example.weatherInfo.repository.PincodeLocationRepository;
import com.example.weatherInfo.repository.WeatherInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class WeatherService {
    
@Autowired
private WeatherInfoRepository weatherInfoRepository;

@Autowired
private PincodeLocationRepository pincodeLocationRepository;

private String OPEN_WEATHER_API_KEY = "df70fe50ab3cfd187fd7e7e5fdb50f6e";

    public PincodeLocation getPincodeLocation(Integer pincode) throws Exception {
        System.out.println("----Inside pincode location funciton----");
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + pincode + ",in&appid=" + OPEN_WEATHER_API_KEY;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GeoCordApiResponse> response = restTemplate.getForEntity(url,GeoCordApiResponse.class);
        double latitude = 0, longitude = 0;
        if(response.getStatusCode().is2xxSuccessful()){
            latitude = response.getBody().getLat();
            longitude = response.getBody().getLon();
        } else
            throw new Exception(response.getStatusCode().toString());
        return new PincodeLocation(pincode,latitude,longitude);
    }

    public WeatherApiResponse getWeatherApiResponse(double latitude,double longitude,LocalDate date)
        throws Exception{
        long unixTimeStamp = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        String url = "" + latitude + "&lon=" + longitude + "&appid=" + OPEN_WEATHER_API_KEY + "&dt=" + unixTimeStamp;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url,WeatherApiResponse.class);
        if (response.getStatusCode().isError()){
            throw new Exception("Error: " + response.getStatusCode().value());
        }
        return response.getBody();

    }

    public WeatherInfo getWeatherInfo(Integer pincode, LocalDate date) throws Exception {

        Optional<WeatherInfo> optionalWeatherInfo = this.weatherInfoRepository.findByPinCodeAndDate(pincode,date);
        if(optionalWeatherInfo.isPresent()) return optionalWeatherInfo.get();

        double latitude;
        double longitude;

        Optional<PincodeLocation> optionalPincodeLocation = null;
        try{
            optionalPincodeLocation = this.pincodeLocationRepository.findById(pincode);
        } catch (DataAccessException ex){
            ex.printStackTrace();
        }
        if(optionalPincodeLocation.isPresent()){
            latitude = optionalPincodeLocation.get().getLatitude();
            longitude = optionalPincodeLocation.get().getLongitude();
        }
        else{
            PincodeLocation pincodeLocation = getPincodeLocation(pincode);
            latitude = pincodeLocation.getLatitude();
            longitude = pincodeLocation.getLongitude();
            this.pincodeLocationRepository.save(pincodeLocation);
        }
        WeatherApiResponse weatherApiResponse = getWeatherApiResponse(latitude,longitude,date);

        WeatherInfo weatherInfo = new WeatherInfo();
        if(weatherApiResponse != null){
            weatherInfo.setPincode(pincode);
            weatherInfo.setDate(date);
            weatherInfo.setTemperature(weatherApiResponse.getMain().getTemp());
            weatherInfo.setHumidity(weatherApiResponse.getMain().getHumidity());
            weatherInfo.setPressure(weatherApiResponse.getMain().getPressure());
            weatherInfo.setWindSpeed(weatherApiResponse.getWind().getSpeed());
            weatherInfo.setDescription(weatherApiResponse.getWeather().get(0).getDescription());
            weatherInfo.setPlace(weatherApiResponse.getName());
            this.weatherInfoRepository.save(weatherInfo);
        }
        System.out.println(weatherInfo.toString());
        return weatherInfo;
    }
}
