/*******************************************************************************
 * Copyright 2013-2015 alladin-IT GmbH
 * Copyright 2013-2015 Rundfunk und Telekom Regulierungs-GmbH (RTR-GmbH)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.rtr.rmbt.dto.qos;

import at.rtr.rmbt.utils.hstoreparser.annotation.HstoreKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * example result:
 * <p>
 * OUTGOING:
 * "udp_objective_outgoing_port"=>"10016",
 * "udp_result_outgoing_num_packets"=>"8",
 * "udp_objective_outgoing_num_packets"=>"8"
 * <p>
 * INCOMING:
 * "udp_objective_incoming_port"=>"37865",
 * "udp_result_incoming_num_packets"=>"11",
 * "udp_objective_incoming_num_packets"=>"11"
 *
 * @author lb
 */
@Getter
@Setter
@NoArgsConstructor
public class UdpResult extends AbstractResult<UdpResult> {

    @JsonProperty("udp_objective_delay")
    @HstoreKey("udp_objective_delay")
    private Object delay;

    @JsonProperty("udp_objective_out_port")
    @HstoreKey("udp_objective_out_port")
    private Object outPort;

    @JsonProperty("udp_result_out_num_packets")
    @HstoreKey("udp_result_out_num_packets")
    private Object resultOutNumPackets;

    @JsonProperty("udp_result_out_response_num_packets")
    @HstoreKey("udp_result_out_response_num_packets")
    private Object resultOutNumPacketsResponse;

    @JsonProperty("udp_objective_out_num_packets")
    @HstoreKey("udp_objective_out_num_packets")
    private Object outNumPackets;

    @JsonProperty("udp_objective_in_port")
    @HstoreKey("udp_objective_in_port")
    private Object inPort;

    @JsonProperty("udp_result_in_num_packets")
    @HstoreKey("udp_result_in_num_packets")
    private Object resultInNumPackets;

    @JsonProperty("udp_objective_in_num_packets")
    @HstoreKey("udp_objective_in_num_packets")
    private Object inNumPackets;

    @JsonProperty("udp_result_in_response_num_packets")
    @HstoreKey("udp_result_in_response_num_packets")
    private Object resultInNumPacketsResponse;

    @JsonProperty("udp_result_in_packet_loss_rate")
    @HstoreKey("udp_result_in_packet_loss_rate")
    private Object incomingPlr;

    @JsonProperty("udp_result_out_packet_loss_rate")
    @HstoreKey("udp_result_out_packet_loss_rate")
    private Object outgoingPlr;

    @Override
    public String toString() {
        return "UdpResult [delay=" + delay + ", outPort=" + outPort
            + ", resultOutNumPackets=" + resultOutNumPackets
            + ", resultOutNumPacketsResponse="
            + resultOutNumPacketsResponse + ", outNumPackets="
            + outNumPackets + ", inPort=" + inPort
            + ", resultInNumPackets=" + resultInNumPackets
            + ", inNumPackets=" + inNumPackets
            + ", resultInNumPacketsResponse=" + resultInNumPacketsResponse
            + ", incomingPlr=" + incomingPlr + ", outgoingPlr="
            + outgoingPlr + "]";
    }
}
