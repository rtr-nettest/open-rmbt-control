package at.rtr.rmbt.request;


import jakarta.persistence.*;
import lombok.*;

/**
 * Simple location request class.
 */
@Getter
@Builder
public class SimpleLocationRequest {

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;




}
