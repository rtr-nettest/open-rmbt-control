package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.QoeCategory;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "qoe_classification")
public class QoeClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "category")
    private QoeCategory category;

    @Column(name = "dl_4")
    private Long download4;

    @Column(name = "dl_3")
    private Long download3;

    @Column(name = "dl_2")
    private Long download2;

    @Column(name = "ul_4")
    private Long upload4;

    @Column(name = "ul_3")
    private Long upload3;

    @Column(name = "ul_2")
    private Long upload2;

    @Column(name = "ping_4")
    private Long ping4;

    @Column(name = "ping_3")
    private Long ping3;

    @Column(name = "ping_2")
    private Long ping2;
}
