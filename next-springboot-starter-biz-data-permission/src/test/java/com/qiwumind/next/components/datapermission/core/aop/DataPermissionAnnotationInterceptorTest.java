///*
// * MIT License
// *
// * Copyright (c) 2026 qiwumind
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.  Author: liks
// * Email: 307039176@qq.com
// */
//
//package com.qiwumind.next.components.datapermission.core.aop;
//
//import cn.hutool.core.collection.CollUtil;
//import com.qiwumind.next.components.datapermission.core.annotation.DataPermission;
//import com.qiwumind.next.components.test.core.ut.BaseMockitoUnitTest;
//import org.aopalliance.intercept.MethodInvocation;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import java.lang.reflect.Method;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
///**
// * {@link DataPermissionAnnotationInterceptor} 的单元测试
// * @author qiwumind
// */
//public class DataPermissionAnnotationInterceptorTest extends BaseMockitoUnitTest {
//
//    @InjectMocks
//    private DataPermissionAnnotationInterceptor interceptor;
//
//    @Mock
//    private MethodInvocation methodInvocation;
//
//    @BeforeEach
//    public void setUp() {
//        interceptor.getDataPermissionCache().clear();
//    }
//
//    @Test // 无 @DataPermission 注解
//    public void testInvoke_none() throws Throwable {
//        // 参数
//        mockMethodInvocation(TestNone.class);
//
//        // 调用
//        Object result = interceptor.invoke(methodInvocation);
//        // 断言
//        assertEquals("none", result);
//        assertEquals(1, interceptor.getDataPermissionCache().size());
//        assertTrue(CollUtil.getFirst(interceptor.getDataPermissionCache().values()).enable());
//    }
//
//    @Test // 在 Method 上有 @DataPermission 注解
//    public void testInvoke_method() throws Throwable {
//        // 参数
//        mockMethodInvocation(TestMethod.class);
//
//        // 调用
//        Object result = interceptor.invoke(methodInvocation);
//        // 断言
//        assertEquals("method", result);
//        assertEquals(1, interceptor.getDataPermissionCache().size());
//        assertFalse(CollUtil.getFirst(interceptor.getDataPermissionCache().values()).enable());
//    }
//
//    @Test // 在 Class 上有 @DataPermission 注解
//    public void testInvoke_class() throws Throwable {
//        // 参数
//        mockMethodInvocation(TestClass.class);
//
//        // 调用
//        Object result = interceptor.invoke(methodInvocation);
//        // 断言
//        assertEquals("class", result);
//        assertEquals(1, interceptor.getDataPermissionCache().size());
//        assertFalse(CollUtil.getFirst(interceptor.getDataPermissionCache().values()).enable());
//    }
//
//    private void mockMethodInvocation(Class<?> clazz) throws Throwable {
//        Object targetObject = clazz.newInstance();
//        Method method = targetObject.getClass().getMethod("echo");
//        when(methodInvocation.getThis()).thenReturn(targetObject);
//        when(methodInvocation.getMethod()).thenReturn(method);
//        when(methodInvocation.proceed()).then(invocationOnMock -> method.invoke(targetObject));
//    }
//
//    static class TestMethod {
//
//        @DataPermission(enable = false)
//        public String echo() {
//            return "method";
//        }
//
//    }
//
//    @DataPermission(enable = false)
//    static class TestClass {
//
//        public String echo() {
//            return "class";
//        }
//
//    }
//
//    static class TestNone {
//
//        public String echo() {
//            return "none";
//        }
//
//    }
//
//}
