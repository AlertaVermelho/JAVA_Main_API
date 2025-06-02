package com.example.redalert.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIClassificationRequestDTO {
    private Long alertId;
    private String text;
}