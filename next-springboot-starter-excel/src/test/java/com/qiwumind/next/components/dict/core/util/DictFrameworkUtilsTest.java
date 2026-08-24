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
//package com.qiwumind.next.components.dict.core.util;
//
//import cn.hutool.core.collection.ListUtil;
//import com.qiwumind.next.components.common.api.system.dict.DictDataCommonApi;
//import com.qiwumind.next.components.common.api.system.dict.dto.DictDataRespDTO;
//import com.qiwumind.next.components.dict.core.DictFrameworkUtils;
//import com.qiwumind.next.components.test.core.ut.BaseMockitoUnitTest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//
//import java.util.List;
//
//import static com.qiwumind.next.components.test.core.util.RandomUtils.randomPojo;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//
///**
// * {@link DictFrameworkUtils} 的单元测试
// */
//public class DictFrameworkUtilsTest extends BaseMockitoUnitTest {
//
//    @Mock
//    private DictDataCommonApi dictDataApi;
//
//    @BeforeEach
//    public void setUp() {
//        DictFrameworkUtils.init(dictDataApi);
//        DictFrameworkUtils.clearCache();
//    }
//
//    @Test
//    public void testParseDictDataLabel() {
//        // mock 数据
//        List<DictDataRespDTO> dictDatas = ListUtil.of(
//                randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("cat").setLabel("猫")),
//                randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("dog").setLabel("狗"))
//        );
//        // mock 方法
//        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(dictDatas);
//
//        // 断言返回值
//        assertEquals("狗", DictFrameworkUtils.parseDictDataLabel("animal", "dog"));
//    }
//
//    @Test
//    public void testParseDictDataValue() {
//        // mock 数据
//        List<DictDataRespDTO> dictDatas = ListUtil.of(
//                randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("cat").setLabel("猫")),
//                randomPojo(DictDataRespDTO.class, o -> o.setDictType("animal").setValue("dog").setLabel("狗"))
//        );
//        // mock 方法
//        when(dictDataApi.getDictDataList(eq("animal"))).thenReturn(dictDatas);
//
//        // 断言返回值
//        assertEquals("dog", DictFrameworkUtils.parseDictDataValue("animal", "狗"));
//    }
//
//}
