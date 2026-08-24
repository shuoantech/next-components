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

package com.qiwumind.next.components.compute.core.dto;



import com.qiwumind.next.components.common.dto.BaseDTO;
import com.qiwumind.next.components.common.result.BaseResultType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;
/**
 * @version 1.0.0
 * @desc
 * @time 2020/5/7 21:23
 */
@Getter
@Setter
public class BaseResp<T> extends BaseDTO {

    private static final long serialVersionUID = -6020382017102785331L;

    /**
     * 请求流水号
     *
     * #reqNo
     */
    @Getter
    @Setter
    private String reqNo;

    /**
     * 返回码
     *
     * #code
     */
    @Setter
    @Getter
    private String respCode;

    /**
     * 返回消息
     *
     */
    @Setter
    @Getter
    private String respMsg;

    @Getter
    @Setter
    private BaseResultType resultType;

    /**
     * 业务数据
     */
    @Getter
    @Setter
    private T result;

    public BaseResp() {
    }

    public BaseResp(String reqNo) {
        this.reqNo = reqNo;
    }

    public BaseResp(String reqNo, BaseResultType resultType) {
        this.reqNo = reqNo;
        this.resultType = resultType;
        this.respCode = resultType.getCode();
        this.respMsg = resultType.getMessage();
    }

    public BaseResp(String reqNo, BaseResultType resultType, Object... args) {
        this.reqNo = reqNo;
        this.resultType = resultType;
        this.respCode = resultType.getCode();
        this.respMsg = MessageFormatter.arrayFormat(StringUtils.stripToEmpty(resultType.getMessage()), args)
                .getMessage();
    }


}
