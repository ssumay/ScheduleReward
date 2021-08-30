package sugan.org.schedulereward;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-18.
 */

public class MyPageFragment extends Fragment {

    Context context;
    Man_data md;
    //String m_id;
    View root;

    public static MyPageFragment newInstance(Man_data man, Context context) {
        MyPageFragment fragment = new MyPageFragment();
        fragment.context = context;
        fragment.md = man;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_mypage, container, false);
        ((TextView) root.findViewById(R.id.name)).setText(md.name);
        Man.setImage(md.img, (ImageView)root.findViewById(R.id.img), context);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ArrayList<Sched_data> group = new ArrayList<Sched_data>();

        ListView listView = (ListView) root.findViewById(R.id.list);
        FragmentSchedAdapter adapter = Schedule.getRightNowList_frag(context, group, md);
        listView.setAdapter(adapter);

        LogoutListener listener = LogoutListener.newInstance(md, context);
        ((TextView) root.findViewById(R.id.logout)).setOnTouchListener(listener);
        SumListener listener1 = SumListener.newInstance(md, context);
        ((TextView) root.findViewById(R.id.sum)).setOnTouchListener(listener1);



    }
}

class TitleListener implements View.OnTouchListener {

    Man_data md;
    Sched_data sd;
    //String s_id;
    //String seq_num;
    Context context;

    public static TitleListener newInstance(Man_data man, Sched_data sd, Context context) {

        TitleListener listener = new TitleListener();
        listener.md = man;
        listener.sd = sd;
        //listener.s_id = s_id;
        //listener.seq_num = seq_num;
        listener.context = context;

        return listener;
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("logout_ontouch", "aaa");
        Intent intent = new Intent(context, DoSchedActivity.class);
        intent.putExtra("man_data", md);
        intent.putExtra("sched", sd);
        //intent.putExtra("s_id", s_id);
        intent.putExtra("seq", "1");
        //intent.putExtra("seq_num", seq_num);
        intent.putExtra("page", MyFragment.DOSCHED);
        context.startActivity(intent);
        return false;
    }
}

class FragmentSchedAdapter extends BaseAdapter {

    Context context;
    ArrayList<Sched_data> items = new ArrayList<Sched_data>();
    Man_data md;

    public FragmentSchedAdapter(Context c, ArrayList<Sched_data> it , Man_data man) {
        context = c;
        items = it;
        md = man;

    }

    @Override
    public int getCount() {
        return items.size();

    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_sched_list, null);

        }
        Sched_data ld = (Sched_data) items.get(i);
        TextView title = ((TextView) convertView.findViewById(R.id.title));
        title.setText(ld.title);
        TitleListener listener = TitleListener.newInstance( md, ld, context);
        title.setOnTouchListener(listener);

        ((TextView)convertView.findViewById(R.id.i)).setText(i+"");
        //((TextView) convertView.findViewById(R.id.seq_num)).setText(ld.seq_num + "");

        return convertView;

    }

    public void addItem(Sched_data item) {
        items.add(item);
    }
}