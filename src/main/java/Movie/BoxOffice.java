package Movie;

import java.util.Scanner;

public class BoxOffice {
    public static void showMenu(String logId) {
        Scanner scanner = new Scanner(System.in);
        label:
        while (true) {
            String texth1 = "\n== 영화관 예매 프로그램 ==\n";
            System.out.print(texth1);
            System.out.println(logId + "님 환영합니다.\n");
            System.out.println("1. 영화 정보 보기");
            System.out.println("2. 예매하기");
            System.out.println("3. 구매리스트 조회");
            System.out.println("종료: exit\n");
            System.out.print("명령어) ");
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    System.out.println("\n== 영화 정보 ==\n");
                    MovieAPI api = new MovieAPI();
                    // API 요청
                    api.requestAPI();
                    break;
                case "2":
                    System.out.println("\n== 예매하기 ==");
                    Reservation reservation = new Reservation();
                    reservation.reserve();
                    break;
                case "3":
                    System.out.println("\n== 구매 리스트 조회 ==\n");
                    PurchaseList purchaseList = new PurchaseList();
                    purchaseList.showPurchaseList();
                    break;
                case "exit":

                    break label;
                default:
                    System.out.println("\n잘못 입력하셨습니다.\n");
                    break;
            }
        }
    }
}
