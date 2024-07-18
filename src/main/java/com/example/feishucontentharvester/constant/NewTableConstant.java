package com.example.feishucontentharvester.constant;

public class NewTableConstant {
    // 创建新表请求地址
    public static final String FEISHU_NEW_TABLE__URL = "https://open.feishu.cn/open-apis/sheets/v3/spreadsheets";
    //插入数据请求地址
    public static final String FEISHU_ADD_URL="https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/";
    //创建表格的range参数
    public static final String RANGE="!A1:E1";

    public static final String ID="编号";
    public static final String UPDATELOG="更新日志";
    public static final String SUMMARY="文章摘要";
    public static final String LINK="文章链接";
    public static final String TAGS="文章关键字";
}
