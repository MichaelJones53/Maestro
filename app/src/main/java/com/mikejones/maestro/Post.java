package com.mikejones.maestro;

import java.util.ArrayList;

public class Post {

    private String postId;
    private String postTitle;
    private String authorId;
    private String authorName;
    private String audioURL;
    private String timestamp;
    private String imageURL;
    private String text;
    private ArrayList<Comment> comments;

    public Post(String pid, String pt, String aid, String an, String aurl,
                String ts, String iurl, String t){
        postId = pid;
        postTitle = pt;
        authorId = aid;
        authorName = an;
        audioURL = aurl;
        timestamp = ts;
        imageURL = iurl;
        text = t;
        comments = new ArrayList<>();
    }

    public Post(String pid, String pt, String aid, String an, String aurl,
                String ts, String iurl, String t, ArrayList<Comment> c){
        postId = pid;
        postTitle = pt;
        authorId = aid;
        authorName = an;
        audioURL = aurl;
        timestamp = ts;
        imageURL = iurl;
        text = t;
        comments = c;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
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

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment c)
    {
        comments.add(c);
    }
}
