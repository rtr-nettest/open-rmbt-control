package at.rtr.rmbt.dto.qos;

import at.rtr.rmbt.dto.qos.annotations.NonComparableField;
import at.rtr.rmbt.utils.hstoreparser.annotation.HstoreKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractResult<T> {

    public final static String COMPARATOR_EQUALS = "eq";

    public final static String COMPARATOR_NOT_EQUALS = "ne";

    public final static String COMPARATOR_GREATER_THEN = "gt";

    public final static String COMPARATOR_GREATER_OR_EQUALS = "ge";

    public final static String COMPARATOR_LOWER_THEN = "lt";

    public final static String COMPARATOR_LOWER_OR_EQUALS = "le";

    public final static String COMPARATOR_CONTAINS = "contains";

    public final static String RESULT_TYPE_INFO = "info";

    public final static String RESULT_TYPE_DEFAULT = "default";

    public final static String BEHAVIOUR_ABORT = "abort";

    public final static String BEHAVIOUR_NOTHING = "nothing";

    @NonComparableField
    @JsonIgnore
    protected Map<String, Object> resultMap = new HashMap<>();

    @JsonProperty("operator")
    @HstoreKey("operator")
    @NonComparableField
    protected String operator;

    @JsonProperty("on_failure")
    @HstoreKey("on_failure")
    @NonComparableField
    protected String onFailure;

    @JsonProperty("on_success")
    @HstoreKey("on_success")
    @NonComparableField
    protected String onSuccess;

    @JsonProperty("evaluate")
    @HstoreKey("evaluate")
    protected Object evaluate;

    @JsonProperty("end_time_ns")
    @HstoreKey("end_time_ns")
    protected Long endTimeNs;

    @JsonProperty("start_time_ns")
    @HstoreKey("start_time_ns")
    protected Long startTimeNs;

    @JsonProperty("duration_ns")
    @HstoreKey("duration_ns")
    protected Long testDuration;

    ///////////////////////////////////////
    //	Advanced implementations:
    ///////////////////////////////////////

    @JsonProperty("success_condition")
    @HstoreKey("success_condition")
    @NonComparableField
    protected String successCondition = "true";

    /**
     * can hold following values:
     * <ul>
     * <li>default: {@link AbstractResult#RESULT_TYPE_DEFAULT}</li>
     * <li>{@link AbstractResult#RESULT_TYPE_INFO}: Will not count as a success or failure. The status of the result will be "info"</li>
     * </ul>
     */
    @JsonProperty("failure_type")
    @HstoreKey("failure_type")
    @NonComparableField
    protected String failureType = RESULT_TYPE_DEFAULT;

    /**
     * @see AbstractResult#failureType
     */
    @JsonProperty("success_type")
    @HstoreKey("success_type")
    @NonComparableField
    protected String successType = RESULT_TYPE_DEFAULT;

    /**
     * the behaviour of the evaluation if the test fails
     * <ul>
     * <li>default: {@link AbstractResult#BEHAVIOUR_NOTHING}</li>
     * <li>{@link AbstractResult#BEHAVIOUR_ABORT}: Will cause the evaluation to abort. All following expected results will be ignored.</li>
     * </ul>
     */
    @JsonProperty("on_failure_behaviour")
    @HstoreKey("on_failure_behaviour")
    @NonComparableField
    protected String onFailureBehaivour = BEHAVIOUR_NOTHING;

    /**
     * the behaviour of the evaluation if the test succeeds
     *
     * @see AbstractResult#onFailureBehaivour
     */
    @JsonProperty("on_success_behaviour")
    @HstoreKey("on_success_behaviour")
    @NonComparableField
    protected String onSuccessBehaivour = BEHAVIOUR_NOTHING;

    /**
     * Test evaluation priority.
     * The lower the value the higher the priority
     * <br>
     * default: {@link Integer#MAX_VALUE}
     */
    @JsonProperty("priority")
    @HstoreKey("priority")
    @NonComparableField
    protected Integer priority = Integer.MAX_VALUE;

    @Override
    public String toString() {
        return "AbstractResult [operator=" + operator + ", onFailure="
            + onFailure + ", onSuccess=" + onSuccess + ", evaluate="
            + evaluate + ", endTimeNs=" + endTimeNs + ", startTimeNs="
            + startTimeNs + ", testDuration=" + testDuration
            + ", successCondition=" + successCondition + ", failureType="
            + failureType + ", successType=" + successType
            + ", onFailureBehaivour=" + onFailureBehaivour
            + ", onSuccessBehaivour=" + onSuccessBehaivour + ", priority="
            + priority + "]";
    }

}
