package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Geo location repository interface.
 */
@Repository
public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
    /**
     * Find all by test order by time asc.
     *
     * @param test the Test
     * @return the result
     */
    List<GeoLocation> findAllByTestOrderByTimeAsc(Test test);

    /**
     * Find max by test.
     *
     * @param test the Test
     * @return the result
     */
    @Query(value = "SELECT MAX(r.timeNs) from GeoLocation r where r.test= :test")
    Optional<Long> findMaxByTest(Test test);
}
