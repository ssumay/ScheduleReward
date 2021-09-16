package sugan.org.schedulereward;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by eunsoo on 2017-11-18.
 * updated by eunsoo on summer 2021
 */

public class Sum {
    static void insertScore(Context context, String man_name, double value){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();

        String sql = "insert into sum (man_name, date, reward ) values ('" + man_name + "', " + Util.today() + ", '" + value + "' )";
        Log.i("insertScore", sql);
        db.execSQL(sql);
        sHelper.close();

    }

    static void deleteDuringSched(Linked_data_man ld, Context context){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();

        String sql = "delete from during_sched where man_name = '" + ld.md.name +
                "' and s_id = " + ld.ld.s_id + " and date <= " +  Util.today() ; Log.i("sql", sql + " ");
        db.execSQL(sql);
        sHelper.close();
    }

    static void executeSchedLink(Linked_data_man ld, Context context, String value, String img) {

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();

        String today = Util.today();

        if(ld.ld.seq==0)     //이전 날짜의 data를 삭제한다 (오늘 포함해도 결과에는 상관없으므로 date <=today)
            deleteDuringSched(ld, context);


        String sql = " insert into during_sched values ( '" +
                      ld.md.name + "', " + ld.ld.s_id + ", " + ld.ld.seq + ", " +
                      today + " ) ";  Log.i("sql = ", sql + " ");

        db.execSQL(sql);

        //insertSum(ld, value+"", context, today);
        if(value.startsWith("coupon|"))
            sql =  "insert into sum values ( '" +ld.md.name + "', " + ld.ld.s_id + ", '" + ld.sched_title + "', '" + ld.ld.link_note +
                    "', " + ld.ld.seq + ", " + today + ", '" + value + "', '" + img + "' , 1)";

        else         sql =  "insert into sum values ( '" +ld.md.name + "', " + ld.ld.s_id + ", '" + ld.sched_title + "', '" + ld.ld.link_note +
                "', " + ld.ld.seq + ", " + today + ", '" + value + "', '" + img + "', null )"; Log.i("sql" , sql);
        db.execSQL(sql);

        if(value.startsWith("coupon|")){
            String[] coupons = value.split("\\|");
            for(int i=1; i<coupons.length; i=i+2 ){
                int j = Integer.parseInt(coupons[i+1]);
                for(int k=0; k<j; k++){
                    sql = "insert into my_coupon " +
                            "(s_title, link_note, man_name, name, date, price_ea, state )" +
                            " values (" +
                            "'" + ld.sched_title + "', '" +
                              ld.ld.link_note + "', '" +
                              ld.md.name + "', '" +
                            coupons[i] + "', " +
                            Util.today() + ", null, null )"; Log.i("insert my coupon", sql);
                    db.execSQL(sql);
                }
                    Log.i("coupons", coupons[i]);
            }
            //sql = "insert into my_coupon values (" + ld.md._id + ", '" + ')"
        }

        sHelper.close();


    }
    /*
    static void insertSum(Linked_data_man ld, String value, Context context, String today){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();

        String sql =  "insert into sum values (" +ld.md._id + ", " + ld.ld.s_id + ", '" + ld.sched_title + "', '" + ld.ld.link_note +
                "', " + ld.ld.seq + ", '" + today + "', '" + value + "' )";
        db.execSQL(sql);

        sHelper.close();

    }
*/
   // static calculate
    public static double calculateIfFormulaLinkResult(ArrayList<TextView> variables, Linked_sched_data ld){

        double result = 0;
        //if(ld.formula != null) {
            // 1|12 - (a-3)*0.8   or 2|a|>|3|a*0.2|a*0.2+0.1
            for(int i=0; i<variables.size(); i++) {
                char vc = (char)('a'+i);
                ld.formula = ld.formula.replace(vc+"", variables.get(i).getText().toString()); Log.i("ld.formula", ld.formula + " ");
            }

            Schedule.parseFormula(ld, ld.formula);  //  ld의 formula와 If_formula에 값이 담김.
            String math_expression = "";

            if(ld.if_formula != null) {

                float if_edit_a = Float.parseFloat(ld.if_formula.if_edit_a);
                float if_edit_c = Float.parseFloat(ld.if_formula.if_edit_c);
                switch(ld.if_formula.if_edit_s) {
                    case ">":
                        if (if_edit_a > if_edit_c) math_expression = ld.if_formula.if_command;
                        else math_expression = ld.if_formula.else_command;
                        break;
                    case ">=":
                        if (if_edit_a >= if_edit_c) math_expression = ld.if_formula.if_command;
                        else math_expression = ld.if_formula.else_command;
                        break;
                    case "<":
                        if (if_edit_a < if_edit_c) math_expression = ld.if_formula.if_command;
                        else math_expression = ld.if_formula.else_command;
                        break;
                    case "<=":
                        if (if_edit_a <= if_edit_c) math_expression = ld.if_formula.if_command;
                        else math_expression = ld.if_formula.else_command;
                        break;
                    case "==":
                        if (if_edit_a == if_edit_c) math_expression = ld.if_formula.if_command;
                        else math_expression = ld.if_formula.else_command;
                        break;
                    case "!=":
                        if (if_edit_a != if_edit_c) math_expression = ld.if_formula.if_command;
                        else math_expression = ld.if_formula.else_command;
                        break;
                }

            }
            else
                math_expression = ld.formula;
            Log.i("math_expression", math_expression + " ");

            result = Util.calculate(math_expression); Log.i("result", result + " ");

        //}
        return result;

    }

    /*
    public static void plusScore(Context context, int man_id, int plus) {

        String strNow = new SimpleDateFormat("yyMMdd HH:mm").format(new Date(System.currentTimeMillis()));
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        TextView imsi = new TextView(context);
        imsi.setText(R.string.bonus);
        String sql  = "insert into sum_res values (null, " + man_id + ", 0 , '"+ imsi.getText().toString()+"', '"
                +strNow + "', -1, "+ plus + ", null)";
        db.execSQL(sql);
        sHelper.close();
    }*/

    /*
    public static void minuScore(Context context, String man_name, int minus1) {

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        int score=0; int _id=0 ; int i=0;
        int minus = minus1;
        String sql; String sql1;
        Cursor cursor;
        do {
            sql = " select score, _id from sum_res where _id in (select min(_id) from sum_res where man_id=" + man_id +
                    " and rew_id=-1 )";
            cursor = db.rawQuery(sql, null);
            cursor.moveToNext();
            score = cursor.getInt(0);
            _id = cursor.getInt(1);
            if (score <= minus) {
                sql1 = "delete from sum_res where _id=" + _id;
                minus = minus - score;
            } else {
                sql1 = "update sum_res set score=score-" + minus + " where _id=" + _id;
                Log.i("minus-sql1", sql1);
                minus = 0;
            }
            db.execSQL(sql1);
        } while(minus>0);
        //Date date = new Date(System.currentTimeMillis());
        //String strNow = new SimpleDateFormat("yyMMdd").format(date);
        String strNow1 = new SimpleDateFormat("yyMMdd HH:mm").format(new Date(System.currentTimeMillis()));
        String sql2 = "insert into used_score_list values (" + man_id + ", '" + strNow1 + "', "+ minus1 +")";
        db.execSQL(sql2);//Log.i("used_score", sql2);
        cursor.close();
        sHelper.close();
    }*/

    public static ArrayList<Man_data> fillNameSpinner(Context context, Spinner spinner) {

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;
        ArrayList<Man_data> mans = new ArrayList<Man_data>();
        cursor = db.rawQuery(
                "select name, img, per_score from man where name!='admin' ", null);
        String[] names = new String[cursor.getCount()]; int i=0;

        while(cursor.moveToNext()) {
            Man_data man = new Man_data();
            man.name = cursor.getString(0);
            man.img = cursor.getString(1);
            man.per_score = cursor.getInt(2);
            names[i++] = man.name;
            mans.add(man);
        }
        ArrayAdapter<CharSequence> adapter;
        adapter = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        cursor.close();
        sHelper.close();
        return mans;
    }

    public static void fillSum(Man_sum  ms, TextView b1, TextView b2, TextView b3, ListView list1, ListView list2, ImageView img, Context context){
        Man.setImage(ms.man.img, img, context);

        if(ms.cp_datas != null ){ //&& ms.cp_datas.size()>1) {
            b3.setVisibility(View.VISIBLE);
        }
        else b3.setVisibility(View.GONE);

        if(ms.sum_datas != null){ // && ms.sum_datas.size()>1) {
            SumListAdapter adapter = new SumListAdapter(context, ms.sum_datas);
            list1.setAdapter(adapter);
            setListViewHeightBasedOnChildren(list1);
        }
        else  {
            list1.setAdapter(null);
        }
        Log.i("ms.cp_datas", ms.cp_datas + " ");
        if(ms.cp_datas != null ){ //&& ms.cp_datas.size()>1){
            Log.i("cp_datas != null ", "---");
            CpListAdapter adapter = new CpListAdapter(context, ms.cp_datas);
            list2.setAdapter(adapter);
            setListViewHeightBasedOnChildren(list2);
        }
        else {
            list2.setAdapter(null);
        }

        b1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (list1.getVisibility() == View.VISIBLE) list1.setVisibility(View.GONE);
                else list1.setVisibility(View.VISIBLE);
                list2.setVisibility(View.GONE);

            }
        });
        b2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (list2.getVisibility() == View.VISIBLE) list2.setVisibility(View.GONE);
                else list2.setVisibility(View.VISIBLE);
                list1.setVisibility(View.GONE);


            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coupon.onMyCoupons(context, ms.man.name, list2);

            }
        });
    }

    public static Man_sum  fillSumByMan(Context context, Man_data md){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Man_sum ms = new Man_sum();  ms.man = md;
        Cursor cursor1;
        Cursor cursor2;

        String sql1 = "select sum(reward) from sum where man_name = '" + md.name + "' and isCP is null";

        cursor1 = db.rawQuery(sql1, null);
        cursor1.moveToNext();
        ms.sum = cursor1.getDouble(0);
        String sql2 = "select s_title, link_note, date, reward, picture from sum where man_name = '" + md.name +
                "' and isCP is null order by date desc, s_id desc, seq asc ";   //limit 20";  //
        Log.i("sql", sql2);
        cursor2 = db.rawQuery(sql2, null);
        //ms.sum_datas = new ArrayList<>(10);
       // ms.cp_datas = new ArrayList<>();



        if(cursor2.getCount()!=0) {
            ms.sum_datas = new ArrayList<>(10);

            Sum_data sd1 = new Sum_data();
            TextView imsi = new TextView(context);
            imsi.setText(R.string.date);
            sd1.date = imsi.getText().toString();
            imsi.setText(R.string.sched);
            sd1.s_title = imsi.getText().toString();
            imsi.setText(R.string.content);
            sd1.link_note = imsi.getText().toString();
            imsi.setText(R.string.score);
            sd1.score = imsi.getText().toString();
            //imsi.setText(R.string.picture);
            //ds.picture = imsi.getText().toString();
            imsi.setText(R.string.content);
            ms.sum_datas.add(sd1);

            while (cursor2.moveToNext()) {
                Sum_data sd = new Sum_data();
                ms.sum_datas.add(sd);
                sd.s_title = cursor2.getString(0);
                sd.link_note = cursor2.getString(1);
                sd.date = cursor2.getString(2);
                sd.score = cursor2.getString(3);
                sd.picture = cursor2.getString(4);Log.i("sd.picture", sd.picture + " " );
            }

        }

        ms.cp_datas = fillCoupons(context, ms.man.name);

        cursor1.close(); cursor2.close();
        sHelper.close();

        return ms;
    }

    static ArrayList<Coupon_data> fillCoupons(Context context, String man ){//Man_sum ms){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        ArrayList<Coupon_data> cps = null;

        String sql3 = "select s_title, link_note, name, date, price_ea, state from my_coupon where man_name= '" + man +
                "' order by date desc ";  // limit 20";

        Log.i("fillcps", sql3 + " ");
        Cursor cursor3 = db.rawQuery(sql3, null);

        if(cursor3.getCount()!=0){
            Log.i("cp-count",cursor3.getCount() + " " );
            cps =  new ArrayList<>(10);
            TextView imsi = new TextView(context);

            Coupon_data ct = new Coupon_data();
            imsi = new TextView(context);
            imsi.setText(R.string.sched);
            ct.s_title = imsi.getText().toString();
            imsi.setText(R.string.content);
            ct.link_note = imsi.getText().toString();
            imsi.setText(R.string.date);
            ct.date = imsi.getText().toString();
            imsi.setText(R.string.price_ea);
            ct.price =   imsi.getText().toString();
            imsi.setText(R.string.coupon);
            ct.name = imsi.getText().toString();
            cps.add(ct);

            while(cursor3.moveToNext()) {

                Coupon_data cp = new Coupon_data();
                cps.add(cp);
                //ms.cp_datas.add(cp);
                cp.s_title = cursor3.getString(0);
                cp.link_note = cursor3.getString(1);
                cp.name = cursor3.getString(2);
                cp.date = cursor3.getString(3);
                cp.price = cursor3.getString(4);
                if(cursor3.getInt(5)==1) cp.state = true;   //1이면 사용한 상태. 그렇지 않은 경우 NULL
            }
        }
        cursor3.close();
        sHelper.close();
        return cps;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)  return;

        int numberOfItems = listAdapter.getCount();

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() *  (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

/*
    public static void addScore(String m_id, Sched_data sd, String link_note, int seq, String seq_num,
                                String score, String picture, Context context) {

        Date date = new Date(System.currentTimeMillis());
        String strNow = new SimpleDateFormat("yyMMdd").format(date);
        //String strNow1 = new SimpleDateFormat("yyMMdd HH:mm").format(date);

        Log.i("addScore-strnow", strNow);
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        sHelper = new SchedDBHelper(context);
        db = sHelper.getWritableDatabase();
        String sql2 = "select man_id from sum where man_id=" + m_id +" and s_id="+sd._id+" and seq="+seq+
                " and date= '"+strNow+"'";
        Cursor cursor = db.rawQuery(sql2, null);
        if(cursor.getCount()!=0) {
            cursor.close();
            sHelper.close();
            return;
        }
        //Log.i("addscore - seq", seq +" " + sd.lds.size());
        //Log.i("addscore - ", seq +" ");

        String sql = "insert into sum  values (" + m_id + ", " + sd._id + ", '"
                +sd.title + "', '" + link_note + "', " + seq + ", " + strNow
                       + ", " + score + ", '"+picture+"') ";
        String sql1;
        String sql3;
        if(seq==1) {
            sql1 = "insert into  during_sched values (" + m_id + ", " + sd._id + ", " + seq
                    + // ", "+ sd.skip_num +
               ", '" + strNow + "' , '')";
            if(sd.reward==-1) {   // normal case.
                sql3 = "insert into sum_res values (null, " + m_id + ", " + sd._id + ", '" + sd.title + "', '"
                        + strNow + "', -1, " + score + " , null)" ;  Log.i("sql3", sql3);
            }
            else {    //one day reward case;
                sql3 = "insert into sum_res values (null, " + m_id + ", " + sd._id +", '" + sd.title + "', '" + strNow +
                        "', " + sd.reward + ", " + score +", null)"; Log.i("sql3", sql3);

            }
        }
        else {
            sql1 = "update during_sched set seq= " + seq + ", skip_num= "+// sd.skip_num +
                    " where man_id =" + m_id + " and " +
                    " s_id= " + sd._id + " and end_time = '" + strNow + "' " ;

            sql3 = "update sum_res set score= score +" + score + " where  man_id =" + m_id + " and " +
                        " s_id= " + sd._id + " and date = '" + strNow + "' " ;Log.i("sql3", sql3);


        }

        db.execSQL(sql);
        db.execSQL(sql1);
        db.execSQL(sql3);

        if(seq_num.equals(Integer.toString(seq))) {
            sql1 = "update during_sched set state= 'done'  where man_id =" + m_id + " and " +
                    " s_id= " + sd._id + " and end_time = '" + strNow + "' " ;
            db.execSQL(sql1);
            if(sd.reward!=-1 ) {   // oneday_rew case.  /&& sd.skip_num>(Integer.parseInt(seq_num) - score_i
                Log.i("rew_desc", sd.rew_desc+" ");
                sql3 = "update sum_res set reward='" +sd.rew_desc+"' where  man_id =" + m_id + " and " +
                        " s_id= " + sd._id + " and date = '" + strNow + "' " ;
                db.execSQL(sql3);
            }

            Log.i("seq_num.equals(seq)", sql1);




        }
        //추후 없앨것 테스트를 위함
        String sql7 = "select man_id, s_id, score, date from  sum_res where rew_id=-1";
        Cursor cursor2 = db.rawQuery(sql7, null);
        while(cursor2.moveToNext()) {
            Log.i("cursor2", cursor2.getString(0)+ " " + cursor2.getString(1)+ " " + cursor2.getString(2)
                    + " " + cursor2.getString(3));
        }
        cursor2.close();
        sHelper.close();
    }
*/
     static String getSumScore(String man_name, String s_id , Context context) {
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        sHelper = new SchedDBHelper(context);
        db = sHelper.getWritableDatabase();
        Cursor cursor;

        String sql = "select reward from sum where man_name='" + man_name + "' and s_id=" + s_id
                + " and date =" + Util.today();   Log.i("getsumscore", sql);

        cursor = db.rawQuery(sql, null);

        double sum = 0;
        String coupons = "";

        while (cursor.moveToNext()){
            String val = cursor.getString(0);   Log.i("value", val + " ");
            if(val.startsWith("coupon")){
                String[] coupon = val.split("\\|");
                for (int i=1; i<coupon.length; i++)
                    coupons += coupon[i] + " ";
            }
            else
                if(!cursor.getString(0).equals(""))
                   sum+= Double.parseDouble(cursor.getString(0));

        }

        cursor.close();
        sHelper.close();
        return sum!=0 ?sum + "  " + coupons: coupons;
    }
}
