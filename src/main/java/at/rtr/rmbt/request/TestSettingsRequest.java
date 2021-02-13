package at.rtr.rmbt.request;

import at.rtr.rmbt.model.enums.ClientType;
import at.rtr.rmbt.model.enums.ServerType;
import at.rtr.rmbt.model.enums.TestPlatform;
import at.rtr.rmbt.model.enums.TestStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "Request to obtain configuration for basic test")
public class TestSettingsRequest {

    @ApiModelProperty(notes = "Platform of the client")
    @JsonProperty("platform")
    private TestPlatform platform;

    @ApiModelProperty(notes = "Version code from the build gradle for Android devices", example = "33201")
    @JsonProperty("softwareVersionCode")
    private Integer softwareVersionCode;

    @ApiModelProperty(notes = "Is NDT test going to be executed")
    @JsonProperty("ndt")
    private boolean ndt;

    @ApiModelProperty(notes = "End status of the previous executed test")
    @JsonProperty("previousTestStatus")
    private TestStatus previousTestStatus;

    @ApiModelProperty(notes = "Number of tests, which client performed, -1 if nothing, without actual starting test")
    @JsonProperty("testCounter")
    private Integer testCounter;

    @ApiModelProperty(notes = "Version of the client - code version", example = "master_64bc39c-dirty")
    @JsonProperty("softwareRevision")
    private String softwareRevision;

    @ApiModelProperty(notes = "Version of the client - build version", example = "3.6.5")
    @JsonProperty("softwareVersion")
    private String softwareVersion;

    @ApiModelProperty(notes = "Whether user have an option to select measurement server by himself")
    @JsonProperty("user_server_selection")
    private boolean userServerSelection;

    @ApiModelProperty(notes = "UUID of the measurement server which user prefers. Leave empty if no preference.", example = "893ee514-6432-43de-a2e9-2f9b0b6716da")
    @JsonProperty("prefer_server")
    private String preferredServer;

    @ApiModelProperty(notes = "Define number of threads used by client. Send -1 or leave empty to use defaults value")
    @JsonProperty("num_threads")
    private Integer numberOfThreads;

    @ApiModelProperty(notes = "Defined if we want to force usage of ipv4 or ipv6 test server, empty or null if we want to let the server to decide")
    @JsonProperty("protocol_version")
    private ProtocolVersion protocolVersion;

    @JsonProperty("location")
    private Location location;

    @ApiModelProperty(example = "1571665024591")
    @JsonProperty("time")
    private Long time;

    @ApiModelProperty(notes = "Timezone in human readable format", example = "Europe/Bratislava")
    @JsonProperty("timezone")
    private String timezone;

    @ApiModelProperty(notes = "Type of the server")
    @JsonProperty("client")
    private ServerType serverType;

    @ApiModelProperty(notes = "Version of the used test set", example = "3.0")
    @JsonProperty("version")
    private String testSetVersion;

    @ApiModelProperty(notes = "Type of the client")
    @JsonProperty("type")
    private ClientType clientType;

    @ApiModelProperty(notes = "Client UUID of the device", example = "c373f294-f332-4f1a-999e-a87a12523f4b")
    @JsonProperty("uuid")
    private String uuid;

    @ApiModelProperty(notes = "2 letters language code or language code with region", example = "en")
    @JsonProperty("language")
    private String language;

    @ApiModelProperty(notes = "Set to true if loop mode is on")
    @JsonProperty("user_loop_mode")
    private boolean userLoopMode;

    @JsonProperty("loopmode_info")
    private LoopModeInfo loopModeInfo;

    @JsonProperty("capabilities")
    private Capabilities capabilities;

    @JsonProperty("android_permission_status")
    private List<PermissionState> androidPermissionStatus;

    public enum ProtocolVersion {
        IPV4("ipv4"), IPV6("ipv6");

        private String label;

        ProtocolVersion(String label) {
            this.label = label;
        }

        @JsonCreator
        public ProtocolVersion forValue(String value) {
            for (ProtocolVersion protocolVersion : ProtocolVersion.values()) {
                if (protocolVersion.label.equals(value))
                    return protocolVersion;
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
        private Integer age;

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
        @ApiModelProperty(notes = "Whole name of the android permission", example = "android.permission.ACCESS_FINE_LOCATION")
        private String permission;

        @ApiModelProperty(notes = "True if it is granted, false otherwise")
        private boolean status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class LoopModeInfo {
        @ApiModelProperty(notes = "ID of the loop settings - internal DB ID", example = "457546")
        @JsonProperty("uid")
        private Long uid;

        @ApiModelProperty(notes = "UUID of the test", example = "37e57f4e-25df-4bcf-9151-9f6eff311279")
        @JsonProperty("test_uuid")
        private String testUuid;

        @ApiModelProperty(notes = "UUID of the client (not necessary to send in this object, it will be taken from /testRequest top level object)")
        @JsonProperty("client_uuid")
        private String clientUuid;

        @ApiModelProperty(notes = "Maximum delay between 2 tests in seconds")
        @JsonProperty("max_delay")
        private Integer maxDelay;

        @ApiModelProperty(notes = "Maximum movement between 2 tests in meters")
        @JsonProperty("max_movement")
        private Integer maxMovement;

        @ApiModelProperty(notes = "How many tets should be executed")
        @JsonProperty("max_tests")
        private Integer maxTests;

        @ApiModelProperty(notes = "Number of the test")
        @JsonProperty("test_counter")
        private Integer testCounter;

        @Deprecated
        @JsonProperty("text_counter")
        @ApiModelProperty(notes = "This is a legacy parent of `test_counter` property. Please do not use it")
        private Integer textCounter;

        @ApiModelProperty(notes = "Loop UUID of the test series with first test will be generated, so first test will be null there in request and new will be generated", example = "37e57f4e-25df-4bcf-9151-9f6eff311279")
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

        @ApiModelProperty(notes = "True, if the client can handle the RMBThttp protocol")
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
