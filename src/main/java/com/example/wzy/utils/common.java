package com.example.wzy.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class common {
    public static LocalDateTime getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        return LocalDateTime.parse(formattedDateTime, formatter);
    }

}
