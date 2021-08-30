package sugan.org.schedulereward;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by eunsoo on 2017-11-03.
 */
/*
public class Day_time_data {
    String day;
    String time;
    TextView v_day;
    TextView  v_time;
    //boolean  isSelected = false;
}
*/
class Week_select_state {

    Day_state[] week_state = new Day_state[7];  //0 - mon, 1 - tue ... 6 - sun

    Context context;

    Week_select_state(Context sa) {

        context = sa;

        for(int i=0; i<7; i++){
            week_state[i] = new Day_state();
        }
        if(sa instanceof ScheduleActivity) {
            setLayout(((ScheduleActivity)sa).findViewById(R.id.day_time));
        }

    }

    void setLayout(LinearLayout layout) {
        week_state[0].v_day =  layout.findViewById(R.id.mon);
        week_state[0].v_time = layout.findViewById(R.id.mon_time);
        week_state[1].v_day = layout.findViewById(R.id.tue);
        week_state[1].v_time = layout.findViewById(R.id.tue_time);
        week_state[2].v_day = layout.findViewById(R.id.wed);
        week_state[2].v_time = layout.findViewById(R.id.wed_time);
        week_state[3].v_day = layout.findViewById(R.id.thu);
        week_state[3].v_time = layout.findViewById(R.id.thu_time);
        week_state[4].v_day = layout.findViewById(R.id.fri);
        week_state[4].v_time = layout.findViewById(R.id.fri_time);
        week_state[5].v_day = layout.findViewById(R.id.sat);
        week_state[5].v_time = layout.findViewById(R.id.sat_time);
        week_state[6].v_day = layout.findViewById(R.id.sun);
        week_state[6].v_time = layout.findViewById(R.id.sun_time);

        week_state[0].v_day.setOnLongClickListener(listener);
        week_state[1].v_day.setOnLongClickListener(listener);
        week_state[2].v_day.setOnLongClickListener(listener);
        week_state[3].v_day.setOnLongClickListener(listener);
        week_state[4].v_day.setOnLongClickListener(listener);
        week_state[5].v_day.setOnLongClickListener(listener);
        week_state[6].v_day.setOnLongClickListener(listener);

    }

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Schedule.onDayClicked((TextView)v, context );
            return false;
        }
    };

}

class  Day_state        {
    int state = 0;   //0-none, 1 - selected, 2 - time selected
    String time = null;
    TextView v_day = null;
    TextView v_time = null;



}
