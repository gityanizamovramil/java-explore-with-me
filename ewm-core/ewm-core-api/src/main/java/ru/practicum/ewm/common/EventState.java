package ru.practicum.ewm.common;

import java.util.Arrays;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static boolean contains(EventState eventState) {
        return Arrays.asList(values()).contains(eventState);
    }
}
