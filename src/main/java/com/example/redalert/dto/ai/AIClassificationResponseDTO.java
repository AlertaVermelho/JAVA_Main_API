package com.example.redalert.dto.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIClassificationResponseDTO {
    private Long alertId;
    private String classifiedSeverity;
    private String classifiedType;
}