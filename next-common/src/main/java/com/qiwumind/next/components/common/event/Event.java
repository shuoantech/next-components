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

package com.qiwumind.next.components.common.event;



import java.io.Serializable;

/**
 * 类Event.java的实现描述：被监听事件
 */
public class Event implements Serializable {

    private static final long serialVersionUID = 4161755693819623893L;

    /**
     * bean
     */
    private Object            bean;

    /**
     * 方法名
     */
    private String            methodName;

    /**
     * 参数
     */
    private Object[]          args;

    @SuppressWarnings("rawtypes")
    private Class[]           clazzs;

    /**
     * @param bean
     * @param methodName
     * @param args
     */
    public Event(Object bean, String methodName, Object[] args) {
        this.bean = bean;
        this.methodName = methodName;
        if (args != null) {
            this.args = args.clone();
        }
    }

    /**
     * @param bean
     * @param methodName
     * @param args
     * @param clazzs
     */
    public Event(Object bean, String methodName, Object[] args, @SuppressWarnings("rawtypes") Class[] clazzs) {
        this.bean = bean;
        this.methodName = methodName;
        if (args != null) {
            this.args = args.clone();
        }
        if (clazzs != null) {
            this.clazzs = clazzs.clone();
        }
    }

    /**
     * @return
     */
    public Object getBean() {
        return this.bean;
    }

    /**
     * @param bean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * @return
     */
    public String getMethodName() {
        return this.methodName;
    }

    /**
     * @param methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return
     */
    public Object[] getArgs() {
        return this.args;
    }

    /**
     * @param args
     */
    public void setArgs(Object[] args) {
        if (args != null) {
            this.args = args.clone();
        }
    }

    /**
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class[] getClazzs() {
        return this.clazzs;
    }

    /**
     * @param clazzs
     */
    @SuppressWarnings("rawtypes")
    public void setClazzs(Class[] clazzs) {
        if (clazzs != null) {
            this.clazzs = clazzs.clone();
        }
    }
}
