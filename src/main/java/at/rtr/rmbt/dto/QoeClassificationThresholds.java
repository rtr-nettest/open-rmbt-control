package at.rtr.rmbt.dto;

import at.rtr.rmbt.enums.QoeCategory;
import at.rtr.rmbt.enums.QoeCriteria;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class QoeClassificationThresholds {

    private final QoeCategory qoeCategory;

    private final Map<QoeCriteria, Long[]> thresholds;
}
