package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
    List<GeoLocation> findAllByTestOrderByTimeAsc(Test test);
}
