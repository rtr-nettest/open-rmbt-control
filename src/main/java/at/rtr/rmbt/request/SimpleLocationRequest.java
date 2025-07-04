package at.rtr.rmbt.request;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
public class SimpleLocationRequest {

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;




}
