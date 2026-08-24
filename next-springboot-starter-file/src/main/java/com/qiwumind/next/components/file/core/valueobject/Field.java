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
/**
 * filed：含有9个属性 <br>
 * fieldEnum--判断属性是字符、数字还是金额,默认string；string、decimal、number 三种类型<br>
 * name--对应的字段名必填<br>
 * property--对应的传值的map key，不写默认同name字段<br>
 * length--值长度必填，不填时需采用不固定长度<br>
 * value--值 基本不填<br>
 * desc--描述选填<br>
 * point--报文是否含有小数点，N，Y，I 默认I 忽略这个格式，如果是decimal则必须填写Y或N<br>
 * sort--排序 1-∞，如果填写，一定按照升序填写即可 ，默认从上到下依次递增 <br>
 * fill--根据固定长度是否补充内容L左面补充内容，R右面补充内容 ，N不补充 默认N不补充 <br>
 * filltext--填充内容默认N不填充 "S-填充空格","N-不填充","0-填充数字0"<br>
 */
@Setter
@Getter
@ToString
public class Field {
    private FieldEnum fieldEnum; //判断属性是字符、数字还是金额
    private String       name;
    private String       property;
    private Integer      length;
    private String       value;
    private String       desc;
    private PointEnum    point;
    private Integer      sort;
    private FillEnum     fill;
    private FillTextEnum filltext; //填充内容

}
