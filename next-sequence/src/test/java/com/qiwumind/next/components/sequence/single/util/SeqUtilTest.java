package com.qiwumind.next.components.sequence.single.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.qiwumind.next.components.sequence.single.exception.SingleSequenceException;

/**
 * 序列号工具单元测试（仅覆盖不依赖数据库的纯算法方法）。
 */
class SeqUtilTest {

    @Test
    void randomIntSequence_returnsPermutationOfRange() {
        int n = 20;
        int[] seq = SeqUtil.randomIntSequence(n);
        assertEquals(n, seq.length);

        Set<Integer> seen = new HashSet<>();
        for (int v : seq) {
            assertTrue(v >= 0 && v < n, "值应在 [0,n) 范围内: " + v);
            seen.add(v);
        }
        // 必须是 0..n-1 的一个排列（无重复、无遗漏）
        assertEquals(n, seen.size());
    }

    @Test
    void randomIntSequence_singleElement() {
        int[] seq = SeqUtil.randomIntSequence(1);
        assertEquals(1, seq.length);
        assertEquals(0, seq[0]);
    }

    @Test
    void randomIntSequence_negativeThrows() {
        assertThrows(SingleSequenceException.class, () -> SeqUtil.randomIntSequence(0));
        assertThrows(SingleSequenceException.class, () -> SeqUtil.randomIntSequence(-5));
    }

    @Test
    void getSeqInsertSql_buildsCorrectStatement() {
        String sql = SeqUtil.getSeqInsertSql("seq_table", "name", "value", "gmt_modified", "gmt_create");
        assertTrue(sql.startsWith("insert into seq_table("));
        assertTrue(sql.contains("name"));
        assertTrue(sql.contains("value"));
        assertTrue(sql.endsWith("values(?,?,?,?)"));
    }

    @Test
    void getSeqSelectSql_buildsCorrectStatement() {
        String sql = SeqUtil.getSeqSelectSql("seq_table", "name", "value");
        assertEquals("select value from seq_table where name = ?", sql);
    }

    @Test
    void getSeqUpdateSql_buildsCorrectStatement() {
        String sql = SeqUtil.getSeqUpdateSql("seq_table", "name", "value", "gmt_modified");
        assertTrue(sql.startsWith("update seq_table set value = ? ,"));
        assertTrue(sql.contains("where name = ? and value = ?"));
    }

    @Test
    void closeStatement_handlesNullSafely() {
        // 不应抛异常
        SeqUtil.closeStatement(null);
        SeqUtil.closeConnection(null);
        SeqUtil.closeResultSet(null);
    }
}
