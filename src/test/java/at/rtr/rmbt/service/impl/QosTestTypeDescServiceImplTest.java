package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.QosTestTypeDescMapper;
import at.rtr.rmbt.model.QosTestTypeDesc;
import at.rtr.rmbt.repository.QosTestTypeDescRepository;
import at.rtr.rmbt.response.QosTestTypeDescResponse;
import at.rtr.rmbt.service.QosTestTypeDescService;
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
public class QosTestTypeDescServiceImplTest {
    private QosTestTypeDescService qosTestTypeDescService;

    @MockBean
    private QosTestTypeDescRepository qosTestTypeDescRepository;
    @MockBean
    private QosTestTypeDescMapper qosTestTypeDescMapper;

    @Mock
    private QosTestTypeDesc qosTestTypeDesc;
    @Mock
    private QosTestTypeDescResponse qosTestTypeDescResponse;

    @Before
    public void setUp() {
        qosTestTypeDescService = new QosTestTypeDescServiceImpl(qosTestTypeDescRepository, qosTestTypeDescMapper);
    }

    @Test
    public void getAll_whenCommonData_expectQosTestTypeDescResponseList() {
        when(qosTestTypeDescRepository.getAllByLang(TestConstants.DEFAULT_LANGUAGE)).thenReturn(List.of(qosTestTypeDesc));
        when(qosTestTypeDescMapper.qosTestTypeDescToQosTestTypeDescResponse(qosTestTypeDesc)).thenReturn(qosTestTypeDescResponse);

        var response = qosTestTypeDescService.getAll(TestConstants.DEFAULT_LANGUAGE);

        assertEquals(List.of(qosTestTypeDescResponse), response);
    }
}