package com.rtr.nettest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class RtrClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "client_type_id")
    private ClientType clientType;

    @Column(name = "time")
    private OffsetDateTime time;

    @Column(name = "sync_group_id")
    private Long syncGroupId;

    @Column(name = "sync_code")
    private String syncCode;

    @Column(name = "terms_and_conditions_accepted")
    private boolean isTermAndConditionsAccepted;

    @Column(name = "terms_and_conditions_accepted_version")
    private Long termAndConditionsVersion;

    @Column(name = "terms_and_conditions_accepted_timestamp")
    private OffsetDateTime termAndConditionsVersionAcceptedTimestamp;

    @Column(name = "last_seen")
    private OffsetDateTime lastSeen;
}
