package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.constant.ErrorMessage;
import at.rtr.rmbt.exception.TestNotFoundException;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;

    @Override
    public Test save(Test test) {
        return testRepository.save(test);
    }

    @Override
    public String getRmbtSetProviderFromAs(Long testUid) {
        return testRepository.getRmbtSetProviderFromAs(testUid);
    }

    @Override
    public Integer getRmbtNextTestSlot(Long testUid) {
        return testRepository.getRmbtNextTestSlot(testUid);
    }

    @Override
    public List<String> getDeviceHistory(Long clientId) {
        var resultList = testRepository.getDistinctModelByClientId(clientId);
        resultList.replaceAll(t -> Objects.isNull(t) ? Constants.UNKNOWN_DEVICE : t);
        return resultList;
    }

    @Override
    public List<String> getGroupNameByClientId(Long clientId) {
        return testRepository.getDistinctGroupNameByClientId(clientId);
    }

    @Override
    public TestResponse getTestByUUID(UUID testUUID) {
        return testRepository.findByUuid(testUUID)
                .map(testMapper::testToTestResponse)
                .orElseThrow(() -> new TestNotFoundException(String.format(ErrorMessage.TEST_NOT_FOUND, testUUID)));
    }
}
