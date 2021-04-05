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
 * Result example:
 * <p>
 * "website_result_info"=>"OK",
 * "website_objective_url"=>"http://alladin.at",
 * "website_result_status"=>"200",
 * "website_result_duration"=>"2194170609",
 * "website_result_rx_bytes"=>"18535",
 * "website_result_tx_bytes"=>"1170",
 * "website_objective_timeout"=>"10000"
 *
 * @author lb
 */
@Getter
@Setter
@NoArgsConstructor
public class WebsiteResult extends AbstractResult<WebsiteResult> {

    @JsonProperty("website_result_info")
    @HstoreKey("website_result_info")
    private String info;

    @JsonProperty("website_objective_url")
    @HstoreKey("website_objective_url")
    private String url;

    @JsonProperty("website_result_status")
    @HstoreKey("website_result_status")
    private String status;

    @JsonProperty("website_result_duration")
    @HstoreKey("website_result_duration")
    private Long duration;

    @JsonProperty("website_result_rx_bytes")
    @HstoreKey("website_result_rx_bytes")
    private Long rxBytes;

    @JsonProperty("website_result_tx_bytes")
    @HstoreKey("website_result_tx_bytes")
    private Long txBytes;

    @JsonProperty("website_objective_timeout")
    @HstoreKey("website_objective_timeout")
    private Long timeout;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "WebsiteResult [info=" + info + ", url=" + url + ", status="
            + status + ", duration=" + duration + ", rxBytes=" + rxBytes
            + ", txBytes=" + txBytes + ", timeout=" + timeout + "]";
    }
}
