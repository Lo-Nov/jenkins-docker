package io.eclectics.cicd.others;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class UniversalResponse {
    private String status;
    private String message;
    @NonNull
    private boolean isExist;
    private Object data;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd/MM/yyyy hh:mm:ss")
    private Date timestamp;
    private Object metadata;

    public UniversalResponse() {
        this.timestamp = new Date();
    }

    public UniversalResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }

    public UniversalResponse(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = new Date();
    }


}
