package com.example.feishucontentharvester.service;

import com.example.feishucontentharvester.constant.FeiShuUrlConstant;
import com.example.feishucontentharvester.service.impl.FeishuServiceApiImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class FeishuTableServiceTest  {
    @Autowired
    private FeishuServiceApiImpl feishuTableService;

    @Test
    public void testGetAccessToken() {
        String accessToken = feishuTableService.getAccessToken();
        System.out.println(accessToken);
    }

    @Test
    public void testGetSheetId(){
        String sheetId = feishuTableService.getSheetId("N5Wts8V9Wh3gXJtyxPvcDbMZnJc");
        System.out.println(sheetId);
    }

    @Test
    public void testGetArticleUrl(){
        ArrayList<String> articleUrl = feishuTableService.getArticleUrl();
        for(int i=0;i<articleUrl.size();i++)
        {
            String url = articleUrl.get(i);
            // 计算开始索引，即字符串总长度减去想要的字符数量
            int startIndex = Math.max(0, url.length() - 27);

            // 使用substring()方法截取最后27个字符
             url = url.substring(startIndex);
            url+="/raw_content";
            System.out.println(FeiShuUrlConstant.FEISHU_CONTENT__URL+url);
        }
    }

    @Test
    public void getContentTest(){
        String contents = feishuTableService.getContent("https://bh5pm72xfy.feishu.cn/docx/EZkDdfGwxozlkJxndTCcsaUcnWb");
        System.out.println(contents);
    }
}
