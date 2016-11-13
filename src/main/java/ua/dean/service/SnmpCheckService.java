package ua.dean.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.dean.dao.CheckDao;
import ua.dean.dao.CheckResultDao;
import ua.dean.domain.*;
import ua.dean.tools.SnmpClient;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class SnmpCheckService extends AbstractCheckService {

    @Autowired
    private SnmpClient client;

    @Autowired
    private CheckDao checkDao;

    @Autowired
    private CheckResultDao checkResultDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(SnmpCheckService.class);

    @Override
    public void doCheck() throws IOException {

        List<SnmpCheck> checks = checkDao.findAll();

        checks.parallelStream().forEach(check -> {
//            if (check.getType().equals(CheckType.SNMP)) {
                SnmpCheck snmpCheck = (SnmpCheck) check;
                Host host = check.getHost();
                String snmpUrl = String.format("udp:%s/%d", host.getIpOrHostname(), snmpCheck.getPort());
                String result = null;
                try {
                    result = client.getAsString(snmpUrl, host.getCommunity(), snmpCheck.getOid());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CheckResult checkResult = new CheckResult(new Date(), result, CheckStatus.OK);
                checkResultDao.persist(checkResult);
                LOGGER.info(checkResult.toString());
            }
        );


    }
}
