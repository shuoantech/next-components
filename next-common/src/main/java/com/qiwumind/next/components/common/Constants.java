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

package com.qiwumind.next.components.common;



/**
 */
public interface Constants {

	String V_PREFIXE = "${";

	String V_POSTFIX = "}";

	/** 应用所属公司信息key */
	String COMPANY_ID = "COMPANY_ID";

	/** 应用所属部门信息key */
	String DEPT_ID = "DEPT_ID";

	/** 应用环境信息key */
	String DEPLOY_ENV = "DEPLOY_ENV";

	/** 应用环境信息value: local, 本地开发环境 */
	String DEPLOY_ENV_LOCAL = "local";

	/** 应用容器host信息key */
	String HOSTNAME = "HOSTNAME";

	/** 应用宿主机IP信息key */
	String HOST_IP = "HOST_IP";

	/** 应用环境信息value: true: 本地开发环境, 反之false或者空 */
	String DEPLOY_LOCAL = "DEPLOY_LOCAL";

}
