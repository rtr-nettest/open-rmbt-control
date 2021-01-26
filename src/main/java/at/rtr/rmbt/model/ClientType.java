package at.rtr.rmbt.model;

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
@Table(name = "client_type")
public class ClientType {
    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_type_seq")
    @SequenceGenerator(name = "client_type_seq", sequenceName = "client_type_uid_seq", allocationSize = 1)
    private Long uid;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private at.rtr.rmbt.model.enums.ClientType clientType;
}
