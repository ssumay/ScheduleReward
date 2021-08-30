package sugan.org.schedulereward;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class Schedule_content_layout {
    View root;
    TextView seqT;
    EditText link_note;
    Spinner reward_type;
    Switch pic_select;
    EditText formula ;
    Button if_b;
    Boolean_wrapper add_if = new Boolean_wrapper();
    String if_edit_s1;
    String if_edit_s1_imsi;  //ifclicked()에서 사용하기 위해
    //AlertDialog linkDia = null;  //link 연결일 때 사용

    Context context;

    Spinner simple_rew_type;
    LinearLayout coupon_layout;
    LinearLayout cash_layout;
    EditText cash;

    TextView regi_coupon;
    TextView add_coupon_list;
    Spinner coupon_spi ;
    LinearLayout coupon_spi_layout;
    ListView coupon_list;
    ArrayList<Coupon_data> coupon_datas;
    ScheduleCouponListAdapter adapter;
    //boolean coupon_setted = false;

    //edt - execute_during_time.xml   // reward_type 3
    EditText start_h;
    EditText start_m;
    EditText till_h;
    EditText till_m;
    EditText edt_cash;
    boolean link_modify = false;
    AlertDialog linkDia;

    Schedule_content_layout(View root, Sched_data sd, int seq, ScheduleActivity sa, boolean link_modify){
//        Log.i("Schedule_content_layout", sd._id + " ");
        Log.i("sched_api", "aaa");
        context = sa;
        this.link_modify = link_modify;
        this.root = root;
        seqT = root.findViewById(R.id.seq);
        seqT.setText(seq+"");
        link_note = root.findViewById(R.id.link_note);

        pic_select = root.findViewById(R.id.pic_select);
        formula = root.findViewById(R.id.formula);
        if_b = root.findViewById(R.id.if_b);

        simple_rew_type = root.findViewById(R.id.simple_rew_type);
        coupon_layout = root.findViewById(R.id.coupon_layout);
        cash_layout = root.findViewById(R.id.cash_layout);
        cash = (EditText)root.findViewById(R.id.cash);
        Log.i("sched_api", "bbb");
        initiate_coupon_variables();
        initiate_edt_variables();   //case 3를 위해

        //coupon_setted = true;
        //}
        Coupon.setCoupons(context, coupon_spi, coupon_spi_layout, null);
        //simple_rew_type.setSelection(0);
        simple_rew_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                switch (i){
                    case 0:cash_layout.setVisibility(View.VISIBLE);
                        coupon_layout.setVisibility(View.GONE);
                        Log.i("simple_rew_type", "0");
                       // if(coupon_setted)
                            adapter.clear();
                        break;

                    case 1:Log.i("simple_rew_type", "1 selected");
                        cash_layout.setVisibility(View.GONE);
                         cash.setText("");
                         coupon_layout.setVisibility(View.VISIBLE);
                         //if(!coupon_setted){
                            // initiate_coupon_variables();
                             //coupon_setted = true;
                         //}
                       // Coupon.setCoupons(context, coupon_spi, coupon_spi_layout, null);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reward_type = root.findViewById(R.id.reward_type);
        //reward_type.setSelection(2);
        Log.i("sched_api", "ccc");

        reward_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("onItemSelected - seq", seq + " " + sd.lds.size());
                switch (i){
                    case 0: root.findViewById(R.id.simple_rew_layout).setVisibility(View.VISIBLE);  //case 0:
                        root.findViewById(R.id.formula_layout).setVisibility(View.GONE);
                        root.findViewById(R.id.execute_during_time).setVisibility(View.GONE);
                        Log.i("sched_api", "ffff" );
                        sd.lds.get(seq).reward_type = 0;
                       // simple_rew_type.setSelection(0);
                        break;
                    case 1: root.findViewById(R.id.simple_rew_layout).setVisibility(View.GONE);    //case 1:
                        root.findViewById(R.id.formula_layout).setVisibility(View.VISIBLE);
                        root.findViewById(R.id.execute_during_time).setVisibility(View.GONE);
                        sd.lds.get(seq).reward_type = 1;
                        Log.i("sched_api", "eeee");
                        break;
                    case 2: root.findViewById(R.id.simple_rew_layout).setVisibility(View.GONE);
                        root.findViewById(R.id.formula_layout).setVisibility(View.GONE);
                        root.findViewById(R.id.execute_during_time).setVisibility(View.GONE);
                        sd.lds.get(seq).reward_type = 2;
                        Log.i("sched_api", "ddd");

                        break;
                    case 3: root.findViewById(R.id.simple_rew_layout).setVisibility(View.GONE);
                        root.findViewById(R.id.formula_layout).setVisibility(View.GONE);
                        root.findViewById(R.id.execute_during_time).setVisibility(View.VISIBLE);

                        sd.lds.get(seq).reward_type = 3;
                        break;

                } Log.i("reward_type",reward_type.getSelectedItemId() + " " + sd.lds.get(seq).reward_type );
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });        reward_type.setSelection(2);


        if(seq !=0) {
            linkDia = new AlertDialog.Builder(context, R.style.SchedLinkDialog).setCancelable(false)
                    .setView(root).create();

            linkDia.getWindow().setBackgroundDrawableResource(R.drawable.add_link_dialog);

            if(link_modify){
                linkDia.setTitle(R.string.modify_link);
                linkDia.show();
            }
            else {
                linkDia.setTitle(R.string.input_link);
                linkDia.show();
            }


            TextView save_link =  root.findViewById(R.id.save_a);
            TextView cancel  = root.findViewById(R.id.cancel_a);

            link_note.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Util.editTextSaveListener(s, save_link, link_note);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            save_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sa.onSaveLink())
                    linkDia.dismiss();


                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!link_modify) {
                        int seq = Integer.parseInt(seqT.getText().toString());
                        sd.lds.remove(seq);
                    }

                    linkDia.dismiss();

                    sa.onCloseLink(v);

                }
            });

        }
        //if(sd._id !=-1) {  //modify page인 경우
        //    Log.i("modify", "page");
        //}
    }

    void initiate_coupon_variables(){

        regi_coupon = root.findViewById(R.id.regi_coupon);   //새쿠폰 등록 버튼
        add_coupon_list = root.findViewById(R.id.add_coupon_list);   //추가 버튼
        coupon_spi = root.findViewById(R.id.coupon_spi);
        coupon_spi_layout = root.findViewById(R.id.coupon_spi_layout);
        coupon_list = root.findViewById(R.id.coupon_list);  //리스트뷰
        coupon_datas = new ArrayList<>();
        adapter = new ScheduleCouponListAdapter(context, coupon_datas );Log.i("adapter", "created");
        coupon_list.setAdapter(adapter);


        regi_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout new_coupon_l = (LinearLayout) View.inflate(context, R.layout.new_coupon_dialog, null);
                EditText new_coupon = new_coupon_l.findViewById(R.id.new_coupon);
                TextView save_a = new_coupon_l.findViewById(R.id.save_a);
                TextView cancel_a = new_coupon_l.findViewById(R.id.cancel_a);

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

                AlertDialog add_coupon =
                        new AlertDialog.Builder(context)   //schedule db에 state = 0로 저장, 즉 사용자// 에게 보이지 않는 스케줄이 됨
                                .setTitle(R.string.add_coupon).setMessage(R.string.ask_new_coupon)
                                .setView(new_coupon_l)
                                //.setIcon(R.drawable.androboy)
                                .show();

                save_a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {Log.i("save", "onClick");
                        Coupon.saveNewCoupon(context, new_coupon.getText().toString().trim());
                        Coupon.setCoupons(context, coupon_spi, coupon_spi_layout, null);
                        add_coupon.dismiss();
                    }
                });

                cancel_a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add_coupon.dismiss();
                    }
                });

            }
        });

        add_coupon_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = (String)coupon_spi.getSelectedItem();
                for(int i=0; i<coupon_datas.size(); i++) {
                    if(coupon_datas.get(i).name.equals(name)) return;

                }
                adapter.addItem( new Coupon_data(name));

            }
        });

    }

    void initiate_edt_variables(){
        start_h = root.findViewById(R.id.start_h);
        start_m = root.findViewById(R.id.start_m);
        till_h = root.findViewById(R.id.till_h);
        till_m = root.findViewById(R.id.till_m);
        edt_cash = root.findViewById(R.id.edt_cash);

    }

}
