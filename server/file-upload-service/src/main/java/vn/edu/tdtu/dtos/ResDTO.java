package vn.edu.tdtu.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResDTO<D> {
    private String message;
    private D data;
    private int code;
}