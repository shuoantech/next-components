/*
 * MIT License
 *
 * Copyright (c) 2026 qiwumind
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  Author: liks
 * Email: 307039176@qq.com
 */

package com.qiwumind.next.components.cache.core;


import com.alibaba.fastjson2.JSON;
import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.common.util.defalter.DeflaterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 类描述：
 *
 * @author KS.Li 2021年10月12日 下午1:39:18
 */
public class EnhancedCache {
    private final Logger log = LoggerFactory.getLogger(EnhancedCache.class);

    private CacheChain cacheChain;

    public EnhancedCache(CacheChain cacheChain) {
        this.cacheChain = cacheChain;
    }

    /**
     * 查询结果数据是否存在,如都不存在，则返回fasle，只要存在返回true
     *
     * @return
     */
    public Boolean queryCacheExist(String key) {
        Boolean flag = false;
        for (Cache cache : cacheChain) {
            flag = cache.queryCacheExist(key);
            if (flag) {
                return true;
            }
        }
        return flag;
    }
    public Boolean delCache(String key) {
        Boolean flag = false;
        for (Cache cache : cacheChain) {
            flag = cache.del(key);
            if (flag) {
                return true;
            }
        }
        return flag;
    }
    /**
     * 查询缓存链
     *
     * @return
     */
    public <V> V queryCache(String key, Class<V> clazz) {

        for (Cache cache : cacheChain) {
            String keyValue = cache.queryCache(key);
            if (keyValue != null) {
                String vla = DeflaterUtils.unzipString(keyValue);
                V v = null;
                // 根据不同类型处理
                if (clazz == Map.class) {
                    // 直接返回Map
                    v = (V) BaseDTO.fromJson(vla, Map.class);
                }else if (clazz == Boolean.class) {
                    // 返回字符串
                    v = (V) Boolean.valueOf(vla);
                }else if (clazz == String.class) {
                    // 返回字符串
                    v = (V) vla;
                } else if (clazz == List.class) {
                    // 返回List（需要知道具体类型，建议使用TypeReference）
                    v = (V) BaseDTO.fromJson(vla, List.class);
                } else {
                    // 转为JavaBean
                    Map map = BaseDTO.fromJson(vla, Map.class);
                    v = JSON.parseObject(JSON.toJSONString(map), clazz);
                }
                log.info("回溯CaffeineCache缓存成功 ={}", vla);
                return v;
            }
        }
        return null;
    }

    /**
     * 保存缓存链
     */
    public <V> Boolean cache(String key, V v) {
        String str = BaseDTO.toJson(v);
        String value = DeflaterUtils.zipString(str);
        Boolean flag = true;
        for (Cache cache : cacheChain) {
            flag = cache.cache(key, value);
            if (!flag) {
                log.error(" cache add exception ={} key={}", cache, key);
            }
        }
        return flag;
    }
}
