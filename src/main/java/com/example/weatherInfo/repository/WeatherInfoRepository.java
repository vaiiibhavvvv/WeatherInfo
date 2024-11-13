package com.example.weatherInfo.repository;

import com.example.weatherInfo.entity.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeatherInfoRepository extends JpaRepository<WeatherInfo,Long> {
    Optional<WeatherInfo> findByPinCodeAndDate(Integer pincode,LocalDate date);


}
