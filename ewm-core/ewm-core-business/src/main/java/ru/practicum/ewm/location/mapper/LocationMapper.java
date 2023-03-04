package ru.practicum.ewm.location.mapper;

import ru.practicum.ewm.common.LocationDto;
import ru.practicum.ewm.location.model.Location;

public final class LocationMapper {
    private LocationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
