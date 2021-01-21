package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.model.QoSTestTypeDesc;
import com.rtr.nettest.mapper.QoSTestTypeDescMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static com.rtr.nettest.TestConstants.DEFAULT_QOS_TEST_TYPE_DESC_NAME;
import static com.rtr.nettest.TestConstants.DEFAULT_TEST_TYPE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class QoSTestTypeDescMapperImplTest {
    private QoSTestTypeDescMapper qosTestTypeDescMapper;

    @Mock
    private QoSTestTypeDesc qosTestTypeDesc;

    @Before
    public void setUp() {
        qosTestTypeDescMapper = new QoSTestTypeDescMapperImpl();
    }

    @Test
    public void qosTestTypeDescToQoSTestTypeDescResponse_whenCommonData_expectQoSTestTypeDescResponse() {
        when(qosTestTypeDesc.getName()).thenReturn(DEFAULT_QOS_TEST_TYPE_DESC_NAME);
        when(qosTestTypeDesc.getTest()).thenReturn(DEFAULT_TEST_TYPE);

        var actualResponse = qosTestTypeDescMapper.qosTestTypeDescToQoSTestTypeDescResponse(qosTestTypeDesc);

        assertEquals(DEFAULT_QOS_TEST_TYPE_DESC_NAME, actualResponse.getName());
        assertEquals(DEFAULT_TEST_TYPE.toString(), actualResponse.getTestType());
    }
}