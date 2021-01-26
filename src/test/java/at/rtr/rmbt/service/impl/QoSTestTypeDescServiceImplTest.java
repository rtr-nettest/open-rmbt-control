package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.QoSTestTypeDescMapper;
import at.rtr.rmbt.model.QoSTestTypeDesc;
import at.rtr.rmbt.repository.QoSTestTypeDescRepository;
import at.rtr.rmbt.response.QoSTestTypeDescResponse;
import at.rtr.rmbt.service.QoSTestTypeDescService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class QoSTestTypeDescServiceImplTest {
    private QoSTestTypeDescService qosTestTypeDescService;

    @MockBean
    private QoSTestTypeDescRepository qoSTestTypeDescRepository;
    @MockBean
    private QoSTestTypeDescMapper qoSTestTypeDescMapper;

    @Mock
    private QoSTestTypeDesc qoSTestTypeDesc;
    @Mock
    private QoSTestTypeDescResponse qoSTestTypeDescResponse;

    @Before
    public void setUp() {
        qosTestTypeDescService = new QoSTestTypeDescServiceImpl(qoSTestTypeDescRepository, qoSTestTypeDescMapper);
    }

    @Test
    public void getAll_whenCommonData_expectQoSTestTypeDescResponseList() {
        when(qoSTestTypeDescRepository.getAllByLang(TestConstants.DEFAULT_LANGUAGE)).thenReturn(List.of(qoSTestTypeDesc));
        when(qoSTestTypeDescMapper.qosTestTypeDescToQoSTestTypeDescResponse(qoSTestTypeDesc)).thenReturn(qoSTestTypeDescResponse);

        var response = qosTestTypeDescService.getAll(TestConstants.DEFAULT_LANGUAGE);

        assertEquals(List.of(qoSTestTypeDescResponse), response);
    }
}