package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.response.QosParamsResponse;
import org.springframework.stereotype.Service;

import java.net.Inet6Address;
import java.net.InetAddress;

@Service
public class QosTestObjectiveMapperImpl implements QosTestObjectiveMapper {

    @Override
    public QosParamsResponse qosTestObjectiveToQosParamsResponse(QosTestObjective qosTestObjective, InetAddress clientAddress) {
        return QosParamsResponse.builder()
                .qosTestUid(qosTestObjective.getUid())
                .concurrencyGroup(qosTestObjective.getConcurrencyGroup())
                .serverAddress(getServerAddress(qosTestObjective, clientAddress))
                .serverPort(qosTestObjective.getTestServer().getPortSsl())
                .port(qosTestObjective.getParam().getPort())
                .request(qosTestObjective.getParam().getRequest())
                .timeout(qosTestObjective.getParam().getTimeout())
                .url(qosTestObjective.getParam().getUrl())
                .outNumPackets(qosTestObjective.getParam().getOutNumPackets())
                .outPort(qosTestObjective.getParam().getOutPort())
                .downloadTimeout(qosTestObjective.getParam().getDownloadTimeout())
                .connTimeout(qosTestObjective.getParam().getConnTimeout())
                .record(qosTestObjective.getParam().getRecord())
                .host(qosTestObjective.getParam().getHost())
                .callDuration(qosTestObjective.getParam().getCallDuration())
                .inPort(qosTestObjective.getParam().getInPort())
                .resolver(qosTestObjective.getParam().getResolver())
                .range(qosTestObjective.getParam().getRange())
                .inNumPackets(qosTestObjective.getParam().getInNumPackets())
                .build();
    }

    private String getServerAddress(QosTestObjective x, InetAddress clientAddress) {
        if (clientAddress instanceof Inet6Address) {
            return String.valueOf(x.getTestServer().getWebAddressIpV6());
        } else {
            return String.valueOf(x.getTestServer().getWebAddressIpV4());
        }
    }
}
