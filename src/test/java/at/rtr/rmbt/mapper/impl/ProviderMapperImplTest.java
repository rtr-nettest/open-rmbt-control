package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.ProviderMapper;
import at.rtr.rmbt.model.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ProviderMapperImplTest {
    private ProviderMapper providerMapper;

    @Mock
    private Provider provider;

    @Before
    public void setUp() {
        providerMapper = new ProviderMapperImpl();
    }

    @Test
    public void providerToProviderResponse_whenCommonData_expectProviderResponse() {
        when(provider.getId()).thenReturn(TestConstants.DEFAULT_UID);
        when(provider.getName()).thenReturn(TestConstants.DEFAULT_PROVIDER);

        var response = providerMapper.providerToProviderResponse(provider);

        assertEquals(TestConstants.DEFAULT_UID, response.getId());
        assertEquals(TestConstants.DEFAULT_PROVIDER, response.getName());
    }
}