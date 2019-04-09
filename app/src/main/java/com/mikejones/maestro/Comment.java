package com.mikejones.maestro;

import java.util.ArrayList;

public class Comment {

    private String authorName;
    private String audioURL;
    private String timestamp;
    private String imageURL;
    private String text;

    public Comment(String an, String aurl,
                String ts, String iurl, String t){

        authorName = an;
        audioURL = aurl;
        timestamp = ts;
        imageURL = iurl;
        text = t;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
