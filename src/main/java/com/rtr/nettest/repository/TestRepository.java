package com.rtr.nettest.repository;

import com.rtr.nettest.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {

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
}
