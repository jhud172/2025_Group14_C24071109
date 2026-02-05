package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardLayoutUpdateRequest {
    private List<LayoutItemDto> items = new ArrayList<>();
}
