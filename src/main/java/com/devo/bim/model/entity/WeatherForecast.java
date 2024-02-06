package com.devo.bim.model.entity;

import com.devo.bim.model.enumulator.WeatherForecastType;
import com.devo.bim.model.vo.WeatherForecastVO;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class WeatherForecast {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private long projectId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json", name = "forecastItems")
    private List<?> forecastItems;

    private LocalDateTime baseDateTime;

    @Enumerated(STRING)
    private WeatherForecastType requestType;
    private LocalDateTime requestDate;

    public void setForecastAtAPIResponse(WeatherForecastVO weatherForecastVO) {
        this.projectId = weatherForecastVO.getProjectId();
        this.forecastItems = weatherForecastVO.getForecastItems();
        this.baseDateTime = weatherForecastVO.getBaseDateTime();
        this.requestType = weatherForecastVO.getRequestType();
        this.requestDate = LocalDateTime.now();
    }
}
