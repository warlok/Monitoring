package ua.dean.domain;

import lombok.Data;

@Data
public class SnmpCheck extends Check {

    private String oid;
    private int port;

}
