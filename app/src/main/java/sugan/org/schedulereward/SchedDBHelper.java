package sugan.org.schedulereward;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by eunsoo on 2017-11-03.
 */

public class SchedDBHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "schedreward.db";
    private static int DATABASE_VERSION = 1;

    public SchedDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SCHED="CREATE TABLE sched (" +
                " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " title TEXT NOT NULL, " +
                " state INTEGER NOT NULL ) ";  //0- 작업중 working => 사용자에게 나타나지 않음,
                                               // 스케줄의 내용이 없거나(link_note), reward_type이 simple과 formula일 떄 reward 의 내용이 채워지지 않았을 떄
                                               // 1 - 실행가능상태 =>사용자의 스케줄에 나타남.

        String CREATE_SCHED_WEEK="CREATE TABLE sched_week (" +
                " s_id INTEGER NOT NULL," +
                " day INTEGER NOT NULL," +
                " time TEXT )";

        String  CREATE_LINKEDSCHED="CREATE TABLE linked_sched (" +
                " s_id INTEGER NOT NULL," +
                " note TEXT NOT NULL," +
                " seq INTEGER NOT NULL," +   //Linked_sched_data.db_seq  / default schedule seq = 0 must exist
                " picture INTEGER NOT NULL," +
                " reward_type INTEGER NOT NULL," + // 2 - without reward,  0 - simple reward,  1 - if-formula reward
                " formula TEXT )";  // ex in case 1 - if-formula reward,
                                     // 1|12 - (a-3)*0.8   or 2|a|>|3|a*0.2|a*0.2+0.1
                                     // in case 0 - simple reward(3|5.5.. cash, 4|name|ea.. coupon, )
                                     //in case 3  - edt execute_during_time  (5|start_h|start_m|till_h|till_m|edt_cash )

        String  CREATE_LINKATOM="CREATE TABLE link_atom (" +     //in case the value of reward_type of linked_sched table  is 1
                " atom_seq INTEGER NOT NULL," +
                " s_id INTEGER NOT NULL," +
                " link_seq INTEGER NOT NULL," +
                " length INTEGER NOT NULL," +
                " unit TEXT not null," +
                " default_v TEXT not null)";


        String  CREATE_MAN = "CREATE TABLE man (" +
                //  " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " name TEXT NOT NULL UNIQUE ," +   //PRIMARY KEY
                " img TEXT," +
                //   " login_id text not null," +   //same as name, so don't allow same name.
                " pwd text not null,"+
                " pwd_on integer not null, " +   // 0 - off  , 1 - on
                " per_score integer) ";
        //   " reward  text )";

        String  CREATE_SCHEDMAN="CREATE TABLE sched_man (" +
                " s_id INTEGER not null," +
                " man_name text not null)";

        String  CREATE_DURING="CREATE TABLE during_sched (" +
                " man_name text not null," +
                " s_id   integer not null," +
                " seq  integer not null," +   //사용자가 실행한 seq까지  ex seq = 3  3까지 실행함
                " date INTEGER not null)";      // 이행한 날짜..  오늘과 비교해보아서 오늘보다 이전이면 삭제한다.


        String  CREATE_SUM="CREATE TABLE sum (" +
                " man_name text NOT NULL," +
                " s_id INTEGER NOT NULL," +
                " s_title text, " +  //나중에 추가됨.  스케줄 이행내역(정산관리)을 위해
                " link_note text, " + //나중에 추가됨.  스케줄 이행내역(정산관리)을 위해
                " seq INTEGER NOT NULL," +
                " date  integer not null," +
                " reward  TEXT not null," +  // cash - 그냥 숫자 ex. 35.7
                                           // coupon인 경우 coupon|nameA|1|nameB|3  그리고 my_coupon table에 nameA쿠폰 1번 그리고 nameB 쿠폰을 3번 저장한다.
                " picture text)";

        String CREATE_MY_COUPON="CREATE TABLE my_coupon (" +
                " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " man_name TEXT NOT NULL," +
                " name text not null, " +
                " date integer not null, " +
                " price_ea TEXT )";  //무료이면 NULL


        String CREATE_COUPON= "CREATE TABLE COUPON (" +
                " name text not null UNIQUE )";

        String CREATE_PERIODIC_COUPON = "CREATE TABLE PERIODIC_COUPON (" +
                " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " name text not null, " +
                " create_date text not null, " +
                " ea integer not null, " +
                " man_name TEXT not null, " +
                " periodic_date text not null, " +    //매달 한번 주어지는 쿠폰은 한자리~두자리이다. 매달의 1 ~28일 and -1(마지막날) 쿠폰이 주어지고
        // 주단위로 주어지는 쿠폰은 월화수목금토일 이며, 1이면 주는 요일이다. ex  0100100  - 화 와 금에 쿠폰이 발행된다.

                " price_ea TEXT )";  //무료이면 NULL

        String CREATE_CHECK_PERIOD_COUPON = "CREATE TABLE CHECK_PERIOD_COUPON (" +
                " date integer not null )";   //마지막으로 체크한 날 저장
/*
                String  CREATE_SUM_RES="CREATE TABLE sum_res (" +
                " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " man_id INTEGER NOT NULL," +
                " s_id INTEGER ," +     //0 - 보너스 점수일 경우
                " s_title text ," +
                " date integer not null," +
                " rew_id integer not null," +  //normal case: -1  one-day case reward_id  , 0 - 보너스 쿠폰
                " score integer," +   //gained score in normal case or gained links in oneday rew case.
                " reward  text )" ;    //gained reward in oneday reward case.

        String  CREATE_USED_SCORE = "CREATE TABLE used_score_list (" +
                " man_id integer not null," +
                " date integer not null," +
                " score integer not null ) ";


        String  CREATE_DURING="CREATE TABLE during_sched (" +
                " man_id integer not null," +
                " s_id   integer not null," +
                " seq  integer not null," +
                " skip_num integer," +  // 일일보상일 때 skip_num 저장해둘 필요. 백해서 돌아올 경우나, 중간에 다시 들어올 경우
                " end_time  text not null," +
                " state  text )";

        String CREATE_REWARD= "CREATE TABLE REWARD (" +   //테이블 삭제할 것
                " _id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " rew_desc text not null," +
                " type integer not null )" ;    //0 - 오늘 1 - 1일후 , 2 - 2일후 ,
        String CREATE_SCORE="CREATE TABLE score ("+
                " man_id integer not null," +
                " per_score integer not null, " +
                " goods text )" ;*/

        try {
            db.execSQL(CREATE_SCHED);
            db.execSQL(CREATE_SCHED_WEEK);
            db.execSQL(CREATE_MAN);
            db.execSQL(CREATE_LINKEDSCHED);
            db.execSQL(CREATE_LINKATOM);
            db.execSQL(CREATE_SCHEDMAN);
            db.execSQL(CREATE_SUM);
            db.execSQL(CREATE_MY_COUPON);
           // db.execSQL(CREATE_SUM_RES);
            //db.execSQL(CREATE_USED_SCORE);
            db.execSQL(CREATE_DURING);
           // db.execSQL(CREATE_REWARD);
            db.execSQL(CREATE_COUPON);
            db.execSQL(CREATE_PERIODIC_COUPON);
            db.execSQL(CREATE_CHECK_PERIOD_COUPON);

            db.execSQL("insert into man values ('admin', null,'1111', 0, null)");
           // db.execSQL("insert into CHECK_PERIOD_COUPON values ('210809')");


        } catch(Exception ex) {
            Log.e( "SchedDBHelper", "Exception in create table", ex);
            Log.i("SchedDBHelper", "exception in create table");

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

