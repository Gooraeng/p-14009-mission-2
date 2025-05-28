package com.back.step10;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;


public class WiseSayingBoard {

    private final List<WiseSaying> wiseSayings;
    private CommandType command;
    private static int lastRegisteredId;

    private static final FileManager fileManager = new FileManager();

    public WiseSayingBoard() {
        wiseSayings = fileManager.loadAllWiseSaying();
        lastRegisteredId = fileManager.getLastId();
        command = CommandType.시작;
    }

    // 명령어 설정
    public void setCommand(String command) {
        this.command = CommandType.valueOf(command.substring(0, 2));
    }

    // 현재 명령어 불러오기
    public CommandType getCommand() {
        return command;
    }

    public static Integer getLastId() {
        return lastRegisteredId;
    }

    // 종료 여부 확인 flag
    public boolean isTerminated() {
        return command.equals(CommandType.종료);
    }

    // 명언 등록
    public WiseSaying register() {
        Scanner scanner = new Scanner(System.in);
        String quote = fillField(scanner, "명언");
        String author = fillField(scanner, "작가");
        WiseSaying newWiseSaying = new WiseSaying(quote, author, ++lastRegisteredId);

        // 목록 추가 및 파일 생성
        wiseSayings.add(newWiseSaying);
        fileManager.createJsonFile(newWiseSaying);
        System.out.println(newWiseSaying.getId() + "번 명언이 등록되었습니다.");
        return newWiseSaying;
    }

    // 명언 수정
    public WiseSaying edit(String command) {
        if (wiseSayings.isEmpty()) {
            System.out.println("등록된 명언이 없습니다.");
            return null;
        }

        Map<String, String> keyWords = extractKeywordFromCommand(command);
        if (keyWords == null) {
            System.out.println("ID가 확인되지 않았습니다. 다시 입력해주세요. 예) 수정?id=1");
            return null;
        }

        int parsedId = Integer.parseInt(keyWords.get("id"));
        int parsedIndex = getIndexById(parsedId);

        if (parsedIndex == -1) {
            System.out.println(parsedId + "번 명언은 존재하지 않습니다.");
            return null;
        }

        WiseSaying foundWiseSaying = wiseSayings.get(parsedIndex);
        Scanner scanner = new Scanner(System.in);

        // 수정
        System.out.println("명언(기존) : " + foundWiseSaying.getContent());
        foundWiseSaying.setContent(fillField(scanner, "명언"));

        System.out.println("작가(기존) : " + foundWiseSaying.getAuthor());
        foundWiseSaying.setAuthor(fillField(scanner, "작가"));

        // 값 수정
        wiseSayings.set(parsedIndex, foundWiseSaying);
        fileManager.updateJsonFile(foundWiseSaying);
        return foundWiseSaying;
    }

    // 명언 삭제
    public WiseSaying remove(String command) {
        if (wiseSayings.isEmpty()) {
            System.out.println("등록된 명언이 없습니다.");
            return null;
        }

        Map<String, String> keyWords = extractKeywordFromCommand(command);
        if (keyWords == null) {
            System.out.println("ID가 확인되지 않았습니다. 다시 입력해주세요. 예) 삭제?id=1");
            return null;
        }

        int parsedId = Integer.parseInt(keyWords.get("id"));
        int parsedIndex = getIndexById(parsedId);

        if (parsedIndex == -1) {
            System.out.println(parsedId + "번 명언은 존재하지 않습니다.");
            return null;
        }

        // 삭제된 명언 데이터를 기반으로 json 파일 삭제
        WiseSaying removedWiseSaying = wiseSayings.remove(parsedIndex);
        fileManager.removeJsonFile(removedWiseSaying);
        System.out.println(parsedId + "번 명언이 삭제되었습니다.");
        return removedWiseSaying;
    }

    // 등록된 모든 아이템을 번호 역순으로 출력
    public void showAll() {
        System.out.println("번호 / 작가 / 명언");
        System.out.println("-----------------------------");

        for (WiseSaying wiseSaying : wiseSayings.reversed()) {
            System.out.println(wiseSaying);
        }
    }

    // 현재 저장된 명언들을 번호 오름차순으로 파일로 저장
    public void build() {
        fileManager.buildDataJson(wiseSayings);
        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }

    // '.'을 제외한 특수문자를 사용불가 하도록 조건 검사
    private boolean isValidInput(String string) {
        return string.matches("^[가-힣A-Za-z0-9.\\s]+$");
    }

    // 조건을 만족하는 아이템을 반환하는 함수
    private String fillField(Scanner scanner, String item) {
        String finalItem;
        boolean passed;

        do {
            System.out.print(item + " : ");
            finalItem = scanner.nextLine().trim();

            // 사용자의 입력값이 유효한지 검사
            passed = isValidInput(finalItem);

            if (!passed) {
                System.out.println("특수문자는 입력하실 수 없습니다.");
            }

        } while(!passed);

        return finalItem;
    }

    // Quotes를 순회하면서 ID에 맞는 Quote의 Index를 검색.
    // 리스트가 비어있거나 검색 실패 시 -1 반환
    private int getIndexById(int id) {
        return wiseSayings.isEmpty() ? -1 : IntStream.range(0, wiseSayings.size())
                .filter(i -> wiseSayings.get(i).getId() == id)
                .findFirst()
                .orElse(-1);

    }

    // 삭제나 수정 등 명령어 동작 시 keyword 셋을 추출하는 메소드
    // {"id" : "1", "something" : "foo", ...}
    private Map<String, String> extractKeywordFromCommand(String command) {
        Map<String, String> keywordItems = new HashMap<>();

        try {
            // '?' 문자 이후의 문자 추출
            // ?id=1&something=foo ==> id=1&something=foo 
            String keyboardString = command.split("\\?")[1];
            // '&' 문자를 기준으로 문자 분리
            String[] keywordList = keyboardString.split("&");
            String[] items;

            for (String keyword : keywordList) {
                // = 를 좌우로 나누어 key, value로 저장
                items = keyword.split("=");
                keywordItems.put(items[0], items[1]);
            }

        } catch (IndexOutOfBoundsException e) {
            keywordItems = null;
        }

        return keywordItems;
    }
}
