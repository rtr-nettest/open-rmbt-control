package at.rtr.rmbt.repository.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.repository.TestHistoryRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestHistoryRepositoryImplTest {
    private TestHistoryRepository testHistoryRepository;

    @MockBean
    private JdbcTemplate jdbcTemplate;
    @Mock
    private RtrClient client;
    @Captor
    private ArgumentCaptor<String> queryArgumentCaptor;

    @Before
    public void setUp() {
        testHistoryRepository = new TestHistoryRepositoryImpl(jdbcTemplate);
    }

    @Ignore("Temporarily until incorrect assert is fixed")
    @Test
    public void getTestHistoryByDevicesAndNetworksAndClient_whenClientIsNotSynced_expectTestHistories() {
        when(client.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        testHistoryRepository.getTestHistoryByDevicesAndNetworksAndClient(
                TestConstants.DEFAULT_RESULT_LIMIT,
                TestConstants.DEFAULT_RESULT_OFFSET,
                List.of(TestConstants.DEFAULT_DEVICE),
                List.of(TestConstants.DEFAULT_NETWORK_NAME),
                client,
                false,
                false
        );

        verify(jdbcTemplate).query(queryArgumentCaptor.capture(), any(ArgumentPreparedStatementSetter.class), any(BeanPropertyRowMapper.class));
        assertEquals(TestConstants.DEFAULT_TEST_HISTORY_FINAL_QUERY, queryArgumentCaptor.getValue());
    }

    @Ignore("Temporarily until incorrect assert is fixed")
    @Test
    public void getTestHistoryByDevicesAndNetworksAndClient_whenClientIsSynced_expectTestHistories() {
        when(client.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(client.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_ID);
        testHistoryRepository.getTestHistoryByDevicesAndNetworksAndClient(
                TestConstants.DEFAULT_RESULT_LIMIT,
                TestConstants.DEFAULT_RESULT_OFFSET,
                List.of(TestConstants.DEFAULT_DEVICE),
                List.of(TestConstants.DEFAULT_NETWORK_NAME),
                client,
                false,
                false
        );

        verify(jdbcTemplate).query(queryArgumentCaptor.capture(), any(ArgumentPreparedStatementSetter.class), any(BeanPropertyRowMapper.class));
        assertEquals(TestConstants.DEFAULT_TEST_HISTORY_FINAL_QUERY_CLIENT_SYNCED, queryArgumentCaptor.getValue());
    }
}