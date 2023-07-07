package at.rtr.rmbt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qos_test_desc")
@Entity
public class QosTestDesc {
    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "desc_key")
    private String descKey;

    @Column(name = "value")
    private String value;

    @Column(name = "lang")
    private String lang;
}
