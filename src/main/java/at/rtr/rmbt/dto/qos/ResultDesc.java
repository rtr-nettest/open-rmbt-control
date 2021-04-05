/*******************************************************************************
 * Copyright 2013-2015 alladin-IT GmbH
 * Copyright 2013-2015 Rundfunk und Telekom Regulierungs-GmbH (RTR-GmbH)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.rtr.rmbt.dto.qos;

import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.utils.hstoreparser.Hstore;
import at.rtr.rmbt.utils.testscript.TestScriptInterpreter;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * holds the result string data
 *
 * @author lb
 */
@NoArgsConstructor
public class ResultDesc implements Comparable<ResultDesc> {

    public final static String STATUS_CODE_FAILURE = "fail";

    public final static String STATUS_CODE_SUCCESS = "ok";

    public final static String STATUS_CODE_INFO = "info";
    private String statusCode;
    private String key;
    private String value;
    private String parsedValue = null;
    private TestType testType;
    private AbstractResult<?> resultObject;
    private List<Long> testResultUidList = new ArrayList<>();
    private Hstore hstore;
    private ResultOptions options;

    /**
     * @param statusCode
     * @param key
     */
    public <T> ResultDesc(String statusCode, String key, AbstractResult<T> resultObject, Hstore hstore, ResultOptions options) {
        this.statusCode = statusCode;
        this.key = key;
        this.resultObject = resultObject;
        this.hstore = hstore;
        this.options = options;
    }

    public <T> ResultDesc(StatusCode statusCode, String key, AbstractResult<T> resultObject, Hstore hstore, ResultOptions options) {
        this(statusCode.name().toLowerCase(Locale.US), key, resultObject, hstore, options);
    }

    @JsonGetter("status")
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @JsonGetter("key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonGetter("desc")
    public String getParsedValue() {
        return (parsedValue != null ? parsedValue : (parsedValue = String.valueOf(TestScriptInterpreter.interprete(value, hstore, resultObject, true, options))));
    }

    @JsonGetter("test")
    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public AbstractResult<?> getResultObject() {
        return resultObject;
    }

    @JsonGetter("uid")
    public List<Long> getTestResultUidList() {
        return testResultUidList;
    }

    public void addTestResultUid(Long testUid) {
        testResultUidList.add(testUid);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ResultDesc [statusCode=" + statusCode + ", key=" + key
            + ", value=" + value + ", testType=" + testType + "]";
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ResultDesc o) {
        if (getValue() != null && o.getValue() != null && getStatusCode() != null && o.getStatusCode() != null) {
            final int s = this.getStatusCode().compareTo(o.getStatusCode());
            final int i = this.getParsedValue().compareTo(o.getParsedValue());

            if (s == 0 && i == 0) {
                return 0;
            }

            return i == 0 ? s : i;
        } else {
            if (this.getKey() == null || o.getKey() == null || getStatusCode() == null || o.getStatusCode() == null) {
                return 1;
            } else {
                final int i = this.getKey().compareTo(o.getKey());
                final int s = this.getStatusCode().compareTo(o.getStatusCode());
                if (i == 0 && s == 0) {
                    return (this.getResultObject().equals(o.getResultObject()) ? 0 : 1);
                }
                return i == 0 ? s : i;
            }
        }
    }

    public enum StatusCode {
        FAIL,
        OK,
        INFO
    }
}
