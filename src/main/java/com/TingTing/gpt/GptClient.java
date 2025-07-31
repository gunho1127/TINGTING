package com.TingTing.gpt;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GptClient {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient httpClient = new OkHttpClient();

    // 최초 프롬프트로 GPT 인삿말 받아오기
    public String getFirstMessage(String systemPrompt) {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", systemPrompt));

        return callGptApi(messages);
    }

    // 전체 대화 메시지 배열 넘기고 응답 받기
    public String getReply(List<GptMessage> messagesList) {
        JSONArray messages = new JSONArray();
        for (GptMessage msg : messagesList) {
            messages.put(new JSONObject()
                    .put("role", msg.role())
                    .put("content", msg.content()));
        }

        return callGptApi(messages);
    }

    // 실제 OpenAI 호출
    private String callGptApi(JSONArray messages) {
        JSONObject requestBody = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("temperature", 0.8)
                .put("messages", messages);

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(
                        requestBody.toString(),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("GPT 호출 실패: " + response);

            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (IOException e) {
            throw new RuntimeException("GPT API 호출 오류", e);
        }
    }
}
