package at.rtr.rmbt.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "title_de")
    private String titleDe;

    @Column(name = "text_en")
    private String textEn;

    @Column(name = "text_de")
    private String textDe;

    private boolean active = true;

    private boolean force;

    @Column(name = "plattform")
    private String platform;

    private Long minSoftwareVersionCode;

    private Long maxSoftwareVersionCode;

    private OffsetDateTime time;

    private UUID uuid;

    @PrePersist
    protected void preInsert() {
        this.time = OffsetDateTime.now();
    }
}
