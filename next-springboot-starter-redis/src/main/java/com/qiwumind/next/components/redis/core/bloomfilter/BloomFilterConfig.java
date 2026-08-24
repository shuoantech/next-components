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

package com.qiwumind.next.components.redis.core.bloomfilter;



//
//
//
//
//
//
//
//
//
//
//
//
//
//
//package com.qiwumind.next.components.redis.bloomfilter;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
///**
// * 布隆过滤器配置管理类
// * 使用枚举实现单例模式，支持不同环境的配置管理
// * 从配置文件读取配置信息
// */
//public enum BloomFilterConfig {
//
//    /**
//     * 单例实例
//     */
//    INSTANCE;
//
//    /**
//     * 环境类型
//     */
//    public enum Environment {
//        DEV, TEST, PRE,PROD
//    }
//
//    private Environment currentEnv = Environment.DEV;
//    private Properties properties;
//
//    /**
//     * 构造函数
//     */
//    BloomFilterConfig() {
//        loadProperties();
//    }
//
//    /**
//     * 加载配置文件
//     */
//    private void loadProperties() {
//        properties = new Properties();
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
//            if (input != null) {
//                properties.load(input);
//                // 从配置文件读取当前环境
//                String activeProfile = properties.getProperty("spring.profiles.active", "dev");
//                switch (activeProfile) {
//                    case "test":
//                    case "sit":
//                        currentEnv = Environment.TEST;
//                        break;
//                    case "pre":
//                    case "uat":
//                        currentEnv = Environment.PRE;
//                        break;
//                    case "prd":
//                    case "prod":
//                        currentEnv = Environment.PROD;
//                        break;
//                    default:
//                        currentEnv = Environment.DEV;
//                        break;
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Failed to load application.properties: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取Redis连接URI
//     * @return Redis连接URI
//     */
//    public String getRedisUri() {
//        String key = "bloom.filter.redis.uri";
//        String value = properties.getProperty(key);
//        if (value != null) {
//            return value;
//        }
//        //  fallback to hardcoded values if property not found
//        switch (currentEnv) {
//            case DEV:
//                return "redis://redis-shzljpnfcuseuwdwz.redis.ivolces.com";
//            case TEST:
//                return "redis://test-redis:6379";
//            case PROD:
//                return "redis://prod-redis:6379";
//            default:
//                return "redis://localhost:6379";
//        }
//    }
//
//    /**
//     * 获取Redis键前缀
//     * @return Redis键前缀
//     */
//    public String getRedisKeyPrefix() {
//        String key = "bloom.filter.redis.keyPrefix";
//        String value = properties.getProperty(key);
//        return value != null ? value : "bloom_filter";
//    }
//
//    /**
//     * 获取默认的预期插入元素数量
//     * @return 默认预期插入元素数量
//     */
//    public long getDefaultExpectedInsertions() {
//        String key = "bloom.filter.default.expectedInsertions";
//        String value = properties.getProperty(key);
//        return value != null ? Long.parseLong(value) : 1000000;
//    }
//
//    /**
//     * 获取默认的期望误判率
//     * @return 默认期望误判率
//     */
//    public double getDefaultFalsePositiveProbability() {
//        String key = "bloom.filter.default.falsePositiveProbability";
//        String value = properties.getProperty(key);
//        return value != null ? Double.parseDouble(value) : 0.01;
//    }
//
//    /**
//     * 设置当前环境
//     * @param env 环境类型
//     */
//    public void setEnvironment(Environment env) {
//        this.currentEnv = env;
//    }
//
//    /**
//     * 获取当前环境
//     * @return 当前环境类型
//     */
//    public Environment getCurrentEnvironment() {
//        return currentEnv;
//    }
//
//    /**
//     * 重新加载配置
//     */
//    public void reload() {
//        loadProperties();
//    }
//}
