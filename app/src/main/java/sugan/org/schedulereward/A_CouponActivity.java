package sugan.org.schedulereward;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-23.
 */

public class A_CouponActivity extends AppCompatActivity {

    TextView regi_coupon;
    LinearLayout new_coupon_dialog;
    EditText new_coupon;
    TextView save_a;
    TextView cancel_a;
    boolean regi_coupon_click = false;

    LinearLayout del_coupon_l;
    Spinner coupon_spi;
    TextView del_coupon;
    LinearLayout give_cpl;
    Spinner  man_spi;
    Spinner  coupon_spi1;

    RadioGroup cpb_rbg;
    RadioButton period;
    RadioButton one_time;
    LinearLayout peroid_l;

    Coupon_data cd;
    Spinner month_day_sel_spi ;  //1 ~ 28 , last day
    EditText price;    //쿠폰 한개당 가격

    Spinner man_spi1;
    ListView period_coupon_list;
    PeriodicCouponListAdapter adapter;
    String man_name1;

    String from;
    TextView give_cp;

    Man man;
    String man_name;

    LinearLayout mycoupon_l;
    TextView mycoupons_b;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_coupon);

        allocate_variables();
        attachListeners();
        initiate();

    }

    void allocate_variables(){
        regi_coupon = findViewById(R.id.regi_coupon);

        new_coupon_dialog = findViewById(R.id.new_coupon_dialog);
        new_coupon = findViewById(R.id.new_coupon);
        save_a = findViewById(R.id.save_a);
        cancel_a = findViewById(R.id.cancel_a);

        del_coupon_l = findViewById(R.id.del_coupon_l);
        coupon_spi =  findViewById(R.id.coupon_spi);
        del_coupon = findViewById(R.id.del_coupon);

        give_cp = findViewById(R.id.give_cp);
        give_cpl = findViewById(R.id.give_cpl);
        man_spi = findViewById(R.id.man_spi);
        coupon_spi1 = findViewById(R.id.coupon_spi1);
        cpb_rbg = findViewById(R.id.cpb_rbg);  //radiogroup
        period = findViewById(R.id.period);  //radio button
        one_time = findViewById(R.id.one_time);
        peroid_l = findViewById(R.id.period_l);
        price = findViewById(R.id.price);

        man_spi1 = findViewById(R.id.man_spi1);
        period_coupon_list = findViewById(R.id.period_coupon_list);

        mycoupon_l = findViewById(R.id.mycoupon_l);
        mycoupons_b = findViewById(R.id.mycoupons_b);

    }

    void attachListeners() {
        regi_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regi_coupon_click = !regi_coupon_click;
                if (regi_coupon_click) {
                    new_coupon_dialog.setVisibility(View.VISIBLE);
                }
                else
                    new_coupon_dialog.setVisibility(View.GONE);
            }
        });
         new_coupon.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Util.editTextSaveListener(s, save_a, new_coupon);
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });

           save_a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {Log.i("save", "onClick");
                    int result ;
                    if(Coupon.saveNewCoupon(A_CouponActivity.this, new_coupon.getText().toString().trim())){
                        result = R.string.saved;
                        Coupon.setCoupons(A_CouponActivity.this, coupon_spi, del_coupon_l, coupon_spi1);
                    }
                    else {
                        result = R.string.coupon_exist;

                    }
                    Toast.makeText(A_CouponActivity.this, result, Toast.LENGTH_SHORT).show();
                    closeNewCouponDialog();


                }
            });

                cancel_a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeNewCouponDialog();
                }
            });

                cpb_rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {   //일회적으로 or 주기적으로 선택 radiogroup
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(group.getId()== R.id.cpb_rbg){
                            switch(checkedId){
                                case R.id.one_time:
                                    peroid_l.setVisibility(View.GONE);
                                    cd.period = "0";
                                    break;
                                case R.id.period:
                                    LinearLayout linear = (LinearLayout) View.inflate(A_CouponActivity.this, R.layout.period_coupon_dialog, null);

                                    AlertDialog period = new AlertDialog.Builder(A_CouponActivity.this)   //schedule db에 state = 0로 저장, 즉 사용자에게 보이지 않는 스케줄이 됨
                                            .setTitle(R.string.cpb_periodical).setMessage(R.string.select_belows)
                                            .setView(linear)
                                            //.setIcon(R.drawable.androboy)
                                            .show();

                                    CheckBox[] week = new CheckBox[7];

                                    week[0] = linear.findViewById(R.id.mon);
                                    week[1] = linear.findViewById(R.id.tue);
                                    week[2] = linear.findViewById(R.id.wed);
                                    week[3] = linear.findViewById(R.id.thu);
                                    week[4] = linear.findViewById(R.id.fri);
                                    week[5] = linear.findViewById(R.id.sat);
                                    week[6] = linear.findViewById(R.id.sun);

                                    month_day_sel_spi = linear.findViewById(R.id.month_day_sel_spi);

                                    CheckBox a_month = linear.findViewById(R.id.a_month);

                                    for(CheckBox i: week) {
                                        i.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                if(isChecked) if(a_month.isChecked()) a_month.setChecked(false);
                                            }
                                        });
                                    }

                                    a_month.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                for(CheckBox i: week) {
                                                    i.setChecked(false);
                                                }
                                            }
                                        }
                                    });

                                    TextView save_b = linear.findViewById(R.id.save_b);
                                    TextView cancel_b = linear.findViewById(R.id.cancel_b);

                                    save_b.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean checked = false;
                                            if(a_month.isChecked()) {
                                                checked = true;
                                                Log.i("selected_day", month_day_sel_spi.getSelectedItemPosition()+" ");
                                                cd.period = month_day_sel_spi.getSelectedItemPosition() <= 27 ?
                                                        month_day_sel_spi.getSelectedItemPosition() + 1+"" : -1+"";

                                            }
                                            else{
                                                for(int i=0; i<7; i++){
                                                    if(week[i].isChecked()) {
                                                        cd.period +="1";
                                                        checked = true;
                                                    }
                                                    else cd.period +="0";
                                                }
                                                cd.period = cd.period.substring(1);
                                                Log.i("cd.period", cd.period+ " ");
                                                if(!checked) { //선택된 것이 없다
                                                    cd.period = "0";
                                                    Toast.makeText(A_CouponActivity.this, R.string.select1, Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }

                                            period.dismiss();
                                            setPeriodLayout();



                                        }
                                    });

                                    cancel_b.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            one_time.setChecked(true);
                                            peroid_l.setVisibility(View.GONE);
                                            period.dismiss();
                                        }
                                    });


                                    break;
                            }
                        }
                    }
                });

        }

        void setPeriodLayout(){
            peroid_l.removeAllViews();
            peroid_l.setVisibility(View.VISIBLE);

            Coupon.parsePeriodicData(cd.period, peroid_l, this);
        }

    void initiate(){

        Coupon.setCoupons(this, coupon_spi, del_coupon_l, coupon_spi1);
        man = new Man();
        fillMan();
        cd = new Coupon_data();

    }

        void closeNewCouponDialog(){
            regi_coupon_click = false;
            new_coupon_dialog.setVisibility(View.GONE);
            new_coupon.setText("");
        }

        public void fillMan() {
            man.fillSpinner(this, man_spi, man_spi1);

            man_spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    man_name = man.mans.get(i).name;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(man_spi1.getAdapter().getCount()==0)  mycoupon_l.setVisibility(View.GONE);
            else mycoupon_l.setVisibility(View.VISIBLE);

        man_spi1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                Log.i("man_spi1","onItemSelected" + i );
                man_name1 = man.mans.get(i).name ;
                adapter = Coupon.getPeriodicCouponListAdapter(A_CouponActivity.this, man_name1);
                period_coupon_list.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void onCpb(View v) {
        if(del_coupon_l.getVisibility()==View.GONE){
            Toast.makeText(this, R.string.regi_coupon, Toast.LENGTH_SHORT).show();
            return;
        }
        if(man_spi.getAdapter().getCount()==0){
            Toast.makeText(this, R.string.regi_man1, Toast.LENGTH_SHORT).show();
            return;
        }
        if(give_cpl.getVisibility()==View.GONE) {
            give_cpl.setVisibility(View.VISIBLE);
            cd = new Coupon_data(coupon_spi1.getSelectedItem()+"");
        }
        else {
            give_cpl.setVisibility(View.GONE);
            coupon_spi1.setSelection(0);
            ((TextView)findViewById(R.id.ea)).setText("1");
            man_spi.setSelection(0);
            one_time.setChecked(true);

            cd = null;
        }

    }

    public void onPublishCP(View v) {

        new AlertDialog.Builder(this).setMessage(R.string.pub_cp).setPositiveButton(R.string.y,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int b) {
                        cd.name = coupon_spi1.getSelectedItem()+"";
                        cd.man_name = man_name;
                        cd.ea = ((TextView)findViewById(R.id.ea)).getText().toString();
                        if(cd.ea.equals("")) cd.ea = "1";
                        cd.price = "'" + price.getText().toString().trim() + "'";
                        if(cd.price.equals("'0'")) cd.price = "NULL";
                        Log.i("price", cd.price + " ");

                        Coupon.giveCoupon(cd, A_CouponActivity.this);
                        //Schedule.delSched(s_id, A_ScListActivity.this);
                        onCpb(null);

                        adapter = Coupon.getPeriodicCouponListAdapter(A_CouponActivity.this, man_name1);
                        period_coupon_list.setAdapter(adapter);
                       // fillMan();
                    }
                }).setNegativeButton(R.string.n, null)
                .show();
    }

    public void onCancelPub(View v) {

    }

    /*

    public void onAddRewClicked(View v) {
        int v1 = add_rew.getVisibility();

        if(v1==View.GONE)
            add_rew.setVisibility(View.VISIBLE);

        else
            add_rew.setVisibility(View.GONE);


    }*/

    public void onHome(View v){
        Intent intent = new Intent(this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onSaveClicked(View v) {

       /* String item_trim = rew_item.getText().toString().trim();
        if(item_trim.equals("")) return;
        if(from==null) {
            for (int i = 0; i < rews.size(); i++) {
                if (item_trim.equals(rews.get(i).name)) {
                    Toast.makeText(this, R.string.reg_before, Toast.LENGTH_SHORT).show();
                    add_rew.setVisibility(View.GONE);
                    return;
                }
            }
        }


        SchedDBHelper sHelper = new SchedDBHelper(this);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        //Log.i("aaa", time_spi.getSelectedItem().toString());
        String sql = "insert into reward values (null, '" + item_trim+"', 0)"; Log.i("sql", sql);
        db.execSQL(sql);
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        add_rew.setVisibility(View.GONE);
        sHelper.close();
        if(from!=null && from.equals("schedA")) {
            finish();

        }*/
       // else
        //listRew();

    }

    public void onDelCoupon(View v) {
        //Log.i("del_coupon", coupon_spi.getSelectedItem()+ " ");
        SchedDBHelper sHelper = new SchedDBHelper(this);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        db.execSQL("delete from coupon where name='" + coupon_spi.getSelectedItem()+"' ");
        sHelper.close();
        Coupon.setCoupons(this, coupon_spi, del_coupon_l, coupon_spi1);

    }
    /*
    public void onCancelClicked(View v) {
        if(from!=null && from.equals("schedA")) {
            finish();

        }
        //else
       // add_rew.setVisibility(View.GONE);

    }*/
    /*
    public void listRew() {

        if( (rews = Coupon.fillReward(this, rew_spi, rew_spi1, 0)).size()==0){
            del_rew.setVisibility(View.GONE);
            rew_spi1.setVisibility(View.GONE);
            return;
        }

        del_rew.setVisibility(View.VISIBLE);
        rew_spi1.setVisibility(View.VISIBLE);

        rew_spi1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               cp_e.setText(rews.get(i).name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }*/

    public void onNewMan(View v){
        Intent intent = new Intent(this, A_MListActivity.class);
        intent.putExtra("from", "couponA");
        from = "new_man";
        startActivity(intent);
    }

    public void onCancelCP(View v) {
       // cp_opened = true;
        onCpb(null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("A_CouponActivity", "onRestart()");
        if(from!=null && from.equals("new_man")) {
        //    cp_opened = false;
            fillMan();
           // onCpb(null);
        }
        from = null;
        Coupon.givePeriodicCoupons(this);   //주기적으로 발행되는 쿠폰 체크하고 발행한다
    }

    public void onMyCoupons(View v){

        String man = (String)man_spi1.getSelectedItem();
        Coupon.onMyCoupons(this, man);

    }
    /*
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("A_CouponActivity", "onStart()");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Coupon.givePeriodicCoupons(this);   //주기적으로 발행되는 쿠폰 체크하고 발행한다
        Log.i("A_CouponActivity", "onResume()");

    }*/
}
