package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.ProviderMapper;
import at.rtr.rmbt.model.Provider;
import at.rtr.rmbt.repository.ProviderRepository;
import at.rtr.rmbt.response.ProviderResponse;
import at.rtr.rmbt.service.ProviderService;
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
public class ProviderServiceImplTest {
    private ProviderService providerService;

    @MockBean
    private ProviderRepository providerRepository;
    @MockBean
    private ProviderMapper providerMapper;

    @Mock
    private Provider provider;
    @Mock
    private ProviderResponse providerResponse;

    @Before
    public void setUp() {
        providerService = new ProviderServiceImpl(providerRepository, providerMapper);
    }

    @Test
    public void getAllProviders_whenExistOneProvider_expectListOfProviderResponse() {
        when(providerRepository.findAll()).thenReturn(List.of(provider));
        when(providerMapper.providerToProviderResponse(provider)).thenReturn(providerResponse);

        var response = providerService.getAllProviders();

        assertEquals(List.of(providerResponse), response);
    }
}