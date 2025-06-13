package at.rtr.rmbt.constant;

public interface Constants {
    Long NEWS_REQUEST_LAST_NEWS_UID = 0L;
    Long NEWS_REQUEST_SOFTWARE_VERSION_CODE = -1L;
    String UNKNOWN_DEVICE = "Unknown Device";
    int SRID900913 = 900913;
    int SRID4326 = 4326;
    int SRID3857 = 3857;
    Integer WEIGHT = 1;
    Integer PRIORITY = 1;
    String SIGNAL_STRENGTH_DBM_TEMPLATE = "%s dBm";
    String SIGNAL_STRENGTH_RSRQ_TEMPLATE = "RSRQ: %s dB";
    String SIGNAL_STRENGTH_TIMING_ADVANCE_TEMPLATE = "TA: %s";
    String SIGNAL_STRENGTH_ACCURACY_TEMPLATE = "+/-%sm";
    String SIGNAL_STRENGTH_ALTITUDE_TEMPLATE = "%s m";
    String SIGNAL_STRENGTH_BEARING_TEMPLATE = "%s\u00B0";
    String SIGNAL_STRENGTH_SPEED_TEMPLATE = "%s km/h";
    String VERSION_TEMPLATE = "%s_%s";
    String SIGNAL_STRENGTH_DELIMITER = ", ";
    Integer MIN_SPEED = 0;
    Integer MAX_SPEED = 800000000;
    Long MIN_PING = 0L;
    Long MAX_PING = 60000000000L;
    String VALUE_AND_UNIT_TEMPLATE = "%s %s";
    String TEST_RESULT_DETAIL_OPEN_TEST_UUID_TEMPLATE = "O%s";
    String TEST_RESULT_DETAIL_OPEN_UUID_TEMPLATE = "P%s";
    String TEST_HISTORY_LOOP_UUID_TEMPLATE = "L%s";
    String PARENTHESES_TEMPLATE = "%s (%s)";
    String TIMEZONE_TEMPLATE = "UTC%sh";
    String OPEN_TEST_UUID_PREFIX = "O";
    Double BYTES_UNIT_CONVERSION_MULTIPLICATOR = 1000d;
    Double PING_CONVERSION_MULTIPLICATOR = 1000000d;
    Double METERS_PER_SECOND_TO_KILOMETERS_PER_HOURS_MULTIPLICATOR = 3.6d;
    Double NANOS_TO_SECONDS_MULTIPLICATOR = 1000000000d;
    Double MILLISECONDS_TO_HOURS = 1000d * 60d * 60d;
    String TIMEZONE_PATTERN = "+0.##;-0.##";
    Integer INTERNATIONAL_ROAMING_TYPE_BARRIER = 1;
    String INET_4_IP_VERSION = "4";
    String INET_6_IP_VERSION = "6";
    Integer NOT_SYNCED_CLIENT_GROUP_ID = 0;
    Integer NETWORK_TYPE_WLAN = 99;
    String STATUS_OK = "OK";
    String WEB_COMMENT = " [web]";
    Integer UUID_PREFIX_SIZE = 1;
    boolean DEFAULT_QOS_SUPPORTS_INFO = false;
    boolean DEFAULT_RMBT_HTTP = false;
    Integer DEFAULT_CLASSIFICATION_COUNT = 3;
    String WKT_EPSG_4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137.0,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.017453292519943295,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
    String WKT_EPSG_3857 =
            "PROJCS[\"WGS 84 / Pseudo-Mercator\", \n"
                    + "  GEOGCS[\"WGS 84\", \n"
                    + "    DATUM[\"World Geodetic System 1984\", \n"
                    + "      SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], \n"
                    + "      AUTHORITY[\"EPSG\",\"6326\"]], \n"
                    + "    PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], \n"
                    + "    UNIT[\"degree\", 0.017453292519943295], \n"
                    + "    AXIS[\"Geodetic longitude\", EAST], \n"
                    + "    AXIS[\"Geodetic latitude\", NORTH], \n"
                    + "    AUTHORITY[\"EPSG\",\"4326\"]], \n"
                    + "  PROJECTION[\"Popular Visualisation Pseudo Mercator\", AUTHORITY[\"EPSG\",\"1024\"]], \n"
                    + "  PARAMETER[\"semi_minor\", 6378137.0], \n"
                    + "  PARAMETER[\"latitude_of_origin\", 0.0], \n"
                    + "  PARAMETER[\"central_meridian\", 0.0], \n"
                    + "  PARAMETER[\"scale_factor\", 1.0], \n"
                    + "  PARAMETER[\"false_easting\", 0.0], \n"
                    + "  PARAMETER[\"false_northing\", 0.0], \n"
                    + "  UNIT[\"m\", 1.0], \n"
                    + "  AXIS[\"Easting\", EAST], \n"
                    + "  AXIS[\"Northing\", NORTH], \n"
                    + "  AUTHORITY[\"EPSG\",\"3857\"]]";
    String WKT_EPSG_900913 = "PROJCS[\"Google Mercator\",\n" +
            "    GEOGCS[\"WGS 84\",DATUM[\"World Geodetic System 1984\",SPHEROID[\"WGS 84\",6378137.0,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.017453292519943295],AXIS[\"Geodetic latitude\",NORTH],\n" +
            "        AXIS[\"Geodetic longitude\",EAST],\n" +
            "        AUTHORITY[\"EPSG\",\"4326\"]],\n" +
            "    PROJECTION[\"Mercator_1SP\"],\n" +
            "    PARAMETER[\"semi_minor\",6378137.0],\n" +
            "    PARAMETER[\"latitude_of_origin\",0.0],\n" +
            "    PARAMETER[\"central_meridian\",0.0],\n" +
            "    PARAMETER[\"scale_factor\",1.0],\n" +
            "    PARAMETER[\"false_easting\",0.0],\n" +
            "    PARAMETER[\"false_northing\",0.0],\n" +
            "    UNIT[\"m\",1.0],\n" +
            "    AXIS[\"Easting\",EAST],\n" +
            "    AXIS[\"Northing\",NORTH],\n" +
            "    AUTHORITY[\"EPSG\",\"900913\"]]";

    static final int SIGNAL_CHANGE_UUID_AFTER_MIN = 10;
}
