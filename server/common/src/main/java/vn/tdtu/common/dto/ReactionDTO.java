package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReactionDTO implements Serializable {

    @Schema(description = "Unique identifier of the reaction")
    private String id;

    @Schema(description = "Type of the reaction (e.g., like, love, etc.)")
    private EReactionType type;

    @Schema(description = "Date and time when the reaction was created")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;

    @Schema(description = "User who made the reaction")
    private UserDTO user;

    @Schema(description = "Count of reactions of this type")
    private Integer count;

    @Schema(description = "Indicates whether the reaction belongs to the current user")
    private Boolean isMine;

    public ReactionDTO(EReactionType type, Integer count) {
        this.type = type;
        this.count = count;
    }
}
