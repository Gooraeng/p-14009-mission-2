package com.back.step8;

import java.util.*;


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
    public void register() {
        Scanner scanner = new Scanner(System.in);
        String quote = fillField(scanner, "명언");
        String author = fillField(scanner, "작가");
        WiseSaying newWiseSaying = new WiseSaying(quote, author, ++lastRegisteredId);

        wiseSayings.add(newWiseSaying);
        fileManager.createJsonFile(newWiseSaying);
        System.out.println(newWiseSaying.getId() + "번 명언이 등록되었습니다.");
    }

    // 명언 수정
    public void edit(String command) {
        if (wiseSayings.isEmpty()) {
            System.out.println("등록된 명언이 없습니다.");
            return;
        }

        Map<String, String> keyWords = extractKeywordFromCommand(command);
        if (keyWords == null) {
            System.out.println("ID가 확인되지 않았습니다. 다시 입력해주세요. 예) 수정?id=1");
            return;
        }

        int parsedId = Integer.parseInt(keyWords.get("id"));
        int parsedIndex = getIndexById(parsedId);

        if (parsedIndex == -1) {
            System.out.println(parsedId + "번 명언은 존재하지 않습니다.");
            return;
        }

        WiseSaying foundWiseSaying = wiseSayings.get(parsedIndex);
        Scanner scanner = new Scanner(System.in);

        System.out.println("명언(기존) : " + foundWiseSaying.getContent());
        foundWiseSaying.setContent(fillField(scanner, "명언"));

        System.out.println("작가(기존) : " + foundWiseSaying.getAuthor());
        foundWiseSaying.setAuthor(fillField(scanner, "작가"));

        wiseSayings.set(parsedIndex, foundWiseSaying);
        fileManager.updateJsonFile(foundWiseSaying);
    }

    // 명언 삭제
    public void remove(String command) {
        if (wiseSayings.isEmpty()) {
            System.out.println("등록된 명언이 없습니다.");
            return;
        }

        Map<String, String> keyWords = extractKeywordFromCommand(command);
        if (keyWords == null) {
            System.out.println("ID가 확인되지 않았습니다. 다시 입력해주세요. 예) 삭제?id=1");
            return;
        }

        int parsedId = Integer.parseInt(keyWords.get("id"));
        int parsedIndex = getIndexById(parsedId);

        if (parsedIndex == -1) {
            System.out.println(parsedId + "번 명언은 존재하지 않습니다.");
            return;
        }

        WiseSaying removedWiseSaying = wiseSayings.remove(parsedIndex);
        fileManager.removeJsonFile(removedWiseSaying);
        System.out.println(parsedId + "번 명언이 삭제되었습니다.");
    }

    // 등록된 모든 아이템을 번호 역순으로 출력
    public void showAll() {
        System.out.println("번호 / 작가 / 명언");
        System.out.println("-----------------------------");

        for (int i = wiseSayings.size()-1; i >= 0; i--) {
            System.out.println(wiseSayings.get(i));
        }
    }

    // '.'을 제외한 특수문자를 사용가능 하도록 조건 검사
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
            passed = isValidInput(finalItem);

            if (!passed) {
                System.out.println("특수문자는 입력하실 수 없습니다.");
            }

        } while(!passed);

        return finalItem;
    }

    // Quotes를 순회하면서 ID에 맞는 Quote의 Index를 검색.
    private int getIndexById(int id) {
        int index = 0;

        for (WiseSaying wiseSaying : wiseSayings) {
            if (wiseSaying.getId() == id) {
                return index;
            }
            index++;
        }
        return -1;
    }

    // 삭제나 수정 등 명령어 동작 시 keyword 셋을 추출하는 메소드
    // {"id" : "1", "something" : "foo", ...}
    private Map<String, String> extractKeywordFromCommand(String command) {
        Map<String, String> keywordItems = new HashMap<>();

        try {
            String keyboardString = command.split("\\?")[1];
            String[] keywordList = keyboardString.split("&");
            String[] items;

            for (String keyword : keywordList) {
                items = keyword.split("=");
                keywordItems.put(items[0], items[1]);
            }
        } catch (IndexOutOfBoundsException e) {
            keywordItems = null;
        }

        return keywordItems;
    }
}
