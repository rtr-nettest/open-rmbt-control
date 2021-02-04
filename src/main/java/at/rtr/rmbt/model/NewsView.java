package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.NewsStatus;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Immutable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "news_view")
public class NewsView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "title_de")
    private String titleDe;

    @Column(name = "text_en")
    private String textEn;

    @Column(name = "text_de")
    private String textDe;

    @Column(name = "active")
    private boolean active;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NewsStatus status;

    @Column(name = "force")
    private boolean force;

    @Column(name = "plattform")
    private String platform;

    @Column(name = "min_software_version_code")
    private Long minSoftwareVersionCode;

    @Column(name = "max_software_version_code")
    private Long maxSoftwareVersionCode;

    @Column(name = "time")
    private OffsetDateTime time;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "start_time")
    private ZonedDateTime startsAt;

    @Column(name = "end_time")
    private ZonedDateTime endsAt;
}
