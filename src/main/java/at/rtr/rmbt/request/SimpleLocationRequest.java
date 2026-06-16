package at.rtr.rmbt.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
public class SimpleLocationRequest {

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Schema(description = "Location accuracy in m", example = "5.1")
    private Double accuracy;

    @Schema(description = "Altitude in meter", example = "222.9")
    private Double altitude;

    @Schema(description = "Bearing in degrees from north", example = "269")
    private Double bearing;

    @Schema(description = "Speed in meter per second", example = "0.45")
    private Double speed;

    @Schema(description = "Location provider", example = "gps")
    private String provider;
}
