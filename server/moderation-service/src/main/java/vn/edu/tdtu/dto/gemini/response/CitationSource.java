package vn.edu.tdtu.dto.gemini.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitationSource {
    public int startIndex;
    public int endIndex;
    public String uri;
}