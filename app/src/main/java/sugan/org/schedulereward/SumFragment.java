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
    AlertDialog oneday_ticket_dia;

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
        per_score_i = ms.man.per_score; Log.i("per_score", per_score_i+" ");

        name.setText(md.name);
        Man.setImage(md.img, img, context);
        t1.setText(ms.sum + "");
        if(ms.man.per_score==0){
            per_score_i=1;      // 보이지 않으므로 상관없음.
            //ms.man.per_score=1;
            //rew_lay.setVisibility(View.GONE);
            per_s_lay.setVisibility(View.GONE);
        }
        else {
            //rew_lay.setVisibility(View.VISIBLE);
            per_s_lay.setVisibility(View.VISIBLE);
           // t2.setText(ms.man.reward + "");
        }
        //t3.setText(((int) (ms.sum / ms.man.per_score)) + "");
        t3.setText(((int) (ms.sum / per_score_i)) + "");
/*
        if(ms.used_scores.size()>1) {
            UsedSumListAdapter adapter1 = new UsedSumListAdapter(context, ms.used_scores);
            list2.setAdapter(adapter1);
            b2.setVisibility(View.VISIBLE);
            A_SumActivity.setListViewHeightBasedOnChildren(list2);
        }
        else b2.setVisibility(View.GONE);
        if(ms.oneday_rews1.size()>0) {
            b3.setVisibility(View.VISIBLE);
        }
        else b3.setVisibility(View.GONE);
        if(ms.sum_datas.size()>1) {
            AccumulatedSumListAdapter adapter = new AccumulatedSumListAdapter(context, ms.sum_datas);
            list1.setAdapter(adapter);
            A_SumActivity.setListViewHeightBasedOnChildren(list1);
            b1.setVisibility(View.VISIBLE);
        }
        else  {
            list1.setAdapter(null);
            b1.setVisibility(View.GONE);
        }*/

        b1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (list1.getVisibility() == View.VISIBLE) list1.setVisibility(View.GONE);
                else list1.setVisibility(View.VISIBLE);
                list2.setVisibility(View.GONE);

            }
        });
        b2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (list2.getVisibility() == View.VISIBLE) list2.setVisibility(View.GONE);
                else list2.setVisibility(View.VISIBLE);
                list1.setVisibility(View.GONE);


            }
        });
        /*
        b3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                final GridLayout linear = (GridLayout) View.inflate(context, R.layout.oneday_not_used, null);
                for (int i = 0; i < ms.oneday_rews1.size(); i++) {
                    final TextView or = (TextView) View.inflate(context, R.layout.my_coupon, null);
                    or.setText(ms.oneday_rews1.get(i).rew_desc);

                    linear.addView(or);
                    or.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(context)
                                    .setMessage(R.string.dia_oneday)
                                    .setPositiveButton(R.string.y, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichbutton) {
                                            String rew_desc = or.getText().toString();
                                           // Sum.removeOnedayRew(context, ms.man._id, rew_desc);
                                            linear.removeView(or);

                                            for (int j = 0; j < ms.oneday_rews1.size(); j++) {
                                                if (ms.oneday_rews1.get(j).rew_desc.equals(rew_desc)) {
                                                    ms.oneday_rews1.remove(j);
                                                    break;
                                                }
                                            }
                                            if (ms.oneday_rews1.size() == 0) {
                                                //oneday_ticket_dia.dismiss();
                                                b3.setVisibility(View.GONE);
                                            }
                                            oneday_ticket_dia.dismiss();


                                        }
                                    })
                                    .setNegativeButton(R.string.n, null)
                                    .show();
                        }
                    });
                }

                oneday_ticket_dia = new AlertDialog.Builder(context)
                        .setTitle(R.string.sel_coupon)
                        //.setIcon(R.drawable.androboy)
                        .setNegativeButton(R.string.cancle, null)
                        .setView(linear)
                        .show();
            }
        });*/
        return root;

    }

}
