package vn.edu.tdtu.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseVM<D> {
    private String message;
    private D data;
    private int code;
}