package at.rtr.rmbt.mapper;

import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.model.QoeClassification;

public interface QoeClassificationMapper {

    QoeClassificationThresholds qoeClassificationToQoeClassificationThresholds(QoeClassification qoeClassification);
}
