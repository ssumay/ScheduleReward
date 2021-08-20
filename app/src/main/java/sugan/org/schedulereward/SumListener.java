package sugan.org.schedulereward;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by eunsoo on 2017-11-18.
 */

public class SumListener implements View.OnTouchListener {

    Man_data md;
    Context context;

    public static SumListener newInstance(Man_data man, Context context) {

        SumListener listener = new SumListener();
        listener.md = man;
        listener.context = context;

        return listener;
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("logout_ontouch", "aaa");
        Intent intent = new Intent(context, DoSchedActivity.class);
        intent.putExtra("man_data", md);
        intent.putExtra("page", MyFragment.SUM);
        context.startActivity(intent);
        return false;
    }
}
