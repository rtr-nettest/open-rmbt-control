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
public class HttpProxyResult extends AbstractResult<HttpProxyResult> {

    @JsonProperty("http_objective_url")
    @HstoreKey("http_objective_url")
    private String target;

    @JsonProperty("http_objective_range")
    @HstoreKey("http_objective_range")
    private String range;

    @JsonProperty("http_result_length")
    @HstoreKey("http_result_length")
    private Long length;

    @JsonProperty("http_result_header")
    @HstoreKey("http_result_header")
    private String header;

    @JsonProperty("http_result_status")
    @HstoreKey("http_result_status")
    private String status;

    @JsonProperty("http_result_hash")
    @HstoreKey("http_result_hash")
    private String hash;

    @JsonProperty("http_result_duration")
    @HstoreKey("http_result_duration")
    private Long duration;

    @Override
    public String toString() {
        return "HttpProxyResult [target=" + target + ", range=" + range
            + ", length=" + length + ", header=" + header + ", status="
            + status + ", hash=" + hash + ", duration=" + duration
            + ", getOperator()=" + getOperator() + ", getOnFailure()="
            + getOnFailure() + ", getOnSuccess()=" + getOnSuccess() + "]";
    }
}
