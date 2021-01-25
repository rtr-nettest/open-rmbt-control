package com.rtr.nettest.model;


import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "test")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    private OffsetDateTime time;

    private UUID uuid;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    private Long clientId;

    private String clientPublicIp;

    private String clientPublicIpAnonymized;

    private String timezone;

    private OffsetDateTime clientTime;

    @Column(name = "public_ip_asn")
    private Long publicIpAsNumber;

    @Column(name = "public_ip_as_name")
    private String publicIpAsName;

    @Column(name = "country_asn")
    private String countryAsn;

    @Column(name = "public_ip_rdns")
    private String publicIpRDNS;

    private String status;

    private Long lastSequenceNumber;

    @PrePersist
    protected void preInsert() {
        this.time = OffsetDateTime.now();
    }
}
