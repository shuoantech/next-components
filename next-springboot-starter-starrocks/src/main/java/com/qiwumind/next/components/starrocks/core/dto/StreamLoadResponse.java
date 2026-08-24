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

package com.qiwumind.next.components.starrocks.core.dto;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stream Load 响应结果
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StreamLoadResponse {

    @JsonProperty("TxnId")
    private Long txnId;
    @JsonProperty("Label")
    private String label;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("NumberTotalRows")
    private Long numberTotalRows;
    @JsonProperty("NumberLoadedRows")
    private Long numberLoadedRows;
    @JsonProperty("NumberFilteredRows")
    private Long numberFilteredRows;
    @JsonProperty("NumberUnselectedRows")
    private Long numberUnselectedRows;
    @JsonProperty("LoadBytes")
    private Long loadBytes;
    @JsonProperty("LoadTimeMs")
    private Long loadTimeMs;

    @JsonProperty("ErrorURL")
    private String errorURL;

    public boolean isSuccess() {
        return "Success".equalsIgnoreCase(status);
    }
    public static StreamLoadResponse failure(String message) {
        StreamLoadResponse response=new StreamLoadResponse();
        response.setMessage(message);
        response.status="fail";
        return response;
    }


}
