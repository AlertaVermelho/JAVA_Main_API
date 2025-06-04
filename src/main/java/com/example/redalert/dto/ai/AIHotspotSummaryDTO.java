package com.example.redalert.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIHotspotSummaryDTO {
    private Integer clusterLabel;
    private Double centroidLat;
    private Double centroidLon;
    private Integer pointCount;
    private String dominantSeverity;
    private String dominantType;
    private List<Long> alertIdsInCluster;
    private Double estimatedRadiusKm;
    private String publicSummary;
    private String lastActivityTimestamp;
}
