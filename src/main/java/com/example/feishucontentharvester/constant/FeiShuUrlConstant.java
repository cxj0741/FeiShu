package com.example.feishucontentharvester.constant;

public class FeiShuUrlConstant {
    //获取tokenURL
    public static final String FEISHU_TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
    // 数据源表格地址(这里默认地址不会变，没有做变化处理)
    public static final String FEISHU_SHEET_ID__URL = "https://open.feishu.cn/open-apis/sheets/v3/spreadsheets/";
    //获取表格信息
    public static final String FEISHU_SHEET_INFORMATION__URL = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/N5Wts8V9Wh3gXJtyxPvcDbMZnJc/values_batch_get?ranges=5cc663!B2:B";
    //获取文章内容
    public static final String FEISHU_CONTENT__URL = "https://open.feishu.cn/open-apis/docx/v1/documents/";
}
