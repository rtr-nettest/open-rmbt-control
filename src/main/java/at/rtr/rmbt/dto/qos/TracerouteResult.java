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

import at.rtr.rmbt.utils.hstoreparser.annotation.HstoreCollection;
import at.rtr.rmbt.utils.hstoreparser.annotation.HstoreKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author lb
 */
@Getter
@Setter
@NoArgsConstructor
public class TracerouteResult extends AbstractResult<TracerouteResult> {

    @JsonProperty("traceroute_objective_host")
    @HstoreKey("traceroute_objective_host")
    private String url;

    @JsonProperty("traceroute_result_status")
    @HstoreKey("traceroute_result_status")
    private String status;

    @JsonProperty("traceroute_result_duration")
    @HstoreKey("traceroute_result_duration")
    private Long duration;

    @JsonProperty("traceroute_objective_timeout")
    @HstoreKey("traceroute_objective_timeout")
    private Long timeout;

    @JsonProperty("traceroute_objective_max_hops")
    @HstoreKey("traceroute_objective_max_hops")
    private Integer maxHops;

    @JsonProperty("traceroute_result_hops")
    @HstoreKey("traceroute_result_hops")
    private Integer hops;

    @JsonProperty("traceroute_result_details")
    @HstoreKey("traceroute_result_details")
    @HstoreCollection(PathElement.class)
    private ArrayList<PathElement> resultEntries;

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
        this.resultMap.put("traceroute_result_details", resultEntries);
    }

    @Override
    public String toString() {
        return "TracerouteResult [url=" + url + ", status=" + status
            + ", duration=" + duration + ", timeout=" + timeout
            + ", maxHops=" + maxHops + ", hops=" + hops
            + ", resultEntries=" + resultEntries + "]";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public final static class PathElement {
        @JsonProperty("time")
        @HstoreKey("time")
        long time;

        @JsonProperty("host")
        @HstoreKey("host")
        String host;

        @Override
        public String toString() {
            return "PathElement [time=" + time + ", host=" + host + "]";
        }
    }
}
