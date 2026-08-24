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

package com.qiwumind.next.components.banner.layout;



public class Description {
    private String name;
    private String description;
    private int    leftCellPadding  = 0;
    private int    rightCellPadding = 1;

    public Description() {

    }

    public Description(String name, String description, int leftCellPadding, int rightCellPadding) {
        this.name = name;
        this.description = description;
        this.leftCellPadding = leftCellPadding;
        this.rightCellPadding = rightCellPadding;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLeftCellPadding() {
        return this.leftCellPadding;
    }

    public void setLeftCellPadding(int leftCellPadding) {
        this.leftCellPadding = leftCellPadding;
    }

    public int getRightCellPadding() {
        return this.rightCellPadding;
    }

    public void setRightCellPadding(int rightCellPadding) {
        this.rightCellPadding = rightCellPadding;
    }
}
