package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.model.QosParams;
import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.model.TestServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class QosTestObjectiveMapperImplTest {
    private QosTestObjectiveMapper qosTestObjectiveMapper;

    private InetAddress inetAddress;

    @Mock
    private QosTestObjective qosTestObjective;
    @Mock
    private TestServer testServer;

    @Before
    public void setUp() {
        qosTestObjectiveMapper = new QosTestObjectiveMapperImpl();
    }

    @Test
    public void qosTestObjectiveToQosParamsResponse_whenClientIpV4_expectQosParamsResponse() {
        inetAddress = mock(Inet4Address.class);
        when(qosTestObjective.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(qosTestObjective.getConcurrencyGroup()).thenReturn(TestConstants.DEFAULT_CONCURRENCY_GROUP);
        when(qosTestObjective.getTestServer()).thenReturn(testServer);
        when(testServer.getWebAddressIpV4()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4);
        when(testServer.getPortSsl()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL);
        when(qosTestObjective.getParam()).thenReturn(getQosParams());

        var response = qosTestObjectiveMapper.qosTestObjectiveToQosParamsResponse(qosTestObjective, inetAddress);

        assertEquals(TestConstants.DEFAULT_UID.toString(), response.getQosTestUid());
        assertEquals(TestConstants.DEFAULT_CONCURRENCY_GROUP.toString(), response.getConcurrencyGroup());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4, response.getServerAddress());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL.toString(), response.getServerPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_PORT, response.getPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_REQUEST, response.getRequest());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_TIMEOUT, response.getTimeout());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_URL, response.getUrl());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_OUT_NUM_PACKETS, response.getOutNumPackets());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_OUT_PORT, response.getOutPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_DOWNLOAD_TIMEOUT, response.getDownloadTimeout());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_CONN_TIMEOUT, response.getConnTimeout());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_RECORD, response.getRecord());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_HOST, response.getHost());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_CALL_DURATION, response.getCallDuration());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_IN_PORT, response.getInPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_RESOLVER, response.getResolver());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_RANGE, response.getRange());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_IN_NUM_PACKETS, response.getInNumPackets());
    }


    @Test
    public void qosTestObjectiveToQosParamsResponse_whenClientIpV6_expectQosParamsResponse() {
        inetAddress = mock(Inet6Address.class);
        when(qosTestObjective.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(qosTestObjective.getConcurrencyGroup()).thenReturn(TestConstants.DEFAULT_CONCURRENCY_GROUP);
        when(qosTestObjective.getTestServer()).thenReturn(testServer);
        when(testServer.getWebAddressIpV6()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6);
        when(testServer.getPortSsl()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL);
        when(qosTestObjective.getParam()).thenReturn(getQosParams());

        var response = qosTestObjectiveMapper.qosTestObjectiveToQosParamsResponse(qosTestObjective, inetAddress);

        assertEquals(TestConstants.DEFAULT_UID.toString(), response.getQosTestUid());
        assertEquals(TestConstants.DEFAULT_CONCURRENCY_GROUP.toString(), response.getConcurrencyGroup());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6, response.getServerAddress());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL.toString(), response.getServerPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_PORT, response.getPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_REQUEST, response.getRequest());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_TIMEOUT, response.getTimeout());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_URL, response.getUrl());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_OUT_NUM_PACKETS, response.getOutNumPackets());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_OUT_PORT, response.getOutPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_DOWNLOAD_TIMEOUT, response.getDownloadTimeout());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_CONN_TIMEOUT, response.getConnTimeout());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_RECORD, response.getRecord());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_HOST, response.getHost());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_CALL_DURATION, response.getCallDuration());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_IN_PORT, response.getInPort());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_RESOLVER, response.getResolver());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_RANGE, response.getRange());
        assertEquals(TestConstants.DEFAULT_QOS_PARAM_IN_NUM_PACKETS, response.getInNumPackets());
    }


    private QosParams getQosParams() {
        return QosParams.builder()
                .port(TestConstants.DEFAULT_QOS_PARAM_PORT)
                .request(TestConstants.DEFAULT_QOS_PARAM_REQUEST)
                .timeout(TestConstants.DEFAULT_QOS_PARAM_TIMEOUT)
                .url(TestConstants.DEFAULT_QOS_PARAM_URL)
                .outNumPackets(TestConstants.DEFAULT_QOS_PARAM_OUT_NUM_PACKETS)
                .outPort(TestConstants.DEFAULT_QOS_PARAM_OUT_PORT)
                .downloadTimeout(TestConstants.DEFAULT_QOS_PARAM_DOWNLOAD_TIMEOUT)
                .connTimeout(TestConstants.DEFAULT_QOS_PARAM_CONN_TIMEOUT)
                .record(TestConstants.DEFAULT_QOS_PARAM_RECORD)
                .host(TestConstants.DEFAULT_QOS_PARAM_HOST)
                .callDuration(TestConstants.DEFAULT_QOS_PARAM_CALL_DURATION)
                .inPort(TestConstants.DEFAULT_QOS_PARAM_IN_PORT)
                .resolver(TestConstants.DEFAULT_QOS_PARAM_RESOLVER)
                .range(TestConstants.DEFAULT_QOS_PARAM_RANGE)
                .inNumPackets(TestConstants.DEFAULT_QOS_PARAM_IN_NUM_PACKETS)
                .build();
    }
}
