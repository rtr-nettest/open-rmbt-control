package at.rtr.rmbt.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
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

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "force")
    private boolean force;

    @Column(name = "plattform")
    private String platform;

    @Column(name = "min_software_version_code")
    private Long minSoftwareVersionCode;

    @Column(name = "max_software_version_code")
    private Long maxSoftwareVersionCode;

    @CreationTimestamp
    @Column(name = "time")
    private OffsetDateTime time;

    @Column(name = "uuid")
    private UUID uuid;

    @Builder.Default
    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startsAt = ZonedDateTime.now();

    @Column(name = "end_time")
    private ZonedDateTime endsAt;
}
