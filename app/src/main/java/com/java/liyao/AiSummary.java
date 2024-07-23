package com.java.liyao;

import android.util.Log;

import com.google.gson.Gson;

import okhttp3.*;
import java.io.IOException;


public class AiSummary {
    private static final String API_KEY = "700e022f1d2f8f8d4c29b675e09d6a82.NZX85Lct83ikjRL2";
    private static final String BASIC_PROMPT = "Please summarize the main content of the following news in concise and accurate simplified Chinese. Ignore any irrelevant words and provide the summary directly without any additional information.\n";
    // private static final ClientV4 CLIENT = new ClientV4.Builder(API_KEY).build();
    private static final String requestIdTemplate = "thu-%d";

    public static String aiSummaryInvoke(String content) {
        // List<ChatMessage> messages = new ArrayList<>();
        final String finalPrompt = BASIC_PROMPT + content;
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        String json_content = "{\n" +
                "    \"model\": \"glm-4\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"" + finalPrompt + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        json_content = json_content.replace("\n", "").replace("\t", "").replace("\r", "");
        RequestBody body = RequestBody.create(mediaType, json_content);

        // Log.d("AiSummary", "aiSummaryInvoke: " + json_content);

        Request request = new Request.Builder()
                .url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String s = response.body().string();
            Log.d("XXXXXXXXXX", "aiSummaryInvoke: " + s);
            AiSummaryRetInfo.ChoicesDTO.MessageDTO messageDTO = new Gson().fromJson(s, AiSummaryRetInfo.class).getChoices().get(0).getMessage();
            s = messageDTO.getContent();
//            Log.d("AiSummary",  s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ret = "failed to get ai summary";
        return ret;
    }
}