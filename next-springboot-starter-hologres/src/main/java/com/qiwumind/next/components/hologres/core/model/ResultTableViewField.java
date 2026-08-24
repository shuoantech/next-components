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

package com.qiwumind.next.components.hologres.core.model;


public class ResultTableViewField {
    private TableView tableView;
    private TableField tableField;

    public TableView getTableView() {
        return tableView;
    }

    public TableField getTableField() {
        return tableField;
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
    }

    public void setTableField(TableField tableField) {
        this.tableField = tableField;
    }

    @Override
    public String toString() {
        return "ResultTableViewField{" +
                "tableView=" + tableView +
                ", tableField=" + tableField +
                '}';
    }

    public static class TableField{
        private String name;
        private String type;
        private String comment;

        public TableField() {
        }

        public TableField(String name, String type, String comment) {
            this.name = name;
            this.type = type;
            this.comment = comment;
        }
        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getComment() {
            return comment;
        }

        @Override
        public String toString() {
            return "TableField{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", comment='" + comment + '\'' +
                    '}';
        }
    }
    public static class TableView{
        private String schemanme;
        private String tablename;

        // ✅ 添加无参构造器（用于框架反射等场景）
        public TableView() {
        }

        // ✅ 添加带参构造器
        public TableView(String schemanme, String tablename) {
            this.schemanme = schemanme;
            this.tablename = tablename;
        }
        public void setSchemanme(String schemanme) {
            this.schemanme = schemanme;
        }

        public void setTablename(String tablename) {
            this.tablename = tablename;
        }


        public String getSchemanme() {
            return schemanme;
        }

        public String getTablename() {
            return tablename;
        }

        @Override
        public String toString() {
            return "TableView{" +
                    "schemanme='" + schemanme + '\'' +
                    ", tablename='" + tablename + '\'' +
                    '}';
        }
    }


}
