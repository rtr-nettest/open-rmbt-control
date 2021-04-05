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
 * example results:
 * <br>
 * IN:
 * "tcp_result_in"=>"OK",
 * "tcp_objective_in_port"=>"20774",
 * "tcp_objective_timeout"=>"3000",
 * "tcp_result_in_response"=>"HELLO TO 20774"
 * <br>
 * OUT:
 * "tcp_result_out"=>"OK",
 * "tcp_objective_timeout"=>"3000",
 * "tcp_objective_out_port"=>"33194",
 * "tcp_result_out_response"=>"PING"
 *
 * @author lb
 */
@Getter
@Setter
@NoArgsConstructor
public class TcpResult extends AbstractResult<TcpResult> {

    @JsonProperty("tcp_result_in")
    @HstoreKey("tcp_result_in")
    private String inResult;

    @JsonProperty("tcp_objective_in_port")
    @HstoreKey("tcp_objective_in_port")
    private Integer inPort;

    @JsonProperty("tcp_result_in_response")
    @HstoreKey("tcp_result_in_response")
    private String inResponse;

    @JsonProperty("tcp_result_out")
    @HstoreKey("tcp_result_out")
    private String outResult;

    @JsonProperty("tcp_objective_out_port")
    @HstoreKey("tcp_objective_out_port")
    private Integer outPort;

    @JsonProperty("tcp_result_out_response")
    @HstoreKey("tcp_result_out_response")
    private String outResponse;

    @JsonProperty("tcp_objective_timeout")
    @HstoreKey("tcp_objective_timeout")
    private Long timeout;

    @Override
    public String toString() {
        return "TcpResult [inResult=" + inResult + ", inPort=" + inPort
            + ", inResponse=" + inResponse + ", outResult=" + outResult
            + ", outPort=" + outPort + ", outResponse=" + outResponse
            + ", timeout=" + timeout + ", toString()=" + super.toString()
            + "]";
    }
}
