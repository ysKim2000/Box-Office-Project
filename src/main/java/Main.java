import Movie.BoxOffice;

import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        DB db = new DB();
        db.loadGlobal();

        MemberDao memberDao = new MemberDao(db);
        MemberService memberService = new MemberService(memberDao);
        MemberController controller = new MemberController(scanner, memberService);

        label:
        while (true) {
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            System.out.println("3. 비회원 로그인");
            System.out.println("종료: exit\n");
            System.out.print("명령어) ");
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    controller.doCommandJoin();
                    break;
                case "2":
                    controller.doCommandLogin();
                    break;
                case "3":
                    controller.doNonLogin();
                    break;
                case "exit":
                    break label;
            }
        }
        scanner.close();
    }

    private static class MemberController {
        Scanner scanner;

        MemberService memberService;

        MemberController(Scanner scanner, MemberService memberService) {
            this.memberService = memberService;
            this.scanner = scanner;
        }

        public boolean isNumeric(String str) { // 숫자 검사기
            return Pattern.matches("^[0-9]*$", str);
        }

        public boolean isAlpha(String str) { // 영어 검사기
            return Pattern.matches("^[a-zA-Z]*$", str);
        }

        public boolean checkDate(String checkDate) {
            try {
                SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMdd"); //검증할 날짜 포맷 설정
                dateFormatParser.setLenient(false); //false일경우 처리시 입력한 값이 잘못된 형식일 시 오류가 발생
                dateFormatParser.parse(checkDate); //대상 값 포맷에 적용되는지 확인
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public boolean isKorean(String str) { // 한국어 검사기
            return Pattern.matches("[가-힣]*$", str);
        }

        public boolean isAlphaNumeric(String str) { // 영문 + 숫자
            return Pattern.matches("[a-zA-Z0-9]*$", str);
        }

        void doNonLogin(){
            String logId = "Non-Name";
            BoxOffice.showMenu(logId);
        }

        void doCommandJoin() {
            System.out.println("\n== 회원가입 시작 ==");

            String loginId;
            String loginPw;
            String loginBirth;
            String regName;
            String loginPwConFirm;

            while (true) {
                System.out.print("회원가입 아이디 : ");
                loginId = scanner.nextLine().trim();

                if (loginId.length() == 0) {
                    System.out.println("회원가입 할 아이디를 입력해 주세요.");
                    continue;
                }
                if (loginId.length() < 2) {
                    System.out.println("회원가입 할 아이디를 2자 이상 입력해 주세요.");
                    continue;
                }
                boolean rsId = isAlphaNumeric(loginId);
                if (rsId) {
                    break;
                } else {
                    System.out.println("영문이나 숫자로만 입력해주세요.");
                }
            }

            while (true) {
                boolean loginPwValid = true;

                while (true) {
                    System.out.print("회원가입 비밀번호 : ");
                    loginPw = scanner.nextLine().trim();

                    if (loginPw.length() == 0) {
                        System.out.println("회원가입 할 비밀번호를 입력해 주세요.");
                        continue;
                    }

                    if (loginPw.length() < 2) {
                        System.out.println("비밀번호를 2자 이상 입력해 주세요.");
                        continue;
                    }
                    break;
                }

                while (true) {
                    System.out.print("회원가입 비밀번호 확인 : ");
                    loginPwConFirm = scanner.nextLine().trim();

                    if (loginPwConFirm.length() == 0) {
                        System.out.println("비밀번호 확인을 위해 설정한 비밀번호를 입력해 주세요.");
                        continue;
                    }
                    if (!loginPw.equals(loginPwConFirm)) {
                        System.out.println("입력한 비밀번호와 일치하지 않습니다.");
                        loginPwValid = false;
                        break;
                    }
                    break;
                }
                if (loginPwValid) {
                    break;
                }
            }
            while (true) {
                System.out.print("이름 : ");
                regName = scanner.nextLine().trim();
                boolean rsEngName = isAlpha(regName);
                boolean rsKorName = isKorean(regName);
                if (regName.length() == 0) {
                    System.out.println("이름을 입력해주세요.");
                } else if (rsEngName || rsKorName) {
                    break;
                } else {
                    System.out.println("올바르지 않은 형식입니다.\n");
                }
            }

            while (true) {
                System.out.print("생년월일(ex:20000411) : ");
                loginBirth = scanner.nextLine().trim();

                if (loginBirth.length() == 0) {
                    System.out.println("생년월일을 입력해 주세요.");
                    continue;
                }
                boolean rsNumBth = isNumeric(loginBirth);
                if (!rsNumBth) {
                    System.out.println("숫자만 입력해주세요.");
                    continue;
                }
                boolean rsBth = checkDate(loginBirth);
                if (rsBth) {
                    break;
                } else {
                    System.out.println("올바르지 않은 형식입니다.");
                }
            }
            boolean rs = memberService.join(loginId, loginPw, loginBirth, regName);

            if (rs) {
                System.out.println("회원가입 성공\n");
            } else {
                System.out.println("회원가입 실패\n");
            }
            System.out.println("== 회원가입 끝 ==\n");
        }

        void doCommandLogin() {
            System.out.println("\n== 로그인 시작 ==");
            while (true) {
                System.out.print("아이디 : ");
                String logId = scanner.nextLine();
                boolean rsId = memberService.isUsedLoginId(logId);
                if (rsId) {
                    System.out.print("비밀번호 : ");
                    String logPw = scanner.nextLine();
                    boolean rsPw = memberService.isUsedLoginPw(logId, logPw);
                    if (rsPw) { // 비밀번호가 맞을 경우 -> 영화 메뉴로 이동
                        System.out.println("로그인 성공");
                        BoxOffice.showMenu(logId);
                        break;
                    } else {
                        System.out.println("잘못된 비밀번호입니다. 다시 시도해주세요.\n");

                    }
                } else {
                    System.out.println("존재하지 않은 아이디입니다. 다시 입력해주세요.\n");
                }
            }
            System.out.println("== 로그아웃 ==\n");
        }
    }
}
