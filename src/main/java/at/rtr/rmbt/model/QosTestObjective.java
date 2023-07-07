package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.TestType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "qos_test_objective")
public class QosTestObjective {

    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "test")
    private TestType testType;

    @Column(name = "test_class")
    private Integer testClassId;

    @Column(name = "param", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private QosParams param;

    @Column(name = "results", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String results;

    @Column(name = "concurrency_group")
    private Integer concurrencyGroup;

    @Column(name = "test_desc")
    private String testDescription;

    @Column(name = "test_summary")
    private String testSummary;

    @ManyToOne
    @JoinColumn(name = "test_server")
    private TestServer testServer;
}
