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

package com.qiwumind.next.components.file.core;



import com.qiwumind.next.components.file.core.enums.*;
import com.qiwumind.next.components.file.core.valueobject.*;
import com.qiwumind.next.components.file.core.xml.XmlFileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class MysqlFileReadCommand implements FileReadCommand {

    @Override
    public XmlFile read() {
        final XmlFileProperty fileProperty = fileProperty();
        final XmlFile xmlFile = new XmlFile();
        xmlFile.setXmlFileProperty(fileProperty);
        xmlFile.setFields(fields());
        return xmlFile;
    }

    public abstract XmlFileProperty fileProperty();

    public abstract List<Field> fields();


    public XmlFileProperty fileProperty(final String desc, String format, String head, String ignore,
                                        final String spacer, Integer count, String targetencoding) {

        format = StringUtils.isBlank(format) ? FormatEnum.UN_FIXED.getName() : format;
        head = StringUtils.isBlank(head) ? HeadEnum.Y.getName() : head;
        ignore = StringUtils.isBlank(ignore) ? IgnoreEnum.Y.getName() : ignore;
        count = count == null ? Integer.MAX_VALUE : count;
        targetencoding = StringUtils.isBlank(targetencoding) ? XmlFileUtil.DEFAULT_CHARSET : targetencoding;
        final XmlFileProperty xmlFileProperty = new XmlFileProperty();
        xmlFileProperty.setDesc(desc);
        xmlFileProperty.setFormat(FormatEnum.tryParse(format));
        xmlFileProperty.setSpacer(spacer);
        xmlFileProperty.setHead(HeadEnum.tryParse(head));
        xmlFileProperty.setIgnore(IgnoreEnum.tryParse(ignore));
        xmlFileProperty.setTargetencoding(targetencoding);
        xmlFileProperty.setCount(count);
        return xmlFileProperty;

    }

}
