package sugan.org.schedulereward;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-25.
 */

public class A_SumActivity extends AppCompatActivity {

    ImageView img;
    Spinner man_spi;
    TextView per_score;  int per_score_i;
    TextView rew_desc;
    TextView b1;
    ListView list1;
    TextView t1;
    EditText e1;
    TextView t2;
    TextView t3;
    EditText e2;
    TextView b2;
    ListView list2;
    TextView b3;
    TextView b4;
    TextView sign; TextView sign1; TextView sum_e;
    boolean state;  //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)
    //LinearLayout oneday_layout;
    int selected_i;

    ArrayList<Man_data> mds;
    //ArrayList<Man_sum> mss;
    Man_sum  ms;
    LinearLayout  per_s_lay;
    LinearLayout  rew_lay;

    AlertDialog oneday_ticket_dia;
    ListView list3;
    TextView b5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sum);

        img = (ImageView) findViewById(R.id.img);
        man_spi = (Spinner) findViewById(R.id.man_spi);
        per_score = (TextView) findViewById(R.id.per_score);
        rew_desc = (TextView) findViewById(R.id.rew_desc);
        rew_lay = (LinearLayout) findViewById(R.id.rew_lay);
        b1 = (TextView) findViewById(R.id.b1);
        list1 = (ListView) findViewById(R.id.list1);
        t1 = (TextView) findViewById(R.id.t1);
        e1 = (EditText) findViewById(R.id.e1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        e2 = (EditText) findViewById(R.id.e2);
        b2 = (TextView) findViewById(R.id.b2);
        list2 = (ListView) findViewById(R.id.list2);
        b3 = (TextView) findViewById(R.id.b3);
        b4 = (TextView) findViewById(R.id.b4);
        sign = (TextView) findViewById(R.id.sign);
        sign1 = (TextView) findViewById(R.id.sign1);
        sum_e = (TextView) findViewById(R.id.sum_e);
        per_s_lay = (LinearLayout) findViewById(R.id.per_s_lay);
        list3 = (ListView) findViewById(R.id.list3);
        b5 = (TextView) findViewById(R.id.b5);

        //oneday_layout = (LinearLayout) findViewById(R.id.oneday_layout);
        mds = Sum.fillNameSpinner(this, man_spi);
        //Sum.fillManSum(this, man_spi);
        man_spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_i = i;
                fillSum();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        e1.addTextChangedListener( new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                int t1_i = Integer.parseInt(t1.getText().toString());
                Log.i("t3", t3.getText().toString()+" ");
                int t3_i = Integer.parseInt(t3.getText().toString());
                if (s.length() >= 0) {
                    int s_i = s.toString().equals("") ? 0: Integer.parseInt(s.toString()); Log.i("s_i", s_i+" "+ (int)(s_i/per_score_i));

                    if(state && s_i > t1_i) {
                        e1.setText("");   e2.setText("");
                        Toast.makeText(A_SumActivity.this, R.string.input_small, Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if(e1.isFocused())
                        e2.setText((int)(s_i/per_score_i)+"");
                    }

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        e2.addTextChangedListener( new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                int t1_i = Integer.parseInt(t1.getText().toString());
                int t3_i = Integer.parseInt(t3.getText().toString());
                if (s.length() >= 0) {
                    int s_i = s.toString().equals("") ? 0: Integer.parseInt(s.toString());
                    if(state && s_i > t3_i) {
                        e1.setText("");  e2.setText("");
                        Toast.makeText(A_SumActivity.this, R.string.input_small, Toast.LENGTH_SHORT).show();

                    }
                    else {
                        int i_1 = s_i*per_score_i;
                        Log.i("s_i*per_score_i", s_i*per_score_i+" ");
                        if(e2.isFocused()) e1.setText((s_i*per_score_i)+"");
                    }

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
/*
        e1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.i("keycode", "enter");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(e1.getWindowToken(), 0);    //hide keyboard"
                    String s_i = e1.getText().toString();
                    int i_1 = s_i.equals("") ? 0: Integer.parseInt(e1.getText().toString());
                    if(i_1 > 0) {
                        if(state)    //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)
                        Sum.minuScore(A_SumActivity.this, ms.man.name, i_1);
                        else Sum.plusScore(A_SumActivity.this, ms.man.name, i_1);
                        fillSum();
                    }
                    e1.setText(""); e2.setText("");
                    return true;
                }
                return false;
            }
        });

        e2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.i("keycode", "enter");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(e2.getWindowToken(), 0);    //hide keyboard"
                    String s_i = e2.getText().toString();
                    int i_1 = (s_i.equals("") ? 0: Integer.parseInt(s_i))*per_score_i;
                    if(i_1 > 0) {
                        if(state)   Sum.minuScore(A_SumActivity.this, ms.man._id, i_1);
                        else  Sum.plusScore(A_SumActivity.this, ms.man._id, i_1);
                        fillSum();
                    }
                    e1.setText(""); e2.setText("");
                    return true;
                }
                return false;
            }
        });*/
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)  return;

        int numberOfItems = listAdapter.getCount();

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() *  (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public void fillSum () {

        b4.setText(R.string.add_sc);
        sign.setText("ㅡ");
        sign.setTextSize(20);
        sign1.setText("ㅡ");
        sign1.setTextSize(20);

        state = true;  //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)

        ms = Sum.fillSumByMan(A_SumActivity.this, mds.get(selected_i));

        Man.setImage(ms.man.img, img);
        per_score_i = ms.man.per_score;Log.i("per_score", per_score_i+" ");
        per_score.setText(per_score_i + "");
       // rew_desc.setText(ms.man.reward);
        t1.setText(ms.sum + "");
        if(ms.man.per_score==0){
            per_score_i=1;      // 보이지 않으므로 상관없음.
            //ms.man.per_score=1;
            rew_lay.setVisibility(View.GONE);
            per_s_lay.setVisibility(View.GONE);
        }
        else {
            rew_lay.setVisibility(View.VISIBLE);
            per_s_lay.setVisibility(View.VISIBLE);
         //   t2.setText(ms.man.reward + "");
        }
        //t3.setText(((int) (ms.sum / ms.man.per_score)) + "");
        t3.setText(((int) (ms.sum / per_score_i)) + "");


        if(ms.sc_datas.size()>1) {
            DoneSchedListAdapter adapter = new DoneSchedListAdapter(A_SumActivity.this, ms.sc_datas);
            list3.setAdapter(adapter);
            setListViewHeightBasedOnChildren(list3);
            b5.setVisibility(View.VISIBLE);
        }
        else  {
            list3.setAdapter(null);
            b5.setVisibility(View.GONE);
        }
        if(ms.sum_datas.size()>1) {
            AccumulatedSumListAdapter adapter = new AccumulatedSumListAdapter(A_SumActivity.this, ms.sum_datas);
            list1.setAdapter(adapter);
            setListViewHeightBasedOnChildren(list1);
            b1.setVisibility(View.VISIBLE);
        }
        else  {
            list1.setAdapter(null);
            b1.setVisibility(View.GONE);
        }

        if(ms.used_scores.size()>1) {
            UsedSumListAdapter adapter1 = new UsedSumListAdapter(A_SumActivity.this, ms.used_scores);
            list2.setAdapter(adapter1);
            b2.setVisibility(View.VISIBLE);
            setListViewHeightBasedOnChildren(list2);
        }
        else b2.setVisibility(View.GONE);
        if(ms.oneday_rews1.size()>0) {
            b3.setVisibility(View.VISIBLE);
        }
        else b3.setVisibility(View.GONE);

        Log.i("ms.oneday_rews1.size", ms.oneday_rews1.size()+" ");

    }

    public void onB5Clicked(View v) {
        if (list3.getVisibility() == View.VISIBLE) list3.setVisibility(View.GONE);
        else list3.setVisibility(View.VISIBLE);
    }

    public void onB4Clicked(View v) {  //점수주기 버튼
        if (state) {   //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)
            sign.setText("+");
            sign.setTextSize(30);
            sign1.setText("+");
            sign1.setTextSize(30);
            b4.setText(R.string.sub_sc);
            sum_e.setText(R.string.sum_enter1);
            state = false;
        } else {
            sign.setText("ㅡ");
            sign.setTextSize(20);
            sign1.setText("ㅡ");
            sign1.setTextSize(20);
            sum_e.setText(R.string.sum_enter);
            b4.setText(R.string.add_sc);
            state = true;
        }
    }

    public void onB1Clicked(View v) {
        if (list1.getVisibility() == View.VISIBLE) list1.setVisibility(View.GONE);
        else list1.setVisibility(View.VISIBLE);
        list2.setVisibility(View.GONE);

    }

    public void onB2Clicked(View v) {
        if (list2.getVisibility() == View.VISIBLE) list2.setVisibility(View.GONE);
        else list2.setVisibility(View.VISIBLE);
        list1.setVisibility(View.GONE);

    }

    public void onB3Clicked(View v) {

        final GridLayout linear = (GridLayout) View.inflate(this, R.layout.oneday_not_used, null);
        for(int i=0; i<ms.oneday_rews1.size(); i++) {
            final TextView or = (TextView)View.inflate(this, R.layout.my_coupon, null);
            or.setText(ms.oneday_rews1.get(i).rew_desc);

            linear.addView(or);
            or.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(A_SumActivity.this)
                            .setMessage(R.string.dia_oneday)
                            .setPositiveButton(R.string.y, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichbutton) {
                                    String rew_desc =  or.getText().toString();
                                  //  Sum.removeOnedayRew(A_SumActivity.this, ms.man._id, rew_desc );
                                    linear.removeView(or);

                                    for(int j=0; j<ms.oneday_rews1.size(); j++) {
                                        if(ms.oneday_rews1.get(j).rew_desc.equals(rew_desc)) {
                                            ms.oneday_rews1.remove(j);
                                            break;
                                        }
                                    }
                                    if(ms.oneday_rews1.size() ==0){
                                        b3.setVisibility(View.GONE);
                                    }
                                    oneday_ticket_dia.dismiss();


                                }
                            } )
                            .setNegativeButton(R.string.n, null)
                            .show();
                }
            });
        }

                oneday_ticket_dia = new AlertDialog.Builder(this)
                    .setTitle(R.string.sel_coupon)
                    //.setIcon(R.drawable.androboy)
                    .setView(linear)
                        .setNegativeButton(R.string.cancle, null)
                    .show();
        }
    public void onHome(View v){
        Intent intent = new Intent(this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}


class AccumulatedSumListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Sum_data> items = new ArrayList<Sum_data>();

    public AccumulatedSumListAdapter(Context c, ArrayList<Sum_data> it) {
        context = c;
        items = it;
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
            convertView = inflater.inflate(R.layout.accu_sum_list, null);

        }
        Sum_data sd = (Sum_data) items.get(i);

        ((TextView) convertView.findViewById(R.id.date)).setText(sd.date);
        ((TextView) convertView.findViewById(R.id.sched)).setText(sd.s_title);
        ((TextView) convertView.findViewById(R.id.score)).setText(sd.score);

        return convertView;

    }

    public void addItem(Sum_data item) {
        items.add(item);
    }
}


class DoneSchedListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Done_sc_data> items = new ArrayList<Done_sc_data>();

    public DoneSchedListAdapter(Context c, ArrayList<Done_sc_data> it) {
        context = c;
        items = it;
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
            convertView = inflater.inflate(R.layout.done_sc_list, null);

        }
        Done_sc_data sd = (Done_sc_data) items.get(i);

        ((TextView) convertView.findViewById(R.id.s_title)).setText(sd.s_title);
        ((TextView) convertView.findViewById(R.id.link_note)).setText(sd.link_note);
        ((TextView) convertView.findViewById(R.id.seq)).setText(sd.seq+"");

        ((TextView) convertView.findViewById(R.id.score)).setText(sd.score+"");
        Log.i("sd.picture", sd.picture+" ");
        Man.setImage(sd.picture, (ImageView) convertView.findViewById(R.id.picture));
        ((TextView) convertView.findViewById(R.id.date)).setText(sd.date);

        return convertView;

    }

    public void addItem(Done_sc_data item) {
        items.add(item);
    }
}


class UsedSumListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Sum_data> items = new ArrayList<Sum_data>();

    public UsedSumListAdapter(Context c, ArrayList<Sum_data> it) {
        context = c;
        items = it;
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
            convertView = inflater.inflate(R.layout.used_sum_list, null);

        }
        Sum_data sd = (Sum_data) items.get(i);

        ((TextView) convertView.findViewById(R.id.date)).setText(sd.date);
        ((TextView) convertView.findViewById(R.id.score)).setText(sd.score);

        return convertView;

    }

    public void addItem(Sum_data item) {
        items.add(item);
    }
}
