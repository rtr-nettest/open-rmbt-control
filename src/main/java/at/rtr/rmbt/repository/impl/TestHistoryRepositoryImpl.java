package at.rtr.rmbt.repository.impl;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.TestHistory;
import at.rtr.rmbt.repository.TestHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class TestHistoryRepositoryImpl implements TestHistoryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final static String GET_TEST_HISTORY = "SELECT DISTINCT"
            + " t.status, t.uuid, t.open_test_uuid, time, timezone, speed_upload, speed_download, ping_median, lte_rsrp, signal_strength, dual_sim, sim_count, network_type, nt.group_name network_type_group_name, l.loop_uuid loop_uuid,"
            + " COALESCE(adm.fullname, t.model) model"
            + " FROM test t"
            + " LEFT JOIN device_map adm ON adm.codename=t.model"
            + " LEFT JOIN network_type nt ON t.network_type=nt.uid"
            + " LEFT JOIN test_loopmode l ON (l.test_uuid = t.uuid)"
            + " WHERE t.deleted = false AND t.implausible = false "
            + " %s %s %s %s" + " ORDER BY time DESC" + " %s";

    @Override
    public List<TestHistory> getTestHistoryByDevicesAndNetworksAndClient(Integer resultLimit, Integer resultOffset, List<String> devices, List<String> networks, RtrClient client, boolean includeFailedTests) {
        ArrayList<Object> args = new ArrayList<>();
        String testStatusRequest = " AND t.status IN (%s)";
        args.add("FINISHED");
        if (includeFailedTests) {
            args.add("ERROR");
            testStatusRequest = String.format(testStatusRequest, "?, ?");
        }
        else {
            testStatusRequest = String.format(testStatusRequest, "?");
        }
        String limitRequest = getLimitRequest(resultLimit, resultOffset);
        String devicesRequest = getDevicesRequest(devices, args);
        String networksRequest = getNetworksRequest(networks, args);
        String clientSyncRequest = getClientSyncRequest(client, args);
        String finalQuery = String.format(GET_TEST_HISTORY, testStatusRequest, clientSyncRequest, devicesRequest, networksRequest, limitRequest);
        return jdbcTemplate.query(finalQuery, new ArgumentPreparedStatementSetter(args.toArray()), new BeanPropertyRowMapper<>(TestHistory.class));
    }

    private String getClientSyncRequest(RtrClient client, ArrayList<Object> args) {
        if (Constants.NOT_SYNCED_CLIENT_GROUP_ID.equals(ObjectUtils.defaultIfNull(client.getSyncGroupId(), NumberUtils.INTEGER_ZERO))) {
            args.add(client.getUid());
            return String.format(" AND client_id = ?");
        } else {
            args.add(client.getUid());
            args.add(client.getSyncGroupId());
            return String.format(" AND (t.client_id IN (SELECT ? UNION SELECT uid FROM client WHERE sync_group_id = ? ))");
        }
    }

    private String getNetworksRequest(List<String> networks, ArrayList<Object> args) {
        if (Objects.nonNull(networks)) {
            StringJoiner joiner = new StringJoiner(", ");
            for (String network : networks) {
                args.add(network);
                joiner.add("?");
            }
            return " AND nt.group_name IN (" + joiner.toString() + ")";
        }
        return StringUtils.EMPTY;
    }

    private String getDevicesRequest(List<String> devices, ArrayList<Object> args) {
        if (Objects.nonNull(devices)) {
            boolean checkUnknown = false;
            StringJoiner joiner = new StringJoiner(", ");
            for (String device : devices) {
                if (device.equals(Constants.UNKNOWN_DEVICE))
                    checkUnknown = true;
                else {
                    args.add(device);
                    joiner.add("?");
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
