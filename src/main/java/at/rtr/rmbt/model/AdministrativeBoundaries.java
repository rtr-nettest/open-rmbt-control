package at.rtr.rmbt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "bev_vgd")
public class AdministrativeBoundaries implements Serializable {

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
