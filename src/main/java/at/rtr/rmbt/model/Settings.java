package at.rtr.rmbt.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "lang")
    private String lang;

    @Column(name = "value")
    private String value;
}
