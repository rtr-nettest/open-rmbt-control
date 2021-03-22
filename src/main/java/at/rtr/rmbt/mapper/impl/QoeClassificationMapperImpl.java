package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.enums.QoeCriteria;
import at.rtr.rmbt.mapper.QoeClassificationMapper;
import at.rtr.rmbt.model.QoeClassification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class QoeClassificationMapperImpl implements QoeClassificationMapper {

    @Override
    public QoeClassificationThresholds qoeClassificationToQoeClassificationThresholds(QoeClassification qoeClassification) {
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
