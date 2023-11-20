package com.example.hellofreshreminder;

public interface AlarmScheduler {
    public void schedule(AlarmItem item);
    public void cancel(AlarmItem item);
}
