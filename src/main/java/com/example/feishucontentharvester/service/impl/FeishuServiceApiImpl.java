package com.example.feishucontentharvester.service.impl;

import com.example.feishucontentharvester.constant.FeiShuUrlConstant;
import com.example.feishucontentharvester.service.FeiShuServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.*;

@Service
@Slf4j
public class FeishuServiceApiImpl implements FeiShuServiceApi {

    //token
    private static final String AccessToken = "t-g1047hkSSMXRD4HL7KXHLKOIAGGBIY4HESJFWKTT";

    @Value("${feishu.appID}")
    private String appID;

    @Value("${feishu.appSecret}")
    private String appSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private Client client;

    /**
     * 获取token
     * @return
     */
    @Override
    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("app_id", appID);
        requestBody.put("app_secret", appSecret);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        //通过自旋刷新失败多次尝试
        int cnt=0;
        Map<String, Object> responseBody=null;
        while (++cnt<10)
        {
            ResponseEntity<Map> response = restTemplate.exchange(
                    FeiShuUrlConstant.FEISHU_TOKEN_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );
            responseBody = response.getBody();

            if (responseBody != null && responseBody.get("code").equals(0)) {
                // todo 可能需要放入数据库
                String token= (String)responseBody.get("tenant_access_token");
                redisTemplate.opsForValue().set("token",token);
                return token;
            }
        }
        throw new RuntimeException("Failed to get tenant_access_token: " + responseBody.get("msg")+requestBody.get("code"));

    }

    /**
     * 获取数据源表格信息
     * @return
     */
    public String getSheetId(String spreadsheetToken){
        //构建请求头，都一样，可以提取出来
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        // todo 从redis中取
        headers.set("Authorization","Bearer "+redisTemplate.opsForValue().get("token"));

        Map<String, String> requestBody = new HashMap<>();


        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);

        System.out.println(FeiShuUrlConstant.FEISHU_SHEET_ID__URL+spreadsheetToken+"/sheets/query");
        ResponseEntity<Map> response = restTemplate.exchange(
                FeiShuUrlConstant.FEISHU_SHEET_ID__URL+spreadsheetToken+"/sheets/query",
                HttpMethod.GET,
                requestEntity,
                Map.class
        );
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.get("code").equals(0)) {
            Map<String, Object> data1 = (Map<String, Object>) responseBody.get("data");
            ArrayList sheets = (ArrayList<Map<String, Object>>) data1.get("sheets");
            Map<String, Object> sheet = (Map<String, Object>)sheets.get(0);
            Map<String, Object> map = (Map<String, Object>) sheet.get("grid_properties");
            Integer row_count = (Integer)map.get("row_count");
            //将数据源的总行数存入redis
            if("N5Wts8V9Wh3gXJtyxPvcDbMZnJc".equals(spreadsheetToken))  redisTemplate.opsForValue().set("row",row_count);
            //todo 存入数据库
            return (String) sheet.get("sheet_id");
        } else {
            throw new RuntimeException("Failed to get tenant_access_token: " + responseBody.get("msg")+responseBody.get("code"));
        }

    }

    /**
     * 获取表格数据
     * @return
     */
    @Override
    public ArrayList<String> getArticleUrl() {
        //构建请求头，都一样，可以提取出来
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        // todo 从redis中取
        headers.set("Authorization","Bearer "+redisTemplate.opsForValue().get("token"));


        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
               FeiShuUrlConstant.FEISHU_SHEET_INFORMATION__URL+redisTemplate.opsForValue().get("row"),
                HttpMethod.GET,
                requestEntity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.get("code").equals(0)) {
            //获取data
            Map<String, Object> data1 = (Map<String, Object>) responseBody.get("data");
            //获取valueRanges，内容信息,这是一个数组，里面只用一个Map<String, Object>元素
            ArrayList valueRanges = (ArrayList<Map<String, Object>>) data1.get("valueRanges");
            Map<String, Object> information = (Map<String, Object>)valueRanges.get(0);

            //获取到文本信息所在大数组values
            ArrayList<ArrayList> values = (ArrayList<ArrayList>) information.get("values");

            //创建一个String类型的数组存储link
            ArrayList res=new ArrayList<String>();

            //遍历该数组，获取到每一个小数组
            for(int i=0;i<values.size();i++)
            {
                //内部小数组
                ArrayList<ArrayList> value = (ArrayList<ArrayList>) values.get(i);

                //这里需要判断是否为一个数组
                if(!(value.get(0) instanceof ArrayList)) continue;

                ArrayList<Map<String,Object>> arr = value.get(0);
                Map<String, Object> map = arr.get(0);
                //获取链接
                String link = (String)map.get("link");
                if(map.get("link")!=null)  res.add(link);
//               log.info("第{}次\n",i);
            }

            return res;
        } else {
            throw new RuntimeException("Failed to get tenant_access_token: " + responseBody.get("msg")+responseBody.get("code"));
        }
    }

    /**
     * 获取文本内容
     * @return
     */
    @Override
    public String getContent(String url) {
        //获取所有的url
//        ArrayList<String> articleUrls = getArticleUrl();
        //构建请求头，都一样，可以提取出来
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        //todo 可能需要放到数据库
        headers.set("Authorization", "Bearer " + redisTemplate.opsForValue().get("token"));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);

        //存放返回结果
//        ArrayList<String> res = new ArrayList<>();

//        遍历url
//        for(int i=0;i<articleUrls.size();i++){
//            String url = articleUrls.get(i);
//            log.info("第{}次，url是{}",i,url);
        // 计算开始索引，即字符串总长度减去想要的字符数量
        int startIndex = Math.max(0, url.length() - 27);

        // 使用substring()方法截取最后27个字符
        url = url.substring(startIndex);
        ResponseEntity<Map> response = null;
        url += "/raw_content";
        Map<String, Object> responseBody = null;
        try {
            response = restTemplate.exchange(
                    FeiShuUrlConstant.FEISHU_CONTENT__URL + url,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

        } catch (Exception e) {
            //todo 这种wink文档应该是知识节点
            log.info("获取不到文章内容");
           }
            if (response == null) {
                return "未获取到";
            }
            responseBody = response.getBody();
            if (responseBody != null && responseBody.get("code").equals(0)) {
                //获取data
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                //获取content
                String content = (String) data.get("content");
                return content;
            } else {
                throw new RuntimeException("Failed to get tenant_access_token: " + responseBody.get("msg") + responseBody.get("code"));
            }
        }


}