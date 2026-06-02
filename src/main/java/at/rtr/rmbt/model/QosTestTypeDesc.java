package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.model.type.TestTypeUserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@Table(name = "qos_test_type_desc")
public class QosTestTypeDesc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "test_desc")
    private String description;

    @Column(name = "test_name")
    private String name;

    @Column(name = "test")
    @Type(TestTypeUserType.class)
    private TestType test;
}
