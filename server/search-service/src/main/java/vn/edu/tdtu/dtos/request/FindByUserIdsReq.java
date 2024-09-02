package vn.edu.tdtu.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class FindByUserIdsReq {
    private List<String> userIds;
}
