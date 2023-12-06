package at.rtr.rmbt.util;

import at.rtr.rmbt.utils.FormatUtils;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FormatUtilsTest {

    @Test
    public void testSpeedFormatting() {
        String s = FormatUtils.formatSpeed(111);
        assertEquals("0.11",s);

        s = FormatUtils.formatSpeed(1111);
        assertEquals("1.1",s);

        s = FormatUtils.formatSpeed(11111);
        assertEquals("11",s);

        s = FormatUtils.formatSpeed(111111);
        assertEquals("111",s);

        s = FormatUtils.formatSpeed(1111111);
        assertEquals("1,111",s);

        s = FormatUtils.formatSpeed(11111111);
        assertEquals("11,111",s);
    }

}