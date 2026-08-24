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

package com.qiwumind.next.components.file.core.xml;



import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qiwumind.next.components.file.core.enums.*;
import com.qiwumind.next.components.file.core.valueobject.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * xml文件解析和根据map传值生成文件
 *
 * @author liks 2019年4月17日 下午4:31:01
 */
public class XmlFileUtil {
    /**
     *
     */
    public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileUtil.class);

    /**
     * 读取xml
     *
     * @param fileName
     * @return
     */
    public static XmlFile xmlread(final String fileName) throws DocumentException {
        final File file = FileUtils.getFile(fileName);
        //第一步：获得一个解析器
        final SAXReader saxreader = new SAXReader();
        //第二步：指定解析的XML文件
        final Document document = saxreader.read(file);
        return xmlFile(document.getRootElement());
    }

    /**
     * 读取xml
     *
     * @return
     * @throws DocumentException
     */
    public static XmlFile xmlread(final InputStreamReader inputStreamReader) throws DocumentException {
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
    public static XmlFile xmlFile(final Element rootElement) {
        final XmlFile xmlFile = new XmlFile();
        final XmlFileProperty fileProperty = xmlFileProperty(rootElement);
        xmlFile.setXmlFileProperty(fileProperty);
        xmlFile.setFields(xmlFields(rootElement, fileProperty));
        return xmlFile;

    }

    /**
     * @param rootElement
     * @return
     */
    public static XmlFileProperty xmlFileProperty(final Element rootElement) {
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
        final String targetencoding = StringUtils.isBlank(rootElement.attributeValue("targetencoding")) ? DEFAULT_CHARSET
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
     * @param rootElement
     * @return
     */
    public static List<Field> xmlFields(final Element rootElement, final XmlFileProperty fileProperty) { //遍历XML文件
        final List<Field> fields = Lists.newLinkedList();
        final Iterator iterator = rootElement.elementIterator();
        Field field = null;
        int so = 1;
        while (iterator.hasNext()) {
            field = new Field();
            final Element e = (Element) iterator.next();
            final String name = e.attributeValue("name");
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
            field.setSort(sort);

            fields.add(field);
            so++;
        }
        return fields;
    }

    /**
     * 返回一行返回的字符组合
     *
     * @param xmlFile
     * @return
     */
    public static String fixHeadValue(final XmlFile xmlFile) {
        final List<Field> fields = xmlFile.getFields();
        Collections.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(final Field o1, final Field o2) {
                return o1.getSort() < o2.getSort() ? -1 : 0;
            }
        });
        final StringBuilder builder = new StringBuilder();
        final String spacer = xmlFile.getXmlFileProperty().getSpacer();
        final IgnoreEnum ignore = xmlFile.getXmlFileProperty().getIgnore();
        final boolean spacerFalg = StringUtils.isNotBlank(spacer);
        for (final Field field : fields) {
            builder.append(field.getName());
            if (spacerFalg) {
                builder.append(spacer);
            }
        }
        final String result = builder.toString();
        return spacerFalg ? (ignore == IgnoreEnum.Y ? result.substring(0, result.length() - spacer.length()) : result)
                : result;
    }

    /**
     * 返回一行返回的字符组合
     *
     * @param xmlFile
     * @param paramMap key要和xml配置的name保持一致
     */
    public static String fixValue(final XmlFile xmlFile, final Map<String, String> paramMap) {
        final List<Field> fields = xmlFile.getFields();
        Collections.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(final Field o1, final Field o2) {
                return o1.getSort() < o2.getSort() ? -1 : 0;
            }
        });
        final StringBuilder builder = new StringBuilder();
        final FormatEnum format = xmlFile.getXmlFileProperty().getFormat();
        final String spacer = xmlFile.getXmlFileProperty().getSpacer();
        final IgnoreEnum ignore = xmlFile.getXmlFileProperty().getIgnore();
        final boolean spacerFalg = StringUtils.isNotBlank(spacer);
        for (final Field field : fields) {
            final String value = paramMap.get(field.getProperty());
            Preconditions.checkArgument(StringUtils.isNotBlank(value), field.getProperty() + "  value 不能为空");
            field.setValue(value);
            final String wrightvalue = StringFormat.format(value, format, field);
            builder.append(wrightvalue);
            if (spacerFalg) {
                builder.append(spacer);
            }
        }
        final String result = builder.toString();
        return StringUtils.isNotBlank(spacer) ? (ignore == IgnoreEnum.Y ? result.substring(0,
                result.length() - spacer.length()) : result) : result;
    }

    /**
     * 返回一行返回的字符组合
     *
     * @param xmlFile
     */
    public static Map<String, String> readValue(final XmlFile xmlFile, final String lineValue) {
        final List<Field> fields = xmlFile.getFields();
        Collections.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(final Field o1, final Field o2) {
                return o1.getSort() < o2.getSort() ? -1 : 0;
            }
        });
        final FormatEnum format = xmlFile.getXmlFileProperty().getFormat();
        final String spacer = xmlFile.getXmlFileProperty().getSpacer();
        return StringFormat.read(lineValue, format, spacer, fields);
    }

}
