package sugan.org.schedulereward;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-03.
 * updated by eunsoo on summer 2021
 */

public class Sched_data implements Serializable {
    String title;
    int  _id = -1;   // new page일 때 -1로 설정함.

    int state = 0;  // 0- 수정중=> 사용자에게 나타나지 않음,   1 - 나타남
    int seq_num;   //count(*) from linked_sched where s_id=" + sd._id
    //int reward;   //reward _id  삭제
    //String rew_desc;   //삭제
    //int rew_day;   //삭제
    ArrayList<Linked_sched_data> lds;
    Week_select_state week_select_state;

    Sched_data(){

    }

    Sched_data(boolean newSc){  // 메인 스케줄 세팅
        lds = new ArrayList<>();
        lds.add(new Linked_sched_data());  //lds.get(0)는 main schedule이다. 그다음 linked sched는 lds.get(1), ...
        lds.get(0).seq = 0;  //추후 db_seq도 0로 할 수 있음.

    }
}

class Linked_sched_data {
    String link_note;
    //int no;
    int pic;
    int seq;
    int s_id;
    //int score;
    //int db_seq;//삭제
    int reward_type;   //0 - simple, 1 - formula, 2 - no reward

    ArrayList<LinkAtom> link_Atoms = new ArrayList<>();
    ArrayList<Coupon_data> coupon_datas = null;
    String cash = "";

    String formula = "";   // reward_type이 1인 경우 사용한다 ex.
    // 1|12 - (a-3)*0.8  조건이 아닌 경우  or 2|a|>|3|a*0.2|a*0.2+0.1  조건인 경우
    If_formula if_formula = null;     //Schedule.onSaveLinkClicked에서 사용

    //CheckBox  cb;
}

class LinkAtom {
    float default_v;
    String unit;
    int length;

}

class If_formula {  // Schedule.parseFormula()
    If_formula(String if_edit_a, String if_edit_s, String if_edit_c, String if_command, String else_command){

        this.if_edit_a = if_edit_a;   //a
        this.if_edit_s = if_edit_s;   // >   R.array.if_edit
        this.if_edit_c = if_edit_c;   // 3

        this.if_command = if_command;   //a*0.2
        this.else_command = else_command;  //a*0.2+0.1

    }

    String if_edit_a = "";
    String if_edit_s = "";
    String if_edit_c = "";

    String if_command = "";
    String else_command = "";
}


