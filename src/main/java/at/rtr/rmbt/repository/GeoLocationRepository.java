package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
    List<GeoLocation> findAllByTestOrderByTimeAsc(Test test);

    @Query(value = "SELECT MAX(r.timeNs) from GeoLocation r where r.test= :test")
    Optional<Long> findMaxByTest(Test test);
}
