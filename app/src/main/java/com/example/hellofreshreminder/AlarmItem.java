package com.example.hellofreshreminder;

import java.time.LocalDateTime;

public class AlarmItem {
    LocalDateTime time;
    String message;

    public AlarmItem() {}

    public AlarmItem(LocalDateTime time, String message) {
        this.time = time;
        this.message = message;
    }
}
