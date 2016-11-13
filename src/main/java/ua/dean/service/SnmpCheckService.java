package ua.dean.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.dean.domain.*;
import ua.dean.tools.SnmpClient;

import java.io.IOException;
import java.util.Date;

@Service
public class SnmpCheckService extends AbstractCheckService {

    @Autowired
    private SnmpClient client;

    @Override
    public CheckResult doCheck(Check check) throws IOException {
        CheckResult checkResult = null;
        if (check.getType().equals(CheckType.SNMP)) {
            SnmpCheck snmpCheck = (SnmpCheck) check;
            Host host = check.getHost();
            String snmpUrl = String.format("udp:%s/%d",host.getIpOrHostname(),snmpCheck.getPort());
            String result = client.getAsString(snmpUrl,host.getCommunity(),snmpCheck.getOid());
            checkResult = new CheckResult(new Date(),result,CheckStatus.OK);
        }
        return checkResult;
    }
}
