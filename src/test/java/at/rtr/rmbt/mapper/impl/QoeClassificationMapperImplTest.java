package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.enums.QoeCategory;
import at.rtr.rmbt.enums.QoeCriteria;
import at.rtr.rmbt.mapper.QoeClassificationMapper;
import at.rtr.rmbt.model.QoeClassification;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QoeClassificationMapperImplTest {
    private final QoeClassificationMapper qoeClassificationMapper = new QoeClassificationMapperImpl();

    @Test
    void qoeClassificationToQoeClassificationThresholds_whenCommonData_expectSuccess() {
        QoeClassification qoeClassification = new QoeClassification(1L, QoeCategory.CLOUD, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        Map<QoeCriteria, Long[]> criteriaThresholdsMap = Map.of(
            QoeCriteria.PING, new Long[]{qoeClassification.getPing4(), qoeClassification.getPing3(), qoeClassification.getPing2()},
            QoeCriteria.DOWN, new Long[]{qoeClassification.getDownload4(), qoeClassification.getDownload3(), qoeClassification.getDownload2()},
            QoeCriteria.UP, new Long[]{qoeClassification.getUpload4(), qoeClassification.getUpload3(), qoeClassification.getUpload2()}
        );

        QoeClassificationThresholds result = qoeClassificationMapper.qoeClassificationToQoeClassificationThresholds(qoeClassification);

        assertEquals(qoeClassification.getCategory(), result.getQoeCategory());
        assertPing(qoeClassification, result);
        assertDownload(qoeClassification, result);
        assertUpload(qoeClassification, result);
    }

    private void assertPing(QoeClassification classification, QoeClassificationThresholds thresholds) {
        assertTrue(thresholds.getThresholds().containsKey(QoeCriteria.PING));
        Long[] expected = thresholds.getThresholds().get(QoeCriteria.PING);
        assertEquals(3, expected.length, "Incorrect number of thresholds");
        assertEquals(classification.getPing4(), expected[0]);
        assertEquals(classification.getPing3(), expected[1]);
        assertEquals(classification.getPing2(), expected[2]);
    }

    private void assertDownload(QoeClassification classification, QoeClassificationThresholds thresholds) {
        assertTrue(thresholds.getThresholds().containsKey(QoeCriteria.DOWN));
        Long[] expected = thresholds.getThresholds().get(QoeCriteria.DOWN);
        assertEquals(3, expected.length, "Incorrect number of thresholds");
        assertEquals(classification.getDownload4(), expected[0]);
        assertEquals(classification.getDownload3(), expected[1]);
        assertEquals(classification.getDownload2(), expected[2]);
    }

    private void assertUpload(QoeClassification classification, QoeClassificationThresholds thresholds) {
        assertTrue(thresholds.getThresholds().containsKey(QoeCriteria.UP));
        Long[] expected = thresholds.getThresholds().get(QoeCriteria.UP);
        assertEquals(3, expected.length, "Incorrect number of thresholds");
        assertEquals(classification.getUpload4(), expected[0]);
        assertEquals(classification.getUpload3(), expected[1]);
        assertEquals(classification.getUpload2(), expected[2]);
    }
}
