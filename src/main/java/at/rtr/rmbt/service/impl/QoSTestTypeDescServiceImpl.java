package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.QoSTestTypeDescMapper;
import at.rtr.rmbt.repository.QoSTestTypeDescRepository;
import at.rtr.rmbt.response.QoSTestTypeDescResponse;
import at.rtr.rmbt.service.QoSTestTypeDescService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QoSTestTypeDescServiceImpl implements QoSTestTypeDescService {

    private final QoSTestTypeDescRepository qosTestTypeDescRepository;
    private final QoSTestTypeDescMapper qoSTestTypeDescMapper;

    @Override
    public List<QoSTestTypeDescResponse> getAll(String language) {
        return qosTestTypeDescRepository.getAllByLang(language).stream()
                .map(qoSTestTypeDescMapper::qosTestTypeDescToQoSTestTypeDescResponse)
                .collect(Collectors.toList());
    }
}
