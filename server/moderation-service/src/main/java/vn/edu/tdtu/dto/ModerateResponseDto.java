package vn.edu.tdtu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModerateResponseDto {
    private boolean accept;
    private String rejectReason;
}