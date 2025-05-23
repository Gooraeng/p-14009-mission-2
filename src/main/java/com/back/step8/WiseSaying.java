package com.back.step8;


public class WiseSaying {
    private String content;
    private String author;
    private final int id;

    public WiseSaying(String content, String author, int id) {
        this.author = author;
        this.content = content;
        this.id = id;
    }

    @Override
    public String toString() {
        return id + " / " + author + " / " + content;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public long getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toJsonString(StringBuilder sb) {
        sb.append("{\n")
            .append("\t\"id\": ").append(id).append(", \n")
            .append("\t\"author\": \"").append(escape(author)).append("\", \n")
            .append("\t\"content\": \"").append(escape(content)).append("\"\n")
        .append("}");

        return sb.toString();
    }

    private String escape(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

}
