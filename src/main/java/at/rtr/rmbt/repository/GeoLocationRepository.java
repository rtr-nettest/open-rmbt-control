package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
}
