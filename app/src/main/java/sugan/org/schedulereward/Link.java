package sugan.org.schedulereward;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by eunsoo on 2017-11-17.
 */

public class Link {

    public static Linked_data_man linkContent(Sched_data sd, int seq, Man_data md, Context context) {
        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(context);
        Cursor cursor;
        db = sHelper.getWritableDatabase();

        Linked_data_man ld = new Linked_data_man();
        ld.md = md;

        if (!Schedule.isNowSCheduledTime(sd._id, context)){  //현재가 스케줄 시간에 해당하는지 체크
            ld.state = 0;
            return ld;
        }

        if(seq==0) {  //요청한 seq가 0이면  during_sched에서 실행중이었는지 체크하고 실행중이라면 다음 실행할 seq로 대체한다.
            String sql = "select seq+1 from during_sched where date = " + Util.today() + " and s_id=" + sd._id + " and man_name = '" + md.name + "'";
            Cursor cursor1 = db.rawQuery(sql, null);
            if(cursor1.moveToNext()) {
                seq = cursor1.getInt(0); Log.i("during_sched에 존재함", seq + " ");
            }
            cursor1.close();
        }

        String sql = "select  note, formula, picture, reward_type  from linked_sched "
                + "where s_id= " + sd._id + " and seq = " + seq ; Log.i("sql", sql + " ");
        cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()) { Log.i("cursor.", "movetoNext");
            ld.ld = new Linked_sched_data();
            ld.ld.s_id = sd._id;
            ld.sched_title = sd.title; //cursor1.getString(0);

            ld.ld.link_note = cursor.getString(0);
            ld.ld.formula = cursor.getString(1);
            ld.ld.pic = cursor.getInt(2);
            ld.ld.reward_type = cursor.getInt(3);

            if(ld.ld.reward_type == 1) {

                sql = "select  length, unit, default_v from link_atom where s_id = " + sd._id + " and link_seq = " + seq + " order by atom_seq ";
                Cursor cursor1 = db.rawQuery(sql, null);
              //  if (cursor1.getCount() != 0) ld.ld.link_Atoms = new ArrayList<>(cursor1.getCount());

                while (cursor1.moveToNext()) {
                    LinkAtom linkAtom = new LinkAtom();
                    linkAtom.length = cursor1.getInt(0);
                    linkAtom.unit = cursor1.getString(1);
                    linkAtom.default_v = Float.parseFloat(cursor1.getString(2));

                    ld.ld.link_Atoms.add(linkAtom);

                }
            }
        }
        else { //완료된 상태
            ld.ld = new Linked_sched_data();
            ld.ld.s_id = sd._id;
            Sum.deleteDuringSched(ld, context);
            return null;
        }

        cursor.close();
        //cursor1.close();
        sHelper.close();
        return ld;
    }

    public static Linked_data_man linkContent_withSkip(Sched_data sd, String m_id, int seq, Context context) {

        SchedDBHelper sHelper;
        SQLiteDatabase db;
        //SchedPerChild schedPerChild;
        sHelper = new SchedDBHelper(context);
        Cursor cursor;
        Cursor cursor1;
        db = sHelper.getWritableDatabase();
        //long now = System.currentTimeMillis();
        //Date date = new Date(System.currentTimeMillis(););
        //SimpleDateFormat sdfNow = new SimpleDateFormat("yymmdd");
        String strNow = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));

        String sql = "select s_id, note, seq, score, type, picture  from linked_sched "
                   + "where s_id= " + sd._id + " order by seq";
      //  String sql1 = "select title from sched where _id= " + sd._id;

        String sql2 = "select s_id, seq, skip_num, state from  during_sched  where man_id = " + m_id
                    + " and s_id= "+ sd._id + " and end_time ='" + strNow + "' ";
        Log.i("sql", sql);
        cursor = db.rawQuery(sql2, null);
        if(cursor.moveToNext()) {
            Log.i("curosr.getstring2", cursor.getString(2)+" ");
            if(cursor.getString(2)!=null && cursor.getString(2).equals("done")) { // completely done today state.
                Log.i("sql2", sql2);
                return null;
            }
            else {  // doing in the middle.
                Log.i("seq before", seq + " ");
                seq = cursor.getInt(1) +1; Log.i("seq after", seq+ " ");
               // sd.skip_num = cursor.getInt(2);

            }

        }

        cursor = db.rawQuery(sql, null);
       // cursor1 = db.rawQuery(sql1, null);
        if(cursor.getCount() < seq)  return null;
        while((seq--)>0) { Log.i("cursor.movetonext", true+" "); cursor.moveToNext(); }
        //cursor1.moveToNext();
        Linked_data_man ld = new Linked_data_man();
        ld.ld.s_id = cursor.getInt(0);  Log.i("cursor.getint",cursor.getInt(0)+" " );
        ld.ld.link_note = cursor.getString(1);
        ld.ld.seq = cursor.getInt(2);
     //   ld.ld.score = cursor.getString(3);
      //  ld.no = cursor.getInt(4);
     //   ld.pic = cursor.getInt(5);
        ld.sched_title = sd.title; //cursor1.getString(0);

        cursor.close();
        //cursor1.close();
        sHelper.close();
        return ld;
    }
}
