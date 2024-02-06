package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.WeatherLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherLocationRepository extends JpaRepository<WeatherLocation, Long> {
}
