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

package com.qiwumind.next.components.groovy.compiler;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qiwumind.next.components.groovy.entity.ScriptEntry;

import groovy.lang.GroovyClassLoader;

/**
 * <p>
 * groovy编译器
 * </p>
 */
public class GroovyCompiler implements DynamicCodeCompiler {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyCompiler.class);

    @Override
    public Class<?> compile(String code, String name) {
        GroovyClassLoader loader = this.getGroovyClassLoader();
        LOG.warn("Compiling filter: " + name);
        return loader.parseClass(code, name);
    }

    @Override
    public Class<?> compile(ScriptEntry scriptEntry) {
        GroovyClassLoader loader = this.getGroovyClassLoader();
        // 以 GroovyCompiler + 脚本的名称作为类名称
        return loader.parseClass(scriptEntry.getScriptContext(),
                GroovyCompiler.class.getSimpleName() + "_" + scriptEntry.getName());
    }

    /**
     * <p>
     * 为什么要New 一个class loader呢？这个就要从Class对象垃圾回收说起，一个Class要被回收必须满足以下条件：
     * <ol>
     * <li>该Class 的所有实例都已经被回收</li>
     * <li>加载该类的classLoader已经被回收</li>
     * <li>该Class 没有被引用</li>
     * </ol>
     * </p>
     * <p>
     * 通过使用 new 一个classLoader 来加载动态脚本就是为了解决动态类回收问题，因为classLoader可以提前被回收，
     * 但是在目前版本的Groovy中，使用同一个GroovyClassLoader来加载脚本Class到方法区，该Class也可以在GroovyClassLoader之前被卸载，
     * 原因是：并不是真正由同一个GroovyClassLoader对象来加载的Class，而是每次GroovyClassLoader加载Class时都会创建一个类型为
     * {@link GroovyClassLoader.InnerLoader}的类加载器来加载Class，所以实质上还是每次加载都使用新的ClassLoader
     * </p>
     * <a>https://blog.csdn.net/Hellowenpan/article/details/127145821?csdn_share_tail=%7B%22type%22%3A%22blog%22%2C%22rType%22%3A%22article%22%2C%22rId%22%3A%22127145821%22%2C%22source%22%3A%22Hellowenpan%22%7D</a>
     *
     * @return a new GroovyClassLoader
     */
    public GroovyClassLoader getGroovyClassLoader() {

        return new GroovyClassLoader();
    }

}
