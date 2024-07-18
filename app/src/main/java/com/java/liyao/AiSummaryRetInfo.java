package com.java.liyao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AiSummaryRetInfo {
    public void setChoices(List<ChoicesDTO> choices) {
        this.choices = choices;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public UsageDTO getUsage() {
        return usage;
    }

    public void setUsage(UsageDTO usage) {
        this.usage = usage;
    }

    @JsonProperty("choices")
    private List<ChoicesDTO> choices;
    @JsonProperty("created")
    private Integer created;
    @JsonProperty("id")
    private String id;
    @JsonProperty("model")
    private String model;
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("usage")
    private UsageDTO usage;

    public List<ChoicesDTO> getChoices() {
        return choices;
    }

    @NoArgsConstructor
    @Data
    public static class UsageDTO {
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }

    @NoArgsConstructor
    @Data
    public static class ChoicesDTO {
        @JsonProperty("finish_reason")
        private String finishReason;
        @JsonProperty("index")
        private Integer index;
        @JsonProperty("message")
        private MessageDTO message;

        public MessageDTO getMessage() {
            return message;
        }

        @NoArgsConstructor
        @Data
        public static class MessageDTO {
            @JsonProperty("content")
            private String content;

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            @JsonProperty("role")
            private String role;
        }
    }
}
