package sugan.org.schedulereward;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-03.
 */

public class Man {
    static final int LOGIN_ID_NOEXIST = 0;
    static final int LOGIN_SUCCESS = 1;
    static final int WRONG_PWD = 2;

    String _id;
    ArrayList<Man_data> mans;
    ArrayList<Man_data> search_mans;


    public static void setSpaceWatch(final EditText et) {
        TextWatcher spaceWatch =  new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() > 0) {
                    String st = s.toString();
                    String ts = st.trim();
                    if (!ts.equals(st))
                        et.setText(ts);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        et.addTextChangedListener(spaceWatch);
    }

    public void searchByName( String name , Context context ) {
        search_mans = new ArrayList<Man_data>();
        for(int i=0; i<mans.size(); i++){
            if (mans.get(i).name.equals(name))
                search_mans.add(mans.get(i));
        }

    }

    public void fillSpinner(Context context, Spinner spinner, Spinner spinner1) {

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;
        mans = new ArrayList<Man_data>();
        cursor = db.rawQuery("select name, img, pwd from man where name !='admin'", null);
        String[] names = new String[cursor.getCount()]; int i=0;

        while(cursor.moveToNext()) {
            Man_data man = new Man_data();
           // man._id = cursor.getInt(1);
            man.name = cursor.getString(0);
            man.img = cursor.getString(1);
           // man.login_id = cursor.getString(3);
            man.pwd = cursor.getString(2);
            names[i++] = man.name;
            mans.add(man);
        }
        ArrayAdapter<CharSequence> adapter;
        adapter = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        if(spinner1!=null) spinner1.setAdapter(adapter);

        cursor.close();
        sHelper.close();

    }

    public static ArrayList<Man_data> getManTotal(Context context){   //except admin.

        ArrayList<Man_data> manTot = null;
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;

        String sql = "select name, img from man where name  not like 'admin'";     L.man_checkManId("sql="+sql);
        cursor = db.rawQuery(sql , null);
        if(cursor.getCount()!=0) {
            manTot = new ArrayList<Man_data>();
            while(cursor.moveToNext()){
                Man_data d = new Man_data();
                d.name = cursor.getString(0);
                d.img = cursor.getString(1);
                manTot.add(d);
            }
        }
        cursor.close();
        sHelper.close();
        return manTot;
    }

    public static Man_data getManData(String name, Context context){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;

        String sql = "select name, img,  pwd, per_score from man where name !='admin' and  name='" + name +"'";     L.man_checkManId("sql="+sql);
        cursor = db.rawQuery(sql , null);

        int c = cursor.getCount();

        if(c==0) {
            return null;
        }
        cursor.moveToNext();
        Man_data d = new Man_data();

        d.name = cursor.getString(0);  Log.i("d.name", d.name+" ");
        d.img = cursor.getString(1);
        d.pwd = cursor.getString(2);
        d.per_score = cursor.getInt(3);

        return d;
    }

    public static void delMan(String _id, Context context) {
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();

        String sql = "delete from man where _id=" + _id;
        db.execSQL(sql);
        sql = "delete from sched_man where man_id=" + _id;
        db.execSQL(sql);
        sHelper.close();
    }

    public static void changeAdPwd(String pwd, Context context) {

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        db.execSQL("update man set pwd='" + pwd + "' where login_id='admin'");
        sHelper.close();
    }

    public static Man_data checkManId(String name, Context context){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;

        String sql = "select name, img, pwd, per_scorefrom man where name='"
                + name +"'";     L.man_checkManId("sql="+sql);
        cursor = db.rawQuery(sql , null);

        int c = cursor.getCount();

        if(c==0) {
            return null;
        }
        cursor.moveToNext();
        Man_data d = new Man_data();

        d.name = cursor.getString(0);  Log.i("d.name", d.name+" ");
        d.img = cursor.getString(1);
        d.pwd = cursor.getString(2);
        d.per_score = cursor.getInt(3);
        return d;
    }

    public int checkManId(String l_id, String pwd, Context context){

        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;

        String sql = "select _id, pwd from man where login_id='"+l_id+"'";     L.man_checkManId("sql="+sql);
        cursor = db.rawQuery(sql , null);

        int c = cursor.getCount();

        if(c==0) {
            return LOGIN_ID_NOEXIST;
        }
        cursor.moveToNext();
        if(pwd.equals(cursor.getString(1))){
            _id = cursor.getString(0);
                                                                L.man_checkManId("_id="+_id);
            return LOGIN_SUCCESS;
        }
        else
            return WRONG_PWD;
    }
    public static ManListAdapter getList(Context context) {
        ArrayList<Man_data> group;
        ArrayList<ArrayList<Sched_data>> child;

        //SchedLink[] schedLinks;
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(context);
        Cursor cursor;
        db = sHelper.getWritableDatabase();
        String sql = "select  name,img  from man where  name!='admin'";
        cursor = db.rawQuery(sql, null);
        group = new ArrayList<Man_data>();
        child = new ArrayList<ArrayList<Sched_data>>();
        ManListAdapter adapter = null;
        if (cursor.getCount() != 0) {
            Log.i("cursor.count", cursor.getCount()+" ");
            while (cursor.moveToNext()) {

                String name = cursor.getString(0);
                String img = cursor.getString(1);
                Man_data md = new Man_data();
                md.name = name;
                md.img = img;   Log.i("name", name+" " +img);
                group.add(md);
                ArrayList<Sched_data> scheds = new ArrayList<Sched_data>();

                String sql1 = "select _id, title from sched where _id IN(" +
                        "select s_id from sched_man where man_name='" + name +"')";

                Log.i("sql1", sql1);
                Cursor cursor1 = db.rawQuery(sql1, null);
                while(cursor1.moveToNext()){
                    Sched_data  sd = new Sched_data();
                    sd._id = cursor1.getInt(0);
                    sd.title = cursor1.getString(1);
                    scheds.add(sd);
                }
                child.add(scheds);

                cursor1.close();
            }
            adapter = new ManListAdapter(context, group, child);
        }
        Log.i("group-length", group.size()+" ");
        Log.i("child-length", child.size()+" ");
        cursor.close();
        sHelper.close();
        return adapter;
    }

    public ArrayList<Man_data> fillSelected_mans( String s_id, Context context){
        ArrayList<Man_data> mds = new ArrayList<Man_data>();
        SchedDBHelper sHelper = new SchedDBHelper(context);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;

        String sql = "select name, img, pwd from man where name in (select man_name from"
                + " sched_man where s_id=" + s_id +")";
        cursor = db.rawQuery(sql , null);
        while(cursor.moveToNext()) {
            for(int i=0; i<mans.size(); i++){
                if(mans.get(i).name.equals(cursor.getString(0))) {
                    mds.add(mans.get(i));
                }
            }
        }
        cursor.close();
        sHelper.close();

        return mds;
    }

    public static void setImage(String file, ImageView img){
        Log.i("file", file +" ");
        if(file!=null) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/scheduleReward/" +
                    file + ".jpg";

            Bitmap bitmapImage = BitmapFactory.decodeFile(filePath);
            img.setImageBitmap(bitmapImage);
        }
        else         img.setImageResource(R.drawable.singer2);

    }
}
