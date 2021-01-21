package com.rtr.nettest.service.impl;

import com.rtr.nettest.mapper.QoSTestTypeDescMapper;
import com.rtr.nettest.repository.QoSTestTypeDescRepository;
import com.rtr.nettest.response.QoSTestTypeDescResponse;
import com.rtr.nettest.service.QoSTestTypeDescService;
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
