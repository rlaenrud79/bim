package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.WeatherXY;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class WeatherLocation {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private String nationCode;
    private String regionCode;
    private String regionFirst;
    private String regionSecond;
    private String regionThird;

    @Embedded
    private WeatherXY weatherXY;

    @Column(name = "longitude_h")
    private String longitudeH;
    @Column(name = "longitude_m")
    private String longitudeM;
    @Column(name = "longitude_s")
    private String longitudeS;
    @Column(name = "latitude_h")
    private String latitudeH;
    @Column(name = "latitude_m")
    private String latitudeM;
    @Column(name = "latitude_s")
    private String latitudeS;

    public String getName(String step)
    {
        if(step.equals("1")) return this.regionFirst;
        if(step.equals("2")) return this.regionSecond;
        if(step.equals("3")) return this.regionThird;
        return "";
    }
}
