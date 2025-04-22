package vn.edu.tdtu.dto.gemini.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Candidate {
    public Content content;
    public String finishReason;
    public CitationMetadata citationMetadata;
    public double avgLogprobs;
}











