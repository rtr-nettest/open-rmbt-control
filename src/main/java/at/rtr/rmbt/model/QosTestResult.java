package at.rtr.rmbt.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "qos_test_result")
public class QosTestResult {
    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qos_test_result_seq")
    @SequenceGenerator(name = "qos_test_result_seq", sequenceName = "qos_test_result_uid_seq", allocationSize = 1)
    private Long uid;

    @Column(name = "test_uid")
    private Long testUid;

    @ManyToOne
    @JoinColumn(name = "qos_test_uid")
    private QosTestObjective qosTestObjective;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "failure_count")
    private Integer failureCount;

    @Column(name = "implausible")
    private boolean implausible;

    @Column(name = "deleted")
    private boolean deleted;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result", columnDefinition = "json")
    private String result;

    @Transient
    private String testDescription;
    @Transient
    private String testSummary;
    @Transient
    private Map<String, String> resultKeyMap = new HashMap<>();
}
