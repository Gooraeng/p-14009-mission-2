package com.back.step8;

import java.util.Scanner;

public class WiseSayingMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;

        WiseSayingBoard wiseSayingBoard = new WiseSayingBoard();

        System.out.println("== 명언 앱 ==");


        while (!wiseSayingBoard.isTerminated()) {
            System.out.print("명령) ");
            command = scanner.nextLine().trim();

            try {
                wiseSayingBoard.setCommand(command);
            } catch (IllegalArgumentException e) {
                System.out.println("유효하지 않은 명령어입니다. 다시 입력해주세요.");
                continue;
            }

            switch (wiseSayingBoard.getCommand()) {
                case 등록 :
                    wiseSayingBoard.register();
                    break;
                case 수정 :
                    wiseSayingBoard.edit(command);
                    break;
                case 삭제:
                    wiseSayingBoard.remove(command);
                    break;
                case 목록:
                    wiseSayingBoard.showAll();
                    break;
            }
        }
    }
}
