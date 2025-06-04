package com.example.redalert.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIClusteringRequestDTO {
    private List<AIClusteringAlertItemDTO> alertsToCluster;
}
