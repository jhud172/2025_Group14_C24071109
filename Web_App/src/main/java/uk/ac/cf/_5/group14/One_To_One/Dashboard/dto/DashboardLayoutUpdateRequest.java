package uk.ac.cf._5.group14.One_To_One.Dashboard.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardLayoutUpdateRequest {
    private List<LayoutItemDto> items = new ArrayList<>();
}
