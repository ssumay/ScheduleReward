package sugan.org.schedulereward;

import android.util.Log;

/**
 * Created by eunsoo on 2017-11-03.
 */
public class L {
    static int Man_checkManId = 0;
    static int SchedList_getList = 0;
    static int ListActivity_getChildView = 0;
    static int AboutSchedActivity_delSched = 0;
    static int SchedPerChild_delSched = 0;
    static int NewSchedActivity_onSaveLinkClicked = 0;
    static int schedule_makeView = 0;
    static int schedule_onSaveLinkClicked = 0;

    public static void man_checkManId(String s) {
        Log.i("Man_checkManId",Man_checkManId++ +" "+ s);

    }
    public static void schedule_onSaveLinkClicked(String s){
        Log.i("schedule_onSaveLinkCl", schedule_onSaveLinkClicked++ + " "+s );
    }
    public static void schedule_makeView(String s) {
        Log.i("schedule_makeView", schedule_makeView++ + " "+s );
    }
    public static void NewSchedActivity_onSaveLinkClicked(String s){
         Log.i("NewSchedAc_onSaveLinkCl",NewSchedActivity_onSaveLinkClicked++ +" " + s);

    }

    public static void SchedList_getList(String s) {
        // Log.i("SchedList_getList",SchedList_getList++ +" " + s);
    }

    public static void ListActivity_getChildView(String s) {
        //Log.i("ListActi_getChildView",ListActivity_getChildView++ +" "+s );

    }

    public static void AboutSchedActivity_delSched(String s) {
        Log.i("AboutSchedAc_delSched", AboutSchedActivity_delSched++ + " " + s);
    }
    public static void SchedPerChild_delSched(String s) {
        Log.i("SchedPerChild_delSched", SchedPerChild_delSched++ + " " + s);
    }
}


