package com.firstclub.membership.dto;

import com.firstclub.membership.enums.TierType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class AuditLogResponse {
    private Long id;
    private String action;
    private TierType fromTier;
    private TierType toTier;
    private String remarks;
    private LocalDateTime createdAt;
}
