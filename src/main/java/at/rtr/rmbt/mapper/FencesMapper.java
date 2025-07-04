package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;


public interface FencesMapper {

    Fences fencesRequestToFences(Fences fr, Test test);

    Fences  fencesRequestToFences(FencesRequest fencesRequest, Test test);
}
