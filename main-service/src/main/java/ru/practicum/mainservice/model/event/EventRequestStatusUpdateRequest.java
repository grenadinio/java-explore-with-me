package ru.practicum.mainservice.model.event;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private String status;
}
