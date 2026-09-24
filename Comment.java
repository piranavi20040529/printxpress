package com.example.printxpress.model;

public class Comment {
    private String commentId;
    private String date;
    private String message;
    private String reply;
    private String username;

    public Comment() { }

    public Comment(String commentId, String date, String message, String reply, String username) {
        this.commentId = commentId;
        this.date = date;
        this.message = message;
        this.reply = reply;
        this.username = username;
    }

    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}


