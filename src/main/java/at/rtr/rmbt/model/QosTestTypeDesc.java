package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.TestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "qos_test_type_desc")
public class QosTestTypeDesc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "value")
    private String description;

    @Column(name = "value_name")
    private String name;

    @Column(name = "test")
    @Enumerated(EnumType.STRING)
    private TestType test;
}
