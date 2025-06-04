package com.example.redalert.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIClusteringAlertItemDTO {
    private Long alertId;
    private Double latitude;
    private Double longitude;
    private String severityIA;
    private String typeIA;
}
