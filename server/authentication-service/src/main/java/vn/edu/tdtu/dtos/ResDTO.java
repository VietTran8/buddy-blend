package vn.edu.tdtu.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDTO<D> {
    private int code;
    private String message;
    private D data;
}