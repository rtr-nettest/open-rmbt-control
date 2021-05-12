package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
    List<GeoLocation> findAllByTestOrderByTimeAsc(Test test);

    @Query(value = "SELECT MAX(r.timeNs) from GeoLocation r where r.test= :test")
    Optional<Long> findMaxByTest(Test test);

    @Modifying
    @Transactional
    @Query(value = "UPDATE geo_location SET location = ST_TRANSFORM(ST_SetSRID(ST_Point(:longitude, :latitude), 4326), 900913) WHERE uid = :geoLocationUid", nativeQuery = true)
    void updateLocation(Long geoLocationUid, Double longitude, Double latitude);
}
