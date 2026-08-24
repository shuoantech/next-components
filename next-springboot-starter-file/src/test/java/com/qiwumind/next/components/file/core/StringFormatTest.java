package com.qiwumind.next.components.file.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.qiwumind.next.components.file.core.enums.FieldEnum;
import com.qiwumind.next.components.file.core.enums.FillEnum;
import com.qiwumind.next.components.file.core.enums.FillTextEnum;
import com.qiwumind.next.components.file.core.enums.FormatEnum;
import com.qiwumind.next.components.file.core.enums.PointEnum;
import com.qiwumind.next.components.file.core.valueobject.Field;

/**
 * 定长/分隔符报文解析工具单元测试（纯逻辑，不依赖外部服务）。
 */
class StringFormatTest {

    private Field field(String property, FieldEnum type, int length, FillEnum fill, FillTextEnum fillText,
                        PointEnum point) {
        Field f = new Field();
        f.setProperty(property);
        f.setFieldEnum(type);
        f.setLength(length);
        f.setFill(fill);
        f.setFilltext(fillText);
        f.setPoint(point == null ? PointEnum.IGNORE : point);
        return f;
    }

    @Test
    void read_unFixed_splitsByDelimiter() {
        List<Field> fields = Arrays.asList(
                field("name", FieldEnum.STRING, 0, FillEnum.NO, FillTextEnum.NO, PointEnum.IGNORE),
                field("age", FieldEnum.NUMBER, 0, FillEnum.NO, FillTextEnum.NO, PointEnum.IGNORE),
                field("city", FieldEnum.STRING, 0, FillEnum.NO, FillTextEnum.NO, PointEnum.IGNORE));
        String line = "alice|30|beijing";
        var map = StringFormat.read(line, FormatEnum.UN_FIXED, "|", fields);
        assertEquals("alice", map.get("name"));
        assertEquals("30", map.get("age"));
        assertEquals("beijing", map.get("city"));
    }

    @Test
    void read_unFixed_blankSpacer_throws() {
        List<Field> fields = Arrays.asList(
                field("name", FieldEnum.STRING, 0, FillEnum.NO, FillTextEnum.NO, PointEnum.IGNORE));
        assertThrows(IllegalArgumentException.class,
                () -> StringFormat.read("alice", FormatEnum.UN_FIXED, " ", fields));
    }

    @Test
    void format_fixed_rightPadWithZero() {
        Field f = field("code", FieldEnum.NUMBER, 6, FillEnum.RIGHT, FillTextEnum.NUMBER_0, PointEnum.IGNORE);
        assertEquals("123000", StringFormat.format("123", FormatEnum.FIXED, f));
    }

    @Test
    void format_fixed_leftPadWithSpace() {
        Field f = field("name", FieldEnum.STRING, 5, FillEnum.LEFT, FillTextEnum.SPACE, PointEnum.IGNORE);
        assertEquals("  abc", StringFormat.format("abc", FormatEnum.FIXED, f));
    }

    @Test
    void format_valueLongerThanLength_throws() {
        Field f = field("code", FieldEnum.NUMBER, 2, FillEnum.RIGHT, FillTextEnum.NUMBER_0, PointEnum.IGNORE);
        assertThrows(IllegalArgumentException.class, () -> StringFormat.format("12345", FormatEnum.FIXED, f));
    }
}
