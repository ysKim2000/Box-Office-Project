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

public class MovieAPI {

    // 일자 포맷
    private final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMdd");

    // Map -> QueryString
    public String makeQueryString(Map<String, String> paramMap) {
        final StringBuilder sb = new StringBuilder();

        paramMap.forEach((key, value) -> {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(key).append('=').append(value);
        });

        return sb.toString();
    }

    public void requestAPI() {
        // 변수설정
        // 하루전 날짜
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1); // 당일은 안됨

        // 변수 설정
        // 요청(Request) 인터페이스 Map
        // 어제자 한국영화 10개 조회
        Map<String, String> paramMap = new HashMap<>();
        String AUTH_KEY = "999bdc7e274c0a5e1557a0642d612aee";
        paramMap.put("key"          , AUTH_KEY);                        // 발급받은 인증키
        paramMap.put("targetDt"     , DATE_FMT.format(cal.getTime()));  // 조회하고자 하는 날짜
        paramMap.put("itemPerPage"  , "5");                            // 결과 ROW 의 개수( 최대 10개 )
        paramMap.put("multiMovieYn" , "");                             // Y:다양성 영화, N:상업영화, Default:전체
        paramMap.put("repNationCd"  , "");                             // K:한국영화, F:외국영화, Default:전체

        try {
            // Request URL 연결 객체 생성
            // 상수 설정
            // 요청(Request) 요청 변수
            String REQUEST_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
            URL requestURL = new URL(REQUEST_URL +"?"+makeQueryString(paramMap));
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

            // 박스오피스 주제 출력
            String boxOfficeType = boxOfficeResult.get("boxofficeType").toString();
            System.out.print(boxOfficeType);
            System.out.println(" 순위");

            // 박스오피스 목록 출력
            JSONArray dailyBoxOfficeList = (JSONArray) boxOfficeResult.get("dailyBoxOfficeList");
            for (Object o : dailyBoxOfficeList) {
                JSONObject boxOffice = (JSONObject) o;
                System.out.printf("  %s - %s \n", boxOffice.get("rnum"), boxOffice.get("movieNm"));
                //boxOffice.get("movieCd")- 영화코드, boxOffice.get("audiAcc")- 관객수, boxOffice.get("openDt")- 개봉일
                System.out.printf("  개봉일: %s,",boxOffice.get("openDt"));
                System.out.printf("  관객수: %s,",boxOffice.get("audiAcc"));
                System.out.printf("  누적 매출액: %s ",boxOffice.get("salesAcc"));
                System.out.println("\n");
            }
            System.out.println("종료: exit\n");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("명령어) ");
            String command = scanner.nextLine();
            if (Objects.equals(command, "exit")) {
                break;
            }
            else{
                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
            }
        }
    }
}
