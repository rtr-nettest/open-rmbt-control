package com.rtr.nettest.repository;

import com.rtr.nettest.model.QoSTestTypeDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QoSTestTypeDescRepository extends JpaRepository<QoSTestTypeDesc, Long> {

    @Query(value = "SELECT nnttd.uid AS uid, " +
            "UPPER(cast(test as text)) as test, nntd.\"value\", " +
            "nntd.lang, " +
            "nntd2.\"value\" AS value_name, " +
            "nntd2.lang AS name_lang "
            + " FROM qos_test_type_desc AS nnttd "
            + " JOIN qos_test_desc nntd ON nnttd.test_desc = nntd.desc_key "
            + " JOIN qos_test_desc nntd2 ON nnttd.test_name = nntd2.desc_key WHERE nntd.lang = ("
            + " CASE WHEN EXISTS(SELECT 1 FROM qos_test_desc WHERE desc_key = nntd.desc_key AND lang = :language) "
            + "     THEN :language ELSE 'en' END) AND nntd2.lang = ("
            + " CASE WHEN EXISTS(SELECT 1 FROM qos_test_desc WHERE desc_key = nntd2.desc_key AND lang = :language) "
            + "     THEN :language ELSE 'en' END)", nativeQuery = true)
    List<QoSTestTypeDesc> getAllByLang(String language);
}
