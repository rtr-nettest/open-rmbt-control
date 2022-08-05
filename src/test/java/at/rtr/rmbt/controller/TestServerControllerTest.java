package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.response.ServerTypeDetailsResponse;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.service.TestServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static at.rtr.rmbt.constant.URIConstants.BY_ID;
import static at.rtr.rmbt.constant.URIConstants.TEST_SERVER;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestServerControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TestServerService testServerService;

    @Before
    public void setUp() {
        TestServerController testServerController = new TestServerController(testServerService);
        mockMvc = MockMvcBuilders.standaloneSetup(testServerController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void getAllTestServers_whenExistOneTestServer_expectTestServerList() throws Exception {
        var testServerResponse = getTestServerResponse();
        var testServerList = List.of(testServerResponse);
        when(testServerService.getAllTestServer()).thenReturn(testServerList);

        mockMvc.perform(MockMvcRequestBuilders.get(TEST_SERVER))
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(TestConstants.DEFAULT_UID))
                .andExpect(jsonPath("$[0].name").value(TestConstants.DEFAULT_TEST_SERVER_NAME))
                .andExpect(jsonPath("$[0].webAddress").value(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS))
                .andExpect(jsonPath("$[0].port").value(TestConstants.DEFAULT_TEST_SERVER_PORT))
                .andExpect(jsonPath("$[0].portSsl").value(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL))
                .andExpect(jsonPath("$[0].city").value(TestConstants.DEFAULT_TEST_SERVER_CITY))
                .andExpect(jsonPath("$[0].country").value(TestConstants.DEFAULT_TEST_SERVER_COUNTRY))
                .andExpect(jsonPath("$[0].latitude").value(TestConstants.DEFAULT_LATITUDE))
                .andExpect(jsonPath("$[0].longitude").value(TestConstants.DEFAULT_LONGITUDE))
                .andExpect(jsonPath("$[0].location.type").value("Point"))
                .andExpect(jsonPath("$[0].location.coordinates[0]").value(TestConstants.DEFAULT_LATITUDE_900913))
                .andExpect(jsonPath("$[0].location.coordinates[1]").value(TestConstants.DEFAULT_LONGITUDE_900913))
                .andExpect(jsonPath("$[0].webAddressIpV4").value(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4))
                .andExpect(jsonPath("$[0].webAddressIpV6").value(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6))
                .andExpect(jsonPath("$[0].serverTypeDetails", hasSize(1)))
                .andExpect(jsonPath("$[0].serverTypeDetails[0].serverType").value(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE.toString()))
                .andExpect(jsonPath("$[0].serverTypeDetails[0].port").value(TestConstants.DEFAULT_TEST_SERVER_PORT))
                .andExpect(jsonPath("$[0].serverTypeDetails[0].portSsl").value(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL))
                .andExpect(jsonPath("$[0].serverTypeDetails[0].encrypted").value(false))
                .andExpect(jsonPath("$[0].priority").value(TestConstants.DEFAULT_TEST_SERVER_PRIORITY))
                .andExpect(jsonPath("$[0].weight").value(TestConstants.DEFAULT_TEST_SERVER_WEIGHT))
                .andExpect(jsonPath("$[0].active").value(TestConstants.DEFAULT_FLAG_TRUE))
                .andExpect(jsonPath("$[0].secretKey").value(TestConstants.DEFAULT_TEST_SERVER_KEY))
                .andExpect(jsonPath("$[0].selectable").value(TestConstants.DEFAULT_FLAG_TRUE))
                .andExpect(jsonPath("$[0].node").value(TestConstants.DEFAULT_TEST_SERVER_NODE))
                .andExpect(jsonPath("$[0].encrypted").value(false))
                .andExpect(jsonPath("$[0].lastMeasurementSuccess").value(TestConstants.DEFAULT_FLAG_TRUE))
                .andExpect(jsonPath("$[0].timeOfLastMeasurement").value(TestConstants.DEFAULT_LAST_TEST_TIMESTAMP.getTime()))
                .andExpect(jsonPath("$[0].lastSuccessfulMeasurement").value(TestConstants.DEFAULT_LAST_SUCCESSFUL_TEST_TIMESTAMP.getTime()))
                .andExpect(status().isOk());

    }

    @Test
    public void deleteTestServerById_whenExistOneTestServer_expectDeleted() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(TEST_SERVER + BY_ID, TestConstants.DEFAULT_UID))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(testServerService).deleteTestServer(TestConstants.DEFAULT_UID);
    }

    private TestServerResponse getTestServerResponse() {
        var serverTypeDetailsResponse = ServerTypeDetailsResponse.builder()
                .serverType(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE)
                .portSsl(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL)
                .port(TestConstants.DEFAULT_TEST_SERVER_PORT)
                .encrypted(false)
                .build();

        return TestServerResponse.builder()
                .id(TestConstants.DEFAULT_UID)
                .name(TestConstants.DEFAULT_TEST_SERVER_NAME)
                .webAddress(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS)
                .port(TestConstants.DEFAULT_TEST_SERVER_PORT)
                .portSsl(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL)
                .city(TestConstants.DEFAULT_TEST_SERVER_CITY)
                .country(TestConstants.DEFAULT_TEST_SERVER_COUNTRY)
                .latitude(TestConstants.DEFAULT_LATITUDE)
                .longitude(TestConstants.DEFAULT_LONGITUDE)
                .location(TestConstants.DEFAULT_LOCATION)
                .webAddressIpV4(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4)
                .webAddressIpV6(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6)
                .serverTypeDetails(Set.of(serverTypeDetailsResponse))
                .priority(TestConstants.DEFAULT_TEST_SERVER_PRIORITY)
                .weight(TestConstants.DEFAULT_TEST_SERVER_WEIGHT)
                .active(TestConstants.DEFAULT_FLAG_TRUE)
                .secretKey(TestConstants.DEFAULT_TEST_SERVER_KEY)
                .selectable(TestConstants.DEFAULT_FLAG_TRUE)
                .node(TestConstants.DEFAULT_TEST_SERVER_NODE)
                .encrypted(false)
                .lastMeasurementSuccess(TestConstants.DEFAULT_FLAG_TRUE)
                .timeOfLastMeasurement(TestConstants.DEFAULT_LAST_TEST_TIMESTAMP)
                .lastSuccessfulMeasurement(TestConstants.DEFAULT_LAST_SUCCESSFUL_TEST_TIMESTAMP)
                .build();
    }
}
