package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.RadioSignalRepository;
import at.rtr.rmbt.request.RadioSignalRequest;
import at.rtr.rmbt.service.RadioSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RadioSignalServiceImpl implements RadioSignalService {

    private final RadioSignalMapper radioSignalMapper;
    private final RadioSignalRepository radioSignalRepository;

    @Override
    public void saveRadioSignalRequests(Collection<RadioSignalRequest> signals, Test test) {
        List<RadioSignal> radioSignals = signals.stream()
                .map(rsr -> radioSignalMapper.radioSignalRequestToRadioSignal(rsr, test))
                .collect(Collectors.toList());

        radioSignalRepository.saveAll(radioSignals);
    }
}
