package ua.dean.domain;

import lombok.Data;

@Data
public abstract class Check {

    protected CheckType type;
    protected Host host;

}
