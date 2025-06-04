package com.example.redalert.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIClusteringResultItemDTO {
    private Long alertId;
    private Integer clusterLabel;
}
