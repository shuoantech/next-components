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

package com.qiwumind.next.components.crypto.core.license;

import cn.hutool.crypto.digest.DigestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 硬件信息获取工具类
 */
public class HardwareUtils {
    private static final Logger logger = LoggerFactory.getLogger(HardwareUtils.class);

    /**
     * 获取 CPU 序列号
     */
    public static String getCpuSerial() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            return processor.getProcessorIdentifier().getProcessorID();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * 获取所有网卡的 MAC 地址
     */
    public static List<String> getMacAddresses() {
        List<String> macList = new ArrayList<>();
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            List<NetworkIF> networkIFs = hardware.getNetworkIFs();

            for (NetworkIF networkIF : networkIFs) {
                try {
                    // 获取 MAC 地址
                    String mac = networkIF.getMacaddr();
                    // 判断是否为有效的物理网卡
                    if (mac != null && !mac.isEmpty() && !"00:00:00:00:00:00".equals(mac)) {
                        // 排除虚拟网卡和回环网卡
                        String name = networkIF.getName();
                        String displayName = networkIF.getDisplayName();
                        // 常见的虚拟网卡名称特征
                        boolean isVirtual = false;
                        if (name != null) {
                            String lowerName = name.toLowerCase();
                            isVirtual = lowerName.contains("virtual")
                                    || lowerName.contains("vmware")
                                    || lowerName.contains("vbox")
                                    || lowerName.contains("docker")
                                    || lowerName.contains("veth")
                                    || lowerName.contains("br-")
                                    || lowerName.startsWith("veth");
                        }

                        if (displayName != null) {
                            String lowerDisplay = displayName.toLowerCase();
                            isVirtual = isVirtual || lowerDisplay.contains("virtual")
                                    || lowerDisplay.contains("vmware")
                                    || lowerDisplay.contains("virtualbox")
                                    || lowerDisplay.contains("hyper-v");
                        }
                        // 检查是否为回环地址
                        boolean isLoopback = mac.equals("00:00:00:00:00:00");

                        if (!isVirtual && !isLoopback) {
                            macList.add(mac);
                        }
                    }
                } catch (Exception e) {
                    logger.debug("获取网卡信息失败", e);
                }
            }

        } catch (Exception e) {
            // ignore
            logger.error("使用 OSHI 获取 MAC 地址失败", e);
        }
        // 方法2：降级方案，使用 Java 原生 API
        if (macList.isEmpty()) {
            macList.add(getMachineCode());
        }
        // 去重
        return macList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取系统指纹
     */
    public static String getFingerprint() {
        String cpuSerial = getCpuSerial();
        List<String> macs = getMacAddresses();
        String macStr = macs.stream().collect(Collectors.joining(","));
        return cpuSerial + "|" + macStr;
    }

    /**
     * 验证硬件信息是否匹配
     */
    public static boolean validateHardware(String expectedFingerprint) {
        if (expectedFingerprint == null || expectedFingerprint.isEmpty()) {
            return true; // 如果没有绑定硬件，则通过
        }

        String currentFingerprint = getFingerprint();
        boolean matched = currentFingerprint.equals(expectedFingerprint);
        if (!matched) {
            logger.warn("硬件指纹不匹配 - 期望: {}, 实际: {}", expectedFingerprint, currentFingerprint);
        }

        return matched;
    }


    /**
     * 获取机器码（基于第一个可用网卡的 MAC 地址，MD5 加密）
     *
     * @return 32 位十六进制机器码
     */
    public static String getMachineCode() {
        try {
            // 获取第一个可用的物理网卡
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                // 跳过虚拟网卡、回环网卡
                if (ni.isLoopback() || ni.isVirtual() || ni.isPointToPoint()) {
                    continue;
                }
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    // 将 MAC 地址转为字符串
                    StringBuilder macStr = new StringBuilder();
                    for (byte b : mac) {
                        macStr.append(String.format("%02X:", b));
                    }
                    if (macStr.length() > 0) {
                        macStr.deleteCharAt(macStr.length() - 1);
                    }
                    logger.info("采集到 MAC 地址：{}，网卡：{}", macStr.toString(), ni.getName());
                    // 使用 MD5 加密生成 32 位机器码
                    String machineCode = DigestUtil.md5Hex(macStr.toString()).toUpperCase();
                    logger.info("机器码生成完成：{}", machineCode);
                    return machineCode;
                }
            }
            // 如果所有网卡都失败，使用主机名兜底
            logger.warn("未找到可用网卡，使用主机名生成机器码");
            String hostName = InetAddress.getLocalHost().getHostName();
            return DigestUtil.md5Hex(hostName).toUpperCase();

        } catch (Exception e) {
            logger.error("获取机器码失败，使用默认值", e);
            // 极端情况返回固定值
            return DigestUtil.md5Hex("UNKNOWN_MACHINE_" + System.currentTimeMillis()).toUpperCase();
        }
    }
}