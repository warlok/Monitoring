package ua.dean.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data @Entity
public class Host {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.IncrementGenerator")
    private Long id;
    /**
     * Network name or IP address of device
     */
    private String ipOrHostname;
    /**
     * Name of device (some descriptor)
     */
    private String alias;
    /**
     * Some information about device
     */
    private String description;
    /**
     * SNMP community for snmp checks
     */
    private String community;
    /**
     * E.g. router/switch/modem/computer/etc.
     */
    private String type;
    /**
     * E.g. cisco/hp/juniper/dlink/etc
     */
    private String vendor;
    /**
     * E.g. des-3200/ex-4200/mx-5/etc
     */
    private String model;

}
