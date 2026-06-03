package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.response.QosParamsResponse;
import at.rtr.rmbt.utils.testscript.TestScriptInterpreter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Qos test objective mapper impl class.
 */
@Service
public class QosTestObjectiveMapperImpl implements QosTestObjectiveMapper {

    /**
     * Qos test objective to qos params response.
     *
     * @param qosTestObjective the Qos test objective
     * @param clientAddress the Client address
     * @return the result
     */
    @Override
    public QosParamsResponse qosTestObjectiveToQosParamsResponse(QosTestObjective qosTestObjective, InetAddress clientAddress) {
        QosParamsResponse qosParamsResponse = QosParamsResponse.builder()
                .qosTestUid(String.valueOf(qosTestObjective.getUid()))
                .concurrencyGroup(String.valueOf(qosTestObjective.getConcurrencyGroup()))
                .serverAddress(getServerAddress(qosTestObjective, clientAddress))
                .serverPort(String.valueOf(qosTestObjective.getTestServer().getPortSsl()))
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
        return interpret(qosParamsResponse);
    }

    /**
     * Interpret.
     *
     * @param qosParamsResponse the Qos params response
     * @return the result
     */
    private QosParamsResponse interpret(QosParamsResponse qosParamsResponse) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> qosTestObjectiveMap = mapper.convertValue(qosParamsResponse, Map.class);
        return mapper.convertValue(qosTestObjectiveMap.entrySet()
                .stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(l -> Pair.of(l.getKey(), String.valueOf(TestScriptInterpreter.interprete(l.getValue(), null))))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue)), QosParamsResponse.class);
    }

    private String getServerAddress(QosTestObjective x, InetAddress clientAddress) {
        if (clientAddress instanceof Inet6Address) {
            return String.valueOf(x.getTestServer().getWebAddressIpV6());
        } else {
            return String.valueOf(x.getTestServer().getWebAddressIpV4());
        }
    }
}
