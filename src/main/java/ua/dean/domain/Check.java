package ua.dean.domain;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import static javax.persistence.CascadeType.*;

@Data @MappedSuperclass
public abstract class Check {

    protected String description;
    protected  String name;
    protected CheckType type;

    @ManyToOne(fetch = FetchType.EAGER, cascade = PERSIST)
    protected Host host;

}
