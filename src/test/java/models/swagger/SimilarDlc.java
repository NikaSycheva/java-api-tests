package models.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimilarDlc{

    @JsonProperty("isFree")
    private Boolean isFree;

    @JsonProperty("dlcNameFromAnotherGame")
    private String dlcNameFromAnotherGame;
}