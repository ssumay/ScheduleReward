package sugan.org.schedulereward;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;

/**
 * Created by eunsoo on 2017-11-23.
 */
class Date_day{
    boolean isLastDay = false;
    int day_of_month;
    int day_of_week ;    //0 - mon, 1 - tue, ..  5 - sat, 6 - sun

    Date_day(int date){
        Calendar day = Calendar.getInstance();
        int y = 2000 + date/10000;
        int m= (date - (date/10000)*10000 )/100 - 1;
        int d = date%100;
        day.set(  y, m, d);  Log.i("y m d", y + " " + m + " " + d);
        day_of_week = day.get(Calendar.DAY_OF_WEEK);  //1 - SUNDAY, 2 - MON, .. 7 - SATURDAY
        day_of_month = day.get(Calendar.DAY_OF_MONTH);
        if(day_of_month == day.getActualMaximum(Calendar.DATE)) isLastDay = true;

        switch (day_of_week){
            case 2: case 3: case 4: case 5: case 6: case 7: day_of_week -=2;break;
            case 1: day_of_week = 6;
        }
        Log.i("day_of_week, month = ", day_of_week + " " + day_of_month + " " + day.getActualMaximum(Calendar.DATE));

    }
}

public class Coupon {

    static void onMyCoupons(Context context, String man ){
        androidx.appcompat.app.AlertDialog coupons_dia;
        final ScrollView root = (ScrollView) View.inflate(context, R.layout.my_coupons, null);
        GridLayout linear = root.findViewById(R.id.grid);

        coupons_dia = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(R.string.sel_coupon)
                //.setIcon(R.drawable.androboy)
                .setView(root)
                .setNegativeButton(R.string.close, null)
                .show();

        ArrayList<Coupon_data> mycoupons = Coupon.getMyCoupons(context, man);
        for(int i=0; i< mycoupons.size(); i++) {
            final LinearLayout cp = (LinearLayout) View.inflate(context, R.layout.my_coupon, null);
            TextView name = cp.findViewById(R.id.name);
            TextView price = cp.findViewById(R.id.price);
            TextView date = cp.findViewById(R.id.date);
            TextView _id = cp.findViewById(R.id._id);

            TextView imsi = new TextView(context);
            imsi.setText(R.string.won);
            ((TextView)cp.findViewById(R.id.name)).setText(mycoupons.get(i).name);
            String p = mycoupons.get(i).price;
            if(p!=null) ((TextView)cp.findViewById(R.id.price)).setText(p + imsi.getText().toString());Log.i("c_price",p + imsi.getText().toString() );
            ((TextView)cp.findViewById(R.id.date)).setText(mycoupons.get(i).date);
            ((TextView)cp.findViewById(R.id._id)).setText(mycoupons.get(i)._id + "");

            linear.addView(cp);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView imsi = new TextView(context);
                    imsi.setText(R.string.use_coupon);

                    String mes = ((TextView)v).getText().toString()  + imsi.getText().toString();
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setMessage(mes)
                            .setPositiveButton(R.string.y, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichbutton) {
                                    //Sum.removeOnedayRew(A_SumActivity.this, ms.man._id, rew_desc );
                                    int _id = Integer.parseInt(((TextView)((LinearLayout)v.getParent()).findViewById(R.id._id)).getText().toString());
                                    if(Coupon.useMyCoupon(context, _id+""))
                                    {
                                        linear.removeView(cp);
                                        TextView imsi = new TextView(context);
                                        imsi.setText(R.string.won);
                                        String mes ="";
                                        for(int i=0; i<mycoupons.size(); i++){
                                            if(mycoupons.get(i)._id==_id) {
                                                Coupon_data cd = mycoupons.get(i);
                                                mes = cd.name + ( p==null? "" : " (" + cd.price + imsi.getText().toString() + ") ");
                                                imsi.setText(R.string.used_coupon);
                                                mes += imsi.getText().toString();
                                                mycoupons.remove(mycoupons.get(i));
                                                break;
                                            }
                                        }
                                        Toast.makeText(context, mes, Toast.LENGTH_SHORT).show();
                                        if(context instanceof A_SumActivity )
                                        ((A_SumActivity)context).fillSum();
                                        else if(context instanceof SumActivity)
                                            ((SumActivity)context).fillSum();


                                    }else
                                        Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show();

                                    if(mycoupons.size()==0) {
                                        coupons_dia.dismiss();
                                        //mycoupons_b.setVisibility(View.GONE);

                                    }
                                }
                            } )
                            .setNegativeButton(R.string.n, null)
                            .show();
                }
            });
        }
    }

    static boolean useMyCoupon(Context context, String _id){
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        String sql = "update my_coupon set state=1 where _id = " + _id;
        //String sql = " delete from my_coupon where _id = " + _id ;
        try {
            db.execSQL(sql);
            return true;
        }catch (Exception e){
            Log.i("fail_usemycoupon", e.getMessage() + " ");
            return false;
        }finally {
            sHelper.close();
        }

    }

    static ArrayList<Coupon_data> getMyCoupons(Context context, String m_name){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        ArrayList<Coupon_data> cds = new ArrayList<>(5);

        String sql = "select name, date, price_ea, _id from my_coupon where man_name = '" + m_name + "' and state is null";

        Cursor cursor = db.rawQuery(sql, null);
        Log.i("getmycoupons", sql);
        Log.i("ea", cursor.getCount() +  " ");
        while (cursor.moveToNext()){
            Coupon_data cd = new Coupon_data(cursor.getString(0));
            cd.date = cursor.getString(1);
            cd.price = cursor.getString(2);
            cd._id = cursor.getInt(3);
            cds.add(cd);
        }
        cursor.close();
        sHelper.close();
        return  cds;
    }

    static void addViewToPeriodLayout(int string_id, String text, LinearLayout linearLayout, Context context){

        TextView imsi = new TextView(context);
        imsi.setPadding(Util.dip2px(context, 3), Util.dip2px(context, 3),
                Util.dip2px(context, 3),Util.dip2px(context, 3));
        if(text==null) imsi.setText(string_id);
        else imsi.setText(text);
        linearLayout.addView(imsi);

    }


    static void parsePeriodicData(String period, LinearLayout linearLayout, Context context){
        TextView imsi = new TextView(context);
        imsi.setPadding(Util.dip2px(context, 3), Util.dip2px(context, 3),
                Util.dip2px(context, 3),Util.dip2px(context, 3));

        if(period.length()<=2) {  //매달
            if(period.equals("-1"))   addViewToPeriodLayout(R.string.last_day, null, linearLayout, context);
            else {
                TextView t = new TextView(context);
                t.setText(R.string.day);
                int pi = Integer.parseInt(period);
                String p;
                switch (pi){
                    case 2: p = pi + "nd " + t.getText().toString();break;
                    case 3: p = pi + "rd " + t.getText().toString();break;
                    default: p = pi + "th " + t.getText().toString();
                }
                addViewToPeriodLayout(0, p, linearLayout,context);
            }
        }
        else {      //주단위
            for (int i = 0; i < 7; i++) {
                if (period.charAt(i) == '1') {

                    switch (i) {
                        case 0:
                            addViewToPeriodLayout(R.string.mon, null,linearLayout, context);
                            break;
                        case 1:
                            addViewToPeriodLayout(R.string.tue, null,linearLayout, context);
                            break;
                        case 2:
                            addViewToPeriodLayout(R.string.wed, null,linearLayout, context);
                            break;
                        case 3:
                            addViewToPeriodLayout(R.string.thu, null,linearLayout, context);
                            break;
                        case 4:
                            addViewToPeriodLayout(R.string.fri, null,linearLayout, context);
                            break;
                        case 5:
                            addViewToPeriodLayout(R.string.sat, null,linearLayout, context);
                            break;
                        case 6:
                            addViewToPeriodLayout(R.string.sun, null,linearLayout, context);
                            break;
                    }
                }

            }
        }
    }

    static PeriodicCouponListAdapter getPeriodicCouponListAdapter(Context context, String man_name1 ){
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        ArrayList<Coupon_data> coupon_datas = new ArrayList<>(5);
        String sql;
        sql = "select _id, name, ea, periodic_date, price_ea from PERIODIC_COUPON where man_name = '" + man_name1 + "' ";
        Cursor cursor = db.rawQuery(sql, null);
        coupon_datas.add(null);  //title을 위해 자리를 비워둔다.
        while(cursor.moveToNext()){
            Coupon_data cd = new Coupon_data();
            cd.period_id = cursor.getInt(0);
            cd.name = cursor.getString(1);
            cd.ea = cursor.getString(2);
            cd.period = cursor.getString(3);
            cd.price = cursor.getString(4);
            coupon_datas.add(cd);

        }
        return new PeriodicCouponListAdapter(context, coupon_datas);

    }

    public static void giveCoupon(Coupon_data cd, Context context){  //주기적 쿠폰 저장

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        String sql;
        try {

            ///transaction 처리할 것
            if (!cd.period.equals("0")) {  //one-time 일회성이 아니면 periodic_coupon table에 data를 저장한다.
                sql = "insert into PERIODIC_COUPON (name, create_date, ea, man_name, periodic_date, price_ea ) values ( '" +
                        cd.name + "', '" + Util.today() + "', " + cd.ea + ", '" + cd.man_name + "', '" + cd.period + "', " +
                        cd.price + ")";
                Log.i("sql", sql + " ");
                db.execSQL(sql);
                saveMyCoupon(cd.name, Integer.parseInt(cd.ea), cd.man_name, Integer.parseInt(Util.today()), cd.period, cd.price, db);
                insertTodayCheckCP(db);
            } else
                publishCoupon(cd.name, Integer.parseInt(cd.ea), cd.man_name, Util.today(), cd.price, db);

        }catch (Exception e){
            Toast.makeText(context, R.string.giveCPfailed, Toast.LENGTH_SHORT).show();
            Log.i("쿠폰 발행 실패", e.getMessage() + " ");
        }finally {
            sHelper.close();
        }
    }

    static void insertTodayCheckCP(SQLiteDatabase db){  //CHECK_PERIOD_COUPON 테이블에 현재날짜를 넣는다.
        String sql = "delete from CHECK_PERIOD_COUPON";
        db.execSQL(sql);
        sql = "insert into CHECK_PERIOD_COUPON values ('" + Util.today() + "')"; Log.i("sql", sql);
        db.execSQL(sql);
    }

    static void givePeriodicCoupons(Context context){  //각 페이지에서 주기적 쿠폰 검토후 발행
        SchedDBHelper sHelper = new SchedDBHelper(context);
        try {
            SQLiteDatabase db = sHelper.getWritableDatabase();
            String sql = "select date from CHECK_PERIOD_COUPON";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int last_date = cursor.getInt(0);
                int today = Integer.parseInt(Util.today());
                cursor.close();
                if (last_date==today) return;
               // Date_day tod = new Date_day(today);

                while(last_date <= today) {
                    publishCoupons(last_date, db);

                    last_date += 1;
                }

                //테스트 마치고 추후 .. CHECK_PERIOD_COUPON 의 date를 다 삭제하고  Util.today를 넣을 것.

            }
            insertTodayCheckCP(db);

        }catch (Exception e){
            Log.i("주기적 쿠폰 발행 error", e.getMessage() + " ");
            Toast.makeText(context, R.string.period_cp_error,  Toast.LENGTH_LONG).show();
        }finally {
            sHelper.close();
        }

    }

    static void saveMyCoupon(String name, int ea, String man_name, int day, String periodic_date, String price, SQLiteDatabase db ){
        Date_day date = new Date_day(day);
        if(periodic_date.length()<3) {  //달단위 지급쿠폰
            int periodic_datei = Integer.parseInt(periodic_date);
            Log.i("periodic_datei", periodic_datei + " ");
            if(periodic_datei==-1 && date.isLastDay || date.day_of_month==periodic_datei) { // last day
                Log.i("월단위 쿠폰 지급", date.day_of_month + " ");
                publishCoupon(name, ea, man_name, day+"", price, db);
                //쿠폰 지급
            }
        }
        else   //주단위 쿠폰지급   1001100 월화수목금토일
            if(periodic_date.charAt(date.day_of_week)=='1' ) //쿠폰 지급
                publishCoupon(name, ea, man_name, day+"", price,  db);
    }

    static void publishCoupons(int day,   SQLiteDatabase db) {
        String sql = "select name, ea, man_name, periodic_date, price_ea from PERIODIC_COUPON";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()) {
            String name = cursor.getString(0);
            int ea = cursor.getInt(1);
            String man_name = cursor.getString(2);
            String periodic_date = cursor.getString(3);
            Log.i("periodic_date", periodic_date + " ");
            String price_ea = cursor.getString(4);
            saveMyCoupon(name, ea, man_name, day,periodic_date, price_ea, db);

        }


    }

    static void publishCoupon( String name, int ea, String man_name, String day, String price,  SQLiteDatabase db){
        while(ea>0) {
            String sql = "insert into my_coupon (man_name, name, date, price_ea) values ( '" + man_name + "', '" + name + "', " + day + ", " + price + " ) ";
            db.execSQL(sql);  Log.i("sql", sql + " " + ea);
            ea--;
        }

    }


    static void setCoupons(Context context, Spinner spi, LinearLayout layout, Spinner spi1){
        if(fillCouponSpinner(context, spi, spi1)) {
            layout.setVisibility(View.VISIBLE);

        }
        else {
            layout.setVisibility(View.GONE);
        }

    }

    static boolean saveNewCoupon(Context context, String name) {
        SchedDBHelper sHelper = new SchedDBHelper(context);
        try {
            SQLiteDatabase db = sHelper.getWritableDatabase();
            String sql = "insert into COUPON values ( '" + name + "' )";
            Log.i("coupon", sql + " ");
            db.execSQL(sql);
            return true;
        }catch (Exception e){
            return false;
        }
        finally {
            sHelper.close();
        }
    }

    static boolean fillCouponSpinner(Context context, Spinner coupon_spi, Spinner coupon_spi1 ){
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select name from COUPON ", null);
        Log.i("cursor.getCount()", cursor.getCount()+" ");

        int size = cursor.getCount();
        if(size==0) return false;

        String[] coupons = new String[size];
        int i = 0;
        while(cursor.moveToNext())
            coupons[i++] = cursor.getString(0); //+" --- "+ rew.type ;

        ArrayAdapter<CharSequence> adapter;
        adapter = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_spinner_item, coupons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        coupon_spi.setAdapter(adapter);
        if(coupon_spi1 != null) coupon_spi1.setAdapter(adapter);

        cursor.close();
        sHelper.close();
        return  true;


    }

    static boolean delPeriodCP(int period_id, Context context){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        try {
            String sql = "delete from PERIODIC_COUPON where _id = " + period_id;
            db.execSQL(sql);
            return true;
        }catch (Exception e) {
            Log.i("exception e", e.getMessage() + " ");
            return false;
        }finally {
            sHelper.close();
        }
    }
/*
    static ArrayList<Coupon_data>  fillReward(Context context, Spinner rew_spi, Spinner rew_spi1, int type) {
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;
                cursor = db.rawQuery("select _id, rew_desc from reward order by _id desc", null);
        Log.i("cursor.getCount()", cursor.getCount()+" ");

        ArrayList<Coupon_data> rews = new ArrayList<Coupon_data>();
        if(cursor.getCount()==0) {
            Log.i("cursor.getCount()", cursor.getCount()+" ");
            //del_rew.setVisibility(View.GONE);
            return rews;
        }

        String[] rews_s = new String[cursor.getCount()];
        int i=0;
        if(type==1) {
            rews_s = new String[cursor.getCount()+1];
            Coupon_data d = new Coupon_data();
            TextView t = new TextView(context);
            t.setText(R.string.sel_rew);

            rews_s[i++] = t.getText().toString();
        }

        while(cursor.moveToNext()) {
            Coupon_data rew = new Coupon_data();
           // rew._id = cursor.getInt(0);
            rew.name = cursor.getString(1);
            //rew.type = cursor.getInt(2);

            rews_s[i++] = rew.name; //+" --- "+ rew.type ;

            rews.add(rew);
        }
        ArrayAdapter<CharSequence> adapter;
        adapter = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_spinner_item, rews_s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        rew_spi.setAdapter(adapter);
        if(rew_spi1!=null ) rew_spi1.setAdapter(adapter);

        cursor.close();
        sHelper.close();
        return  rews;
    }*/

}

class PeriodicCouponListAdapter extends ScheduleCouponListAdapter{

    PeriodicCouponListAdapter(Context context, ArrayList<Coupon_data> coupon_datas){
        super(context, coupon_datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position==0 ){
            convertView = inflater.inflate(R.layout.period_coupon_listview_title, parent, false);
        }
        else {
           // if (convertView == null) {
                convertView = inflater.inflate(R.layout.period_coupon_listview, parent, false);

           // }
            TextView name = convertView.findViewById(R.id.name);
            name.setText(coupon_datas.get(position).name);
            TextView ea = convertView.findViewById(R.id.ea);
            ea.setText(coupon_datas.get(position).ea);
            TextView price = convertView.findViewById(R.id.price);
            price.setText(coupon_datas.get(position).price);
            Log.i("period_id", coupon_datas.get(position).period_id + " ");
            LinearLayout period_day = convertView.findViewById(R.id.period_day);
            period_day.removeAllViews();
            Coupon.parsePeriodicData(coupon_datas.get(position).period, period_day, context);

            TextView period_id = convertView.findViewById(R.id.period_id);
            period_id.setText(coupon_datas.get(position).period_id + "");
            TextView del_period_cp = convertView.findViewById(R.id.del_period_cp);

            del_period_cp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(context).setTitle(R.string.del_periodCP)
                            .setMessage(R.string.query_cancel_periodCP)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int b) {
                                    if (Coupon.delPeriodCP(coupon_datas.get(position).period_id, context)) {
                                        coupon_datas.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }
                            }).setNegativeButton(R.string.noo, null)
                            //.setIcon(R.drawable.androboy)
                            .show();

                    // coupon_datas.remove(position);
                    // notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }

}

class ScheduleCouponListAdapter extends BaseAdapter{

    LayoutInflater inflater;
    Context context;
    ArrayList<Coupon_data> coupon_datas;

    ScheduleCouponListAdapter(Context context, ArrayList<Coupon_data> coupon_datas) {

        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.coupon_datas = coupon_datas;


    }

    @Override
    public int getCount() {
        return coupon_datas.size();
    }

    @Override
    public Object getItem(int position) {
        return coupon_datas.get(position).name;
    }

    public void addItem(Coupon_data coupon_data){
        coupon_datas.add(coupon_data);Log.i("additem", coupon_data.name + " " + coupon_data.ea);
        notifyDataSetChanged();

    }
    public void clear(){
        coupon_datas.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // if(convertView==null){
            convertView = inflater.inflate(R.layout.coupon_listview, parent, false);

        //}
        TextView name = (TextView)convertView.findViewById(R.id.name);
        name.setText(coupon_datas.get(position).name);
        EditText ea = (EditText)convertView.findViewById(R.id.ea);
        ea.setText(coupon_datas.get(position).ea);
        TextView seq = convertView.findViewById(R.id.position);
        seq.setText(position+ "");
        TextView del = convertView.findViewById(R.id.del);

        ea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){ coupon_datas.get(Integer.parseInt(seq.getText().toString())).ea.equals("1"); return; }
                coupon_datas.get(Integer.parseInt(seq.getText().toString())).ea = ea.getText().toString();
                //Log.i(seq.getText().toString() + "ea", coupon_datas.get(position).ea + " ");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coupon_datas.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
