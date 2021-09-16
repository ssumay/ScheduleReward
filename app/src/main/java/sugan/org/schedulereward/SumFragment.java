package sugan.org.schedulereward;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by eunsoo on 2017-12-12.
 * updated by eunsoo on summer 2021
 */

public class SumFragment extends Fragment {
    Context context;
    Man_data md;
    Man_sum  ms;
    //String m_id;
    View root;

    TextView name;
    ImageView img;
    TextView t1;
    TextView t2;
    TextView t3;

    TextView b1;
    TextView b2;
    ListView list1;
    ListView list2;
    TextView b3;
    LinearLayout per_s_lay;
   // AlertDialog oneday_ticket_dia;

    public static SumFragment newInstance(Man_data man, Context context) {
        SumFragment fragment = new SumFragment();
        fragment.context = context;
        fragment.md = man;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_sum, container, false);
        name = (TextView) root.findViewById(R.id.name);
        name.setText(md.name);
        img = (ImageView) root.findViewById(R.id.img);

        Man.setImage(md.img, img, context);
        t1 = (TextView) root.findViewById(R.id.t1);
        t2 = (TextView) root.findViewById(R.id.t2);
        t3 = (TextView) root.findViewById(R.id.t3);
        b1 = (TextView) root.findViewById(R.id.b1);
        b2 = (TextView) root.findViewById(R.id.b2);
        list1 = (ListView) root.findViewById(R.id.list1);
        list2 = (ListView) root.findViewById(R.id.list2);
        b2 = (TextView) root.findViewById(R.id.b2);
        b3 = (TextView) root.findViewById(R.id.b3);
        HomeListener listener1 = HomeListener.newInstance(md, context);
        ((TextView) root.findViewById(R.id.home)).setOnTouchListener(listener1);
        LogoutListener listener = LogoutListener.newInstance(md, context);
        ((TextView) root.findViewById(R.id.logout)).setOnTouchListener(listener);
        per_s_lay = (LinearLayout) root.findViewById(R.id.per_s_lay);
        int per_score_i;

        ms = Sum.fillSumByMan(context, md);

        Sum.fillSum(ms, b1, b2, b3, list1, list2, img, context);

        per_score_i = ms.man.per_score; Log.i("per_score", per_score_i+" ");

        name.setText(md.name);
       // Man.setImage(md.img, img, context);   들어감
        t1.setText(ms.sum + "");
        if(ms.man.per_score==0){
            per_score_i=1;      // 보이지 않으므로 상관없음.
            per_s_lay.setVisibility(View.GONE);
        }
        else {
            per_s_lay.setVisibility(View.VISIBLE);
        }
        t3.setText(((int) (ms.sum / per_score_i)) + "");

        return root;

    }

}
