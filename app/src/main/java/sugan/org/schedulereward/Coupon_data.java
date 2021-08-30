package sugan.org.schedulereward;

/**
 * Created by eunsoo on 2017-11-23.
 */

public class Coupon_data {
    String  name;
    String ea = "1";

    String man_name;
    String date;

    String period = "0";  //default - 일회적 one-time
                           // 한달 한 번 1 ~ 28일 중 혹은 마지막 날 (값은 1 ~ 28이고, 마지막날인 경우 -1이 들어간다.  ex period = "-1" 마지막 날
                           //"1010101"  //월수금일 발행

    String s_title;
    String link_note;
    boolean state = false;   //true이면 사용한 상태

    int period_id;
    int _id;  //my_coupon _id
    String price;  //쿠폰 한개당 가격

    Coupon_data(String name) {
        this.name = name;
    }
    Coupon_data(String name, String ea){
        this(name);
        this.ea = ea;
    }
    Coupon_data() {

    }
    //int type;
}
