package com.mikejones.maestro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Comparable<Post>{

    private String postId;
    private String postTitle;
    private String authorId;
    private String authorName;
    private String audioURL;
    private String timestamp;
    private String imageURL;
    private String text;
    private ArrayList<Post> comments;

    public Post(){}

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
                String ts, String iurl, String t, ArrayList<Post> c){
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

    public ArrayList<Post> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Post> comments) {
        this.comments = comments;
    }

    public void addComment(Post c)
    {
        comments.add(c);
    }

    @Override
    public int compareTo(Post post) {
        SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        Date date1 = new Date();
        Date date2 = new Date();
        try {
            date1 = parserSDF.parse(timestamp);
            date2 = parserSDF.parse(post.getTimestamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date1.before(date2)){
            return 1;
        }
        if(date2.before(date1))
            return -1;

        return 0;
    }
}
