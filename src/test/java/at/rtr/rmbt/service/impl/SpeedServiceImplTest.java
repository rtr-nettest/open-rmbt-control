package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.enums.SpeedDirection;
import at.rtr.rmbt.model.speed.Speed;
import at.rtr.rmbt.model.speed.SpeedItem;
import at.rtr.rmbt.repository.SpeedRepository;
import at.rtr.rmbt.request.SpeedDetailsRequest;
import at.rtr.rmbt.service.SpeedService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SpeedServiceImplTest {
    private SpeedService speedService;

    @MockBean
    private SpeedRepository speedRepository;

    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private SpeedDetailsRequest speedDetailsRequestFirst;
    @Mock
    private SpeedDetailsRequest speedDetailsRequestSecond;
    @Mock
    private SpeedDetailsRequest speedDetailsRequestThird;

    @Before
    public void setUp() {
        speedService = new SpeedServiceImpl(speedRepository);
    }

    @Test
    public void processSpeedRequests_whenCommonRequest_expectSpeedSaved() {
        var requests = List.of(speedDetailsRequestFirst, speedDetailsRequestSecond, speedDetailsRequestThird);
        var expectedSpeed = getSpeed();
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(speedDetailsRequestFirst.getDirection()).thenReturn(TestConstants.DEFAULT_SPEED_DIRECTION_FIRST);
        when(speedDetailsRequestFirst.getThread()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_THREAD_FIRST);
        when(speedDetailsRequestFirst.getBytes()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_BYTES_FIRST);
        when(speedDetailsRequestFirst.getTime()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_TIME_FIRST);
        when(speedDetailsRequestSecond.getDirection()).thenReturn(TestConstants.DEFAULT_SPEED_DIRECTION_SECOND);
        when(speedDetailsRequestSecond.getThread()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_THREAD_SECOND);
        when(speedDetailsRequestSecond.getBytes()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_BYTES_SECOND);
        when(speedDetailsRequestSecond.getTime()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_TIME_SECOND);
        when(speedDetailsRequestThird.getDirection()).thenReturn(TestConstants.DEFAULT_SPEED_DIRECTION_THIRD);
        when(speedDetailsRequestThird.getThread()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_THREAD_THIRD);
        when(speedDetailsRequestThird.getBytes()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_BYTES_THIRD);
        when(speedDetailsRequestThird.getTime()).thenReturn(TestConstants.DEFAULT_SPEED_ITEM_TIME_THIRD);
        speedService.processSpeedRequests(requests, test);

        verify(speedRepository).save(expectedSpeed);
    }

    private Speed getSpeed() {
        SpeedItem uploadSpeedItemFirst = SpeedItem.builder()
                .bytes(TestConstants.DEFAULT_SPEED_ITEM_BYTES_FIRST)
                .time(TestConstants.DEFAULT_SPEED_ITEM_TIME_FIRST)
                .build();
        SpeedItem uploadSpeedItemSecond = SpeedItem.builder()
                .bytes(TestConstants.DEFAULT_SPEED_ITEM_BYTES_SECOND)
                .time(TestConstants.DEFAULT_SPEED_ITEM_TIME_SECOND)
                .build();
        SpeedItem downloadSpeedItemFirst = SpeedItem.builder()
                .bytes(TestConstants.DEFAULT_SPEED_ITEM_BYTES_THIRD)
                .time(TestConstants.DEFAULT_SPEED_ITEM_TIME_THIRD)
                .build();
        List<SpeedItem> uploadSpeedItems = List.of(uploadSpeedItemFirst, uploadSpeedItemSecond);
        List<SpeedItem> downloadSpeedItems = List.of(downloadSpeedItemFirst);

        Map<Long, List<SpeedItem>> uploadSpeedItemMap = new HashMap<>();
        uploadSpeedItemMap.put(TestConstants.DEFAULT_SPEED_ITEM_THREAD_FIRST, uploadSpeedItems);

        Map<Long, List<SpeedItem>> downloadSpeedItemMap = new HashMap<>();
        downloadSpeedItemMap.put(TestConstants.DEFAULT_SPEED_ITEM_THREAD_THIRD, downloadSpeedItems);

        Map<SpeedDirection, Map<Long, List<SpeedItem>>> items = new HashMap<>();
        items.put(SpeedDirection.UPLOAD, uploadSpeedItemMap);
        items.put(SpeedDirection.DOWNLOAD, downloadSpeedItemMap);

        Speed speed = new Speed();
        speed.setOpenTestUuid(TestConstants.DEFAULT_TEST_UUID);
        speed.setItems(items);
        return speed;
    }
}