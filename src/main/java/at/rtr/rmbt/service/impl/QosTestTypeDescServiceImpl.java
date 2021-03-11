package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.QosTestTypeDescMapper;
import at.rtr.rmbt.repository.QosTestTypeDescRepository;
import at.rtr.rmbt.response.QosTestTypeDescResponse;
import at.rtr.rmbt.service.QosTestTypeDescService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QosTestTypeDescServiceImpl implements QosTestTypeDescService {

    private final QosTestTypeDescRepository qosTestTypeDescRepository;
    private final QosTestTypeDescMapper qosTestTypeDescMapper;

    @Override
    public List<QosTestTypeDescResponse> getAll(String language) {
        return qosTestTypeDescRepository.getAllByLang(language).stream()
                .map(qosTestTypeDescMapper::qosTestTypeDescToQosTestTypeDescResponse)
                .collect(Collectors.toList());
    }
}
