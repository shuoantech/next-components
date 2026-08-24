package com.qiwumind.next.components.groovy.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.qiwumind.next.components.groovy.constants.ExecutionStatus;

/**
 * Groovy 脚本执行结果单元测试（纯逻辑）。
 */
class EngineExecutorResultTest {

    @Test
    void success_withContext_holdsValueAndStatus() {
        EngineExecutorResult result = EngineExecutorResult.success("hello");
        assertEquals(ExecutionStatus.SUCCESS, result.getExecutionStatus());
        assertEquals("hello", result.getContext());
        assertNull(result.getException());
    }

    @Test
    void success_withExplicitStatus() {
        EngineExecutorResult result = EngineExecutorResult.success(ExecutionStatus.NO_SCRIPT, 42);
        assertEquals(ExecutionStatus.NO_SCRIPT, result.getExecutionStatus());
        assertEquals(42, (Object) result.getContext());
    }

    @Test
    void context_returnsTypedValue() {
        EngineExecutorResult result = EngineExecutorResult.success("payload");
        String value = result.context();
        assertEquals("payload", value);
    }

    @Test
    void failed_withThrowable_setsStatusAndException() {
        RuntimeException ex = new RuntimeException("boom");
        EngineExecutorResult result = EngineExecutorResult.failed(ex);
        assertEquals(ExecutionStatus.FAILED, result.getExecutionStatus());
        assertSame(ex, result.getException());
    }

    @Test
    void failed_withMessage_setsParamErrorStatus() {
        EngineExecutorResult result = EngineExecutorResult.failed("bad param");
        assertEquals(ExecutionStatus.PARAM_ERROR, result.getExecutionStatus());
        assertEquals("bad param", result.getErrorMessage());
    }

    @Test
    void success_result_isNotFailed() {
        EngineExecutorResult result = EngineExecutorResult.success("ok");
        assertFalse(result.getExecutionStatus() == ExecutionStatus.FAILED);
        assertTrue(result.getExecutionStatus() == ExecutionStatus.SUCCESS);
    }
}
