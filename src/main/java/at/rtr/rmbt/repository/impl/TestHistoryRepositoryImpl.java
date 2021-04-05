package at.rtr.rmbt.repository.impl;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.TestHistory;
import at.rtr.rmbt.repository.TestHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Repository
@RequiredArgsConstructor
public class TestHistoryRepositoryImpl implements TestHistoryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final static String GET_TEST_HISTORY = "SELECT DISTINCT"
            + " t.uuid, time, timezone, speed_upload, speed_download, ping_median, lte_rsrp, signal_strength, dual_sim, sim_count, network_type, nt.group_name network_type_group_name, l.loop_uuid loop_uuid,"
            + " COALESCE(adm.fullname, t.model) model"
            + " FROM test t"
            + " LEFT JOIN device_map adm ON adm.codename=t.model"
            + " LEFT JOIN network_type nt ON t.network_type=nt.uid"
            + " LEFT JOIN test_loopmode l ON (l.test_uuid = t.uuid)"
            + " WHERE t.deleted = false AND t.implausible = false AND t.status = 'FINISHED'"
            + " %s %s %s" + " ORDER BY time DESC" + " %s";

    @Override
    public List<TestHistory> getTestHistoryByDevicesAndNetworksAndClient(Integer resultLimit, Integer resultOffset, List<String> devices, List<String> networks, RtrClient client) {
        String limitRequest = getLimitRequest(resultLimit, resultOffset);
        String devicesRequest = getDevicesRequest(devices);
        String networksRequest = getNetworksRequest(networks);
        String clientSyncRequest = getClientSyncRequest(client);
        String finalQuery = String.format(GET_TEST_HISTORY, clientSyncRequest, devicesRequest, networksRequest, limitRequest);
        return jdbcTemplate.query(finalQuery, new BeanPropertyRowMapper<>(TestHistory.class));
    }

    private String getClientSyncRequest(RtrClient client) {
        if (Constants.NOT_SYNCED_CLIENT_GROUP_ID.equals(ObjectUtils.defaultIfNull(client.getSyncGroupId(), NumberUtils.INTEGER_ZERO))) {
            return String.format(" AND client_id = %s", client.getUid());
        } else {
            return String.format(" AND (t.client_id IN (SELECT %s UNION SELECT uid FROM client WHERE sync_group_id = %s ))", client.getUid(), client.getSyncGroupId());
        }
    }

    private String getNetworksRequest(List<String> networks) {
        if (Objects.nonNull(networks)) {
            StringJoiner joiner = new StringJoiner(", ");
            for (String network : networks) {
                joiner.add(String.format(Constants.SQL_QUERY_STRING_VALUE_TEMPLATE, network));
            }
            return " AND nt.group_name IN (" + joiner.toString() + ")";
        }
        return StringUtils.EMPTY;
    }

    private String getDevicesRequest(List<String> devices) {
        if (Objects.nonNull(devices)) {
            boolean checkUnknown = false;
            StringJoiner joiner = new StringJoiner(", ");
            for (String device : devices) {
                if (device.equals(Constants.UNKNOWN_DEVICE))
                    checkUnknown = true;
                else {
                    joiner.add(String.format(Constants.SQL_QUERY_STRING_VALUE_TEMPLATE, device));
                }
            }
            if (joiner.length() > 0) {
                return " AND (COALESCE(adm.fullname, t.model) IN (" + joiner.toString() + ")" + (checkUnknown ? " OR model IS NULL OR model = ''" : "") + ")";
            }
        }
        return StringUtils.EMPTY;
    }

    private String getLimitRequest(Integer resultLimit, Integer resultOffset) {
        if (Objects.nonNull(resultLimit)) {
            String offsetString = StringUtils.EMPTY;
            if (Objects.nonNull(resultOffset)) {
                offsetString = " OFFSET " + resultOffset;
            }
            return " LIMIT " + resultLimit + offsetString;
        }
        return StringUtils.EMPTY;
    }
}
