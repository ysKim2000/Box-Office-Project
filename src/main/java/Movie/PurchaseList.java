package Movie;

import java.util.Objects;
import java.util.Scanner;

import static Movie.Seat.userTicketList;

public class PurchaseList extends MenuManager {
    public void showPurchaseList() {
        Scanner scanner = new Scanner(System.in);
        showTicketList();
        System.out.println();
        showFoodList();
        System.out.println();
        while (true) {
            System.out.println("종료: exit\n");
            System.out.print("명령문) ");
            String command = scanner.nextLine();

            if (Objects.equals(command, "exit")) {
                break;
            }
            else {
                System.out.println("잘못 입력하셨습니다.");
            }
        }
    }

    private void showTicketList() {
        if (userTicketList.isEmpty()){
            System.out.println("티켓을 구매한 정보가 없습니다.\n");
        }
        else {
            System.out.println("- 영화 좌석 -");
        }
        for (String s : userTicketList) {
            System.out.println(s);
        }
    }
    private void showFoodList() {
        if (userFoodList.isEmpty()){
            System.out.println("음실을 구매한 정보가 없습니다.\n");
        }
        else {
            System.out.println("- 주문한 상품 -");
        }
        for (String s : userFoodList) {
            System.out.println(s);
        }
    }
}
