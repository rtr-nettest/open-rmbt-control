package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.TestType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "qos_test_objective")
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class)
})
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
    @Type(type = "json")
    private QosParams param;

    @Column(name = "results", columnDefinition = "json")
    @Type(type = "json")
    private List<QosResults> results;

    @Column(name = "concurrency_group")
    private Integer concurrencyGroup;

    @ManyToOne
    @JoinColumn(name = "test_server")
    private TestServer testServer;
}
