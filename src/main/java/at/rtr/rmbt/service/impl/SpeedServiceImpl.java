package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.SpeedDirection;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.model.speed.Speed;
import at.rtr.rmbt.model.speed.SpeedItem;
import at.rtr.rmbt.repository.SpeedRepository;
import at.rtr.rmbt.request.SpeedDetailsRequest;
import at.rtr.rmbt.service.SpeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SpeedServiceImpl implements SpeedService {

    private final SpeedRepository speedRepository;

    @Override
    public void processSpeedRequests(Collection<SpeedDetailsRequest> speedDetailsRequests, Test test) {
        Speed newSpeed = new Speed();
        newSpeed.setOpenTestUuid(test.getOpenTestUuid());

        speedDetailsRequests.forEach(speedItemRequest -> {
            SpeedDirection direction = speedItemRequest.getDirection();
            Long thread = speedItemRequest.getThread();
            SpeedItem speedItem = new SpeedItem(speedItemRequest.getTime(), speedItemRequest.getBytes());
            newSpeed.addSpeedItem(speedItem, direction, thread);
        });

        speedRepository.save(newSpeed);
    }
}
