package at.rtr.rmbt.utils.testscript;

import at.rtr.rmbt.dto.qos.TracerouteResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class SystemApi {

    final static DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("##0.00");

    static {
        DEFAULT_DECIMAL_FORMAT.setMaximumFractionDigits(2);
    }

    public static String getRandomUrl(String prefix, String suffix, int length) {
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

        StringBuilder randomUrl = new StringBuilder();
        randomUrl.append(prefix);
        Random rnd = new Random();

        for (int i = 0; i < length; i++) {
            randomUrl.append(digits[rnd.nextInt(16)]);
        }

        randomUrl.append(suffix);
        return randomUrl.toString();
    }

    public int getCount(Object array) {
        if (array != null && array.getClass().isArray()) {
            return Array.getLength(array);
        }

        return 0;
    }

    public boolean isEmpty(Object array) {
        return getCount(array) == 0;
    }

    public boolean isNull(Object o) {
        return o == null;
    }


    /**
     * Single entry point for the QoS evaluation script, which calls {@code nn.parseTraceroute(x)}
     * with either a JSON string, a list of {@link TracerouteResult.PathElement}, or {@code null}.
     * It is intentionally <em>not</em> overloaded: Nashorn cannot pick between two reference-typed
     * overloads when the argument is {@code null}, which previously failed with
     * "Can't unambiguously select between ... parseTraceroute".
     */
    public String parseTraceroute(Object path) throws JSONException {
        if (path == null) {
            return null;
        }
        if (path instanceof String json) {
            return parseTracerouteFromJson(json);
        }
        if (path instanceof List<?> elements) {
            return parseTracerouteFromPathElements(elements);
        }
        return null;
    }

    private String parseTracerouteFromJson(String path) throws JSONException {
        final JSONArray traceRoute = new JSONArray(path);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < traceRoute.length(); i++) {
            final JSONObject e = traceRoute.getJSONObject(i);
            sb.append(e.getString("host"));
            sb.append("  time=");
            try {
                sb.append(DEFAULT_DECIMAL_FORMAT.format((float) e.getLong("time") / 1000000f));
                sb.append("ms\n");
            } catch (Exception ex) {
                sb.append(e.getLong("time"));
                sb.append("ns\n");
            }
        }

        return sb.toString();
    }

    private String parseTracerouteFromPathElements(List<?> path) {
        StringBuilder sb = new StringBuilder();
        for (Object item : path) {
            TracerouteResult.PathElement e = (TracerouteResult.PathElement) item;
            sb.append(e.getHost());
            sb.append("  time=");
            try {
                sb.append(DEFAULT_DECIMAL_FORMAT.format((float) e.getTime() / 1000000f));
                sb.append("ms\n");
            } catch (Exception ex) {
                sb.append(e.getTime());
                sb.append("ns\n");
            }
        }

        return sb.toString();
    }

    public Object debug(Object toLog) {
        System.out.println("QoSLOG: " + toLog);
        return toLog;
    }
}
