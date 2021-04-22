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
 * @author lb
 */
@Getter
@Setter
@NoArgsConstructor
public class VoipResult extends AbstractResult<VoipResult> {

    @JsonProperty("voip_objective_in_port")
    @HstoreKey("voip_objective_in_port")
    Object inPort;

    @JsonProperty("voip_objective_out_port")
    @HstoreKey("voip_objective_out_port")
    Object outPort;

    @JsonProperty("voip_objective_call_duration")
    @HstoreKey("voip_objective_call_duration")
    Object callDuration;

    @JsonProperty("voip_objective_bits_per_sample")
    @HstoreKey("voip_objective_bits_per_sample")
    Object bitsPerSample;

    @JsonProperty("voip_objective_sample_rate")
    @HstoreKey("voip_objective_sample_rate")
    Object sampleRate;

    @JsonProperty("voip_objective_delay")
    @HstoreKey("voip_objective_delay")
    Object delay;

    @JsonProperty("voip_objective_timeout")
    @HstoreKey("voip_objective_timeout")
    Object timeout;

    @JsonProperty("voip_objective_payload")
    @HstoreKey("voip_objective_payload")
    Object payload;

    @JsonProperty("voip_result_in_max_jitter")
    @HstoreKey("voip_result_in_max_jitter")
    Object maxJitterIn;

    @JsonProperty("voip_result_in_mean_jitter")
    @HstoreKey("voip_result_in_mean_jitter")
    Object minJitterIn;

    @JsonProperty("voip_result_in_max_delta")
    @HstoreKey("voip_result_in_max_delta")
    Object maxDeltaIn;

    @JsonProperty("voip_result_in_num_packets")
    @HstoreKey("voip_result_in_num_packets")
    Object numPacketsIn;

    @JsonProperty("voip_result_in_skew")
    @HstoreKey("voip_result_in_skew")
    Object skewIn;

    @JsonProperty("voip_result_out_max_jitter")
    @HstoreKey("voip_result_out_max_jitter")
    Object maxJitterOut;

    @JsonProperty("voip_result_out_mean_jitter")
    @HstoreKey("voip_result_out_mean_jitter")
    Object minJitterOut;

    @JsonProperty("voip_result_out_max_delta")
    @HstoreKey("voip_result_out_max_delta")
    Object maxDeltaOut;

    @JsonProperty("voip_result_out_num_packets")
    @HstoreKey("voip_result_out_num_packets")
    Object numPacketsOut;

    @JsonProperty("voip_result_out_skew")
    @HstoreKey("voip_result_out_skew")
    Object skewOut;

    @JsonProperty("voip_result_in_sequence_error")
    @HstoreKey("voip_result_in_sequence_error")
    Object seqErrorsIn;

    @JsonProperty("voip_result_out_sequence_error")
    @HstoreKey("voip_result_out_sequence_error")
    Object seqErrorsOut;

    @JsonProperty("voip_result_in_short_seq")
    @HstoreKey("voip_result_in_short_seq")
    Object shortSequenceIn;

    @JsonProperty("voip_result_out_short_seq")
    @HstoreKey("voip_result_out_short_seq")
    Object shortSequenceOut;

    @JsonProperty("voip_result_in_long_seq")
    @HstoreKey("voip_result_in_long_seq")
    Object longSequenceIn;

    @JsonProperty("voip_result_out_long_seq")
    @HstoreKey("voip_result_out_long_seq")
    Object longSequenceOut;

    @JsonProperty("voip_result_status")
    @HstoreKey("voip_result_status")
    String status;

    @Override
    public String toString() {
        return "VoipResult [inPort=" + inPort + ", outPort=" + outPort
            + ", callDuration=" + callDuration + ", bitsPerSample="
            + bitsPerSample + ", sampleRate=" + sampleRate + ", delay="
            + delay + ", timeout=" + timeout + ", payload=" + payload
            + ", maxJitterIn=" + maxJitterIn + ", minJitterIn="
            + minJitterIn + ", maxDeltaIn=" + maxDeltaIn
            + ", numPacketsIn=" + numPacketsIn + ", skewIn=" + skewIn
            + ", maxJitterOut=" + maxJitterOut + ", minJitterOut="
            + minJitterOut + ", maxDeltaOut=" + maxDeltaOut
            + ", numPacketsOut=" + numPacketsOut + ", skewOut=" + skewOut
            + ", seqErrorsIn=" + seqErrorsIn + ", seqErrorsOut="
            + seqErrorsOut + ", shortSequenceIn=" + shortSequenceIn
            + ", shortSequenceOut=" + shortSequenceOut
            + ", longSequenceIn=" + longSequenceIn + ", longSequenceOut="
            + longSequenceOut + "]";
    }
}
