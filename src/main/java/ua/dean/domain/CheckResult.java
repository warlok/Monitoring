package ua.dean.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data @AllArgsConstructor
public class CheckResult {

    private Date date;
    private String value;
    private CheckStatus status;

}
