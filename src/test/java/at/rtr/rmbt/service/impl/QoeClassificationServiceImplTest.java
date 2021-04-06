package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.enums.QoeCategory;
import at.rtr.rmbt.enums.QoeCriteria;
import at.rtr.rmbt.mapper.QoeClassificationMapper;
import at.rtr.rmbt.model.QoeClassification;
import at.rtr.rmbt.repository.QoeClassificationRepository;
import at.rtr.rmbt.service.QoeClassificationService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QoeClassificationServiceImplTest {
    private final QoeClassificationRepository qoeClassificationRepository = mock(QoeClassificationRepository.class);
    private final QoeClassificationMapper qoeClassificationMapper = mock(QoeClassificationMapper.class);
    private final QoeClassificationService qoeClassificationService = new QoeClassificationServiceImpl(
        qoeClassificationRepository,
        qoeClassificationMapper
    );

    @Test
    void getQoeClassificationThreshold_whenCommonData_shouldReturnList() {
        QoeClassification qoeClassification1 = new QoeClassification(1L, QoeCategory.CLOUD, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        QoeClassification qoeClassification2 = new QoeClassification(2L, QoeCategory.GAMING, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        List<QoeClassification> qoeClassificationList = List.of(qoeClassification1, qoeClassification2);
        QoeClassificationThresholds expected1 = mapQoeClassification(qoeClassification1);
        QoeClassificationThresholds expected2 = mapQoeClassification(qoeClassification2);

        QoeClassificationThresholds[] expected = new QoeClassificationThresholds[]{expected1, expected2};

        when(qoeClassificationRepository.findAll()).thenReturn(qoeClassificationList);
        when(qoeClassificationMapper.qoeClassificationToQoeClassificationThresholds(qoeClassification1)).thenReturn(expected1);
        when(qoeClassificationMapper.qoeClassificationToQoeClassificationThresholds(qoeClassification2)).thenReturn(expected2);

        List<QoeClassificationThresholds> resultList = qoeClassificationService.getQoeClassificationThreshold();

        assertTrue(Arrays.deepEquals(expected, resultList.toArray()));
    }

    private QoeClassificationThresholds mapQoeClassification(QoeClassification qoeClassification) {
        Map<QoeCriteria, Long[]> criteriaThresholdsMap = Map.of(
            QoeCriteria.PING, new Long[]{qoeClassification.getPing4(), qoeClassification.getPing3(), qoeClassification.getPing2()},
            QoeCriteria.DOWN, new Long[]{qoeClassification.getDownload4(), qoeClassification.getDownload3(), qoeClassification.getDownload2()},
            QoeCriteria.UP, new Long[]{qoeClassification.getUpload4(), qoeClassification.getUpload3(), qoeClassification.getUpload2()}
        );
        return QoeClassificationThresholds.builder()
            .qoeCategory(qoeClassification.getCategory())
            .thresholds(criteriaThresholdsMap)
            .build();
    }
}
