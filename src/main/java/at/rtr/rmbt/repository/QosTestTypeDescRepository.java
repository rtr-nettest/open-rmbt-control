package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.QosTestTypeDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QosTestTypeDescRepository extends JpaRepository<QosTestTypeDesc, Long> {

    @Query(value = "SELECT nnttd.uid AS uid, " +
            "UPPER(cast(test as text)) as test, nntd.\"value\" as test_desc, " +
            "nntd.lang, " +
            "nntd2.\"value\" AS test_name, " +
            "nntd2.lang AS name_lang "
            + " FROM qos_test_type_desc AS nnttd "
            + " JOIN qos_test_desc nntd ON nnttd.test_desc = nntd.desc_key "
            + " JOIN qos_test_desc nntd2 ON nnttd.test_name = nntd2.desc_key WHERE nntd.lang = ("
            + " CASE WHEN EXISTS(SELECT 1 FROM qos_test_desc WHERE desc_key = nntd.desc_key AND lang = :language) "
            + "     THEN :language ELSE 'en' END) AND nntd2.lang = ("
            + " CASE WHEN EXISTS(SELECT 1 FROM qos_test_desc WHERE desc_key = nntd2.desc_key AND lang = :language) "
            + "     THEN :language ELSE 'en' END)", nativeQuery = true)
    List<QosTestTypeDesc> getAllByLang(String language);
}
