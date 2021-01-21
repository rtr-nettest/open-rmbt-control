package com.rtr.nettest.service.impl;

import com.rtr.nettest.mapper.QoSTestTypeDescMapper;
import com.rtr.nettest.model.QoSTestTypeDesc;
import com.rtr.nettest.repository.QoSTestTypeDescRepository;
import com.rtr.nettest.response.QoSTestTypeDescResponse;
import com.rtr.nettest.service.QoSTestTypeDescService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.rtr.nettest.TestConstants.DEFAULT_LANGUAGE;
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
        when(qoSTestTypeDescRepository.getAllByLang(DEFAULT_LANGUAGE)).thenReturn(List.of(qoSTestTypeDesc));
        when(qoSTestTypeDescMapper.qosTestTypeDescToQoSTestTypeDescResponse(qoSTestTypeDesc)).thenReturn(qoSTestTypeDescResponse);

        var response = qosTestTypeDescService.getAll(DEFAULT_LANGUAGE);

        assertEquals(List.of(qoSTestTypeDescResponse), response);
    }
}