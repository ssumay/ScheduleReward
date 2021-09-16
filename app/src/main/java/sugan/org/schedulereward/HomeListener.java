package sugan.org.schedulereward;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by eunsoo on 2017-11-19.
 * updated by eunsoo on summer 2021
 */

public class HomeListener implements View.OnTouchListener {
    //String m_id;
    Man_data md;

    Context context;

    public static HomeListener newInstance(Man_data man, Context context) {

        HomeListener listener = new HomeListener();
        listener.md = man;
        listener.context = context;

        return listener;
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("home_ontouch", "aaa");
        Intent intent = new Intent(context, DoSchedActivity.class);
        intent.putExtra("man_data", md);
        //intent.putExtra("m_id", m_id);
        intent.putExtra("page", MyFragment.MYPAGE);
        context.startActivity(intent);
        return true;
    }
}
