package vn.edu.tdtu.dto.gemini.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsageMetadata{
    public int promptTokenCount;
    public int candidatesTokenCount;
    public int totalTokenCount;
}