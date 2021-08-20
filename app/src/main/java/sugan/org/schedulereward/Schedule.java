package sugan.org.schedulereward;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by eunsoo on 2017-11-03.
 */

public class Schedule {
    //static final int NO_RADIO_CHECKBS = 0;
    static AlertDialog linkDia;
    static AlertDialog atomDia;
    static AlertDialog dtDialog;
    static AlertDialog dialog2;
    static LinearLayout day_time;
    static ScrollView timepicker_select;
    static boolean link_noted = false;
    static boolean scored = false;
    static TextView save_link;
    static TextView modify_link ;
    static TextView del_link;
    static EditText link_note ;
    static EditText formula ;
    static Spinner  reward_type;
    static LinearLayout score_layout;
    static EditText score;
    //static Switch no_select;
    static Switch pic_select;
    static CheckBox cb;
    static String s_time;
    static String e_time;
    static TextView selected_day;
    static boolean one_day_rew= false;
    static Boolean_wrapper add_if = new Boolean_wrapper() ;


    static LinearLayout linkDiaLayout = null ;   //추후 삭제
    static String if_edit_s1;

    ArrayList<Sched_data> scheds;

   // static int atom_length;
   // static ArrayList<LinkAtom> currentlink_Atoms = new ArrayList<>();


    static ScheduleActivity sa;

    //ArrayList<ArrayList<Linked_sched_data>> s_links;

    Schedule(ScheduleActivity sa){
        this.sa = sa;
    }

    public void fillSpinner( Spinner spinner) {

        SchedDBHelper sHelper = new SchedDBHelper(sa);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select title, _id from sched order by _id desc", null);

        //cursor = db.rawQuery("select title, _id, mon, tue, wed, thu, fri, sat, sun, reward, " +
        //                " skip_num from sched order by _id desc", null);

        scheds = new ArrayList<Sched_data>();
        //s_links = new ArrayList<ArrayList<Linked_sched_data>>();
        String[] titles = new String[cursor.getCount()];  int i=0;

        while(cursor.moveToNext()) {
            Sched_data sched = new Sched_data();
            ArrayList<Linked_sched_data> lds = new ArrayList<Linked_sched_data>();
            //s_links.add(lds);

            sched.title = cursor.getString(0);
            titles[i++] = sched.title;
            sched._id = cursor.getInt(1); /*
            sched.mon = cursor.getString(2);
            sched.tue = cursor.getString(3);
            sched.wed = cursor.getString(4);
            sched.thu = cursor.getString(5);
            sched.fri = cursor.getString(6);
            sched.sat = cursor.getString(7);
            sched.sun = cursor.getString(8);
            sched.reward = cursor.getInt(9);    Log.i("reward---", sched.reward+ " " + sched.skip_num);
            sched.skip_num = cursor.getInt(10);*/
            scheds.add(sched);
/*
            String sql1 = "select note, s_id, seq, score, type, picture" +
                    " from linked_sched where s_id=" + sched._id +" order by seq";
            Cursor cursor1;
            cursor1 = db.rawQuery(sql1, null);
            int j=1;
            while(cursor1.moveToNext()) {
                Linked_sched_data ld = new Linked_sched_data();
                ld.link_note = cursor1.getString(0);
                ld.s_id = cursor1.getInt(1);
                ld.db_seq = cursor1.getInt(2);
                ld.score = cursor1.getInt(3);
                ld.no = cursor1.getInt(4);
                ld.pic = cursor1.getInt(5);
                ld.seq = j++;
                lds.add(ld);
            }
            cursor1.close();            */
        }
        ArrayAdapter<CharSequence> adapter;
        adapter = new ArrayAdapter<CharSequence>(sa,
                android.R.layout.simple_spinner_item, titles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        cursor.close();
        sHelper.close();

    }
    public static Sched_data getSchedData(int s_id) {

        SchedDBHelper sHelper = new SchedDBHelper(sa);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select title, _id, reward, "  +
                                 " skip_num  from sched where _id="+ s_id, null);

        cursor.moveToNext();
        Sched_data sched = new Sched_data();
        sched.lds = new ArrayList<Linked_sched_data>();
        sched.week_select_state = new Week_select_state(sa);
            //s_links.add(lds);

            sched.title = cursor.getString(0);
            sched._id = cursor.getInt(1);

            sched.state = cursor.getInt(2); //   Log.i("reward---", sched.reward+ " " + sched.skip_num);
           // sched.skip_num = cursor.getInt(3);

            String sql1 = "select note, s_id, seq, score, type, picture, formula " +
                    " from linked_sched where s_id=" + sched._id +" order by seq";
            Cursor cursor1;
            cursor1 = db.rawQuery(sql1, null);
            int j=1;
            while(cursor1.moveToNext()) {
                Linked_sched_data ld = new Linked_sched_data();
                ld.link_note = cursor1.getString(0);
                ld.s_id = cursor1.getInt(1);
                ld.db_seq = cursor1.getInt(2);
               // ld.score = cursor1.getInt(3);
                //ld.no = cursor1.getInt(4);
                ld.pic = cursor1.getInt(5);
                ld.seq = j++;
                String formula = cursor1.getString(6);

                if(! formula.equals("")) {
                    parseFormula(ld, formula);
                    ld.link_Atoms = new ArrayList<>(5);

                    String sql2 = "select length, unit, default_v from link_atom where  s_id=" + sched._id +
                            " and link_seq =" + ld.seq + " order by atom_seq ";
                    Cursor cursor2;
                    cursor2 = db.rawQuery(sql2, null);
                    while(cursor2.moveToNext()) {
                        LinkAtom atom = new LinkAtom();
                        atom.length = cursor2.getInt(0);
                        atom.unit = cursor2.getString(1);
                        atom.default_v = Float.parseFloat(cursor2.getString(2));
                        ld.link_Atoms.add(atom);

                    }
                    cursor2.close();
                }


                sched.lds.add(ld);
            }
            cursor1.close();
        cursor.close();
        Cursor cursor2;
        cursor2 = db.rawQuery("select day, time  from sched_week where s_id="+ s_id + " order by s_id, day ", null);
        while(cursor2.moveToNext()) {
            Day_state ds = sched.week_select_state.week_state[cursor2.getInt(0)];
            String time = cursor2.getString(1);
            if(time==null)
            ds.state = 1;
            else {
                ds.state = 2;
                ds.time = time;
                Log.i("time = ", time);
            }
            }
        for(int i=0; i<7; i++) Log.i("day_state[" + i + "]", sched.week_select_state.week_state[i].state + " ");

        cursor2.close();
        sHelper.close();
        return sched;

    }

    public static void parseFormula(Linked_sched_data ld, String formula) {
        String[] r = formula.split("\\|");
        for(int i=0; i<r.length; i++) {
            Log.i("r["+i+"]", r[i] + " ");
        }

        if(r[0].equals("1")){
            ld.formula = r[1]; Log.i("ld.formula = " , ld.formula + " ");}
        else ld.if_formula = new If_formula(r[1], r[2], r[3], r[4], r[5]);

    }

    public static void delSched(Context context,  String s_id ) {
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(context);
        String sql = "delete from sched where _id= " + s_id;
        String sql1 = "delete from linked_sched where s_id= " + s_id;
        String sql2 = "delete from sched_man where s_id= " + s_id;
        String sql3 = "delete from sched_week where s_id= " + s_id;
        String sql4 = "delete from link_atom where s_id= " + s_id;
        String sql5 = "delete from during_sched where s_id= " + s_id;

        db = sHelper.getWritableDatabase();
        db.execSQL(sql);
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        db.execSQL(sql5);

        sHelper.close();
    }

    public static void delMSc(String m_id, String s_id) {
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(sa);
        String sql = "delete from sched_man  where s_id= " + s_id +" and man_id="+ m_id;
        db = sHelper.getWritableDatabase();
        db.execSQL(sql);

        sHelper.close();
    }
    public static void delLink(String s_id, String seq ) {
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(sa);
        String sql = "delete from linked_sched where s_id= " + s_id +" and seq="+ seq;
        db = sHelper.getWritableDatabase();
        db.execSQL(sql);

        sHelper.close();
    }

    public static void onSaveDayTime(Context context) {
        dtDialog.dismiss();

    }



    public static FragmentSchedAdapter getRightNowList_frag(Context context, ArrayList<Sched_data> group,
                                                             Man_data man) {
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(context);
        Cursor cursor;
        db = sHelper.getWritableDatabase();

        int nWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); //1-일 2-월 3-화 4-수 5-목 6-금 7-토
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        String strNow = sdfNow.format(date);   // current time

        String day = "sun";
        if(nWeek==1) day = "sun";
        else if(nWeek==2) day="mon";
        else if(nWeek==3) day="tue";
        else if(nWeek==4) day="wed";
        else if(nWeek==5) day="thu";
        else if(nWeek==6) day="fri";
        else if(nWeek==7) day="sat";
        String strNow1 = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));

        String sql = "select  title, _id,  from sched "
      //  String sql = "select  title, _id, "+ day +", reward, skip_num  from sched "
                + " where _id in (select s_id from sched_man where man_name = "+ man.name
                + ") and "+ day+" is not null and _id not in (select s_id from during_sched where "
                + " man_name= " + man.name + " and end_time='"+ strNow1 + "' and state='done' ) order by _id desc";
        cursor = db.rawQuery(sql, null);
        FragmentSchedAdapter adapter = null;

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {


                String today = cursor.getString(2);

                String start_time = today.substring(0, 4);
                String end_time =   today.substring(8, 12);

                if (strNow.compareTo(start_time)>=0 && strNow.compareTo(end_time)<= 0 ) {

                    Sched_data sd = new Sched_data();
                    sd._id = cursor.getInt(1);
                    //TextView imsi = new TextView(context);
                  //  imsi.setText(R.string.one_day_rew);
                    sd.title =
                            cursor.getString(0) ;
                  //  sd.reward = cursor.getInt(3);
                  //  sd.skip_num = cursor.getInt(4);
                    String sql1 = "select count(*) from linked_sched where s_id=" + sd._id;
                   // String sql2 = "select rew_desc, type from reward where _id=" + sd.reward;
                    group.add(sd);
                    Cursor cursor2 = db.rawQuery(sql1, null);
                  //  Cursor cursor3 = db.rawQuery(sql2, null);
                    cursor2.moveToNext();
                    sd.seq_num = cursor2.getInt(0);
                   /* if(cursor3.getCount()>0){
                        cursor3.moveToNext();
                        sd.rew_desc = cursor3.getString(0);  Log.i("sd.rew_desc", sd.rew_desc);
                        sd.rew_day = cursor3.getInt(1);
                    }*/

                    cursor2.close();  //cursor3.close();

                }
            }
            adapter = new FragmentSchedAdapter(context, group, man );
        }
        cursor.close();
        sHelper.close();

        return adapter;
    }

    public static int getDayOfWeek() {   //day = 0 mon, 1 tue, ... 5 sat, 6 sun
        int nWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); //1-일 2-월 3-화 4-수 5-목 6-금 7-토
        Log.i("nweek", nWeek + " ");

        int day =0;
        if(nWeek==1) day = 6;    //sun
        else if(nWeek==2) day = 0;  //mon
        else if(nWeek==3) day = 1;
        else if(nWeek==4) day = 2;
        else if(nWeek==5) day = 3;
        else if(nWeek==6) day = 4;
        else if(nWeek==7) day = 5;

        return day;
    }

    static boolean isNotCompletedToday(int s_id, int m_id, Context context){  //sum data에 있으면서 during_sched에 없으면 완료된 상태임.

        SchedDBHelper sHelper = new SchedDBHelper(context);;
        SQLiteDatabase db = sHelper.getWritableDatabase();;

        Cursor cursor;
        String sql = "select seq from sum where s_id = " + s_id + " and man_id = " + m_id + " and date = " + Util.today();
        cursor = db.rawQuery(sql, null);

        if(cursor.moveToNext()) { // Log.i("sum data exist", cursor.getInt(0) + " ");
            sql = "select seq from during_sched where s_id = " + s_id + " and man_id = " + m_id + " and date = " + Util.today();
            Cursor cursor1 = db.rawQuery(sql, null);
            if(cursor1.moveToNext()) { //Log.i("during_sched data exist", cursor1.getInt(0) + " ");
                return true;
            }
            return false;
        }
        else return true;


    }

    public static boolean isNowSCheduledTime(int s_id, Context context) {

        boolean result = false;

        SchedDBHelper sHelper;
        SQLiteDatabase db;
        sHelper = new SchedDBHelper(context);
        Cursor cursor;
        db = sHelper.getWritableDatabase();

        int nWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); //1-일 2-월 3-화 4-수 5-목 6-금 7-토
        Log.i("nweek", nWeek + " ");

        int day = getDayOfWeek();

        String strNow = new SimpleDateFormat("hh:mm").format(new Date(System.currentTimeMillis()));

        String sql = "select  time from sched_week where s_id = " + s_id + " and day = " + day ;
        cursor = db.rawQuery(sql, null);
        if( cursor.moveToNext()) {
            String time = cursor.getString(0);
            if (time == null) {
                result = true;
            } else {
                String start_time = time.substring(0, 5);
                Log.i("start_time", start_time);
                String end_time = time.substring(8);
                Log.i("end_time", end_time);
                if (strNow.compareTo(start_time) >= 0 && strNow.compareTo(end_time) <= 0)
                    result = true;
            }
        }
        cursor.close();
        sHelper.close();
        return result;

    }

    public static CurrentSchedListAdapter  getRightNowList(Context context, ArrayList<Sched_data> group,
                                                           ArrayList<ArrayList<Linked_sched_data>> child,
                                String m_name) {

        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(context);
        Cursor cursor;
        db = sHelper.getWritableDatabase();

        String strNow = new SimpleDateFormat("hh:mm").format(new Date(System.currentTimeMillis()));

        String sql = "select title, _id from sched "
                + " where _id in (select s_id from sched_man where man_name = " + m_name
                + " ) and _id in (select s_id from sched_week where day = " + getDayOfWeek() + ") "
                + " and state != 0 ";   //나중에 테스트해볼 것 state ==0에 대해
       /* String sql = "select  title, _id, "+ day +", reward, skip_num  from sched "
        + " where _id in (select s_id from sched_man where man_id = "+ m_id
        + ") and "+ day+" is not null and _id not in (select s_id from during_sched where "
        + " man_id= " + m_id + " and end_time='"+ strNow + "' and state='done' ) order by _id desc"; */
        cursor = db.rawQuery(sql, null);
        CurrentSchedListAdapter adapter = null;
Log.i("nowsched", sql);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String s_title =  cursor.getString(0);
                int s_id = cursor.getInt(1);

                if (isNowSCheduledTime(s_id, context)
                       // && isNotCompletedToday(s_id, m_id, context)
                ) {  //when right now is scheduled for s_id && 오늘 수행되었는지 여부.

                    Sched_data sd = new Sched_data();
                    group.add(sd);
                    sd._id = s_id;
                    sd.title = s_title;
                   // sd.reward = cursor.getInt(3);
                   // sd.skip_num = cursor.getInt(4);
                    ArrayList<Linked_sched_data> notes = new ArrayList<Linked_sched_data>();

                    //int c_id = cursor.getInt(2);

                    String sql3 = "select note, s_id, seq from linked_sched where s_id=" + s_id + " order by seq";
                  //  String sql2 = "select rew_desc, type from reward where _id=" + sd.reward;

                    Cursor cursor3 = db.rawQuery(sql3, null);
                  /*  Cursor cursor2 = db.rawQuery(sql2, null);
                    if(cursor2.getCount()>0) {
                        cursor3.moveToNext();
                        sd.rew_desc = cursor2.getString(0);
                        Log.i("sd.rew_desc", sd.rew_desc);
                        sd.rew_day = cursor2.getInt(1);
                        cursor2.close();
                    }*/
                    while (cursor3.moveToNext()) {
                        Linked_sched_data ld = new Linked_sched_data();
                        ld.link_note = cursor3.getString(0);
                        ld.s_id = cursor3.getInt(1);
                        ld.seq = cursor3.getInt(2);
                        notes.add(ld);
                        Log.i("childs", ld.link_note + " " + ld.s_id + " " + ld.seq);
                    }
                    child.add(notes);
                    //sd.lds = notes;
                    sd.seq_num = notes.size();

                    cursor3.close();
                }
            }
            adapter = new CurrentSchedListAdapter(context, group, child);
        }
        Log.i("group-length", group.size()+" ");
        Log.i("child-length", child.size()+" ");
        cursor.close();
        sHelper.close();

        return adapter;
    }

    public static SchedListAdapter_base  getList(Context context,  String m_name) {
        ArrayList<Sched_data> scheds;
        SchedListAdapter_base adapter = null;

        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(sa);
        Cursor cursor;
        db = sHelper.getWritableDatabase();
        String sql = "select s_id from sched_man where man_name= " + m_name
                +" order by s_id desc";
        //String sql = "select  title, _id  from sched where order by _id desc";
        cursor = db.rawQuery(sql, null);
        if(cursor.getCount()!=0){
            scheds = new ArrayList<Sched_data>();
            while(cursor.moveToNext()) {
                Cursor cursor1;
                Sched_data sd = new Sched_data();

                String sql1 = "select _id, title, reward from sched where _id= " + cursor.getString(0);
                cursor1 = db.rawQuery(sql1, null);
                cursor1.moveToNext();
                sd._id = cursor1.getInt(0);
                TextView imsi = new TextView(sa);
                imsi.setText(R.string.one_day_rew);
                sd.title = (cursor1.getInt(2)==-1)? cursor1.getString(1): cursor1.getString(1) + " - " +
                        imsi.getText().toString();
               // sd.reward = cursor1.getInt(2);
                scheds.add(sd);
                cursor1.close();
            }
            adapter = new SchedListAdapter_base (context, scheds);
        }
        cursor.close();
        sHelper.close();
        return  adapter;
    }
    public static SchedListAdapter getList(Context context) {
        ArrayList<Sched_data> group;
        ArrayList<ArrayList<Linked_sched_data>> child;

        //SchedLink[] schedLinks;
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(context);
        Cursor cursor;
        db = sHelper.getWritableDatabase();
        String sql = "select  title, _id, state  from sched order by _id desc";
        cursor = db.rawQuery(sql, null);
        group = new ArrayList<Sched_data>();
        child = new ArrayList<ArrayList<Linked_sched_data>>();
        SchedListAdapter adapter = null;
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {

                int s_id = cursor.getInt(1);
                Sched_data sd = new Sched_data();
                sd._id = s_id;
                TextView imsi = new TextView(context);
                imsi.setText(R.string.working);
                sd.title = (cursor.getInt(2)== 1)? cursor.getString(0): cursor.getString(0) + " - " +
                        imsi.getText().toString();
                sd.state = cursor.getInt(2);
                group.add(sd);
                ArrayList<Linked_sched_data> notes = new ArrayList<Linked_sched_data>();

                //int c_id = cursor.getInt(2);

                String sql1 = "select note, s_id, seq from linked_sched where s_id=" + s_id +" order by seq";

                Log.i("sql1", sql1);
                Cursor cursor1 = db.rawQuery(sql1, null);
                while(cursor1.moveToNext()){
                    Linked_sched_data  ld = new Linked_sched_data();
                    ld.link_note = cursor1.getString(0);
                    ld.s_id = cursor1.getInt(1);
                    ld.seq = cursor1.getInt(2);
                    notes.add(ld);
                    Log.i("childs", ld.link_note+" "+ld.s_id  + " "+ld.seq);
                }
                child.add(notes);

                cursor1.close();
            }
            adapter = new SchedListAdapter(context, group, child);
        }
        Log.i("group-length", group.size()+" ");
        Log.i("child-length", child.size()+" ");
        cursor.close();
        sHelper.close();
        return adapter;
    }
    public static void  onSaveTimeClicked() {
        TimePicker ps_time = (TimePicker) timepicker_select.findViewById(R.id.s_time);
        TimePicker pe_time = (TimePicker) timepicker_select.findViewById(R.id.e_time);
        int sh = ps_time.getCurrentHour(); int sm = ps_time.getCurrentMinute();
        int eh = pe_time.getCurrentHour(); int em = pe_time.getCurrentMinute();
        String ssh;
        String ssm;
        String seh;
        String sem;
        if(sh>9)  ssh = sh+ ""; else ssh = "0" +sh;
        if(sm>9)  ssm = sm+ ""; else ssm = "0" +sm;
        if(eh>9)  seh = eh+ ""; else seh = "0" +eh;
        if(em>9)  sem = em+ ""; else sem = "0" +em;

        if(sh*100+sm >= eh*100+em) {
            Log.i("sh*100+sm >= eh*100+em", " ");
            Toast.makeText(sa, R.string.time_again, Toast.LENGTH_LONG).show();
            return ;
        }

        s_time = ssh+ ":" + ssm;
        e_time = seh + ":" +sem;

        dialog2.dismiss(); //selected_day.setTextColor(0x70000000);
        //Day_time_data  d = new Day_time_data();
        String day = ((TextView)sa.findViewById(R.id.day)).getText().toString();
        Week_select_state ws =  sa.week_select_state;
        int ds=0 ;
        for(int i=0; i<7; i++) {
            if(day.equals(ws.week_state[i].v_day.getText().toString())) {
                ds = i;
                break;
            }
        }
        String time = s_time + " ~ "+ e_time;
      //  d.day = day;
      //  d.time = time;
        Day_state d = sa.week_select_state.week_state[ds];
        d.time = time;
        d.v_time.setText(time);
        d.state = 2;
        d.v_day.setBackground(sa.getResources().getDrawable(R.drawable.day_click));
       // d.v_time.setVisibility(View.VISIBLE);

          //  d.v_time = (TextView)scheduleActivity.findViewById(R.id.sun_time);
          //  d.v_day = (TextView)((ScheduleActivity)context).findViewById(R.id.sun);


       // d.v_time.setText(time);
       // d.v_time.setVisibility(View.VISIBLE);
      //  day_time_datas[Integer.parseInt(day)] = d;
        //return day_time_data;
    }
    public static void onDayClicked( TextView v) {
            timepicker_select = (ScrollView) View.inflate(sa, R.layout.time_picker, null);
            String day = v.getText().toString();
        ((TextView)sa.findViewById(R.id.day)).setText(day);
        ((TextView)timepicker_select.findViewById(R.id.day)).setText(day);
        ((TextView)timepicker_select.findViewById(R.id.day1)).setText(day);

        dialog2 = new AlertDialog.Builder(sa)
                .setTitle(R.string.select_time)
                //.setIcon(R.drawable.androboy)
                .setView(timepicker_select)
                .setCancelable(false)
                .show();
        //selected_day.setTextColor(0x70000000);
    }
    /*
    public static void onDayTimeClicked(Context context, Day_time_data[] d) {
            day_time = (LinearLayout)((ScheduleActivity)context).findViewById(R.id.day_time);

            dtDialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.select_day)
                    //.setIcon(R.drawable.androboy)
                    .setView(day_time)
                    .setCancelable(false)
                    .show();

        Log.i("ondaytime", " "+
                ((TextView)day_time_select.findViewById(R.id.mon_time)).getText().toString());

        setDayTimeOnSelect(d);
       // return day_time_select;

    }*/
    /*
    public static void setDayTimeOnSelect(Day_time_data[] d) {
        if(d[0]!=null) {
            //Log.i("d[0].v_time!=null", "1 ");
            d[0].v_time = (TextView) day_time.findViewById(R.id.mon_time);
            //Log.i("d[0].v_time!=null", " 2");
            d[0].v_day = (TextView) day_time.findViewById(R.id.mon);
            //Log.i("d[0].v_time!=null", " 3");
            d[0].v_time.setText(d[0].time); d[0].v_day.setTextColor(Color.GREEN);
            //Log.i("d[0].v_time!=null", "4 ");

        }
        if(d[1]!=null) {
            d[1].v_time = (TextView) day_time.findViewById(R.id.tue_time);
            d[1].v_day = (TextView) day_time.findViewById(R.id.tue);
            d[1].v_time.setText(d[1].time); d[1].v_day.setTextColor(Color.GREEN);
        }if(d[2]!=null) {
            d[2].v_time = (TextView) day_time.findViewById(R.id.wed_time);
            d[2].v_day = (TextView) day_time.findViewById(R.id.wed);
            d[2].v_time.setText(d[2].time); d[2].v_day.setTextColor(Color.GREEN);
        }if(d[3]!=null) {
            d[3].v_time = (TextView) day_time.findViewById(R.id.thu_time);
            d[3].v_day = (TextView) day_time.findViewById(R.id.thu);
            d[3].v_time.setText(d[3].time); d[3].v_day.setTextColor(Color.GREEN);
        }if(d[4]!=null) {
            d[4].v_time = (TextView) day_time.findViewById(R.id.fri_time);
            d[4].v_day = (TextView) day_time.findViewById(R.id.fri);
            d[4].v_time.setText(d[4].time); d[4].v_day.setTextColor(Color.GREEN);
        }if(d[5]!=null) {
            d[5].v_time = (TextView) day_time.findViewById(R.id.sat_time);
            d[5].v_day = (TextView) day_time.findViewById(R.id.sat);
            d[5].v_time.setText(d[5].time); d[5].v_day.setTextColor(Color.GREEN);
        }if(d[6]!=null) {
            d[6].v_time = (TextView) day_time.findViewById(R.id.sun_time);
            d[6].v_day = (TextView) day_time.findViewById(R.id.sun);
            d[6].v_time.setText(d[6].time); d[6].v_day.setTextColor(Color.GREEN);
        }
    }
*/
    public static void  saveSc(int page_mode, Sched_data sd,    ArrayList<Man_data> selected_mans,
                                Week_select_state week_select_state ) {
              //page_mode 0-newsc save , 1- modify
        SchedDBHelper sHelper = new SchedDBHelper(sa);
        SQLiteDatabase db = sHelper.getWritableDatabase();

        int s_id ;
        String sql;

        if(page_mode==1) {     //modify sc
            s_id = sd._id;
            db.execSQL("delete from sched where _id= " + s_id);
            db.execSQL("delete from linked_sched where s_id=" + s_id);
            db.execSQL("delete from sched_man where s_id=" + s_id);
            db.execSQL("delete from sched_week where s_id=" + s_id);
            db.execSQL("delete from link_atom where s_id=" + s_id);


        }
        else {   //page_mode = 0    //new sc
            sql = "select max(_id)+1 from sched ";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToNext();
            s_id = cursor.getInt(0);
            cursor.close();
        }

        for(int i=0; i<7; i++) {
            int state = week_select_state.week_state[i].state;
            if(state ==1) {
                sql = "insert into sched_week (s_id, day) values ("+ s_id + " , " + i + ")";
                db.execSQL(sql);              Log.i("sql", sql);

            }
            else if(state==2) {
                sql = "insert into sched_week values (" + s_id + ", " + i + ", '" + week_select_state.week_state[i].time + "') ";   //00:00 ~ 00:00
                db.execSQL(sql);            Log.i("sql", sql);

            }

        }

        sql = "insert into sched values ("+s_id+" , '" + sd.title + "', " +
                sd.state + " ) ";
        Log.i("sql", sql);
        db.execSQL(sql);Log.i("sd.lds.size()", sd.lds.size() + " ");
        for (int i=0; i<sd.lds.size(); i++) {
            Linked_sched_data ld = sd.lds.get(i);
            String formula = "";
            switch (sd.lds.get(i).reward_type ) {
                case 0:   //simple rew
                    formula = setSimpleRewLink( ld); break;
                case 1:   //if_formula rew
                    formula = setIf_formulaLink(db, s_id, ld); break;
                case 3:   //edt execute_during_time rew
                    formula = ld.formula; break;
                }
            Log.i("formula", formula + " ");

            sql = "insert into linked_sched values ("+ s_id +", '" +
                    ld.link_note +"', " +ld.seq+
                    "," + ld.pic + ", " + ld.reward_type +
                    ", '" + formula + "' )";          Log.i("sql", sql);

            db.execSQL(sql);


        }
        for (int i=0; i<selected_mans.size(); i++) {
            sql = "insert into sched_man values (" + s_id + ", '" + selected_mans.get(i).name +"' )";
            db.execSQL(sql);

        }
        sHelper.close();
    }

    static String setSimpleRewLink(Linked_sched_data ld) {
        String formula = "";
        if(ld.cash.equals("")){
            formula = "4|";   //coupon
            for(int i=0; i<ld.coupon_datas.size(); i++){
                formula += ld.coupon_datas.get(i).name + "|" + ld.coupon_datas.get(i).ea+"|";
            }
        }
        else formula = "3|" + ld.cash;
        return formula;
    }

    static String setIf_formulaLink(SQLiteDatabase db, int s_id, Linked_sched_data ld){
        String formula = "";

        if(ld.link_Atoms!=null && ld.link_Atoms.size()>0) {

            for(int j=0; j<ld.link_Atoms.size(); j++) {
                LinkAtom atom = ld.link_Atoms.get(j);
                String sql = "insert into link_atom values (" + j+ ", "
                        + s_id + " , " + ld.seq + ", " + atom.length + ", '" + atom.unit + "', '" + atom.default_v+"' )";
                Log.i("sql", sql);

                db.execSQL(sql);
            }

            if(ld.formula.length()>0) formula = "1|" + ld.formula;
            else formula = "2|" + ld.if_formula.if_edit_a +
                    "|" +ld.if_formula.if_edit_s +
                    "|" +ld.if_formula.if_edit_c +
                    "|" +ld.if_formula.if_command +
                    "|" +ld.if_formula.else_command;
        }

        return formula;
    }

/*
    public static void onAddLinkClicked( ) {

        currentlink_Atoms = new ArrayList<>();
        linkDiaLayout = (LinearLayout) View.inflate(sa, R.layout.add_sched_content, null);
        save_link =  linkDiaLayout.findViewById(R.id.save_link);

        link_note =  linkDiaLayout.findViewById(R.id.link_note);
       // sa.setReward_type( true, linkDiaLayout);
        formula = linkDiaLayout.findViewById(R.id.formula);

        save_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sa.onSaveLink(null);

            }
        });

        linkDia = new AlertDialog.Builder(sa, R.style.SchedLinkDialog).setTitle(R.string.input_link)
                //.setIcon(R.drawable.androboy)
        .setCancelable(false)
                .setView(linkDiaLayout).show();

        linkDia.getWindow().setBackgroundDrawableResource(R.drawable.add_link_dialog);

        link_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

             Util.titleSaveListener(s, save_link, link_note);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        /*score_layout = (LinearLayout) linear.findViewById(R.id.score_layout);
        score = (EditText) linear.findViewById(R.id.score);

        if(position<=0) {   //rew_spi가 비어서 안보이는 경우 position = -1로 전달됨.
            score_layout.setVisibility(View.VISIBLE);
            score.addTextChangedListener(scoreWatch);
            scored = false;
        }
        else {
            Log.i("score", "gone");
            scored = true;
            one_day_rew = true;
            score_layout.setVisibility(View.GONE);
        }


        cb = (CheckBox) linear.findViewById(R.id.checkb);
        no_select = (Switch) linear.findViewById(R.id.no_select);
        pic_select = (Switch) linkDiaLayout.findViewById(R.id.pic_select);




    }
*/

    public static void onLinkClicked(Linked_sched_data data,  int position ) {
        linkDiaLayout  = (LinearLayout) View.inflate(sa, R.layout.add_sched_content, null);
        link_note = (EditText) linkDiaLayout.findViewById(R.id.link_note);
        formula = linkDiaLayout.findViewById(R.id.formula);

        pic_select = (Switch) linkDiaLayout.findViewById(R.id.pic_select);
        modify_link = (TextView) linkDiaLayout.findViewById(R.id.modify_a);
        del_link = (TextView) linkDiaLayout.findViewById(R.id.del_a);
        del_link.setVisibility(View.VISIBLE);
        modify_link.setVisibility(View.VISIBLE);
        link_note.setText(data.link_note);
        link_noted = true;
        //if(data.link_note.equals("")) link_noted = false;
        //else link_noted  = true;
        //if(data.score==0) scored = false; else scored = true;

      /*  score.setText(data.score+"");  Log.i("data", data.no+" "+data.pic);
        if(data.no==1) {
            no_select.setChecked(true);
        }
        else no_select.setChecked(false);  */
        if(data.pic==1) {
            pic_select.setChecked(true);
        }
        else pic_select.setChecked(false);
        /*
        scored=true;
        if(position!=0) {
            score_layout.setVisibility(View.GONE);

            //scored = false;
        }
        else {
            Log.i("score", "gone");
            //scored = true;
            //one_day_rew = true;
            score_layout.setVisibility(View.VISIBLE);
            score.addTextChangedListener(scoreWatch_m);

        }*/

        link_note.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length()>0) {
                    link_noted = true;
                    if(link_noted && scored)
                        modify_link.setVisibility(View.VISIBLE);

                }
                else { link_noted = false; modify_link.setVisibility(View.GONE);     }
            }
            @Override
            public void afterTextChanged(Editable editable) {            }
        });

        modify_link = (TextView) linkDiaLayout.findViewById(R.id.modify_a);
        modify_link.setVisibility(View.VISIBLE);
       // cb = (CheckBox) linkDiaLayout.findViewById(R.id.checkb);
       // no_select = (Switch) linear.findViewById(R.id.no_select);

        linkDia = new AlertDialog.Builder(sa)
                .setTitle(R.string.modify_link)
                //.setIcon(R.drawable.androboy)
                .setView(linkDiaLayout)
                .show();


    }

/*
    public static void onAdScAtom(Context context) {

        LinearLayout linear = (LinearLayout) View.inflate(sa, R.layout.add_sched_atom, null);
        arrangeScAtomLayout(linear, context);

        atomDia = new AlertDialog.Builder(sa)
                .setTitle(R.string.skd_atom)
                //.setIcon(R.drawable.androboy)
                .setView(linear)
                //.setCancelable(false)
                .show();

    }

    public static void ifClicked() {
        if(add_if.value){
            add_if.value = false;
            ((Button)linkDiaLayout.findViewById(R.id.if_b)).setText(R.string.add_if);
            linkDiaLayout.findViewById(R.id.formula).setVisibility(View.VISIBLE);
            linkDiaLayout.findViewById(R.id.if_layout).setVisibility(View.GONE);
            ((EditText)linkDiaLayout.findViewById(R.id.if_edit_a)).setText("");
            ((EditText)linkDiaLayout.findViewById(R.id.if_edit_c)).setText("");
            ((EditText)linkDiaLayout.findViewById(R.id.if_command)).setText("");
            ((EditText)linkDiaLayout.findViewById(R.id.else_command)).setText("");


        }
        else{
            add_if.value = true;
            ((Button)linkDiaLayout.findViewById(R.id.if_b)).setText(R.string.cancle_if);
            Schedule.linkDiaLayout.findViewById(R.id.formula).setVisibility(View.GONE);
            Schedule.linkDiaLayout.findViewById(R.id.if_layout).setVisibility(View.VISIBLE);
            ((EditText)linkDiaLayout.findViewById(R.id.formula)).setText("");

            Spinner if_edit_s = (Spinner)linkDiaLayout.findViewById(R.id.if_edit_s);
           ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(sa, R.array.if_edit, android.R.layout.
                    simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if_edit_s.setAdapter(adapter);
            if_edit_s.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if_edit_s.setSelection(0);
            if_edit_s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if_edit_s1 = (String)if_edit_s.getSelectedItem();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }
*/
    public static void onDialogCancelClicked( int i) {  //추후 삭제
            if(i==1)  dtDialog.dismiss();
            else if(i==2) {dialog2.dismiss(); selected_day.setTextColor(0x70000000); }
        }


    static void arrangeValues_layout(Context context, LinearLayout vl, ArrayList<LinkAtom> schAtoms,
                                     boolean editText, int rightPadding) {
        arrangeValues_layout(context, vl, schAtoms,
                editText, true, rightPadding, null);
    }

    static void arrangeValues_layout(Context context, LinearLayout vl, ArrayList<LinkAtom> schAtoms,
                                     boolean editText, boolean variable, int rightPadding, ArrayList<TextView> variables){
        //variable 은 edittext 밑에 a,b,c.. 표시기능
        LinearLayout l = new LinearLayout(context);
        Log.i("arrangevalues_l", schAtoms.size() + " ");
        for(int i=0; i<schAtoms.size(); i++) {
            if(i%2==0) {
                l = new LinearLayout(context);
                vl.addView(l);
            }

            TextView e = new TextView(context);
            TextView t = new TextView(context);
            TextView v ;
            char a = 'a';

            if(!editText) e.setPadding(0,0,Util.dip2px(context,5),0);
            if(editText){
                FrameLayout fl = (FrameLayout) View.inflate(context, R.layout.unit_default_layout, null);
                e = (EditText)fl.findViewById(R.id.edit_text);
                t = fl.findViewById(R.id.unit);
                if(variable) {
                    v = fl.findViewById(R.id.variable);
                    char vc = (char) (a + i);
                    v.setText(vc + "");
                }

                l.addView(fl);
            }
            //e = (EditText) View.inflate(context, R.layout.edittext_textend, null);

            else {
                l.addView(e);
                l.addView(t);
            }


            if(!editText) t.setPadding(0, 0, Util.dip2px(context, rightPadding), 0);

            if(variables != null) {
                variables.add(e);
            }      //  DoSchedFragment.showLinkContent

            int length = schAtoms.get(i).length;
            float dv = schAtoms.get(i).default_v;
            String unit = schAtoms.get(i).unit;

            Log.i("length", length + " ");
            if(editText)
                Util.setViewWidthAndMarginright(e, context, length);
            e.setText(dv+"");

            t.setText(unit);

        }
    }

    static float getEDTResult(int tillHour, int tillMinute, int fromHour, int fromMinute, float money){  //in case 3  - edt execute_during_time

        Calendar setTime = Calendar.getInstance();
        setTime.set(Calendar.HOUR_OF_DAY, tillHour);
        setTime.set(Calendar.MINUTE, tillMinute);

        Calendar nowTime = Calendar.getInstance();
        int minDiff = (int)(setTime.getTimeInMillis() - nowTime.getTimeInMillis())/1000/60; Log.i("mindiff", minDiff + " ");

        float period = tillHour*60 + tillMinute - (fromHour*60 + fromMinute);  Log.i("period", period + " ");
        if(minDiff >= period )  return money;

        else  //Log.i("resu", Math.round(minDiff*100/period)/100.0f*money + " ");
            return Math.round(minDiff*100/period)/100.0f*money;

    }

/*


    static TextWatcher scoreWatch =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if(s.length()>0) {
                scored = true;
                if(link_noted && scored)
                    save_link.setVisibility(View.VISIBLE);
            }
            else {scored = false; save_link.setVisibility(View.GONE);     }
        }
        @Override
        public void afterTextChanged(Editable editable) {            }
    };

    static TextWatcher scoreWatch_m =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if(s.length()>0) {
                scored = true;
                if(link_noted && scored)
                    modify_link.setVisibility(View.VISIBLE);
            }
            else {scored = false; modify_link.setVisibility(View.GONE);     }
        }
        @Override
        public void afterTextChanged(Editable editable) {            }
    };*/
}

class CurrentSchedListAdapter extends BaseExpandableListAdapter {
    Context context;
    //ArrayList<SchedItem>  items = new ArrayList<SchedItem>();

    private ArrayList<Sched_data> groups;

    private ArrayList<ArrayList<Linked_sched_data>> children;

    public CurrentSchedListAdapter(Context context, ArrayList<Sched_data> groups,
                                   ArrayList<ArrayList<Linked_sched_data>> children){

        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public ArrayList<Linked_sched_data> getChildList(int groupPostion)    {
        return children.get(groupPostion);
    }



    @Override

    public Object getChild(int groupPosition, int childPosition)    {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)  {
        return childPosition;
    }
    @Override

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        Log.i("aaaa", "          ");

        if (convertView == null)  {
            Log.i("bbbb", "          ");

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.link_list, null);

        }
        Log.i("cccc", "          ");
        Linked_sched_data ld = (Linked_sched_data) getChild(groupPosition, childPosition);
        Log.i("child_view", ld.link_note+" "+ld.s_id+" "+ld.seq);

        ((TextView)convertView.findViewById(R.id.note)).setText(ld.link_note);

        ((TextView)convertView.findViewById(R.id.s_id)).setText(ld.s_id+"");

        ((TextView)convertView.findViewById(R.id.seq)).setText(ld.seq+"");
        //((TextView)convertView.findViewById(R.id.del)).setVisibility(View.GONE);
        //if(childPosition%2==1) convertView.setBackgroundColor(0x208f8c8f);

        return convertView;

    }



    @Override

    public int getChildrenCount(int groupPosition)   {
        return children.get(groupPosition).size();

    }



    @Override

    public Object getGroup(int groupPosition)  {
        return groups.get(groupPosition);
    }



    @Override

    public int getGroupCount() {
        return groups.size();
    }



    @Override

    public long getGroupId(int groupPosition)

    {

        return groupPosition;

    }



    // 그룹 뷰 반환. 그룹 뷰의 레이아웃을 load

    @Override

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,

                             ViewGroup parent)

    {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.current_sched_list, null);
        }
        Log.i("groupposition", groupPosition + " ");
        Sched_data sd = (Sched_data) getGroup(groupPosition);
        ((TextView)convertView.findViewById(R.id.title)).setText(sd.title);
        ((TextView)convertView.findViewById(R.id.i)).setText(groupPosition+"");
        //((TextView)convertView.findViewById(R.id.seq_num)).setText(sd.seq_num+"");


        return convertView;

    }



    @Override

    public boolean hasStableIds()

    {

        return true;

    }



    @Override

    public boolean isChildSelectable(int arg0, int arg1)

    {

        return true;

    }

}

