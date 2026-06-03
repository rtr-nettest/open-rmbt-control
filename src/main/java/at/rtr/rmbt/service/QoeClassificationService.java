package at.rtr.rmbt.service;

import at.rtr.rmbt.dto.QoeClassificationThresholds;

import java.util.List;

/**
 * Qoe classification service interface.
 */
public interface QoeClassificationService {

    List<QoeClassificationThresholds> getQoeClassificationThreshold();
}
