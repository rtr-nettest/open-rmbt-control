package at.rtr.rmbt.model.type;

import at.rtr.rmbt.enums.TestType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Maps {@link TestType} to the PostgreSQL enum column {@code qostest} (used by
 * {@code qos_test_objective.test} and {@code qos_test_type_desc.test}).
 *
 * <p>The stored labels are the lowercase {@link TestType#getValue() values} ({@code udp},
 * {@code tcp}, {@code website}, ...). They are bound with {@link Types#OTHER} so the PostgreSQL
 * driver sends them untyped and the server casts {@code text -> qostest}; binding them as a plain
 * {@code VARCHAR} fails with "column ... is of type qostest but expression is of type character
 * varying". Scoping this to the affected columns avoids a connection-wide {@code stringtype=unspecified}.
 */
public class TestTypeUserType implements UserType<TestType> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    /**
     * Returned class.
     *
     * @return the result
     */
    @Override
    public Class<TestType> returnedClass() {
        return TestType.class;
    }

    @Override
    public boolean equals(TestType x, TestType y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(TestType x) {
        return Objects.hashCode(x);
    }

    /**
     * Null safe get.
     *
     * @param rs the Rs
     * @param position the Position
     * @param session the Session
     * @param owner the Owner
     * @return the result
     * @throws SQLException if an error occurs
     */
    @Override
    public TestType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        final String value = rs.getString(position);
        return rs.wasNull() ? null : TestType.forValue(value);
    }

    /**
     * Null safe set.
     *
     * @param st the St
     * @param value the Value
     * @param index the Index
     * @param session the Session
     * @throws SQLException if an error occurs
     */
    @Override
    public void nullSafeSet(PreparedStatement st, TestType value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            // Types.OTHER + a String makes the PostgreSQL driver send it untyped, so the server
            // casts it to the qostest enum (a plain VARCHAR bind would be rejected).
            st.setObject(index, value.getValue(), Types.OTHER);
        }
    }

    /**
     * Deep copy.
     *
     * @param value the Value
     * @return the result
     */
    @Override
    public TestType deepCopy(TestType value) {
        return value; // enum constants are immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * Disassemble.
     *
     * @param value the Value
     * @return the result
     */
    @Override
    public Serializable disassemble(TestType value) {
        return value;
    }

    /**
     * Assemble.
     *
     * @param cached the Cached
     * @param owner the Owner
     * @return the result
     */
    @Override
    public TestType assemble(Serializable cached, Object owner) {
        return (TestType) cached;
    }
}
