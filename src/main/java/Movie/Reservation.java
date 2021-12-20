package Movie;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class Reservation{
    public void reserve() {
        SimplifiedFacade simplifiedFacade = new SimplifiedFacade();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. 영화 예매");
            System.out.println("2. 음식 구매");
            System.out.println("종료: exit\n");
            System.out.print("명령어) ");
            String command = scanner.nextLine();

            if (Objects.equals(command, "exit")) {
                break;
            } else if (Objects.equals(command, "1")) { // 영화 예매 페이지 이동
                simplifiedFacade.getTicket();
            } else if (Objects.equals(command, "2")) {
                simplifiedFacade.getFoodAndTicket();
            } else {
                System.out.println("\n잘못 입력하셨습니다. 다시 입력해주세요.");
                continue;
            }
            break;
        }
    }
}

class MovieSystem { // Choose Movie
    public void MethodOne() {
        System.out.println("\n== 영화 선택 ==");
        MenuManager.chooseMovie();
    }
}

class SelectSequence { // Choose Food
    public void MethodTwo() {
        System.out.println("\n== 선택 ==");
        System.out.println("1. 영화 먼저 예매하기");
        System.out.println("2. 음식 먼저 주문하기");
    }
}

class EndSystem {
    public void MethodThree() {
        System.out.println("종료: exit\n");
    }
}

class SetSequenceNum {
    public void MethodFour() { // 영화 + 음식 기능 수행
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("명령어) ");
            String command = scanner.nextLine();

            if (Objects.equals(command, "exit")) {
                break;
            } else if (Objects.equals(command, "1")) { // 영화 예매 페이지 이동
                // 템플릿 메소드 패턴 A 적용
                ProcedureMenu procedureMenu = new MenuClass();
                procedureMenu.templateMethodA();
                break;
            } else if (Objects.equals(command, "2")) { // 음식 메뉴 페이지로 이동
                // 템플릿 메소드 패턴 B 적용
                ProcedureMenu procedureMenu = new MenuClass();
                procedureMenu.templateMethodB();
                break;
            } else {
                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
            }
        }
    }
}

class SimplifiedFacade {
    private final MovieSystem one;
    private final SelectSequence two;
    private final EndSystem three;
    private final SetSequenceNum four;

    public SimplifiedFacade() {
        one = new MovieSystem();
        two = new SelectSequence();
        three = new EndSystem();
        four = new SetSequenceNum();
    }

    public void getTicket() {
        one.MethodOne();
        three.MethodThree();
    }

    public void getFoodAndTicket() {
        two.MethodTwo();
        three.MethodThree();
        four.MethodFour();
    }
}

abstract class ProcedureMenu {
    protected abstract void orderFood();

    protected abstract void orderTicket();

    public void templateMethodA() {
        orderTicket();
        orderFood();
    }

    public void templateMethodB() {
        orderFood();
        orderTicket();
    }
}

class MenuClass extends ProcedureMenu {
    protected void orderTicket() {
        System.out.println("\n== 영화 선택 ==");
        MenuManager.chooseMovie();
    }

    protected void orderFood() {
        MenuManager.chooseFood();
    }
}

class MenuManager {
    // 상수 설정
    // 요청(Request) 요청 변수
    private static final String REQUEST_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private static final String AUTH_KEY = "999bdc7e274c0a5e1557a0642d612aee";

    // 일자 포맷
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMdd");

    // Map -> QueryString
    public static String makeQueryString(Map<String, String> paramMap) {
        final StringBuilder sb = new StringBuilder();

        paramMap.forEach((key, value) -> {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(key).append('=').append(value);
        });

        return sb.toString();
    }

    public static void chooseMovie() {
        List<String> movieNameList = new ArrayList<>();
        // 변수설정
        // 하루전 날짜
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1); // 당일은 안됨

        // 변수 설정
        // 요청(Request) 인터페이스 Map
        // 어제자 한국영화 10개 조회
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("key", AUTH_KEY);                        // 발급받은 인증키
        paramMap.put("targetDt", DATE_FMT.format(cal.getTime()));  // 조회하고자 하는 날짜
        paramMap.put("itemPerPage", "3");                            // 결과 ROW 의 개수( 최대 10개 )
        paramMap.put("multiMovieYn", "");                             // Y:다양성 영화, N:상업영화, Default:전체
        paramMap.put("repNationCd", "");                             // K:한국영화, F:외국영화, Default:전체

        try {
            // Request URL 연결 객체 생성
            URL requestURL = new URL(REQUEST_URL + "?" + makeQueryString(paramMap));
            HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();

            // GET 방식으로 요청
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // 응답(Response) 구조 작성
            // Stream -> JSONObject
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String readline;
            StringBuilder response = new StringBuilder();

            while ((readline = br.readLine()) != null) {
                response.append(readline);
            }

            JSONParser parser = new JSONParser();
            JSONObject responseBody = (JSONObject) parser.parse(response.toString());

            // 데이터 추출
            JSONObject boxOfficeResult = (JSONObject) responseBody.get("boxOfficeResult");

            // 박스오피스 목록 출력
            JSONArray dailyBoxOfficeList = (JSONArray) boxOfficeResult.get("dailyBoxOfficeList");
            for (Object o : dailyBoxOfficeList) {
                JSONObject boxOffice = (JSONObject) o;
                System.out.printf("  %s - %s \n", boxOffice.get("rnum"), boxOffice.get("movieNm"));
                //boxOffice.get("movieCd")
                movieNameList.add((String) boxOffice.get("movieNm"));
            }
            System.out.println("종료: exit\n");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("명령어) ");
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    String movieRankedOne = movieNameList.get(0);
                    Seat.reserve(movieRankedOne);
                    break;
                case "2":
                    String movieRankedTwo = movieNameList.get(1);
                    Seat.reserve(movieRankedTwo);
                    break;
                case "3":
                    String movieRankedThree = movieNameList.get(2);
                    Seat.reserve(movieRankedThree);
                    break;
                case "exit":
                    break;
                default:
                    System.out.println("잘못입력하셨습니다. 다시 입력해주세요");
                    continue;
            }
            break;
        }
    }

    static List<String> userFoodList = new ArrayList<>();
    public static void chooseFood() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n== MENU ==");
            System.out.println("1. 팝콘");
            System.out.println("2. 콜라");
            System.out.println("3. 오징어 구이");
            System.out.println("4. 나쵸");
            System.out.print("\n명령어) ");
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    System.out.println("\n== 팝콘 ==");
                    System.out.println("어떤 맛으로 고르시겠습니까?");
                    System.out.println("1. 오리지널");
                    System.out.println("2. 카라멜");
                    System.out.println("3. 치즈");
                    System.out.print("\n명령어) ");
                    String taste = scanner.nextLine();
                    switch (taste) {
                        case "1":
                            System.out.println("\n오리지널 맛으로 구매하시겠습니까?(Yes or No) ");
                            System.out.print("\n명령어) ");
                            String a;
                            a = scanner.nextLine();
                            if (a.equals("y") || a.equals("Y") || a.equals("Yes") || a.equals("yes") || a.equals("YES")) {
                                System.out.println("\n오리지널 맛으로 구매완료되었습니다. ");
                                userFoodList.add("오리지널 팝콘");
                            } else if (a.equals("n") || a.equals("N") || a.equals("No") || a.equals("no") || a.equals("NO")) {
                                System.out.println("\n구매취소되었습니다. ");
                            } else{
                                System.out.println("잘못 입력하셨습니다.");
                                continue;
                            }
                            break;

                        case "2":
                            System.out.println("\n카라멜 맛으로 구매하시겠습니까?(Yes or No) ");
                            System.out.print("\n명령어) ");
                            String b;
                            b = scanner.nextLine();
                            if (b.equals("y") || b.equals("Y") || b.equals("Yes") || b.equals("yes") || b.equals("YES")) {
                                System.out.println("\n카라멜 맛으로 구매완료되었습니다. ");
                                userFoodList.add("카라멜맛 팝콘");
                            } else if (b.equals("n") || b.equals("N") || b.equals("No") || b.equals("no") || b.equals("NO")) {
                                System.out.println("\n 구매취소되었습니다. ");
                            } else {
                                System.out.println("잘못 입력하셨습니다.");
                            }
                            break;
                        case "3":
                            System.out.println("\n치즈 맛으로 구매하시겠습니까?(Yes or No) ");
                            System.out.print("\n명령어) ");
                            String c;
                            c = scanner.nextLine();
                            if (c.equals("y") || c.equals("Y") || c.equals("Yes") || c.equals("yes") || c.equals("YES")) {
                                System.out.println("\n치즈 맛으로 구매완료되었습니다. ");
                                userFoodList.add("치즈맛 팝콘");
                            } else if (c.equals("n") || c.equals("N") || c.equals("No") || c.equals("no") || c.equals("NO")) {
                                System.out.println("\n구매취소되었습니다. ");
                            } else {
                                System.out.println("잘못 입력하셨습니다.");
                            }
                            break;
                        default:
                            System.out.println("잘못된 입력입니다.");
                            break;
                    }
                    break;
                case "2":
                    System.out.println("\n== 콜라 ==");
                    System.out.println("사이즈를 골라주세요");
                    System.out.println("1. Large");
                    System.out.println("2. Medium");
                    System.out.println("3. Small");
                    System.out.print("\n명령어) ");
                    String size = scanner.nextLine();
                    switch (size) {
                        case "1":
                            System.out.println("\n Large 사이즈로 구매하시겠습니까?(Yes or No) ");
                            System.out.print("\n명령어) ");
                            String cokeL;
                            cokeL = scanner.nextLine();
                            if (cokeL.equals("y") || cokeL.equals("Y") || cokeL.equals("Yes") || cokeL.equals("yes") || cokeL.equals("YES")) {
                                System.out.println("\n Large 사이즈로 구매완료되었습니다. ");
                                userFoodList.add("콜라(Large)");
                            } else if (cokeL.equals("n") || cokeL.equals("N") || cokeL.equals("No") || cokeL.equals("no") || cokeL.equals("NO")) {
                                System.out.println("\n 구매취소되었습니다. ");
                            } else {
                                System.out.println("잘못 입력하셨습니다.");
                            }
                            break;

                        case "2":
                            System.out.println("\n Medium 사이즈로 구매하시겠습니까?(Yes or No) ");
                            System.out.print("\n명령어) ");
                            String cokeM;
                            cokeM = scanner.nextLine();
                            if (cokeM.equals("y") || cokeM.equals("Y") || cokeM.equals("Yes") || cokeM.equals("yes") || cokeM.equals("YES")) {
                                System.out.println("\n Medium 사이즈로 구매완료되었습니다. ");
                                userFoodList.add("콜라(Medium)");
                            } else if (cokeM.equals("n") || cokeM.equals("N") || cokeM.equals("No") || cokeM.equals("no") || cokeM.equals("NO")) {
                                System.out.println("\n 구매취소되었습니다. ");
                            } else {
                                System.out.println("잘못 입력하셨습니다.");
                            }
                            break;
                        case "3":
                            System.out.println("\n Small 사이즈로 구매하시겠습니까?(Yes or No) ");
                            System.out.print("\n명령어) ");
                            String cokeS;
                            cokeS = scanner.nextLine();
                            if (cokeS.equals("y") || cokeS.equals("Y") || cokeS.equals("Yes") || cokeS.equals("yes") || cokeS.equals("YES")) {
                                System.out.println("\n Small 사이즈로 구매완료되었습니다. ");
                                userFoodList.add("콜라(Small)");
                            } else if (cokeS.equals("n") || cokeS.equals("N") || cokeS.equals("No") || cokeS.equals("no") || cokeS.equals("NO")) {
                                System.out.println("\n 구매취소되었습니다. ");
                            } else {
                                System.out.println("잘못 입력하셨습니다.");
                            }
                            break;
                    }
                    break;

                case "3":
                    System.out.println("\n== 오징어 구이 ==");
                    System.out.println("\n오징어 구이를 고르시겠습니까?(Yes or No) ");
                    System.out.print("\n명령어) ");
                    String sq;
                    sq = scanner.nextLine();
                    if (sq.equals("y") || sq.equals("Y") || sq.equals("Yes") || sq.equals("yes") || sq.equals("YES")) {
                        System.out.println("\n오징어 구이가 구매완료되었습니다. ");
                        userFoodList.add("오징어 구이");
                    } else if (sq.equals("n") || sq.equals("N") || sq.equals("No") || sq.equals("no") || sq.equals("NO")) {
                        System.out.println("\n 구매 취소되었습니다. ");
                    } else {
                        System.out.println("잘못 입력하셨습니다.");
                    }
                    break;
                case "4":
                    System.out.println("\n== 나쵸 ==");
                    System.out.println("나쵸를 고르시겠습니까?(Yes or No)");
                    String na;
                    na = scanner.nextLine();
                    if (na.equals("y") || na.equals("Y") || na.equals("Yes") || na.equals("yes") || na.equals("YES")) {
                        System.out.println("\n나쵸가 구매완료되었습니다. ");
                        userFoodList.add("나쵸");
                    } else if (na.equals("n") || na.equals("N") || na.equals("No") || na.equals("no") || na.equals("NO")) {
                        System.out.println("\n구매취소되었습니다. ");
                    } else {
                        System.out.println("잘못 입력하셨습니다.");
                    }
                    break;
                case "exit":
                    System.out.println("메뉴 선택 중에는 종료할 수 없습니다.");
                    continue;
                default:
                    System.out.println("\n잘못된 입력입니다.");
                    continue;
            }

            System.out.println("음식주문을 계속하시겠습니까?(Yes or No)");
            System.out.print("\n명령어) ");
            String order;
            order = scanner.nextLine();
            if (order.equals("y") || order.equals("Y") || order.equals("Yes") || order.equals("yes") || order.equals("YES")) {
                System.out.println();
            } else if (order.equals("n") || order.equals("N") || order.equals("No") || order.equals("no") || order.equals("NO")) {
                break;
            } else {
                System.out.println("잘못 입력하셨습니다.");
            }
        }
    }
}

class Seat {
    static Map<String, Integer[][]> map = new HashMap<>();
    static List<String> userTicketList = new ArrayList<>();
    public static void reserve(String movie) { // ex: movie = 이터널스
        Integer[][] seats = map.getOrDefault(movie, new Integer[5][5]);     // 2차원 배열을 이용
        String strColumn;   // 열이름
        char inputColumn;
        int rowNum;         // 행번호
        Scanner sc = new Scanner(System.in);
        boolean isRun = true;   // 반복 flag
        do {
            System.out.println();
            System.out.println(movie);
            System.out.println("──────────────────SCREEN──────────────────");
            System.out.println();
            System.out.print("       ");
            for (int i = 0; i < seats.length; i++) {
                System.out.print(" [ " + (i + 1) + " ] ");
            }
            System.out.println();
            for (int i = 0; i < seats.length; i++) {
                System.out.println();
                System.out.print(" [ " + (char) (i + 65) + " ] ");
                for (int j = 0; j < seats[i].length; j++) {
                    if (seats[i][j] == null) {
                        System.out.print(" [ □ ] ");
                    } else if (seats[i][j] == 0) {
                        System.out.print(" [ □ ] ");
                    } else {
                        System.out.print(" [ ■ ] ");
                    }
                }
                System.out.println();
            }
            System.out.print("──────────────────────────────────────────\n");
            System.out.println("(예약종료는 exit)");
            System.out.print("예약하실 좌석의 열을 입력(A~E) : ");

            strColumn = sc.next();
            if (strColumn.equals("exit")) {
                System.out.println("종료되었습니다");
                break;
            }

            inputColumn = strColumn.trim().charAt(0);
            System.out.println("입력한 열 : " + inputColumn);
            if (inputColumn < 65 || inputColumn > 69) {
                System.out.println("선택할 수 없는 좌석입니다");
                continue;
            }
            int column = inputColumn - 65;
            System.out.print("예약하실 좌석의 행 번호를 입력(1~5) : ");
            rowNum = sc.nextInt();

            if (rowNum < 1 || rowNum > 5) {
                System.out.println("선택할 수 없는 행 번호입니다");
                continue;
            }
            System.out.println("선택하신 좌석은 : " + inputColumn + " 열이고 " + rowNum + " 행입니다");
            System.out.print("예약 완료 하시겠습니까 ? (Yes / No) : ");
            String s = sc.next();
            if (s.equals("y") || s.equals("Y") || s.equals("Yes") || s.equals("yes") || s.equals("YES")) {
                if (seats[column][rowNum - 1] == null) {
                    seats[column][rowNum - 1] = 1;
                    System.out.println("예약이 완료되었습니다\n");
                } else if (seats[column][rowNum - 1] == 0) {
                    seats[column][rowNum - 1] = 1;
                    System.out.println("예약이 완료되었습니다\n");
                } else {
                    System.out.print("\n이미 예약된 좌석입니다.\n");
                }
            } else if (s.equals("n") || s.equals("N") || s.equals("no") || s.equals("No") || s.equals("NO")) {
                System.out.println("취소되었습니다");
                isRun = false;
            } else {
                System.out.println("잘못 입력하셨습니다.");
            }
            userTicketList.add(movie + " " + inputColumn + "열 " + rowNum + "번" );
        }
        while (isRun);

        map.put(movie, seats);
    }
}

