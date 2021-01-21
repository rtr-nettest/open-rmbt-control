package com.rtr.nettest.model;

import com.rtr.nettest.enums.TestType;
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
public class QoSTestTypeDesc {

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
//    @Formula("UPPER(test::text)")
    private TestType test;
}
