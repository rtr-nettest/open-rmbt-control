package at.rtr.rmbt.dto;

import at.rtr.rmbt.enums.NetworkGroupName;
import lombok.*;

/**
 * Lte frequency dto class.
 */
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class LteFrequencyDto {

    private Integer channelNumber;

    private NetworkGroupName technology;
}
