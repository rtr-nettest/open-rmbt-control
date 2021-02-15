package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TestRepository extends PagingAndSortingRepository<Test, Long> {
    @Procedure("rmbt_set_provider_from_as")
    String getRmbtSetProviderFromAs(Long testUid);

    @Procedure("rmbt_get_next_test_slot")
    Integer getRmbtNextTestSlot(Long testUid);

    @Query(value = "SELECT DISTINCT group_name" +
        " FROM test t" +
        " JOIN network_type nt ON t.network_type=nt.uid " +
        "WHERE t.deleted = false" +
        " AND t.status = 'FINISHED' " +
        " AND t.client_id = :clientId" +
        " AND group_name IS NOT NULL " +
        "ORDER BY group_name;", nativeQuery = true)
    List<String> getDistinctGroupNameByClientId(Long clientId);


    @Query(value = "SELECT DISTINCT COALESCE(adm.fullname, t.model) model"
        + " FROM test t"
        + " LEFT JOIN device_map adm ON adm.codename=t.model " +
        "WHERE t.client_id = :clientId" +
        " AND t.deleted = false" +
        " AND t.implausible = false" +
        " AND t.status = 'FINISHED' " +
        "ORDER BY model ASC", nativeQuery = true)
    List<String> getDistinctModelByClientId(Long clientId);

    Page<Test> findAll(Pageable pageable);
}
