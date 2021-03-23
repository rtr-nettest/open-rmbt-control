package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.QosTestObjectiveRepository;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosParamsResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import at.rtr.rmbt.utils.HeaderExtrudeUtil;
import com.google.common.net.InetAddresses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QosMeasurementServiceImpl implements QosMeasurementService {

    private final QosTestObjectiveRepository qosTestObjectiveRepository;
    private final QosTestObjectiveMapper qosTestObjectiveMapper;
    private final ApplicationProperties applicationProperties;

    private static final int STATIC_CLASS_NAME = 1;

    @Override
    public MeasurementQosResponse getQosParameters(HttpServletRequest httpServletRequest, Map<String, String> headers) {
        InetAddress clientAddress = InetAddresses.forString(HeaderExtrudeUtil.getIpFromNgNixHeader(httpServletRequest, headers));
        String clientIpString = InetAddresses.toAddrString(clientAddress);

        Map<TestType, List<QosParamsResponse>> objectives = new HashMap<>();
        qosTestObjectiveRepository.getByTestClassIdIn(List.of(STATIC_CLASS_NAME)).forEach(qosTestObjective -> {
            List<QosParamsResponse> paramsList;

            if (objectives.containsKey(qosTestObjective.getTestType())) {
                paramsList = objectives.get(qosTestObjective.getTestType());
            } else {
                paramsList = new ArrayList<>();
                objectives.put(qosTestObjective.getTestType(), paramsList);
            }

            QosParamsResponse params = qosTestObjectiveMapper.qosTestObjectiveToQosParamsResponse(qosTestObjective, clientAddress);

            paramsList.add(params);
        });

        return MeasurementQosResponse.builder()
                .objectives(objectives)
                .testDuration(applicationProperties.getDuration())
                .testNumThreads(applicationProperties.getThreads())
                .testNumPings(applicationProperties.getPings())
                .clientRemoteIp(clientIpString)
                .error(Collections.emptyList())
                .build();
    }
}
