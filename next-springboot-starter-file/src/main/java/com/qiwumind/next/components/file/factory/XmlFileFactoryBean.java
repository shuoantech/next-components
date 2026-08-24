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

package com.qiwumind.next.components.file.factory;



import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.qiwumind.next.components.file.core.valueobject.XmlFile;
import com.qiwumind.next.components.file.core.valueobject.XmlFileBean;
import com.qiwumind.next.components.file.core.xml.XmlFileUtil;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.google.common.collect.Lists;

/**
 * 类XmlFileFactory.java的实现描述
 * 
 * @author liks 2019年5月14日 下午2:43:49
 */
public class XmlFileFactoryBean implements FactoryBean<XmlFileBean>, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(XmlFileFactoryBean.class);
    private Resource[]   xmlLocations;
    private XmlFileBean  xmlFileBean;

    @Override
    public void afterPropertiesSet() throws Exception {
        xmlFileBean = buildXmlFile();
        logger.info("加载自定义xml文件={}", xmlFileBean);
    }

    protected XmlFileBean buildXmlFile() throws IOException {
        final List<XmlFile> files = Lists.newLinkedList();
        if (!org.springframework.util.ObjectUtils.isEmpty(this.xmlLocations)) {
            for (final Resource mapperLocation : this.xmlLocations) {
                if (mapperLocation == null) {
                    continue;
                }
                try {
                    final XmlFile xmlFile = XmlFileUtil.xmlread(new InputStreamReader(mapperLocation.getInputStream()));
                    xmlFile.setXmlFileName(mapperLocation.getFilename());
                    files.add(xmlFile);
                } catch (final DocumentException e) {
                    logger.info("加载xml文件出现异常{}", e);
                }
            }
        }
        final XmlFileBean xmlFilebean = new XmlFileBean();
        xmlFilebean.setXmlFiles(files);
        return xmlFilebean;
    }

    @Override
    public XmlFileBean getObject() throws Exception {
        return this.xmlFileBean;
    }

    @Override
    public Class<? extends XmlFileBean> getObjectType() {
        return this.xmlFileBean != null ? this.xmlFileBean.getClass() : XmlFileBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * @param xmlLocations
     */
    public void setXmlLocations(final Resource[] xmlLocations) {
        this.xmlLocations = xmlLocations;
    }
}
