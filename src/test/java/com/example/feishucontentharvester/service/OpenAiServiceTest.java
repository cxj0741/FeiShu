package com.example.feishucontentharvester.service;

import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.feishucontentharvester.service.impl.FeishuServiceApiImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OpenAiServiceTest {
    @Autowired
    private OpenAiService openAiService;

    @Test
    public void callWithMessageTest(){
        try {
            GenerationResult generationResult = openAiService.callWithMessage(Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("You are a helpful assistant.")
                    .build(), Message.builder()
                    .role(Role.USER.getValue())
                    .content("https://open.feishu.cn/open-apis/docx/v1/documents/OC64dif0Ro4ZUcxWbz1cS8ufnCg/raw_content," +
                            "通过链接的内容生成一个文章摘要")
                    .build());
            GenerationOutput output = generationResult.getOutput();
            GenerationUsage usage = generationResult.getUsage();
            //这块代码
            List<GenerationOutput.Choice> choices = output.getChoices();
            GenerationOutput.Choice choice = choices.get(0);
            Message message = choice.getMessage();
            String content = message.getContent();
        } catch (NoApiKeyException e) {
            e.printStackTrace();
        } catch (InputRequiredException e) {
            e.printStackTrace();
        }
    }

    @Test
        public  void test1() {
        String content = "文章题目：互联网创业与内容创作实战心得\n" +
                "文章摘要：创业选择，大胆尝试，找能解决问题的人，资源整合，流量与产品，赚钱策略，写作原则，复盘与专注，价值创造，变现思维，时间管理，阅读与思考，互联网知识筛选，快乐与成功。";

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

        System.out.println("str1 = " + str1);
        System.out.println("str2 = " + str2);
        }

}
