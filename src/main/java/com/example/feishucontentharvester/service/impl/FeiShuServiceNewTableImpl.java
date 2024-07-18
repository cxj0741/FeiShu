package com.example.feishucontentharvester.service.impl;

import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.feishucontentharvester.constant.FeiShuUrlConstant;
import com.example.feishucontentharvester.constant.NewTableConstant;
import com.example.feishucontentharvester.entity.Article;
import com.example.feishucontentharvester.entity.SpreadsheetData;
import com.example.feishucontentharvester.entity.ValueRange;
import com.example.feishucontentharvester.service.FeiShuServiceNewTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FeiShuServiceNewTableImpl implements FeiShuServiceNewTable {

    //这个新表格写入数据是需要 获取表格token的
    private static final String FEISHU_INIT_NEW_TABLE__URL = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/UPQjsF5nrhoHMBthyrsc6ZK8nAb/values_append"; // 创建新表请求地址

    @Autowired
    private FeishuServiceApiImpl feishuServiceApi;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OpenAiServiceImpl openAiService;
    /**
     * 创建一个表格，将表格地址，表格spreadsheet_token保存在数据库,经常使用同时放在redis
     */
    @Override
    public void createNewTable() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");

        headers.set("Authorization","Bearer "+redisTemplate.opsForValue().get("token"));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                NewTableConstant.FEISHU_NEW_TABLE__URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.get("code").equals(0)) {
            //获取data
            Map<String, Object> data1 = (Map<String, Object>) responseBody.get("data");

            //获取"spreadsheet"
            Map<String, Object> spreadsheet =  (Map<String, Object>)data1.get("spreadsheet");

            //获得属性值
            // todo 后续放入数据库和redis(redis可以用hash存)
            String spreadsheetToken=(String) spreadsheet.get("spreadsheet_token");
            String url=(String) spreadsheet.get("url");
            redisTemplate.opsForValue().set("spreadsheetToken",spreadsheetToken);
            redisTemplate.opsForValue().set("url",url);
//            log.info("生成表格的一些信息: spreadsheetToken:{},url:{}",spreadsheetToken,url);

        } else {
            throw new RuntimeException("Failed to get tenant_access_token: " + responseBody.get("msg")+responseBody.get("code"));
        }
    }

    /**
     * 初始化表格字段
     */
    @Override
    // todo 应该是需要参数的，需要进行表的对比
    public void initNewTable()  {
        //获取表格sheet_id
        String shettId = feishuServiceApi.getSheetId((String) redisTemplate.opsForValue().get("spreadsheetToken"));

        //构建valueRange对象
        ValueRange valueRange=new ValueRange();
        String range=shettId+ NewTableConstant.RANGE;
        List<String> value1=new ArrayList<>();
        value1.add(NewTableConstant.ID);
        value1.add(NewTableConstant.UPDATELOG);
        value1.add(NewTableConstant.LINK);
        value1.add(NewTableConstant.SUMMARY);
        value1.add(NewTableConstant.TAGS);

        List<List<String>> values=new ArrayList<>();
        values.add(value1);
        valueRange.setRange(range);
        valueRange.setValues(values);
        SpreadsheetData spreadsheetData=new SpreadsheetData();
        spreadsheetData.setValueRange(valueRange);
            addNewTable(spreadsheetData);

    }

    /**
     *添加方法
     */
    public void addNewTable(SpreadsheetData valueRange)  {
        ObjectMapper mapper = new ObjectMapper();
        String jsonValueRabge = null;
        try {
            jsonValueRabge = mapper.writeValueAsString(valueRange);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Authorization","Bearer "+redisTemplate.opsForValue().get("token"));

        //注意这里是字符串
        String requestBody = jsonValueRabge;

//        requestBody.put("valueRange","{\"valueRange\":{\"range\":\"302ef2!A1:E1\",\"values\":[[\"编号\",\"更新日志\",\"文章链接\",\"文章摘要\",\"文章关键字\"]]}}");
//        System.out.println(jsonValueRabge);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody,headers);

//        String url=NewTableConstant.FEISHU_ADD_URL+
//                redisTemplate.opsForValue().get("spreadsheetToken")+"/values_append";
//        System.out.println(url);
        ResponseEntity<Map> response = restTemplate.exchange(
                NewTableConstant.FEISHU_ADD_URL+
                        redisTemplate.opsForValue().get("spreadsheetToken")+"/values_append?insertDataOption=INSERT_ROWS",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );
    }

    /**
     * 自增方法生成id
     * @return
     */
    public String invoiceNumber() {
        Long sequence = redisTemplate.opsForValue().increment("invoice_number_key");
        return String.format("%08d", sequence);
    }

    /**
     * 表中原来有的数据进行迁移
     */
    public void copyDataFromTo(){
        //使用Article来存储文章信息
        Article article=new Article();
        //得到原有数据链接
        ArrayList<String> articleUrl = feishuServiceApi.getArticleUrl();


        for(int i=0;i<articleUrl.size();i++)
        {
            //生成id
            String id = invoiceNumber();
            //获取文章链接
            String url = articleUrl.get(i);
            //更新日志（记录时间）
            // 创建一个格式器，指定输出格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updatelog="此表更新于"+ LocalDateTime.now().format(formatter);

            //通过文章链接获取内容
            String content = feishuServiceApi.getContent(url);
            // 获取前5000个字符
            content = content.substring(0, Math.min(content.length(), 5000));

            //文章标题
            String title=null;
            //文章摘要
            String tags=null;
            //获取表格sheet_id
            String shettId = feishuServiceApi.getSheetId((String) redisTemplate.opsForValue().get("spreadsheetToken"));


            if("未获取到".equals(content)) {
                title="未获取到";
                tags="未获取到";

                //数据整合，放入新表格
                System.out.println("title:"+title+"\ntags:"+tags);
                source(id,updatelog,url,title,tags);
                //返回

                continue;
            }
            //通过ai生成文章摘要和关键字
            GenerationResult generationResult=null;
            try {
                 generationResult = openAiService.callWithMessage(Message.builder()
                        .role(Role.SYSTEM.getValue())
                        .content("You are a helpful assistant.")
                        .build(), Message.builder()
                        .role(Role.USER.getValue())
                        .content(content +
                                "根据文章内容返回一个文章标题，和2到3个关键词\n" +
                                "返回格式为：\n" +
                                "文章题目：{？}\n" +
                                "文章摘要：{？，？，？...}")
                        .build());
            } catch (NoApiKeyException e) {
                e.printStackTrace();
            } catch (InputRequiredException e) {
                e.printStackTrace();
            }

            //先获取到content
            GenerationOutput output = generationResult.getOutput();
            List<GenerationOutput.Choice> choices = output.getChoices();
            GenerationOutput.Choice choice = choices.get(0);
            Message message = choice.getMessage();
            String content1 = message.getContent();

            //获取到标题和摘要
            ArrayList<String> fun = fun(content1);
            title=fun.get(0);
            tags=fun.get(1);

            //数据整合迁移
            System.out.println("title:"+title+"\ntags:"+tags);
            source(id,updatelog,url,title,tags);
        }
    }

    public ArrayList<String> fun(String content){


        String str1 = ""; // 文章题目
        String str2 = ""; // 文章摘要

        String[] parts = content.split("(?m)^文章", 0);

        for (String part : parts) {
            if (part.contains("题目")) {
                str1 = part.split("：", 2)[1].trim();
            } else if (part.contains("摘要")) {
                str2 = part.split("：", 2)[1].trim();
            }
        }

//        System.out.println("str1 = " + str1);
//        System.out.println("str2 = " + str2);
        ArrayList<String> res=new ArrayList<>();
        res.add(str1);
        res.add(str2);

        return res;
    }

    public void source(String id,String update,String link,String summary,String tags){
        //获取表格sheet_id
        String shettId = feishuServiceApi.getSheetId((String) redisTemplate.opsForValue().get("spreadsheetToken"));

        //构建valueRange对象
        ValueRange valueRange=new ValueRange();
        String range=shettId+ NewTableConstant.RANGE;
        List<String> value1=new ArrayList<>();
        value1.add(id);
        value1.add(update);
        value1.add(link);
        value1.add(summary);
        value1.add(tags);

        List<List<String>> values=new ArrayList<>();
        values.add(value1);
        valueRange.setRange(range);
        valueRange.setValues(values);
        SpreadsheetData spreadsheetData=new SpreadsheetData();
        spreadsheetData.setValueRange(valueRange);
        addNewTable(spreadsheetData);
    }

}
