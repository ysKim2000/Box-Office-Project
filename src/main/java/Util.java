import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {
    static String getNowDateStr() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return Date.format(cal.getTime());
    }
}
