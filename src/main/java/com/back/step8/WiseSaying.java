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

    // Getter & Setter
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

    // number json 파일 저장을 위해 사용하는 로직
    public String toJsonString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{\n")
            .append("\t\"id\": ").append(id).append(", \n")
            .append("\t\"author\": \"").append(escape(author)).append("\", \n")
            .append("\t\"content\": \"").append(escape(content)).append("\"\n")
        .append("}");

        return sb.toString();
    }
    // data.json 파일 저장을 위해 사용하는 로직

    public String toDataJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t{\n")
            .append("\t\t\"id\": ").append(id).append(", \n")
            .append("\t\t\"author\": \"").append(escape(author)).append("\", \n")
            .append("\t\t\"content\": \"").append(escape(content)).append("\"\n")
        .append("\t}");

        return sb.toString();
    }

    // 불필요한 문장 기호를 제거하는 로직
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

    @Override
    public String toString() {
        return id + " / " + author + " / " + content;
    }
}
