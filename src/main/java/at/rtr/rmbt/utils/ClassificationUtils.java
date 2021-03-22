package at.rtr.rmbt.utils;

import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.enums.QoeCriteria;
import at.rtr.rmbt.response.QoeClassificationResponse;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ClassificationUtils {

    public static final int[] THRESHOLD_UPLOAD = {10000, 1000, 500}; // 10Mbit/s, 1Mbit/s, 500kbit/s
    public static final String[] THRESHOLD_UPLOAD_CAPTIONS = {"1", "0.5"};

    public static final int[] THRESHOLD_DOWNLOAD = {30000, 2000, 1000}; // 30Mbit/s, 2Mbit/s, 1Mbit/s
    public static final String[] THRESHOLD_DOWNLOAD_CAPTIONS = {"2", "1"};

    public static final int[] THRESHOLD_PING = {10000000, 25000000, 75000000}; // 10ms, 25ms, 75ms
    public static final String[] THRESHOLD_PING_CAPTIONS = {"25", "75"};

    // RSSI limits used for 2G,3G (and 4G when RSSI is used)
    // only odd values are reported by 2G/3G
    public static final int[] THRESHOLD_SIGNAL_MOBILE = {-75, -85, -101}; // -75 is still ultra-green, -85 is still green, -101 is still yellow
    public static final String[] THRESHOLD_SIGNAL_MOBILE_CAPTIONS = {"-85", "-101"};

    // RSRP limit used for 4G
    public static final int[] THRESHOLD_SIGNAL_RSRP = {-85, -95, -111};

    // RSSI limits used for Wifi
    public static final int[] THRESHOLD_SIGNAL_WIFI = {-51, -61, -76};
    public static final String[] THRESHOLD_SIGNAL_WIFI_CAPTIONS = {"-61", "-76"};

    private static final int CLASSIFICATION_ITEMS = 4;


    public int classify(final int[] threshold, final long value, int classificationItems) {
        int init = threshold.length - (classificationItems - 1);
        final boolean inverse = threshold[0] < threshold[1];

        if (!inverse) {
            /*
             * 1 = RED
             * classificationItems = ULTRA GREEN
             */

            int c = 0;
            for (int i = init; i < threshold.length; i++, c++) {
                if (value >= threshold[i]) {
                    return classificationItems - c;
                }
            }

            return 1;
        }

        int c = 0;
        for (int i = init; i < threshold.length; i++, c++) {
            if (value <= threshold[i]) {
                return classificationItems - c;
            }
        }
        return 1;
    }

    public int[] getThresholdForSignal(Integer networkType) {
        if (Objects.nonNull(networkType) && (networkType == 99 || networkType == 0)) {
            return ClassificationUtils.THRESHOLD_SIGNAL_WIFI;
        } else {
            return ClassificationUtils.THRESHOLD_SIGNAL_MOBILE;
        }
    }

    public static List<QoeClassificationResponse> classify(long pingNs, long downKbps, long upKbps, List<QoeClassificationThresholds> classifiers) {
        ArrayList<QoeClassificationResponse> ret = new ArrayList<>();

        for (QoeClassificationThresholds classifier : classifiers) {
            int minClass = CLASSIFICATION_ITEMS; //start with highest class, grade down
            double minQuality = 1d;


            for (Map.Entry<QoeCriteria, Long[]> entry : classifier.getThresholds().entrySet()) {
                final long value;
                switch (entry.getKey()) {
                    case DOWN:
                        value = downKbps;
                        break;
                    case UP:
                        value = upKbps;
                        break;
                    case PING:
                    default:
                        value = pingNs;
                        break;
                }

                Long[] threshold = entry.getValue();

                final boolean inverse = threshold[0] < threshold[1];
                int assignedClass;
                double assignedQuality;
                double a1 = threshold[threshold.length - 1];
                double a3 = threshold[0];
                double c = Math.sqrt(a3 / a1);
                double a0 = a1 / c;
                double a2 = a1 * c;
                double a4 = a3 * c;

                assignedQuality = ((Math.log(value) - Math.log(a0)) / (Math.log(a4) - Math.log(a0)));

                if (!inverse) {
                    //down, up
                    assignedClass = value >= a3 ? 4 : value >= a2 ? 3 : value >= a1 ? 2 : 1;

                } else {
                    assignedClass = value <= a3 ? 4 : value <= a2 ? 3 : value <= a1 ? 2 : 1;
                }
                assignedQuality = Math.max(0d, Math.min(1, assignedQuality));

                if (assignedClass == CLASSIFICATION_ITEMS) {
                    assignedQuality = 1d;
                }

                if (assignedClass < minClass) {
                    minClass = assignedClass;
                }
                if (assignedQuality < minQuality) {
                    minQuality = assignedQuality;
                }

            }
            QoeClassificationResponse newQoeClassificationResponse = QoeClassificationResponse.builder()
                    .classification(minClass)
                    .quality(minQuality)
                    .category(classifier.getQoeCategory().getValue())
                    .build();
            ret.add(newQoeClassificationResponse);
        }

        return ret;
    }
}
