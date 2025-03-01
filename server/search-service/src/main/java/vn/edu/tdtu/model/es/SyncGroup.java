package vn.edu.tdtu.model.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import vn.edu.tdtu.message.SyncGroupMsg;

@Document(indexName = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Setting(settingPath = "essettings/elastic-analyzer.json")
public class SyncGroup {
    private String id;
    @Field(type = FieldType.Text, analyzer = "autocomplete_analyzer", searchAnalyzer = "search_analyzer")
    private String name;

    public SyncGroup(SyncGroupMsg msg) {
        this.id = msg.getId();
        this.name = msg.getName();
    }
}
