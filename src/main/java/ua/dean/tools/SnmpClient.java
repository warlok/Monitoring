package ua.dean.tools;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
// http://techdive.in/snmp/snmp4j-snmp-set-example
public class SnmpClient {

    private Snmp snmp = null;
    private String address = null;

    public SnmpClient(String add) {
        address = add;
    }

    public static void main(String[] args) throws IOException {
        /**
         * Port 161 is used for Read and Other operations
         * Port 162 is used for the trap generation
         */
        SnmpClient client = new SnmpClient("udp:127.0.0.1/161");
        client.start();
        /**
         * OID - .1.3.6.1.2.1.1.1.0 => SysDec
         * OID - .1.3.6.1.2.1.1.5.0 => SysName
         * => MIB explorer will be usefull here, as discussed in previous article
         */
        String sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"));
        System.out.println(sysDescr);
        client.close();
    }
    /**
     * Start the Snmp session. If you forget the listen() method you will not
     * get any answers because the communication is asynchronous
     * and the listen() method listens for answers.
     *
     * @throws IOException
     */
    private void start() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        // Do not forget this line!
        transport.listen();
    }

    private void close() throws IOException {
        snmp.close();
    }

    /**
     * Method which takes a single OID and returns the response from the agent as a String.
     *
     * @param oid
     * @return
     * @throws IOException
     */
    public String getAsString(OID oid) throws IOException {
        ResponseEvent event = get(new OID[]{oid});
        return event.getResponse().get(0).getVariable().toString();
    }

    public int getAsInteger(OID oid) throws IOException {
        ResponseEvent event = get(new OID[]{oid});
        return event.getResponse().get(0).getVariable().toInt();
    }

    public long getAsLong(OID oid) throws IOException {
        ResponseEvent event = get(new OID[]{oid});
        return event.getResponse().get(0).getVariable().toLong();
    }

    /**
     * This method is capable of handling multiple OIDs
     *
     * @param oids
     * @return
     * @throws IOException
     */
    public ResponseEvent get(OID oids[]) throws IOException {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);
        ResponseEvent event = snmp.send(pdu, getTarget(), null);
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
    private Target getTarget() {
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1000);
        target.setVersion(SnmpConstants.version2c);

        return target;
    }

}

