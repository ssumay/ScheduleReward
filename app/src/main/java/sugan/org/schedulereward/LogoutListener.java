package sugan.org.schedulereward;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by eunsoo on 2017-11-18.
 * updated by eunsoo on summer 2021
 */

public class LogoutListener implements View.OnTouchListener {

    Man_data md;
    Context context;

    public static LogoutListener newInstance(Man_data man, Context context) {

        LogoutListener listener = new LogoutListener();
        listener.md = man;
        listener.context = context;

        return listener;
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
           // Log.i("logout_ontouch", md.name + " " + DoSchedActivity.SFDoSchedFragment.ids.size());
            Intent intent = new Intent(context, DoSchedActivity.class);
            intent.putExtra("man_data", md);
            intent.putExtra("page", MyFragment.LOGOUT);
            context.startActivity(intent);
        }
        return true;

    }
}
