package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.QoSTestTypeDescMapper;
import at.rtr.rmbt.model.QoSTestTypeDesc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

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
        when(qosTestTypeDesc.getName()).thenReturn(TestConstants.DEFAULT_QOS_TEST_TYPE_DESC_NAME);
        when(qosTestTypeDesc.getTest()).thenReturn(TestConstants.DEFAULT_TEST_TYPE);

        var actualResponse = qosTestTypeDescMapper.qosTestTypeDescToQoSTestTypeDescResponse(qosTestTypeDesc);

        assertEquals(TestConstants.DEFAULT_QOS_TEST_TYPE_DESC_NAME, actualResponse.getName());
        assertEquals(TestConstants.DEFAULT_TEST_TYPE.toString(), actualResponse.getTestType());
    }
}