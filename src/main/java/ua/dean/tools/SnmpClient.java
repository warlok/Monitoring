package ua.dean.tools;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SnmpClient {

    private Snmp snmp = null;

    public SnmpClient() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    public static void main(String[] args) throws IOException {
        /**
         * Port 161 is used for Read and Other operations
         * Port 162 is used for the trap generation
         */
        SnmpClient client = new SnmpClient();
        /**
         * OID - .1.3.6.1.2.1.1.1.0 => SysDec
         * OID - .1.3.6.1.2.1.1.5.0 => SysName
         * => MIB explorer will be usefull here, as discussed in previous article
         */
        String sysDescr = client.getAsString("udp:127.0.0.1/161","public",".1.3.6.1.2.1.1.1.0",".1.3.6.1.2.1.1.5.0",".1.3.6.1.2.1.1.1.0");
        System.out.println(sysDescr);
        client.close();
    }

    private void close() throws IOException {
        snmp.close();
    }

    /**
     * Method which takes a single OID and returns the response from the agent as a String.
     *
     * @param oidStrs
     * @return
     * @throws IOException
     */
    public String getAsString(String addr, String community, String ... oidStrs) throws IOException {
        OID[] oids = new OID[oidStrs.length];
        for (int i=0; i<oidStrs.length; i++) {
            OID oid = new OID(oidStrs[i]);
            oids[i] = oid;
        }
        ResponseEvent event = get(addr, community, oids);
        return event.getResponse().get(0).getVariable().toString();
    }

//    public int getAsInteger(OID oid) throws IOException {
//        ResponseEvent event = get(new OID[]{oid});
//        return event.getResponse().get(0).getVariable().toInt();
//    }
//
//    public long getAsLong(OID oid) throws IOException {
//        ResponseEvent event = get(new OID[]{oid});
//        return event.getResponse().get(0).getVariable().toLong();
//    }

    /**
     * This method is capable of handling multiple OIDs
     *
     * @param oids
     * @return
     * @throws IOException
     */
    public ResponseEvent get(String addr, String community, OID oids[]) throws IOException {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);
        ResponseEvent event = snmp.send(pdu, getTarget(addr,community), null);
        if (event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    /**
     * This method returns a Target, which contains information about
     * where the data should be fetched and how.
     *
     * @return
     */
    private Target getTarget(String address, String community) {
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(1000);
        target.setVersion(SnmpConstants.version2c);

        return target;
    }

}

