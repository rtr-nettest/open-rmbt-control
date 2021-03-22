package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.mapper.QoeClassificationMapper;
import at.rtr.rmbt.repository.QoeClassificationRepository;
import at.rtr.rmbt.service.QoeClassificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QoeClassificationServiceImpl implements QoeClassificationService {

    private final QoeClassificationRepository qoeClassificationRepository;
    private final QoeClassificationMapper qoeClassificationMapper;

    @Override
    public List<QoeClassificationThresholds> getQoeClassificationThreshold() {
        return qoeClassificationRepository.findAll().stream()
                .map(qoeClassificationMapper::qoeClassificationToQoeClassificationThresholds)
                .collect(Collectors.toList());
    }
}
