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

package com.qiwumind.next.components.redis.core.retry;



import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryLogListener implements RetryListener {
    @Override
    public <V> void onRetry(final Attempt<V> attempt) {
        // 第几次重试,(注意:第一次重试其实是第一次调用); 距离第一次重试的延迟; 重试结果: 是异常终止, 还是正常返回
        log.info("retry time : [{}]  retry delay : [{}]  hasException={}  hasResult={}", attempt.getAttemptNumber(),
                attempt.getDelaySinceFirstAttempt(), attempt.hasException(), attempt.hasResult());
        // 是什么原因导致异常
        if (attempt.hasException()) {
            log.info("causeBy={}", attempt.getExceptionCause().toString());
        } 
//        else {
//            // 正常返回时的结果
//            log.info("result={}", attempt.getResult());
//        }
//        log.info("************RetryLogListener listen over***********");
    }
}
