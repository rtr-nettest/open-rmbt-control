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
public class NonTransparentProxyResult extends AbstractResult<NonTransparentProxyResult> {

    @JsonProperty("nontransproxy_objective_request")
    @HstoreKey("nontransproxy_objective_request")
    private String request;

    @JsonProperty("nontransproxy_objective_timeout")
    @HstoreKey("nontransproxy_objective_timeout")
    private Long timeout;

    @JsonProperty("nontransproxy_result")
    @HstoreKey("nontransproxy_result")
    private String result;

    @JsonProperty("nontransproxy_result_response")
    @HstoreKey("nontransproxy_result_response")
    private String response;

    @JsonProperty("nontransproxy_objective_port")
    @HstoreKey("nontransproxy_objective_port")
    private Integer port;

    @Override
    public String toString() {
        return "NonTransparentProxyResult [request=" + request + ", timeout="
            + timeout + ", result=" + result + ", response=" + response
            + ", port=" + port + "]";
    }
}
