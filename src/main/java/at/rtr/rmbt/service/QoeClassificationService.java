package at.rtr.rmbt.service;

import at.rtr.rmbt.dto.QoeClassificationThresholds;

import java.util.List;

public interface QoeClassificationService {

    List<QoeClassificationThresholds> getQoeClassificationThreshold();
}
