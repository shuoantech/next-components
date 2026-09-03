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
//package com.qiwumind.next.components.common.util.collection;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import lombok.EqualsAndHashCode;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//import java.util.function.BiFunction;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
///**
// * {@link CollectionUtils} 的单元测试
// */
//public class CollectionUtilsTest {
//
//    @Getter
//    @Setter
//    @ToString
//    @EqualsAndHashCode
//    @AllArgsConstructor
//    private static class Dog {
//
//        private Integer id;
//        private String name;
//        private String code;
//
//    }
//
//    @Test
//    public void testDiffList() {
//        // 准备参数
//        Collection<Dog> oldList = Arrays.asList(
//                new Dog(1, "花花", "hh"),
//                new Dog(2, "旺财", "wc")
//        );
//        Collection<Dog> newList = Arrays.asList(
//                new Dog(null, "花花2", "hh"),
//                new Dog(null, "小白", "xb")
//        );
//        BiFunction<Dog, Dog, Boolean> sameFunc = (oldObj, newObj) -> {
//            boolean same = oldObj.getCode().equals(newObj.getCode());
//            // 如果相等的情况下，需要设置下 id，后续好更新
//            if (same) {
//                newObj.setId(oldObj.getId());
//            }
//            return same;
//        };
//
//        // 调用
//        List<List<Dog>> result = CollectionUtils.diffList(oldList, newList, sameFunc);
//        // 断言
//        assertEquals(result.size(), 3);
//        // 断言 create
//        assertEquals(result.get(0).size(), 1);
//        assertEquals(result.get(0).get(0), new Dog(null, "小白", "xb"));
//        // 断言 update
//        assertEquals(result.get(1).size(), 1);
//        assertEquals(result.get(1).get(0), new Dog(1, "花花2", "hh"));
//        // 断言 delete
//        assertEquals(result.get(2).size(), 1);
//        assertEquals(result.get(2).get(0), new Dog(2, "旺财", "wc"));
//    }
//
//}
