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



import com.google.common.collect.Lists;
import com.qiwumind.next.components.file.core.enums.*;
import com.qiwumind.next.components.file.core.valueobject.Field;
import com.qiwumind.next.components.file.core.valueobject.XmlFile;
import com.qiwumind.next.components.file.core.valueobject.XmlFileProperty;
import com.qiwumind.next.components.file.core.xml.XmlFileUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class XmlFileReadCommand implements FileReadCommand {
    InputStreamReader inputStreamReader;

    public XmlFileReadCommand(final InputStreamReader inputStreamReader) {
        this.inputStreamReader = inputStreamReader;
    }

    @Override
    public XmlFile read() {
        try {
            return xmlread(inputStreamReader);
        } catch (final DocumentException e) {
            log.info("加载xml文件出现异常{}", e);
        }
        return null;
    }

    /**
     * 读取xml
     * 
     * @param inputStreamReader
     * @return
     * @throws DocumentException
     */
    public XmlFile xmlread(final InputStreamReader inputStreamReader) throws DocumentException {
        //第一步：获得一个解析器
        final SAXReader saxreader = new SAXReader();
        //第二步：指定解析的XML文件
        final Document document = saxreader.read(inputStreamReader);
        return xmlFile(document.getRootElement());

    }

    /**
     * @param rootElement
     * @return
     */
    public XmlFile xmlFile(final Element rootElement) {
        final XmlFileProperty fileProperty = xmlFileProperty(rootElement);
        final XmlFile xmlFile = new XmlFile();
        xmlFile.setXmlFileProperty(fileProperty);
        xmlFile.setFields(xmlFields(rootElement.elementIterator(), fileProperty));
        return xmlFile;

    }

    /**
     * @param rootElement
     * @return
     */
    private XmlFileProperty xmlFileProperty(final Element rootElement) {
        final String desc = rootElement.attributeValue("desc");
        final String format = StringUtils.isBlank(rootElement.attributeValue("format")) ? FormatEnum.UN_FIXED.getName()
                : rootElement.attributeValue("format");
        final String head = StringUtils.isBlank(rootElement.attributeValue("head")) ? HeadEnum.Y.getName()
                : rootElement.attributeValue("head");
        final String ignore = StringUtils.isBlank(rootElement.attributeValue("ignore")) ? IgnoreEnum.Y.getName()
                : rootElement.attributeValue("ignore");
        final String spacer = rootElement.attributeValue("spacer");
        final int count = StringUtils.isBlank(rootElement.attributeValue("count")) ? Integer.MAX_VALUE : Integer
                .valueOf(rootElement.attributeValue("count"));
        final String targetencoding = StringUtils.isBlank(rootElement.attributeValue("targetencoding")) ?
                XmlFileUtil.DEFAULT_CHARSET
                : rootElement.attributeValue("targetencoding");
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

    /**
     * 解析xml配置文件，不含value值
     * 
     * @return
     */
    private List<Field> xmlFields(final Iterator iterator, final XmlFileProperty fileProperty) {
        final List<Field> fields = Lists.newLinkedList();
        Field field = null;
        int so = 1;
        while (iterator.hasNext()) {
            field = new Field();
            final Element e = (Element) iterator.next();
            final String name = e.attributeValue("name");
            final String value = e.attributeValue("value");
            final String property = StringUtils.isBlank(e.attributeValue("property")) ? name : e
                    .attributeValue("property");
            final String desc = e.attributeValue("desc");
            final int sort = StringUtils.isBlank(e.attributeValue("sort")) ? so : Integer.valueOf(e
                    .attributeValue("sort"));
            if (fileProperty.getFormat() == FormatEnum.UN_FIXED) {
                field.setPoint(PointEnum.IGNORE);
                field.setFill(FillEnum.NO);
                field.setFilltext(FillTextEnum.NO);
                field.setFieldEnum(FieldEnum.STRING);
                field.setLength(null);
            } else {
                final int length = StringUtils.isBlank(e.attributeValue("length")) ? 0 : Integer.valueOf(e
                        .attributeValue("length"));
                final FieldEnum fieldEnum = FieldEnum.tryParse(e.getName());
                final String point = e.attributeValue("point");
                final String fill = e.attributeValue("fill");
                final String filltext = e.attributeValue("filltext");
                if (fieldEnum == FieldEnum.DECIMAL) {
                    field.setPoint(PointEnum.tryParse(StringUtils.isBlank(point) ? PointEnum.POINT_N.getName() : point));
                    field.setFill(FillEnum.tryParse(StringUtils.isBlank(fill) ? FillEnum.LEFT.getName() : fill));
                    field.setFilltext(FillTextEnum.tryParse(StringUtils.isBlank(filltext) ? FillTextEnum.NUMBER_0
                            .getName() : filltext));
                } else if (fieldEnum == FieldEnum.NUMBER) {
                    field.setPoint(PointEnum.tryParse(StringUtils.isBlank(point) ? PointEnum.POINT_N.getName() : point));
                    field.setFill(FillEnum.tryParse(StringUtils.isBlank(fill) ? FillEnum.LEFT.getName() : fill));
                    field.setFilltext(FillTextEnum.tryParse(StringUtils.isBlank(filltext) ? FillTextEnum.NUMBER_0
                            .getName() : filltext));
                } else if (fieldEnum == FieldEnum.STRING) {
                    field.setPoint(PointEnum.tryParse(StringUtils.isBlank(point) ? PointEnum.IGNORE.getName() : point));
                    field.setFill(FillEnum.tryParse(StringUtils.isBlank(fill) ? FillEnum.RIGHT.getName() : fill));
                    field.setFilltext(FillTextEnum.tryParse(StringUtils.isBlank(filltext) ? FillTextEnum.SPACE
                            .getName() : filltext));
                }
                field.setFieldEnum(FieldEnum.tryParse(fieldEnum.getName()));
                field.setLength(length);
            }
            field.setProperty(property);
            field.setDesc(desc);
            field.setName(name);
            field.setValue(value);
            field.setSort(sort);

            fields.add(field);
            so++;
        }
        return fields;
    }
}
