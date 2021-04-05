package at.rtr.rmbt.util;

import at.rtr.rmbt.dto.qos.*;
import at.rtr.rmbt.utils.ResultComparer;
import at.rtr.rmbt.utils.hstoreparser.Hstore;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static at.rtr.rmbt.dto.qos.ResultDesc.STATUS_CODE_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultComparerTest {
    private static final Hstore HSTORE_PARSER = new Hstore(HttpProxyResult.class, NonTransparentProxyResult.class,
        DnsResult.class, TcpResult.class, UdpResult.class, WebsiteResult.class, VoipResult.class, TracerouteResult.class);
    @Test
    void compare_whenLong_expectOk() {
        assertEquals(-1, ResultComparer.compare("1", "2"));
    }

    @Test
    void compare_whenDouble_expectOk() {
        assertEquals(1, ResultComparer.compare("2.0", "1.1"));
    }

    @Test
    void compare_whenSecondIsNull_expectGreater() {
        assertEquals(1, ResultComparer.compare(1, null));
    }

    @Test
    void compare_whenFirstIsNull_expectLess() {
        assertEquals(-1, ResultComparer.compare(null, 1));
    }

    @Test
    void compare_whenCommonData_expectGreaterObject() throws IllegalAccessException {
        HttpProxyResult result1 = new HttpProxyResult();
        result1.setRange("12");
        result1.setOperator(AbstractResult.COMPARATOR_EQUALS);
        HttpProxyResult result2 = new HttpProxyResult();
        result1.setRange("2");
        ResultDesc resultDesc = ResultComparer.compare(result1, result2, HSTORE_PARSER, new ResultOptions(Locale.ENGLISH));
        assertEquals(STATUS_CODE_SUCCESS, resultDesc.getStatusCode());
        assertEquals(result2, resultDesc.getResultObject());
    }
}
