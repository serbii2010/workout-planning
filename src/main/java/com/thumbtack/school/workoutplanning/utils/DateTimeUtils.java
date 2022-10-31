package com.thumbtack.school.workoutplanning.utils;

import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public  static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public  static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
