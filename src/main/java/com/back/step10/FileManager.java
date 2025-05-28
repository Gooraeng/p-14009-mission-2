package com.back.step10;

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
    private final String DataJsonFile = "db/wiseSaying/data.json";
    private final String lastIdTxtPath = "db/wiseSaying/lastId.txt";

    public FileManager() {
        init();
    }

    // 명언 데이터 폴더 생성
    private void init() {
        File dir = new File(wiseSayingDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Json 파일 업데이트(수정, 등록)
    public void updateJsonFile(WiseSaying wiseSaying) {
        String jsonString = wiseSaying.toJsonString();
        String path = wiseSayingDirectory + wiseSaying.getId() + ".json";

        save(jsonString, path);
    }

    // Json 파일 생성 및 마지막 저장 ID 갱신
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

    // data.json 파일 생성 로직
    public void buildDataJson(List<WiseSaying> wiseSayings) {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < wiseSayings.size(); i++) {
            sb.append("\n").append(wiseSayings.get(i).toDataJsonString());

            if (i < wiseSayings.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("\n]");

        save(sb.toString(), DataJsonFile);
    }

    // data.json을 제외한 모든 number 이름의 json을 로드하여
    // wiseSayings에 저장
    public List<WiseSaying> loadAllWiseSaying() {
        List<WiseSaying> wiseSayings = new ArrayList<>();

        File folder = new File(wiseSayingDirectory);
        File[] files = folder.listFiles(file ->
            file.getName().endsWith(".json") && !file.getName().contains("data")
        );
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

    // json 스트링을 특정 경로에 저장하는 함수
    private void save(String jsonString, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(jsonString);
        } catch (IOException e) {
            System.out.println("파일 저장 실패 : " + e.getMessage());
        }
    }

    // 추출된 json string으로부터 특정 key를 기준으로 값을 검색 후 반환하는 함수
    private String extractString(String json, String key) {
        Matcher matcher = Pattern.compile(key + "\\s*:\\s*([^,]+)(?:,|$)")
                                .matcher(json);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Key not found or invalid format: " + key);
        }

        return matcher.group(1);
    }
}
