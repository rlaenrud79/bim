package com.devo.bim.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Calendar {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String date;
    private String weekDay;
    private int year;
    private int month;
    private int day;
}