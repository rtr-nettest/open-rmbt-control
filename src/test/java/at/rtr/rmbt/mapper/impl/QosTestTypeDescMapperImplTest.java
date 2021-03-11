package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.QosTestTypeDescMapper;
import at.rtr.rmbt.model.QosTestTypeDesc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class QosTestTypeDescMapperImplTest {
    private QosTestTypeDescMapper qosTestTypeDescMapper;

    @Mock
    private QosTestTypeDesc qosTestTypeDesc;

    @Before
    public void setUp() {
        qosTestTypeDescMapper = new QosTestTypeDescMapperImpl();
    }

    @Test
    public void qosTestTypeDescToQosTestTypeDescResponse_whenCommonData_expectQoSTestTypeDescResponse() {
        when(qosTestTypeDesc.getName()).thenReturn(TestConstants.DEFAULT_QOS_TEST_TYPE_DESC_NAME);
        when(qosTestTypeDesc.getTest()).thenReturn(TestConstants.DEFAULT_TEST_TYPE);

        var actualResponse = qosTestTypeDescMapper.qosTestTypeDescToQosTestTypeDescResponse(qosTestTypeDesc);

        assertEquals(TestConstants.DEFAULT_QOS_TEST_TYPE_DESC_NAME, actualResponse.getName());
        assertEquals(TestConstants.DEFAULT_TEST_TYPE.toString(), actualResponse.getTestType());
    }
}