package vn.edu.tdtu.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResDTO<D> implements Serializable {
    private String message;
    private D data;
    private int code;
}
