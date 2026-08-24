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

package com.qiwumind.next.components.cache.core.handler;


import com.qiwumind.next.components.cache.core.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;

/**
 * 类描述：
 *
 * @author KS.Li 2021年10月11日 下午2:12:00
 */
@Slf4j
public class CaffeineCache implements Cache, Ordered {
    private com.github.benmanes.caffeine.cache.Cache<String, String> cache;

    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<String, String> cache) {
        this.cache = cache;
    }

    @Override
    public int getOrder() {
        return 1;
    }


    @Override
    public  Boolean cache(String key, String v) {
        cache.put(key, v);
        log.info("*** add CaffeineCache key:{} vlaue={}***", key, v);
        return true;
    }

    @Override
    public Boolean del(String key) {
        return false;
    }


    @Override
    public String queryCache(String key) {
        String keyValue = cache.getIfPresent(key);
        if (StringUtils.isNotBlank(keyValue)) {
            log.info("***use CaffeineCache value={} ***", keyValue);
            return keyValue;
        }
        return null;
    }

    @Override
    public Boolean queryCacheExist(String key) {
        return false;
    }


}
