package com.br.luminous.entity;

import com.br.luminous.enums.ClimateType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "weather_tip")
public class WeatherTip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String tip;

    @Enumerated(EnumType.STRING)
    private ClimateType climate;

    @ManyToMany(mappedBy="weatherTips")
    private List<User> users;
}
