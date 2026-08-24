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

package com.qiwumind.next.components.file.core.valueobject;



import com.qiwumind.next.components.file.core.enums.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.Charset;

/**
 * file：含有4个属性 <br>
 * desc--描述选填、<br>
 * spacer--间隔符号选填、生成文件时字段之间的间隔符号<br>
 * ignore -- 行尾是否忽略间隔符 Y是意味着不含有间隔符，N否意味着含有间隔符 , 默认Y不含有<br>
 * targetencoding--输出文件字符编码可选，默认utf-8 <br>
 * format--格式 ，unfixed不固定长度 默认unfixed； fixed固定长度<br>
 * count--单文本数据量，默认无限量<br>
 */
@Setter
@Getter
@ToString
public class XmlFileProperty {
    public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
    private String             spacer;
    private HeadEnum head            = HeadEnum.Y;
    private FormatEnum         format          = FormatEnum.UN_FIXED;
    private String             desc;
    private IgnoreEnum         ignore          = IgnoreEnum.Y;
    private String             targetencoding  = DEFAULT_CHARSET;
    private int                count           = Integer.MAX_VALUE;
}
