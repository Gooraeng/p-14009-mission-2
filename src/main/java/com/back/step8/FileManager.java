package com.back.step8;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManager {

    private final String wiseSayingDirectory = "db/wiseSaying/";
    private final String lastIdTxtPath = "db/wiseSaying/lastId.txt";

    public FileManager() {
        init();
    }

    private void init() {
        File dir = new File(wiseSayingDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Json 파일 저장(등록) 및 마지막 저장 ID 갱신
    public void updateJsonFile(WiseSaying wiseSaying) {
        String jsonString = wiseSaying.toJsonString(new StringBuilder());
        String path = wiseSayingDirectory + wiseSaying.getId() + ".json";

        save(jsonString, path);
    }

    public void createJsonFile(WiseSaying wiseSaying) {
        updateJsonFile(wiseSaying);
        save(WiseSayingBoard.getLastId().toString(), lastIdTxtPath);
    }

    // lastId.txt 파일로부터 마지막 저장 ID를 불러와서 Board로 넘김
    public int getLastId() {
        Path path = Paths.get(lastIdTxtPath);

        if (!path.toFile().exists()) {
            return 0;
        }

        try {
            return Integer.parseInt(Files.readString(path));
        } catch (IOException e) {
            return 0;
        }
    }

    // 삭제 명령어 수행 시
    public void removeJsonFile(WiseSaying wiseSaying) {
        Path path = Paths.get(wiseSayingDirectory + wiseSaying.getId() + ".json");

        try {
            Files.delete(path);
            System.out.println("파일 삭제 완료");
        } catch (IOException e) {
            System.out.println("파일 삭제 중 오류 발생 : " + e.getMessage());
        }
    }

    private void save(String jsonString, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(jsonString);
        } catch (IOException e) {
            System.out.println("파일 저장 실패 : " + e.getMessage());
        }
    }

    public List<WiseSaying> loadAllWiseSaying() {
        List<WiseSaying> wiseSayings = new ArrayList<>();

        File folder = new File(wiseSayingDirectory);
        File[] files = folder.listFiles(file -> file.getName().endsWith(".json"));
        Path path;

        if (files == null) {
            return wiseSayings;
        }

        for (File file : files) {
            path = file.toPath();

            try{
                String json = Files.readString(path).trim()
                        .replaceAll("[\\n\\r\\t{}\"]", "");

                int id = Integer.parseInt(extractString(json, "id"));
                String author = extractString(json, "author");
                String content = extractString(json, "content");

                wiseSayings.add(new WiseSaying(content, author, id));

            } catch (Exception e) {
                System.out.println("파일 읽기 실패 : " + path + " (" + e.getMessage() + ")");
            }
        }

        return wiseSayings;
    }

    private String extractString(String json, String key) {
        Matcher matcher = Pattern.compile(key + "\\s*:\\s*([^,]+)(?:,|$)")
                                .matcher(json);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Key not found or invalid format: " + key);
        }

        return matcher.group(1);
    }
}
