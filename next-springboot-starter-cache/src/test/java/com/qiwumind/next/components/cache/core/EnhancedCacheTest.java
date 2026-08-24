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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiwumind.next.components.cache.autoconfigure.CacheConfiguration;
import com.qiwumind.next.components.cache.core.handler.CaffeineCache;
import com.qiwumind.next.components.common.dto.BaseDTO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EnhancedCacheTest {

    public static void main(String[] args) {
        CacheConfiguration cacheConfiguration=new CacheConfiguration();
//
        com.github.benmanes.caffeine.cache.Cache<String, String> rowcellcache = Caffeine.newBuilder()
                // 设置过期时间
                .expireAfterWrite(cacheConfiguration.getExpireAfterWrite(), TimeUnit.SECONDS)
                // 设置访问过期时间
                .expireAfterAccess(cacheConfiguration.getExpireAfterAccess(), TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(cacheConfiguration.getInitialCapacity())
                // 缓存的最大条数
                .maximumSize(cacheConfiguration.getMaximumSize())
                .build();
        CaffeineCache caffeineCache=new CaffeineCache(rowcellcache);


//        new RedisCache(jedisCache, cacheConfiguration.getRedisExpireTime());


        List<Cache> chain = new ArrayList<>();
        chain.add(caffeineCache);
//        chain.add(redisCache);
        CacheChain cacheChain=new CacheChain(chain);

        EnhancedCache enhancedCache= new EnhancedCache(cacheChain);

        List<CacheConfiguration> list = new ArrayList<>();
        list.add(cacheConfiguration);
        boolean flag=enhancedCache.cache("demo", list);
        System.out.println(flag);
        List<CacheConfiguration> res= enhancedCache.queryCache("demo",List.class);

        System.out.println("res="+res);
    }

    @Test
    public void test(){
        String json=" {\"userId\":1,\"deptId\":103,\"token\":\"d60c726e-f844-4c01-99e7-dcb3af31f27f\",\"loginTime\":1776067810170,\"expireTime\":1776069610170,\"ipaddr\":\"127.0.0.1\",\"loginLocation\":\"内网IP\",\"browser\":\"Chrome 130\",\"os\":\"Mac OS >=10.15.7\",\"permissions\":[\"*:*:*\"],\"user\":{\"createBy\":\"admin\",\"createTime\":\"2026-04-06 22:27:54\",\"remark\":\"管理员\",\"userId\":1,\"deptId\":103,\"userName\":\"admin\",\"nickName\":\"qiwumind\",\"email\":\"ry@163.com\",\"phonenumber\":\"15888888888\",\"sex\":\"1\",\"avatar\":\"\",\"status\":\"0\",\"delFlag\":\"0\",\"loginIp\":\"127.0.0.1\",\"loginDate\":\"2026-04-13 16:01:07\",\"pwdUpdateDate\":\"2026-04-07 06:27:54\",\"dept\":{\"deptId\":103,\"parentId\":101,\"ancestors\":\"0,100,101\",\"deptName\":\"研发部门\",\"orderNum\":1,\"leader\":\"若依\",\"status\":\"0\",\"children\":[]},\"roles\":[{\"roleId\":1,\"roleName\":\"超级管理员\",\"roleKey\":\"admin\",\"roleSort\":1,\"dataScope\":\"1\",\"menuCheckStrictly\":false,\"deptCheckStrictly\":false,\"status\":\"0\",\"flag\":false,\"admin\":true}],\"admin\":true},\"enabled\":true,\"accountNonExpired\":true,\"accountNonLocked\":true,\"username\":\"admin\",\"authorities\":[{\"authority\":\"*:*:*\"}],\"password\":\"$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2\",\"credentialsNonExpired\":true}   ";
        Map map=BaseDTO.fromJson(json, Map.class);
        System.out.println("map="+map);
    }

}