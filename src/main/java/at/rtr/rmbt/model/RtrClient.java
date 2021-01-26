package at.rtr.rmbt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class RtrClient {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_uid_seq", allocationSize = 1)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_type_id")
    private ClientType clientType;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "sync_group_id")
    private Integer syncGroupId;

    @Column(name = "sync_code")
    private String syncCode;

    @Column(name = "terms_and_conditions_accepted")
    private Boolean termsAndConditionsAccepted;

    @Column(name = "sync_code_timestamp")
    private ZonedDateTime syncCodeTimestamp;

    @Column(name = "blacklisted")
    private Boolean blacklisted;

    @Column(name = "terms_and_conditions_accepted_version")
    private Long termsAndConditionsAcceptedVersion;

    @Column(name = "last_seen")
    private ZonedDateTime lastSeen;

    @Column(name = "terms_and_conditions_accepted_timestamp")
    private ZonedDateTime termsAndConditionsAcceptedTimestamp;
}
