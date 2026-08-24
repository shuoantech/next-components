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



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 如果file属性format="unfixed",则可以忽略fill、filltext、length<br>
 * 如果file属性format="fixed",fill、filltext必填 <br>
 * <br>
 * 如果field为string,则默认point="I",fill="N",filltext="N"<br>
 * 如果field为decimal,则默认point="N",fill="L",filltext="0"<br>
 * 如果field为number,则默认point="N",fill="N",filltext="N"<br>
 */
@Setter
@Getter
@ToString
public class XmlFile {
    private String          xmlFileName;
    private XmlFileProperty xmlFileProperty;
    private List<Field>     fields;
}
