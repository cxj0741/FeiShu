package com.example.feishucontentharvester.service;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

public interface OpenAiService {
    public GenerationResult callWithMessage(Message systemMsg, Message userMsg) throws NoApiKeyException, InputRequiredException;
}

