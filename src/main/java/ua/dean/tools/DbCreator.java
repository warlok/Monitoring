package ua.dean.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.dean.dao.CheckDao;
import ua.dean.domain.CheckType;
import ua.dean.domain.Host;
import ua.dean.domain.SnmpCheck;

import javax.annotation.PostConstruct;

@Component
public class DbCreator {

    @Autowired
    private CheckDao checkDao;

    @PostConstruct
    public void init() {
        Host host = new Host();
        host.setAlias("home");
        host.setCommunity("public");
        host.setDescription("Localhost");
        host.setIpOrHostname("127.0.0.1");

        SnmpCheck check = new SnmpCheck();
        check.setType(CheckType.SNMP);
        check.setHost(host);
        check.setOid(".1.3.6.1.2.1.1.1.0");
        check.setName("SysDec");

        checkDao.persist(check);
    }

}
