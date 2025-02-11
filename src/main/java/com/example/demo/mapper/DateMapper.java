package com.example.demo.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateMapper {

    // Convert Date to LocalDate
    public static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Convert LocalDate to Date
    public static Date toDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}

