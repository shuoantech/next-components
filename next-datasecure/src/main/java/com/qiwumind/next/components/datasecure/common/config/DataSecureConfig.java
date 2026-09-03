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

package com.qiwumind.next.components.datasecure.common.config;




import com.qiwumind.next.components.datasecure.common.DataSecureConstants;
import com.qiwumind.next.components.datasecure.common.enums.SensitiveRulesEnum;
import com.qiwumind.next.components.datasecure.utils.SecretKeyUtil;
import com.qiwumind.next.components.datasecure.utils.SensitiveProcessUtils;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jun 8, 2017 3:28:15 PM
 */
@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DataSecureConfig {
    private String secretkey;
    private String secswitch = DataSecureConstants.IS_ENCRYPT_OPEN;//默认开

    @PostConstruct
    public void init() throws Exception {
        SecretKeyUtil.setSecretKey(this.secretkey);
        if (!DataSecureConstants.IS_ENCRYPT_OPEN.equals(this.secswitch)) {
            EncryptSwitchConfig.setEncryptFlag(false);
        }
        log.info("secretkey={},secswitch = {}", SensitiveProcessUtils.shield(SensitiveRulesEnum.NAME, this.secretkey),
                EncryptSwitchConfig.getEncryptFlag());
    }
}
