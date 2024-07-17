package com.java.liyao;

import android.annotation.SuppressLint;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;

import java.util.ArrayList;
import java.util.List;

public class AiSummary {
    private static final String apiKey = "700e022f1d2f8f8d4c29b675e09d6a82.NZX85Lct83ikjRL2";
    private static final String basicPrompt = "Please summarize the main content of the following news in concise and accurate English. Ignore any irrelevant words and provide the summary directly without any additional information.\n";
    private static final ClientV4 client = new ClientV4.Builder(apiKey).build();
    private static final String requestIdTemplate = "thu-%d";

    public static String aiSummaryInvoke(String content) {
        List<ChatMessage> messages = new ArrayList<>();
        final String finalPrompt = basicPrompt + content;
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), finalPrompt);
        messages.add(chatMessage);
        @SuppressLint("DefaultLocale") String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        String ret = "";
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ret = mapper.writeValueAsString(invokeModelApiResp);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ret;
    }
}