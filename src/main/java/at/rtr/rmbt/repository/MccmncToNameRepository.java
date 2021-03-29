package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.MccMncName;
import at.rtr.rmbt.model.RadioCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface MccmncToNameRepository extends JpaRepository<MccMncName, Long> {

    @Query(value = "SELECT m.name from MccMncName m WHERE m.mccmnc in (:mccMnc)")
    List<String> getCountryByMvvMnc(Set<String> mccMnc);

}
