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
//import com.qiwumind.next.components.datapermission.core.annotation.DataPermission;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.mockito.Mockito.mock;
//
///**
// * {@link DataPermissionContextHolder} 的单元测试
// * @author qiwumind
// */
//class DataPermissionContextHolderTest {
//
//    @BeforeEach
//    public void setUp() {
//        DataPermissionContextHolder.clear();
//    }
//
//    @Test
//    public void testGet() {
//        // mock 方法
//        DataPermission dataPermission01 = mock(DataPermission.class);
//        DataPermissionContextHolder.add(dataPermission01);
//        DataPermission dataPermission02 = mock(DataPermission.class);
//        DataPermissionContextHolder.add(dataPermission02);
//
//        // 调用
//        DataPermission result = DataPermissionContextHolder.get();
//        // 断言
//        assertSame(result, dataPermission02);
//    }
//
//    @Test
//    public void testPush() {
//        // 调用
//        DataPermission dataPermission01 = mock(DataPermission.class);
//        DataPermissionContextHolder.add(dataPermission01);
//        DataPermission dataPermission02 = mock(DataPermission.class);
//        DataPermissionContextHolder.add(dataPermission02);
//        // 断言
//        DataPermission first = DataPermissionContextHolder.getAll().get(0);
//        DataPermission second = DataPermissionContextHolder.getAll().get(1);
//        assertSame(dataPermission01, first);
//        assertSame(dataPermission02, second);
//    }
//
//    @Test
//    public void testRemove() {
//        // mock 方法
//        DataPermission dataPermission01 = mock(DataPermission.class);
//        DataPermissionContextHolder.add(dataPermission01);
//        DataPermission dataPermission02 = mock(DataPermission.class);
//        DataPermissionContextHolder.add(dataPermission02);
//
//        // 调用
//        DataPermission result = DataPermissionContextHolder.remove();
//        // 断言
//        assertSame(result, dataPermission02);
//        assertEquals(1, DataPermissionContextHolder.getAll().size());
//    }
//
//}
