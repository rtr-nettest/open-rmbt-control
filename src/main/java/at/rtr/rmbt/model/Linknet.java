package at.rtr.rmbt.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "linknet")
public class Linknet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gid")
    private Long gid;

    @Column(name = "link_id")
    private Long linkId;

    @Column(name = "name1")
    private String name1;

    @Column(name = "name2")
    private String name2;
}
