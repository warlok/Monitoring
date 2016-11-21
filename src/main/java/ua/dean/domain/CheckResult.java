package ua.dean.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class CheckResult {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.IncrementGenerator")
    private Long id;
    private Date date;
    private String value;
    private CheckStatus status;

    public CheckResult(Date date, String value, CheckStatus status) {
        super();
        this.date=date;
        this.value=value;
        this.status=status;
    }

}
