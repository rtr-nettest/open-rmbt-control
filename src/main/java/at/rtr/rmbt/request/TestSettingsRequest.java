package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to obtain configuration for basic test")
public class TestSettingsRequest {

    @Schema(description = "Platform of the client")
    @JsonProperty("platform")
    private TestPlatform platform;

    @Schema(description = "Version code from the build gradle for Android devices", example = "33201")
    @JsonProperty("softwareVersionCode")
    private Integer softwareVersionCode;

    @Schema(description = "Is NDT test going to be executed")
    @JsonProperty("ndt")
    private Boolean ndt;

    @Schema(description = "End status of the previous executed test")
    @JsonProperty("previousTestStatus")
    private TestStatus previousTestStatus;

    @Schema(description = "Number of tests, which client performed, -1 if nothing, without actual starting test")
    @JsonProperty("testCounter")
    private Integer testCounter;

    @Schema(description = "Version of the client - code version", example = "master_64bc39c-dirty")
    @JsonProperty("softwareRevision")
    private String softwareRevision;

    @Schema(description = "Version of the client - build version", example = "3.6.5")
    @JsonProperty("softwareVersion")
    private String softwareVersion;

    @Schema(description = "Whether user have an option to select measurement server by himself")
    @JsonProperty("user_server_selection")
    private boolean userServerSelection;

    @Schema(description = "UUID of the measurement server which user prefers. Leave empty if no preference.", example = "893ee514-6432-43de-a2e9-2f9b0b6716da")
    @JsonProperty("prefer_server")
    private String preferredServer;

    @Schema(description = "Define number of threads used by client. Send -1 or leave empty to use defaults value")
    @JsonProperty("num_threads")
    private Integer numberOfThreads;

    @Schema(description = "Defined if we want to force usage of ipv4 or ipv6 test server, empty or null if we want to let the server to decide")
    @JsonProperty("protocol_version")
    private ProtocolVersion protocolVersion;

    @JsonProperty("location")
    private Location location;

    @Schema(example = "1571665024591")
    @JsonProperty("time")
    private Long time;

    @Schema(description = "Timezone in human readable format", example = "Europe/Bratislava")
    @JsonProperty("timezone")
    private String timezone;

    @Schema(description = "Type of the server")
    @JsonProperty("client")
    private ServerType serverType;

    @Schema(description = "Version of the used test set", example = "3.0")
    @JsonProperty("version")
    private String testSetVersion;

    @Schema(description = "Type of the client")
    @JsonProperty("type")
    private ClientType clientType;

    @Schema(description = "Client UUID of the device", example = "c373f294-f332-4f1a-999e-a87a12523f4b")
    @JsonProperty("uuid")
    private String uuid;

    @Schema(description = "2 letters language code or language code with region", example = "en")
    @JsonProperty("language")
    private String language;

    @Schema(description = "Set to true if loop mode is on")
    @JsonProperty("user_loop_mode")
    private boolean userLoopMode;

    @JsonProperty("loopmode_info")
    private LoopModeInfo loopModeInfo;

    @JsonProperty("capabilities")
    private Capabilities capabilities;

    @JsonProperty("android_permission_status")
    private List<PermissionState> androidPermissionStatus;

    @JsonProperty("measurement_type_flag")
    private MeasurementType measurementType;

    public enum ProtocolVersion {
        IPV4("ipv4"), IPV6("ipv6");

        private String label;

        ProtocolVersion(String label) {
            this.label = label;
        }

        @JsonCreator
        public ProtocolVersion forValue(String value) {
            for (ProtocolVersion protocolVersion : ProtocolVersion.values()) {
                if (protocolVersion.label.equals(value)) return protocolVersion;
            }
            return null;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Location {
        @JsonProperty("lat")
        private Double latitude;

        @JsonProperty("long")
        private Double longitude;

        @JsonProperty("provider")
        private String provider;

        @JsonProperty("speed")
        private Float speed;

        @JsonProperty("altitude")
        private Double altitude;

        @JsonProperty("time")
        private Long time;

        @JsonProperty("age")
        private Long age;

        @JsonProperty("accuracy")
        private Float accuracy;

        @JsonProperty("bearing")
        private Float bearing;

        @JsonProperty("mock_location")
        private boolean mockLocation;

        @JsonProperty("satellites")
        private Integer satellites;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PermissionState {
        @Schema(description = "Whole name of the android permission", example = "android.permission.ACCESS_FINE_LOCATION")
        private String permission;

        @Schema(description = "True if it is granted, false otherwise")
        private boolean status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class LoopModeInfo {
        @Schema(description = "ID of the loop settings - internal DB ID", example = "457546")
        @JsonProperty("uid")
        private Long uid;

        @Schema(description = "UUID of the test", example = "37e57f4e-25df-4bcf-9151-9f6eff311279")
        @JsonProperty("test_uuid")
        private String testUuid;

        @Schema(description = "UUID of the client (not necessary to send in this object, it will be taken from /testRequest top level object)")
        @JsonProperty("client_uuid")
        private String clientUuid;

        @Schema(description = "Maximum delay between 2 tests in seconds")
        @JsonProperty("max_delay")
        private Integer maxDelay;

        @Schema(description = "Maximum movement between 2 tests in meters")
        @JsonProperty("max_movement")
        private Integer maxMovement;

        @Schema(description = "How many tets should be executed")
        @JsonProperty("max_tests")
        private Integer maxTests;

        @Schema(description = "Number of the test")
        @JsonProperty("test_counter")
        private Integer testCounter;

        @Deprecated
        @JsonProperty("text_counter")
        @Schema(description = "This is a legacy parent of `test_counter` property. Please do not use it")
        private Integer textCounter;

        @Schema(description = "Loop UUID of the test series with first test will be generated, so first test will be null there in request and new will be generated", example = "37e57f4e-25df-4bcf-9151-9f6eff311279")
        @JsonProperty("loop_uuid")
        private String loopUuid;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Capabilities {
        @JsonProperty("classification")
        private ClassificationCapabilities classification;

        @JsonProperty("qos")
        private QosCapabilities qos;

        @Schema(description = "True, if the client can handle the RMBThttp protocol")
        @JsonProperty("RMBThttp")
        private boolean rmbtHttp;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode
        public static class ClassificationCapabilities {
            @JsonProperty("count")
            private Integer count;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode
        public static class QosCapabilities {
            @JsonProperty("supports_info")
            private boolean supportsInfo;
        }
    }
}
