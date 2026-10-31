package at.rtr.rmbt.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PostgresArrayUtils {

    /**
     * Parses the text representation of a one-dimensional PostgreSQL array, as stored in the
     * settings {@code value} column (e.g. {@code {https://a/ip,https://b/ip}}), into a list of
     * strings. Double-quoted elements with {@code \"} / {@code \\} escapes are supported, and an
     * unquoted {@code NULL} element becomes {@code null}.
     *
     * @return {@code null} when {@code raw} is {@code null} or blank (so callers can omit the field
     * entirely when the setting is absent), an empty list for {@code {}}, otherwise the elements.
     */
    public List<String> parseStringArray(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        String body = raw.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
        }
        List<String> result = new ArrayList<>();
        if (body.isEmpty()) {
            return result;
        }
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean quotedElement = false;
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (inQuotes) {
                if (c == '\\' && i + 1 < body.length()) {
                    current.append(body.charAt(++i));
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
                quotedElement = true;
            } else if (c == ',') {
                result.add(finishElement(current, quotedElement));
                current.setLength(0);
                quotedElement = false;
            } else {
                current.append(c);
            }
        }
        result.add(finishElement(current, quotedElement));
        return result;
    }

    private String finishElement(StringBuilder sb, boolean quoted) {
        String value = sb.toString();
        if (!quoted) {
            value = value.trim();
            if (value.equalsIgnoreCase("NULL")) {
                return null;
            }
        }
        return value;
    }
}
