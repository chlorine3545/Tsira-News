package com.java.liyao;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@lombok.NoArgsConstructor
@lombok.Data
public class NewsInfo {

    @com.fasterxml.jackson.annotation.JsonProperty("pageSize")
    private String pageSize;
    @com.fasterxml.jackson.annotation.JsonProperty("total")
    private Integer total;
    @com.fasterxml.jackson.annotation.JsonProperty("data")
    private List<DataDTO> data;
    @com.fasterxml.jackson.annotation.JsonProperty("currentPage")
    private String currentPage;

    public List<DataDTO> getData() {
        return data;
    }

    public void generateUniqueID() {
        for (DataDTO data : data) {
            data.generateSingleUniqueID();
        }
        // Log.d("UniqueIDSuccessfully", "generateUniqueID: 生成唯一标识符成功！");
    }

    // 全给老子变浮点数（核善的危啸）

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class DataDTO implements Serializable {
        @com.fasterxml.jackson.annotation.JsonProperty("image")
        private String image; // TMD 到底是哪个大聪明设计的字符串形状的数组？！
        @com.fasterxml.jackson.annotation.JsonProperty("publishTime")
        private String publishTime;
        @com.fasterxml.jackson.annotation.JsonProperty("keywords")
        private List<KeywordsDTO> keywords;
        @com.fasterxml.jackson.annotation.JsonProperty("language")
        private String language;
        @com.fasterxml.jackson.annotation.JsonProperty("video")
        private String video;
        @com.fasterxml.jackson.annotation.JsonProperty("title")
        private String title;
        @com.fasterxml.jackson.annotation.JsonProperty("when")
        private List<WhenDTO> when;
        @com.fasterxml.jackson.annotation.JsonProperty("content")
        private String content;
        @com.fasterxml.jackson.annotation.JsonProperty("openRels")
        private String openRels;
        @com.fasterxml.jackson.annotation.JsonProperty("url")
        private String url;
        @com.fasterxml.jackson.annotation.JsonProperty("persons")
        private List<PersonsDTO> persons;
        @com.fasterxml.jackson.annotation.JsonProperty("newsID")
        private String newsID;
        @com.fasterxml.jackson.annotation.JsonProperty("crawlTime")
        private String crawlTime;
        @com.fasterxml.jackson.annotation.JsonProperty("organizations")
        private List<OrganizationsDTO> organizations;
        @com.fasterxml.jackson.annotation.JsonProperty("publisher")
        private String publisher;
        @com.fasterxml.jackson.annotation.JsonProperty("locations")
        private List<LocationsDTO> locations;
        @com.fasterxml.jackson.annotation.JsonProperty("where")
        private List<?> where;
        @com.fasterxml.jackson.annotation.JsonProperty("scholars")
        private List<?> scholars;
        @com.fasterxml.jackson.annotation.JsonProperty("category")
        private String category;
        @com.fasterxml.jackson.annotation.JsonProperty("who")
        private List<WhoDTO> who;

        public String getUniqueID() {
            return uniqueID;
        }

        private String uniqueID;
        private boolean isLiked = false;

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        private void generateSingleUniqueID() {
            String source = this.title + this.publishTime + this.publisher;
            this.uniqueID = sha256Hex(source);
        }

        // Getter
        public String getPublisher() {
            return publisher;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public String getUrl() {
            return url;
        }

        public List<String> getImage() {
            if (image == null || image.isEmpty()) {
                return Collections.emptyList();
            }

            // Remove the square brackets at the beginning and end
            String trimmedImage = image.trim();
            if (trimmedImage.startsWith("[") && trimmedImage.endsWith("]")) {
                trimmedImage = trimmedImage.substring(1, trimmedImage.length() - 1);
            }

            // Split the string by comma, considering that URLs might contain commas
            List<String> imageList = new ArrayList<>();
            StringBuilder currentUrl = new StringBuilder();
            boolean inUrl = false;

            for (char c : trimmedImage.toCharArray()) {
                if (c == ',' && !inUrl) {
                    if (currentUrl.length() > 0) {
                        imageList.add(currentUrl.toString().trim());
                        currentUrl = new StringBuilder();
                    }
                } else if (c == 'h' && currentUrl.length() == 0) {
                    inUrl = true;
                    currentUrl.append(c);
                } else if (c == ' ' && inUrl) {
                    inUrl = false;
                    if (currentUrl.length() > 0) {
                        imageList.add(currentUrl.toString().trim());
                        currentUrl = new StringBuilder();
                    }
                } else {
                    currentUrl.append(c);
                }
            }

            if (currentUrl.length() > 0) {
                imageList.add(currentUrl.toString().trim());
            }

            return imageList;
        }

        public List<KeywordsDTO> getKeywords() {
            return keywords;
        }

        public List<WhenDTO> getWhen() {
            return when;
        }

        public List<PersonsDTO> getPersons() {
            return persons;
        }

        public List<OrganizationsDTO> getOrganizations() {
            return organizations;
        }

        public List<LocationsDTO> getLocations() {
            return locations;
        }

        public List<WhoDTO> getWho() {
            return who;
        }

        public String getThumbnail() {
            List<String> images = getImage();
            if (!images.isEmpty()) {
                return images.get(0);
            }
            return null;
        }

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class KeywordsDTO implements Serializable{
            @com.fasterxml.jackson.annotation.JsonProperty("score")
            private Double score;
            @com.fasterxml.jackson.annotation.JsonProperty("word")
            private String word;
        }

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class WhenDTO implements Serializable{
            @com.fasterxml.jackson.annotation.JsonProperty("score")
            private Double score;
            @com.fasterxml.jackson.annotation.JsonProperty("word")
            private String word;
        }

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class PersonsDTO implements Serializable{
            @com.fasterxml.jackson.annotation.JsonProperty("count")
            private Double count;
            @com.fasterxml.jackson.annotation.JsonProperty("linkedURL")
            private String linkedURL;
            @com.fasterxml.jackson.annotation.JsonProperty("mention")
            private String mention;
        }

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class OrganizationsDTO implements Serializable{
            @com.fasterxml.jackson.annotation.JsonProperty("count")
            private Double count;
            @com.fasterxml.jackson.annotation.JsonProperty("linkedURL")
            private String linkedURL;
            @com.fasterxml.jackson.annotation.JsonProperty("mention")
            private String mention;
        }

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class LocationsDTO implements Serializable{
            @com.fasterxml.jackson.annotation.JsonProperty("count")
            private Double count;
            @com.fasterxml.jackson.annotation.JsonProperty("linkedURL")
            private String linkedURL;
            @com.fasterxml.jackson.annotation.JsonProperty("mention")
            private String mention;
        }

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class WhoDTO implements Serializable{
            @com.fasterxml.jackson.annotation.JsonProperty("score")
            private Double score;
            @com.fasterxml.jackson.annotation.JsonProperty("word")
            private String word;
        }
    }
}