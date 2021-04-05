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
import lombok.*;

import java.util.HashSet;

/**
 * @author lb
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DnsResult extends AbstractResult<DnsResult> {
    @JsonProperty("dns_objective_host")
    @HstoreKey("dns_objective_host")
    private String host;

    @JsonProperty("dns_result_info")
    @HstoreKey("dns_result_info")
    private String info;

    @JsonProperty("dns_result_status")
    @HstoreKey("dns_result_status")
    private String status;

    @JsonProperty("dns_objective_resolver")
    @HstoreKey("dns_objective_resolver")
    private String resolver;

    @JsonProperty("dns_objective_dns_record")
    @HstoreKey("dns_objective_dns_record")
    private String record;

    @JsonProperty("dns_objective_timeout")
    @HstoreKey("dns_objective_timeout")
    private Object timeout;

    @JsonProperty("dns_result_entries_found")
    @HstoreKey("dns_result_entries_found")
    private Object entriesFound;

    @JsonProperty("dns_result_duration")
    @HstoreKey("dns_result_duration")
    private Object duration;

    @JsonProperty("dns_result_entries")
    @HstoreKey("dns_result_entries")
    @HstoreCollection(DnsEntry.class)
    private HashSet<DnsEntry> resultEntries;

    @Override
    public String toString() {
        return "DnsResult [host=" + host + ", info=" + info + ", status="
            + status + ", resolver=" + resolver + ", record=" + record
            + ", timeout=" + timeout + ", entriesFound=" + entriesFound
            + ", duration=" + duration + ", resultEntries=" + resultEntries
            + ", operator=" + operator + ", onFailure=" + onFailure
            + ", onSuccess=" + onSuccess + ", evaluate=" + evaluate
            + ", endTimeNs=" + endTimeNs + ", startTimeNs=" + startTimeNs
            + ", testDuration=" + testDuration + "]";
    }

    /**
     * @author lb
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DnsEntry {
        @JsonProperty("dns_result_address")
        @HstoreKey("dns_result_address")
        private String address;

        @JsonProperty("dns_result_ttl")
        @HstoreKey("dns_result_ttl")
        private Long ttl;

        @JsonProperty("dns_result_priority")
        @HstoreKey("dns_result_priority")
        private Short priority;

        @Override
        public String toString() {
            return "[address=" + address + ", ttl=" + ttl
                + (priority != null ? ", priority=" + priority : "") + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((address == null) ? 0 : address.hashCode());
            result = prime * result
                + ((priority == null) ? 0 : priority.hashCode());
            result = prime * result + ((ttl == null) ? 0 : ttl.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DnsEntry other = (DnsEntry) obj;
            if (address == null) {
                if (other.address != null)
                    return false;
            } else if (!address.equals(other.address))
                return false;
            if (priority == null) {
                if (other.priority != null)
                    return false;
            } else if (!priority.equals(other.priority))
                return false;
            if (ttl == null) {
                if (other.ttl != null)
                    return false;
            } else if (!ttl.equals(other.ttl))
                return false;
            return true;
        }
    }
}
