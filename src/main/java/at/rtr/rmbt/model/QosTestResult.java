package at.rtr.rmbt.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "qos_test_result")
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonStringType.class),
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class QosTestResult {
    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Type(type = "jsonb")
    @Column(name = "result", columnDefinition = "json")
    private String result;

    @Transient
    private String testDescription;
    @Transient
    private String testSummary;
    @Transient
    private Map<String, String> resultKeyMap = new HashMap<>();
}
