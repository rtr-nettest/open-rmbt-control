package at.rtr.rmbt.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "bev_vgd")
public class AdministrativeBoundaries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gid")
    private Long gid;

    @Column(name = "kg_nr_int")
    private Long kgNrInt;

    @Column(name = "kg")
    private String locality;

    @Column(name = "pg")
    private String community;

    @Column(name = "pb")
    private String district;

    @Column(name = "bl")
    private String province;
}
