package com.example.feishucontentharvester.service;

import java.util.ArrayList;

public interface FeiShuServiceApi {
    /**
     * 获取token
     * ok
     * @return
     */
    public String getAccessToken();
    /**
     * 获取数据源表格信息
     * ok
     * @return
     */
    public String getSheetId(String spreadsheetToken);

    /**
     * 获取表格数据
     */
    public ArrayList<String> getArticleUrl();

    /**
     * 获取文本内容
     * @return
     */
    public String getContent(String url);
}
