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
//package com.qiwumind.next.components.excel.core.convert;
//
//import cn.hutool.core.collection.ListUtil;
//import cn.idev.excel.metadata.data.ReadCellData;
//import cn.idev.excel.metadata.data.WriteCellData;
//import cn.idev.excel.metadata.property.ExcelContentProperty;
//import com.qiwumind.next.components.common.api.system.dict.DictDataCommonApi;
//import com.qiwumind.next.components.common.api.system.dict.dto.DictDataRespDTO;
//import com.qiwumind.next.components.dict.core.DictFrameworkUtils;
//import com.qiwumind.next.components.excel.core.annotations.DictFormat;
//import com.qiwumind.next.components.test.core.ut.BaseMockitoUnitTest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//
//import java.lang.reflect.Field;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//
//import static com.qiwumind.next.components.test.core.util.RandomUtils.randomPojo;
//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// * {@link MultiDictConvert} 的单元测试
// * @author qiwumind NaCl
// */
//public class MultiDictConvertTest extends BaseMockitoUnitTest {
//
//    private static final String DICT_TYPE = "test_dict";
//
//    @Mock
//    private DictDataCommonApi dictDataApi;
//
//    private MultiDictConvert convert;
//
//    @BeforeEach
//    public void setUp() {
//        convert = new MultiDictConvert();
//        DictFrameworkUtils.init(dictDataApi);
//        DictFrameworkUtils.clearCache();
//    }
//
//    @Test
//    public void testConvertToExcelData_string() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        WriteCellData<String> result = convert.convertToExcelData("1,2", mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("男、女", result.getStringValue());
//    }
//
//    @Test
//    public void testConvertToExcelData_single() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        WriteCellData<String> result = convert.convertToExcelData("1", mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("男", result.getStringValue());
//    }
//
//    @Test
//    public void testConvertToExcelData_order() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        WriteCellData<String> result = convert.convertToExcelData("2,1", mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("女、男", result.getStringValue());
//    }
//
//    @Test
//    public void testConvertToExcelData_null() {
//        // 调用
//        WriteCellData<String> result = convert.convertToExcelData(null, null, null);
//        // 断言
//        assertEquals("", result.getStringValue());
//    }
//
//    @Test
//    public void testConvertToExcelData_collection() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        WriteCellData<String> result = convert.convertToExcelData(ListUtil.of(1, 2),
//                mockContentProperty("listValues"), null);
//        // 断言
//        assertEquals("男、女", result.getStringValue());
//    }
//
//    @Test
//    public void testConvertToExcelData_array() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        WriteCellData<String> result = convert.convertToExcelData(new int[]{1, 2},
//                mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("男、女", result.getStringValue());
//    }
//
//    @Test
//    public void testConvertToExcelData_unknownValue() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        WriteCellData<String> result = convert.convertToExcelData("1,3", mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("", result.getStringValue());
//    }
//
//    @Test
//    public void testConvertToJavaData_string() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData("男、女"),
//                mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("1,2", result);
//    }
//
//    @Test
//    public void testConvertToJavaData_single() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData("男"),
//                mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("1", result);
//    }
//
//    @Test
//    public void testConvertToJavaData_blank() throws NoSuchFieldException {
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData(""),
//                mockContentProperty("stringValues"), null);
//        // 断言
//        assertEquals("", result);
//    }
//
//    @Test
//    public void testConvertToJavaData_blankList() throws NoSuchFieldException {
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData(""),
//                mockContentProperty("listValues"), null);
//        // 断言
//        assertTrue(result instanceof List);
//        assertTrue(((List<?>) result).isEmpty());
//    }
//
//    @Test
//    public void testConvertToJavaData_blankSet() throws NoSuchFieldException {
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData(""),
//                mockContentProperty("setValues"), null);
//        // 断言
//        assertTrue(result instanceof LinkedHashSet);
//        assertTrue(((Set<?>) result).isEmpty());
//    }
//
//    @Test
//    public void testConvertToJavaData_blankArray() throws NoSuchFieldException {
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData(""),
//                mockContentProperty("arrayValues"), null);
//        // 断言
//        assertArrayEquals(new Integer[0], (Integer[]) result);
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void testConvertToJavaData_list() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData("男、女"),
//                mockContentProperty("listValues"), null);
//        // 断言
//        assertEquals(ListUtil.of(1, 2), (List<Integer>) result);
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void testConvertToJavaData_set() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData("男，女"),
//                mockContentProperty("setValues"), null);
//        // 断言
//        assertEquals(new LinkedHashSet<>(Arrays.asList(1L, 2L)), (Set<Long>) result);
//    }
//
//    @Test
//    @SuppressWarnings("SimplifiableAssertion")
//    public void testConvertToJavaData_hashSet() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData("男，女"),
//                mockContentProperty("hashSetValues"), null);
//        // 断言
//        assertTrue(result instanceof HashSet);
//        assertEquals(new HashSet<>(Arrays.asList(1L, 2L)), result);
//    }
//
//    @Test
//    public void testConvertToJavaData_array() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData("男,女"),
//                mockContentProperty("arrayValues"), null);
//        // 断言
//        assertArrayEquals(new Integer[]{1, 2}, (Integer[]) result);
//    }
//
//    @Test
//    public void testConvertToJavaData_unknownLabel() throws NoSuchFieldException {
//        // mock 数据
//        mockDictDatas();
//
//        // 调用
//        Object result = convert.convertToJavaData(mockReadCellData("男、未知"),
//                mockContentProperty("stringValues"), null);
//        // 断言
//        assertNull(result);
//    }
//
//    private void mockDictDatas() {
////        List<DictDataRespDTO> dictDatas = ListUtil.of(
////                randomPojo(DictDataRespDTO.class, o -> o.setDictType(DICT_TYPE).setValue("1").setLabel("男")),
////                randomPojo(DictDataRespDTO.class, o -> o.setDictType(DICT_TYPE).setValue("2").setLabel("女"))
////        );
////        when(dictDataApi.getDictDataList(eq(DICT_TYPE))).thenReturn(dictDatas);
//    }
//
//    private static ReadCellData<?> mockReadCellData(String value) {
//        ReadCellData<?> readCellData = mock(ReadCellData.class);
//        when(readCellData.getStringValue()).thenReturn(value);
//        return readCellData;
//    }
//
//    private static ExcelContentProperty mockContentProperty(String fieldName) throws NoSuchFieldException {
//        Field field = TestExcelVO.class.getDeclaredField(fieldName);
//        ExcelContentProperty contentProperty = mock(ExcelContentProperty.class);
//        when(contentProperty.getField()).thenReturn(field);
//        return contentProperty;
//    }
//
//    static class TestExcelVO {
//
//        @DictFormat(DICT_TYPE)
//        private String stringValues;
//
//        @DictFormat(DICT_TYPE)
//        private List<Integer> listValues;
//
//        @DictFormat(DICT_TYPE)
//        private Set<Long> setValues;
//
//        @DictFormat(DICT_TYPE)
//        private HashSet<Long> hashSetValues;
//
//        @DictFormat(DICT_TYPE)
//        private Integer[] arrayValues;
//
//    }
//
//}
