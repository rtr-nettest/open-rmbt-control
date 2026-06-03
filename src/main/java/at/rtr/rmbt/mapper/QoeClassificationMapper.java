package at.rtr.rmbt.mapper;

import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.model.QoeClassification;

/**
 * Qoe classification mapper interface.
 */
public interface QoeClassificationMapper {

    /**
     * Qoe classification to qoe classification thresholds.
     *
     * @param qoeClassification the Qoe classification
     * @return the result
     */
    QoeClassificationThresholds qoeClassificationToQoeClassificationThresholds(QoeClassification qoeClassification);
}
