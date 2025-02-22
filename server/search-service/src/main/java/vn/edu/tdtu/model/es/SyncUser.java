package vn.edu.tdtu.model.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setting(settingPath = "essettings/elastic-analyzer.json")
@Document(indexName = "users")
public class SyncUser {
    private String id;
    private String email;
    @Field(type = FieldType.Text, analyzer = "autocomplete_analyzer", searchAnalyzer = "search_analyzer")
    private String fullName;
}