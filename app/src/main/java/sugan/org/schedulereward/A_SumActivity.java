package sugan.org.schedulereward;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
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
    //TextView per_score;
    int per_score_i;
   // TextView rew_desc;
    TextView b1;
    ListView list1;
    TextView t1;
    EditText e1;
    //TextView t2;
   // TextView t3;
   // EditText e2;
    TextView b2;
    ListView list2;
    TextView b3;
    TextView b4;
    TextView sign;
    //TextView sign1;
    TextView sum_e;
    boolean state;  //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)
    //LinearLayout oneday_layout;
    int selected_i;

    ArrayList<Man_data> mds;
    //ArrayList<Man_sum> mss;
    Man_sum  ms;
    //LinearLayout  per_s_lay;
    //LinearLayout  rew_lay;

    ListView list3;
    Man_data admin;

    //TextView b5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_sum);

        img = findViewById(R.id.img);
        man_spi =  findViewById(R.id.man_spi);
        b1 = findViewById(R.id.b1);
        list1 = findViewById(R.id.list1);
        t1 = findViewById(R.id.t1);
        e1 = findViewById(R.id.e1);
        b2 = findViewById(R.id.b2);
        list2 = findViewById(R.id.list2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        sign = findViewById(R.id.sign);
        sum_e = findViewById(R.id.sum_e);
        list3 = findViewById(R.id.list3);

        //oneday_layout = (LinearLayout) findViewById(R.id.oneday_layout);
        mds = Sum.fillNameSpinner(this, man_spi);

        if(mds.size()==0) {
            Toast.makeText(A_SumActivity.this, R.string.regi_man1, Toast.LENGTH_LONG).show();
            finish();

        }
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
                String s1 = t1.getText().toString();
                double t1_i = Double.parseDouble(s1.equals("")? "0" : s1);
               // Log.i("t3", t3.getText().toString()+" ");
               // int t3_i = Integer.parseInt(t3.getText().toString());
                if (s.length() >= 0) {
                    double s_i = s.toString().equals("") ? 0: Double.parseDouble(s.toString()); Log.i("s_i", s_i+" "+ (int)(s_i/per_score_i));

                    /*
                    if(state && s_i > t1_i) {
                        e1.setText("");  // e2.setText("");
                        Toast.makeText(A_SumActivity.this, R.string.input_small, Toast.LENGTH_SHORT).show();

                    }*/
                    //else {
                      //  if(e1.isFocused())
                       // e2.setText((int)(s_i/per_score_i)+"");
                    //}

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        e1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.i("keycode", "enter");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(e1.getWindowToken(), 0);    //hide keyboard"
                    String s_i = e1.getText().toString();
                    double i_1 = s_i.equals("") ? 0: Double.parseDouble(e1.getText().toString());
                    if(i_1 > 0) {
                        if(state)    //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)
                            i_1 = -i_1;
                        Sum.insertScore(A_SumActivity.this, ms.man.name, i_1);
                        fillSum();
                    }
                    e1.setText("");
                    return true;
                }
                return false;
            }
        });
/*
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

        Intent intent = getIntent();
        admin = (Man_data)intent.getSerializableExtra("man_data");

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

        state = true;  //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)

        ms = Sum.fillSumByMan(A_SumActivity.this, mds.get(selected_i));

        Man.setImage(ms.man.img, img, this);
        per_score_i = ms.man.per_score;Log.i("per_score", per_score_i+" ");
        //per_score.setText(per_score_i + "");
       // rew_desc.setText(ms.man.reward);
        t1.setText(ms.sum + "");
        if(ms.man.per_score==0){
            per_score_i=1;      // 보이지 않으므로 상관없음.
            //ms.man.per_score=1;
        //    rew_lay.setVisibility(View.GONE);
         //   per_s_lay.setVisibility(View.GONE);
        }
        //else {
       //     rew_lay.setVisibility(View.VISIBLE);
        //    per_s_lay.setVisibility(View.VISIBLE);
         //   t2.setText(ms.man.reward + "");
        //}
        //t3.setText(((int) (ms.sum / ms.man.per_score)) + "");
        //t3.setText(((int) (ms.sum / per_score_i)) + "");

        if(ms.sum_datas.size()>1) {
            SumListAdapter adapter = new SumListAdapter(A_SumActivity.this, ms.sum_datas);
            list1.setAdapter(adapter);
            setListViewHeightBasedOnChildren(list1);
        }
        else  {
            list1.setAdapter(null);
        }
        if(ms.cp_datas.size()>1){
            CpListAdapter adapter = new CpListAdapter(A_SumActivity.this, ms.cp_datas);
            list2.setAdapter(adapter);
            setListViewHeightBasedOnChildren(list2);

        }
    }


    public void onB4Clicked(View v) {  //점수주기 버튼
        if (state) {   //true - 빼기(점수주기버튼), false - 더하기 (점수빼기버튼)
            sign.setText("+");
            sign.setTextSize(30);
            //sign1.setText("+");
            //sign1.setTextSize(30);
            b4.setText(R.string.sub_sc);
            sum_e.setText(R.string.sum_enter1);
            state = false;
        } else {
            sign.setText("ㅡ");
            sign.setTextSize(20);
            //sign1.setText("ㅡ");
            //sign1.setTextSize(20);
            sum_e.setText(R.string.sum_enter);
            b4.setText(R.string.add_sc);
            state = true;
        }
    }

    public void onB1Clicked(View v) {   //점수내역 sum history
        list1.setVisibility(list1.getVisibility() == View.VISIBLE? View.GONE:View.VISIBLE);
        list2.setVisibility(View.GONE);
    }

    public void onB2Clicked(View v) {   //쿠폰내역 coupon history
        list2.setVisibility(list2.getVisibility() == View.VISIBLE? View.GONE:View.VISIBLE);
        list1.setVisibility(View.GONE);
    }

    public void onMyCoupons(View v) {
        Coupon.onMyCoupons(this, man_spi.getSelectedItem()+"");

        }
    public void onHome(View v){
        Intent intent = new Intent(this, AdminActivity.class);
        intent.putExtra("man_data", admin);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}


class SumListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Sum_data> items = new ArrayList<Sum_data>();

    public SumListAdapter(Context c, ArrayList<Sum_data> it) {
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
            convertView = inflater.inflate(R.layout.sum_list, null);

        }
        Sum_data sd = (Sum_data) items.get(i);
Log.i("sd.picture", sd.picture + " ");
        ((TextView) convertView.findViewById(R.id.date)).setText(sd.date);
        ((TextView) convertView.findViewById(R.id.s_title)).setText(sd.s_title);
        ((TextView) convertView.findViewById(R.id.link_note)).setText(sd.link_note);
        ((TextView) convertView.findViewById(R.id.s_title)).setText(sd.s_title);
        if(sd.picture != null)
        Man.setImage(sd.picture, (ImageView) convertView.findViewById(R.id.picture), context);

        ((TextView) convertView.findViewById(R.id.score)).setText(sd.score);

        return convertView;

    }

    public void addItem(Sum_data item) {
        items.add(item);
    }
}
class CpListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Coupon_data> items = new ArrayList<>();

    public CpListAdapter(Context c, ArrayList<Coupon_data> it) {
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
            convertView = inflater.inflate(R.layout.coupon_list, null);

        }
        Coupon_data cd = (Coupon_data) items.get(i);

        ((TextView) convertView.findViewById(R.id.date)).setText(cd.date);
        ((TextView) convertView.findViewById(R.id.s_title)).setText(cd.s_title);
        ((TextView) convertView.findViewById(R.id.link_note)).setText(cd.link_note);
        ((TextView) convertView.findViewById(R.id.price)).setText(cd.price);
        ((TextView) convertView.findViewById(R.id.name)).setText(cd.name);
        ((TextView) convertView.findViewById(R.id.state)).setText(cd.state?"used": "");

        return convertView;

    }

    public void addItem(Coupon_data item) {
        items.add(item);
    }
}
