package vn.edu.tdtu.dto.gemini.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Root{
    public ArrayList<Candidate> candidates;
    public UsageMetadata usageMetadata;
    public String modelVersion;

    public String getContent() {
        return candidates.get(0).getContent().getParts().get(0).getText();
    }
}