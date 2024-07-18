package com.example.feishucontentharvester.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface FeiShuServiceNewTable {
    /**
     * 创建一个表格，将表格地址，表格spreadsheet_token保存在数据库,经常使用同时放在redis
     */
    public void createNewTable();

    /**
     * 初始化一些字段，后续可以考虑设置单元格格式
     * 这也是添加的方法（向范围写入数据）
     */
    public void initNewTable() throws JsonProcessingException;
}
