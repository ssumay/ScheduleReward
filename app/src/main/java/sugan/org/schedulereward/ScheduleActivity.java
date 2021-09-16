package sugan.org.schedulereward;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-10.
 * upgraded by eunsoo Aug 2021.
 */

public class ScheduleActivity extends AppCompatActivity {
    Spinner man_spi;
    Spinner sched_spi;

    LinearLayout linkedl;
    ListView man_list;
    EditText man_search;
    TextView man_regb;
    ImageView img;
    Man man;
    int selected_i;                 //선택된 사람
    ArrayList<Man_data> selected_mans;
    ArrayList<CheckBox> search_cbs;

    AlertDialog search_dialog;
    String s_id;

    Week_select_state week_select_state;
    LinearLayout day_time;

    int dt_datas_length = 0;
    ArrayList<Coupon_data> rews;

    int sel_seq = 0;
    ArrayList<RadioButton> rbs;
    Sched_data sd;
    Linked_sched_data main = null;   //sd.lds.get(0)

    TextView save;
    int page_mode = 1;   //0- newsced    1-modifySc
    LinearLayout man_lay;
    TextView new_man;
    AlertDialog dialog;   //언제 사용되는거지?
    String from;
    TextView new_rew;

    TextView addlink;
    TextView dellink;
    boolean existLink = false;   //연결된 링크가 있는지 여부. if there is connected link on schedule.

    Schedule_content_layout cl;
    Schedule_content_layout ldl = null;   //link dialog layout
    int atom_length;

    ArrayList<Sched_data> scheds;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        findViewById(R.id.dialog_save_gone_linear).setVisibility(View.GONE);

        allocate_variables();

        Intent intent = getIntent();
        s_id = intent.getStringExtra("s_id");
        String i_page = intent.getStringExtra("page");
        Log.i("i_page ", i_page + " " + s_id);

        if (i_page != null && i_page.equals("newSc")) {
            Log.i("i_page!=null ", "&& i_page.equals('newSc')");
            //day_time_datas = new Day_time_data[7];
            //dt_dates_imsi = new Day_time_data[7];
            selected_mans = new ArrayList<Man_data>();

            findViewById(R.id.del).setVisibility(View.GONE);
            // final TextView addlink = findViewById(R.id.addlink);
            page_mode = 0;
            sd = new Sched_data(true);
            main = sd.lds.get(0);

            //sd.reward = -1;   //삭제
            LinearLayout sc_lay = findViewById(R.id.sc_lay);  //schedule title layout
            sc_lay.setVisibility(View.VISIBLE);
            save.setText(R.string.save);
            save.setVisibility(View.GONE);
            final EditText sched_title = findViewById(R.id.sched_title);
           // showScView();  //검토후 삭제


            //link_view.setVisibility(View.GONE);

            sched_title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    Util.editTextSaveListener(s, save, sched_title);
                    sd.title = sched_title.getText().toString().trim();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            clSet(true, 0);

        } else {
            sched_spi.setVisibility(View.VISIBLE);
            //sched = new Schedule(this);
            listScheds();


        }



        //day_time_datas = new Day_time_data[7];
        //dt_dates_imsi = new Day_time_data[7];
        //setRewSpi();
        //schedule_week = new Schedule_week();
        week_select_state = new Week_select_state(this);

        initiate();

    }

    void clSet(boolean isMain, int seq){
        if(isMain){
            cl = new Schedule_content_layout(getWindow().getDecorView().getRootView(), sd, 0, this, false);
        }
        else ldl = new Schedule_content_layout((LinearLayout) View.inflate(this, R.layout.add_sched_content, null), sd, seq, this, true);
        final Schedule_content_layout layout = isMain? cl : ldl ;
        Linked_sched_data lsd = sd.lds.get(isMain?0: seq);



        // if(isMain) layout =  cl ;
       // else layout = ldl;

        //main = sd.lds.get(0);

        layout.link_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(Util.checkFrontBlank(s)){
                    layout.link_note.setText(s.toString().substring(1));

                    return;}

                if(s.toString().equals("")) {
                    addlink.setVisibility(View.GONE);
                    dellink.setVisibility(View.GONE);
                    linkedl.setVisibility(View.GONE);
                    existLink = false;
                }
                else {
                    addlink.setVisibility(View.VISIBLE);
                    if(sd.lds.size()>1) {
                        dellink.setVisibility(View.VISIBLE);
                    }
                    //dellink.setVisibility(View.VISIBLE);
                    linkedl.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if(sd._id != -1|| !isMain) {  //modify page일 떄
            //Linked_sched_data data = sd.lds.get(0);
            layout.link_note.setText(lsd.link_note);
            layout.pic_select.setChecked(lsd.pic == 1 ? true : false);

           // cl.reward_type.setSelection(0);
            Log.i("lsd.reward_type", lsd.reward_type + " ");

            layout.reward_type.setSelection(lsd.reward_type);
             Log.i("lsd.reward_type1", lsd.reward_type + " ");

            if (lsd.reward_type == 1) {   //if_formula rew   1|12 - (a-3)*0.8   or 2|a|>|3|a*0.2|a*0.2+0.1
                Log.i("reward_type", lsd.formula + " : " + (lsd.if_formula == null));

                arrangeLinkDialog();

               // cl.reward_type.setSelection(1);
                if (lsd.if_formula == null) {
                    layout.formula.setText(lsd.formula);
                } else {

                    ((TextView) findViewById(R.id.if_b)).setText(R.string.cancle_if);
                    findViewById(R.id.formula).setVisibility(View.GONE);
                    findViewById(R.id.if_layout).setVisibility(View.VISIBLE);

                    ((EditText) layout.root.findViewById(R.id.if_edit_a)).setText(lsd.if_formula.if_edit_a);
                    ((EditText) layout.root.findViewById(R.id.if_edit_c)).setText(lsd.if_formula.if_edit_c);
                    int j = 0;
                    switch (lsd.if_formula.if_edit_s) {
                        case ">=":
                            j = 1;
                            break;
                        case "<":
                            j = 2;
                            break;
                        case "<=":
                            j = 3;
                            break;
                        case "==":
                            j = 4;
                            break;
                        case "!=":
                            j = 5;


                    }
                    ((Spinner) layout.root.findViewById(R.id.if_edit_s)).setSelection(j);
                    ((EditText) layout.root.findViewById(R.id.if_command)).setText(lsd.if_formula.if_command);
                    ((EditText) layout.root.findViewById(R.id.else_command)).setText(lsd.if_formula.else_command);
                }
            }
            else if(lsd.reward_type == 0) {//simple rew  (3|5.5.. cash, 4|name|ea.. coupon, )
                // cl.reward_type.setSelection(0);

                Log.i("clset", lsd.reward_type + " " + lsd.formula);
                if (lsd.formula.startsWith("3"))
                    ((EditText) layout.root.findViewById(R.id.cash)).setText(lsd.formula.substring(2));
                    //main.formula.substring(2));

                else {
                    layout.simple_rew_type.setSelection(1);
                    String[] cps = lsd.formula.substring(2).split("\\|");
                    for (int i = 0; i < cps.length; i = i + 2) {
                        String ea = cps[i + 1];
                        layout.adapter.addItem(new Coupon_data(cps[i], ea));

                        Log.i("coupons", cps[i]);
                    }
                }
            }
                else if(lsd.reward_type == 3){
                layout.reward_type.setSelection(3);
                    String[] t = lsd.formula.substring(2).split("\\|");
                layout.start_h.setText(t[0]);
                layout.start_m.setText(t[1]);
                layout.till_h.setText(t[2]);
                layout.till_m.setText(t[3]);
                layout.edt_cash.setText(t[4]);

                }

        }


    }

    void schedSet(int i){
        sd = Schedule.getSchedData( scheds.get(i)._id, ScheduleActivity.this);
        sd.title = scheds.get(i).title;
        sd._id = scheds.get(i)._id;
        main = sd.lds.get(0);

        //Intent intent = getIntent();
        //if ( intent.getStringExtra("s_id") != null) {
        clSet(true, 0);

        // cl = new Schedule_content_layout(getWindow().getDecorView().getRootView(), sd, 0, ScheduleActivity.this);//}

        Log.i("sch_spi sd.lds.size", sd.lds.size() + " ");

        //     one_day_rew = false;
                /*
                LinearLayout linear = (LinearLayout) View.inflate(RegisterManActivity.this, R.layout.sched_item_view, null);
                title = (TextView) linear.findViewById(R.id.title);
                s_idt = (TextView) linear.findViewById(R.id.s_id);
                srb = ((RadioButton) linear.findViewById(R.id.radiob));
                //srb.setChecked(true);
                srb.setOnClickListener(bListener);
                rbs.clear();
                rbs.add(srb);
                if(sd.reward!=-1)  {
                    one_day_rew = true;
                    for(int j=0; j<rews.size();j++) {
                        if (rews.get(j)._id == sd.reward) {
                            rew_spi.setSelection(j + 1);
                            break;
                        }
                    }
                }
                schedl.addView(linear);*/
        // showScView();
        /*
        showLinkView();
        showWeekView();


        selected_i1 = i;
        ScheduleActivity.this.s_id = sd._id+"";
        selected_mans = man.fillSelected_mans( sd._id+"",
                this);
                //ScheduleActivity.this);
        SelectedMansAdapter adapter = new SelectedMansAdapter(ScheduleActivity.this, selected_mans);
        man_list.setAdapter(adapter);*/
    }
    private void listScheds() {
        scheds = new ArrayList<Sched_data>(5);
        Schedule.fillSpinner(sched_spi, scheds, this);

        Log.i("s_id", s_id + " ");
        sched_spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                schedSet(i);

                showLinkView();
                showWeekView();

                ScheduleActivity.this.s_id = sd._id+"";
                selected_mans = man.fillSelected_mans( sd._id+"",
                        //this);
                ScheduleActivity.this);
                SelectedMansAdapter adapter = new SelectedMansAdapter(ScheduleActivity.this, selected_mans);
                man_list.setAdapter(adapter);
                Log.i("sched_spi", i + " ");
                //  day_time_datas = new Day_time_data[7];
                //  dt_dates_imsi = new Day_time_data[7];

                //schedl.removeAllViews();
                //linkedl.removeAllViews();
                //dtsl.removeAllViews();
                Log.i("scheds.geti._id",scheds.get(i)._id + " " );

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (s_id != null) {
            int i_sid = Integer.parseInt(s_id);
            int j=-1;
            Log.i("scheds.size", scheds.size() + " ");
            for (int i = 0; i < scheds.size(); i++) {
                if (scheds.get(i)._id == i_sid) {
                    sched_spi.setSelection(i);
                    j = i; Log.i("abcj = ", j + " " + i_sid);
                    break;
                }
            }
            schedSet(j);

            //   Log.i("listsched", sd.lds.size() + " ");

        }

    }

    public void setRewSpi() {

       // rews = Coupon.fillReward(this, rew_spi, null, 1);
        if (rews.size() == 0) {
            // rew.setVisibility(View.GONE);
            new_rew.setVisibility(View.VISIBLE);
        } else {
            // rew.setVisibility(View.VISIBLE);
            new_rew.setVisibility(View.GONE);
        }
/*
        rew_spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (one_day_rew = (i > 0) ? true : false) num_lay.setVisibility(View.VISIBLE);
                else num_lay.setVisibility(View.GONE);

                rew_id = (i >= 1) ? rews.get(i - 1)._id : -1;
                Log.i("rew_id", rew_id + " ");
                //sd.reward = rew_id;
                sd.rew_desc = (i >= 1) ? rews.get(i - 1).name : "";

                showLinkView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }

    /*
    public void onAddCoupon(View v){
        ArrayList<Coupon_data> coupon_datas = new ArrayList<>();
        ScheduleCouponListAdapter adapter = new ScheduleCouponListAdapter(this, coupon_datas );
        coupon_list.setAdapter(adapter);
    }
*/

    public void onModLink(View v) {
        LinearLayout linear = (LinearLayout) (((LinearLayout) v.getParent()).getParent());
        TextView t = (TextView) (linear.findViewById(R.id.seq));
        Log.i("link_seq", t.getText().toString() + " ");
        sel_seq = Integer.parseInt(t.getText().toString());     Log.i("sel_seq", sel_seq + " ");

        Linked_sched_data ld = sd.lds.get(sel_seq);
        ld.link_note = ((EditText) linear.findViewById(R.id.link_note)).getText().toString();
       /* ld.score = Integer.parseInt(((EditText)linear.findViewById(R.id.score)).getText().toString());
        if(((Switch)linear.findViewById(R.id.no_select)).isChecked())
            ld.no = 1;
        else ld.no = 0;*/
        if (((Switch) linear.findViewById(R.id.pic_select)).isChecked())
            ld.pic = 1;
        else ld.pic = 0;

        onCloseLink(v);
        showLinkView();

    }

    public void onDelLink(View v) {
        AlertDialog state_0 = new AlertDialog.Builder(this)   //schedule db에 state = 0로 저장, 즉 사용자에게 보이지 않는 스케줄이 됨
                .setTitle(R.string.del_link_title).setMessage(R.string.del_link)
                //.setIcon(R.drawable.androboy)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int b) {
                        sd.lds.remove(sel_seq);
                        for (int i = sel_seq; i < sd.lds.size(); i++) {
                            sd.lds.get(i).seq--;
                        }
                        //onLinkClose(v);
                        showLinkView();
                    }
                }).setNegativeButton(R.string.cancel, null)
                .show();

    }

    public void onNewMan(View v) {
        Log.i("onNewMan", "   ");
        Intent intent = new Intent(this, A_MListActivity.class);
        intent.putExtra("from", "schedA");
        from = "new_man";
        startActivity(intent);
    }

    public void onSaveMan(View v) {

        dialog.dismiss();
        setMans();

    }

    public void onDialogCancel(View v) {
        dialog.dismiss();
    }

    public void onCloseLink(View v) {
        cl.if_edit_s1 = cl.if_edit_s1_imsi;
        Log.i("main - if_edit_s1", cl.if_edit_s1 + " ");

        showLinkView();

        ldl = null;
        //Schedule.onDialogCancelClicked(0);

    }

    public void onDialogCancelClicked1(View v) {
        Schedule.onDialogCancelClicked(1);
        // if(!dt_input_state) day_time_datas = new Day_time_data[7];

    }

    public void onDialogCancelClicked2(View v) {
        Schedule.onDialogCancelClicked(2);
    }

    boolean saveContent_data(Linked_sched_data data, int seq, Schedule_content_layout layout) {
        data.link_note = layout.link_note.getText().toString().trim();
        // link_data.no = no_select.isChecked()?1:0 ;
        data.pic = layout.pic_select.isChecked() ? 1 : 0;

        if (data.reward_type ==1) {   //if_formula rew
            data.formula = layout.formula.getText().toString().trim();
            String if_edit_a = ((EditText) layout.root.findViewById(R.id.if_edit_a)).getText().toString().trim();
            //Spinner if_edit_s = (Spinner)linkDiaLayout.findViewById(R.id.if_edit_s);
            String if_edit_s1 = (String) ((Spinner) layout.root.findViewById(R.id.if_edit_s)).getSelectedItem();
            Log.i("if_edit_s1", if_edit_s1 + " ");

            String if_edit_c = ((EditText) layout.root.findViewById(R.id.if_edit_c)).getText().toString().trim();


            String if_command = ((EditText) layout.root.findViewById(R.id.if_command)).getText().toString().trim();
            String else_command = ((EditText) layout.root.findViewById(R.id.else_command)).getText().toString().trim();

            if (seq != 0) {
                if (data.link_Atoms.size()==0){
                    Toast.makeText(this, R.string.input_atom, Toast.LENGTH_LONG).show();
                    return false;
                }
                if((!layout.add_if.value && data.formula.equals("")) ||
                                layout.add_if.value && (if_command.equals("") || if_edit_a.equals("") || if_edit_c.equals("") || else_command.equals(""))
                ) {
                    Log.i("formula.equals", " ");
                    Toast.makeText(this, R.string.input_formula, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            if (layout.add_if.value) {
                If_formula if_formula = new If_formula(
                        if_edit_a, if_edit_s1, if_edit_c, if_command, else_command);
                data.if_formula = if_formula;
                data.formula = "";
            }

        }
        else if(data.reward_type ==0){  //simple rew
                if(layout.simple_rew_type.getSelectedItemPosition()==0) {   //cash
                    data.cash = layout.cash.getText().toString();  Log.i("cash = ", data.cash + " ");
                    if(seq != 0 && data.cash.equals("")) {
                        Toast.makeText(this, R.string.input_won, Toast.LENGTH_LONG).show();
                        return false;
                    }
                    data.formula = "3|" + data.cash;
                }
                else {  //coupon
                    if(seq != 0 && layout.coupon_datas.size()==0) {
                        Toast.makeText(this, R.string.input_coupon, Toast.LENGTH_LONG).show();
                        return false;
                    }
                    data.coupon_datas = layout.coupon_datas;
                    data.formula = Schedule.getCpFormula(data.coupon_datas);

                    data.cash = "";
                }
            }
        else if(data.reward_type ==3) { //edt execute_during_time rew
            if(seq!=0){
                if( layout.start_h.getText().toString().trim().equals("")||
                        layout.start_m.getText().toString().trim().equals("")||
                        layout.till_h.getText().toString().trim().equals("")||
                        layout.till_m.getText().toString().trim().equals("")||
                        layout.edt_cash.getText().toString().trim().equals("")) {
                    Toast.makeText(this, R.string.input_edt, Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            data.formula = "5|" + layout.start_h.getText().toString().trim() + "|"
                    + layout.start_m.getText().toString().trim() + "|"
                    + layout.till_h.getText().toString().trim() + "|"
                    + layout.till_m.getText().toString().trim() + "|"
                    + layout.edt_cash.getText().toString().trim();
        }

        return true;
    }

    boolean onSaveLink() {

        cl.if_edit_s1 = cl.if_edit_s1_imsi;
       // int seq = sd.lds.size() - 1;    Log.i("saveLinkClicked seq =", seq  + " ");
        int seq = Integer.parseInt(ldl.seqT.getText().toString());
        Linked_sched_data link_data = sd.lds.get(seq);

        if (saveContent_data(link_data, seq, ldl)) {

           // link_datas_add(link);

            //link_datas.add (sched_data);                         L.NewSchedActivity_onSaveLinkClicked("link_datas.add");
            showLinkView();
            ldl = null;
            return true;
            //showschedLinear.addView(dl);     L.NewSchedActivity_onSaveLinkClicked("showschedLinear.addView");

        }
        return false;
        //if(link_datas.size()>0) save.setVisibility(View.VISIBLE);
    }
/*
    public void link_datas_add(Linked_sched_data data) { //Log.i("link_datas_add", rb_seq+" ");

        sd.lds.add(rb_seq, data);
        for (int i = rb_seq + 1; i < sd.lds.size(); i++) {
            int bseq = sd.lds.get(i).seq;      //Log.i("link_data.get", bseq+" ");
            sd.lds.get(i).seq = bseq + 1;

        }
    }
    */
/*
    public Linked_sched_data getSelectedLD() {
        for (int i = 0; i < sd.lds.size(); i++) {
            Log.i("i", sd.lds.get(i).seq + " ");
            if (sd.lds.get(i).seq == sel_seq) {
                return sd.lds.get(i);
            }
        }
        return null;
    }
*/
    public void onLinkClicked(View v) {
        TextView t = (TextView) ((TableLayout) ((TableRow) v.getParent()).getParent()).findViewById(R.id.seq);
        Log.i("link_seq", t.getText().toString() + " ");
        sel_seq = Integer.parseInt(t.getText().toString());     Log.i("sel_seq", sel_seq + " ");

        clSet(false, sel_seq);

       // Schedule.onLinkClicked( sd.lds.get(sel_seq), this); 삭제
    }

    public void onAddLinkClicked(View v) {

        // currentlink_Atoms = new ArrayList<>();
        Linked_sched_data link = new Linked_sched_data();
        if(sel_seq!= 0 && sel_seq < sd.lds.size()-1){  //중간에 삽입하는 경우
            Log.i("onaddlinkclick sel_seq", sel_seq + " ");
            sd.lds.add(sel_seq+1, link);
            link.seq = sel_seq+1;

            for(int i = sel_seq+2; i<sd.lds.size(); i++) sd.lds.get(i).seq++;
        }
        else {
            Log.i("onaddlinkclicked", "else");
            sd.lds.add(link);
            int seq = sd.lds.size() - 1;
            link.seq = seq;
        }

        ldl = new Schedule_content_layout((LinearLayout) View.inflate(this, R.layout.add_sched_content, null),
                                                                        sd, link.seq, this, false);
        // Schedule.onAddLinkClicked();   //삭제
    }

    public void onAdScAtom(View v) {
        Log.i(" onAdScAtom", " clicked");
        LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.add_sched_atom, null);
        //LinearLayout values_layout = (LinearLayout)linear.findViewById(R.id.value_layout);

        Spinner box_length = linear.findViewById(R.id.box_length);

        EditText box_unit = linear.findViewById(R.id.box_unit);
        EditText box_default = linear.findViewById(R.id.box_default);

        EditText layout_default = linear.findViewById(R.id.layout_default);
        TextView layout_unit = linear.findViewById(R.id.layout_unit);

        AlertDialog atomDia =
                new AlertDialog.Builder(this)
                        .setTitle(R.string.skd_atom)
                        //.setIcon(R.drawable.androboy)
                        .setView(linear)
                        //.setCancelable(false)
                        .show();


        TextView save = linear.findViewById(R.id.save_b);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkAtom atom = new LinkAtom();
                String dv = box_default.getText().toString();
                if (dv.equals("")) atom.default_v = 0;
                else atom.default_v = Float.parseFloat(box_default.getText().toString());

                if (box_unit.getText().toString().equals("")) box_unit.setText(R.string.unit);
                atom.unit = box_unit.getText().toString();
                atom.length = atom_length;
                if (ldl != null) {   //연결스케줄 추가 dialog에서 열었을 경우
                    sd.lds.get(sd.lds.size() - 1).link_Atoms.add(atom);

                } else {  //main schedule에서 열었을 경우
                    Linked_sched_data ld = main;
                    if (ld.link_Atoms == null) ld.link_Atoms = new ArrayList<>();
                    ld.link_Atoms.add(atom);
                }
                arrangeLinkDialog();
                atomDia.dismiss();
                Log.i("save", atom.unit + " ");

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.box_length, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        box_length.setAdapter(adapter);
        box_length.setSelection(0);
        box_length.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s_length = (String) box_length.getSelectedItem();
                String sel_length = s_length.substring(0, s_length.length() - 2);
                atom_length = Integer.parseInt(sel_length);
                Util.setViewWidthAndMarginright(layout_default, ScheduleActivity.this, Float.parseFloat(sel_length));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        box_default.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    layout_default.setText(s.toString().trim());
                else layout_default.setText("0.0");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        box_unit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    layout_unit.setText(s.toString().trim());
                else layout_unit.setText(R.string.unit);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void arrangeLinkDialog() {
        if (ldl != null) {//즉 연결 스케줄일 때
            LinearLayout vl = (LinearLayout) ldl.root.findViewById(R.id.values_layout);
            vl.removeAllViews();
            if (sd.lds.get(sd.lds.size() - 1).link_Atoms.size() > 0) {
                //(ldl.root.findViewById(R.id.formula_l)).setVisibility(View.VISIBLE);
                Schedule.arrangeValues_layout(this, vl, sd.lds.get(sd.lds.size() - 1).link_Atoms, ldl.root.findViewById(R.id.formula_l)
                ,true, true, 25, null);

            }
        } else {   // main schedule일 때
            LinearLayout vl = findViewById(R.id.values_layout);
            vl.removeAllViews();
            Log.i("arrangelinkdialog", main.link_Atoms.size() + " ");
            if (main.link_Atoms.size() > 0) {
               // (findViewById(R.id.formula_l)).setVisibility(View.VISIBLE);
                Schedule.arrangeValues_layout(this, vl, main.link_Atoms, findViewById(R.id.formula_l), true, true, 25, null);
            }

        }

    }

    LinearLayout makeLinkItemView(Context context, Linked_sched_data sched_data) {

        LinearLayout linear = (LinearLayout) View.inflate(context, R.layout.link_item_view, null);
        TextView note = linear.findViewById(R.id.note);

        TextView pic_exist = linear.findViewById(R.id.pic_exist);
        L.schedule_makeView(" ");
        TextView seq = linear.findViewById(R.id.seq);
        L.schedule_makeView("sched_data.seq= " + sched_data.seq);
        TextView seq1 = linear.findViewById(R.id.seq1);
        seq.setText(Integer.toString(sched_data.seq));
        seq1.setText(Integer.toString(sched_data.seq));
        LinearLayout l = linear.findViewById(R.id.verticalLinear);
        TextView formula = linear.findViewById(R.id.formula);
        if(sched_data.reward_type==1) {
            l.setVisibility(View.VISIBLE);
            formula.setVisibility(View.VISIBLE);

            formula.setText(sched_data.formula);
            if (sched_data.if_formula != null) {
                formula.setText("if " + sched_data.if_formula.if_edit_a + sched_data.if_formula.if_edit_s +
                        sched_data.if_formula.if_edit_c
                        + "\n  " +
                        sched_data.if_formula.if_command + "\n" +
                        "else  " + sched_data.if_formula.else_command);
            }
            if (sched_data.link_Atoms != null)
                Schedule.arrangeValues_layout(this, l, sched_data.link_Atoms, null, false, 10);
        }
        else if(sched_data.reward_type==0) {  //simple rew
            TextView imsi = new TextView(context);
            imsi.setText(R.string.won);
            String won = imsi.getText().toString();
            l.setVisibility(View.GONE);
            Log.i("cash", sched_data.cash+" ");
            if(!sched_data.cash.equals("")) {
                formula.setVisibility(View.VISIBLE);
                formula.setText(sched_data.cash + " " + won);
            }
            else {
                formula.setVisibility(View.GONE);
                LinearLayout cl = linear.findViewById(R.id.verticalLinear);
                cl.setVisibility(View.VISIBLE);
                int size = sched_data.coupon_datas.size();
                Log.i("coupon -", size + " ");
                for (int i = 0; i < size; i++) {
                    TextView t = new TextView(context);
                    t.setText(sched_data.coupon_datas.get(i).name + " coupons " + sched_data.coupon_datas.get(i).ea);
                    cl.addView(t);

                }
            }
        }
        else if(sched_data.reward_type==3) { // edt execute_during_time
            formula = linear.findViewById(R.id.formula);
            formula.setVisibility(View.VISIBLE);
            String[] edt = sched_data.formula.substring(2).split("\\|");
            TextView imsi = new TextView(context);
            imsi.setText(R.string.won);
            String won = imsi.getText().toString();

            String e = edt[0] + "h " + edt[1] + "m ~ " + edt[2] + "h " + edt[3] + "m \n" + edt[4] + won;
            formula.setText(e);

        }

        //LinearLayout rect = linear.findViewById(R.id.rect);                         L.schedule_makeView(" ");
        note.setText(sched_data.link_note);
       /* if(sched_data.no==1) {
            no_exist.setText(R.string.no_exist);
            no_exist.setVisibility(View.VISIBLE);
        }   */
        //L.schedule_makeView(" ");
        if (sched_data.pic == 1) {
            pic_exist.setText(R.string.pic_exist);
            pic_exist.setVisibility(View.VISIBLE);
        }
        L.schedule_makeView(" ");

        //PaintDrawable rectp = new PaintDrawable(Color.YELLOW);                         L.schedule_makeView(" ");
        //rectp.setCornerRadius(10.0f);
        //rect.setBackgroundDrawable(rectp);
        return linear;

    }

    public void searchByName() {
        man.searchByName(man_search.getText().toString(), this);
        int n = man.search_mans.size();
        Log.i("man.search_mans.size", n + " ");
        if (n == 0)
            Toast.makeText(this, R.string.no_man, Toast.LENGTH_SHORT).show();

        if (n == 1) {
            searchMan(man.search_mans.get(0).name);

        }
        if (n > 1) {
            search_cbs = new ArrayList<CheckBox>();
            LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.search_man_dialog, null);
            ListView search_list = (ListView) linear.findViewById(R.id.search_list);
            search_dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.select_mans)
                    //.setIcon(R.drawable.androboy)
                    .setView(linear)
                    .setCancelable(false)
                    .show();
            SearchAdapter adapter = new SearchAdapter(this, man.search_mans);
            search_list.setAdapter(adapter);

        }


    }

    public void searchMan(String _id) {
        for (int j = 0; j < man.mans.size(); j++) {
            if (_id.equals(man.mans.get(j).name)) {
                onRegister(j);
                return;
            }
        }

    }

    public void onSelectManClicked(View v) {
        search_dialog.dismiss();
        Log.i("cbs.size", search_cbs.size() + " ");
        for (int i = 0; i < search_cbs.size(); i++) {
            CheckBox c = search_cbs.get(i);
            if (c.isChecked()) {
                Log.i("c.ischecked", true + " ");
                String _id = ((TextView) ((LinearLayout) c.getParent()).findViewById(R.id._id)).getText().toString();
                searchMan(_id);
            }
        }

    }

    public void onCancelClicked(View v) {
        search_dialog.dismiss();
    }

    public void onDaytimeDialogCancel(View v) {
        Schedule.onDialogCancelClicked(1);
        //if(!dt_input_state) day_time_datas = new Day_time_data[7];

    }

    public void onRegister(int j) {
        for (int i = 0; i < selected_mans.size(); i++) {
            if (selected_mans.get(i) == man.mans.get(j)) {
                Toast.makeText(this, R.string.reg_before, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        selected_mans.add(man.mans.get(j));
        SelectedMansAdapter adapter = new SelectedMansAdapter(this, selected_mans);
        man_list.setAdapter(adapter);
    }

    public void onManRegist(View v) {
        onRegister(selected_i);
    }

    public void onDelMan(View v) {
        String is = ((TextView) (((LinearLayout) v).findViewById(R.id.i))).getText().toString();
        selected_mans.remove(Integer.parseInt(is));
        SelectedMansAdapter adapter = new SelectedMansAdapter(this, selected_mans);
        man_list.setAdapter(adapter);
    }

    public void onIfClicked(View v) {

        View rootview;
        Boolean_wrapper add_if;

        if (v == cl.if_b) {  //main content
            add_if = cl.add_if;
            Log.i("ifclicked", "main content - " + add_if.value + " ");
            rootview = cl.root;                       //getWindow().getDecorView().getRootView();
        } else {  //link
            add_if = ldl.add_if;
            cl.if_edit_s1_imsi = cl.if_edit_s1; //임시 저장 후 savelink하거나 취소(onlinkclose)할 떄 돌려받는다.
            cl.if_edit_s1 = Schedule.if_edit_s1;
            rootview = ldl.root;
        }

        if (add_if.value) {
            add_if.value = false;
            Log.i("add_if" + add_if.value + " ", add_if.value + " ");
            ((Button) rootview.findViewById(R.id.if_b)).setText(R.string.add_if);
            rootview.findViewById(R.id.formula).setVisibility(View.VISIBLE);
            rootview.findViewById(R.id.if_layout).setVisibility(View.GONE);
            ((EditText) rootview.findViewById(R.id.if_edit_a)).setText("");
            ((EditText) rootview.findViewById(R.id.if_edit_c)).setText("");
            ((EditText) rootview.findViewById(R.id.if_command)).setText("");
            ((EditText) rootview.findViewById(R.id.else_command)).setText("");


        } else {
            add_if.value = true;
            Log.i("else", cl.add_if.value + " ");
            ((Button) rootview.findViewById(R.id.if_b)).setText(R.string.cancle_if);
            rootview.findViewById(R.id.formula).setVisibility(View.GONE);
            rootview.findViewById(R.id.if_layout).setVisibility(View.VISIBLE);
            ((EditText) rootview.findViewById(R.id.formula)).setText("");

            Spinner if_edit_s = (Spinner) rootview.findViewById(R.id.if_edit_s);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.if_edit, android.R.layout.
                    simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if_edit_s.setAdapter(adapter);
            if_edit_s.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if_edit_s.setSelection(0);
            if_edit_s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cl.if_edit_s1 = (String) if_edit_s.getSelectedItem();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }

    boolean isSched_state_0() {  // 스케줄의 내용이 없거나(link_note), reward_type이 simple과 formula일 떄 reward 의 내용이 채워지지 않았을 떄 return true;
  //Log.i("main.formula", main.formula+ " "); Log.i("cl.simple_type", cl.simple_rew_type.getSelectedItemPosition() + " ");
        switch (main.reward_type) {  //0 - simple, 1 - formula, 2 - no reward  , 3 - edt, exeute_during_time
            case 0:
                if(cl.simple_rew_type.getSelectedItemPosition()==0) { //cash
                    if (((EditText) cl.root.findViewById(R.id.cash)).getText().toString().equals(""))
                        return true;
                }
                else  //coupon
                    if(cl.coupon_datas.size()==0) return true;
                break;

            case 1:
                if (main.link_Atoms == null) return true;
                if (main.formula.equals("") && main.if_formula ==null) return true;
                break;

            case 3:
                if(cl.start_h.getText().toString().equals("")||cl.start_m.getText().toString().equals("")||cl.till_h.getText().toString().equals("")||
                cl.till_m.getText().toString().equals("")||cl.edt_cash.getText().toString().equals("")) return true;
                break;
        }
        return false;
    }

    public void onSaveSc(View v) {
        Log.i("link_Note", cl.link_note.getText().toString() + " ");
        saveContent_data(main, 0, cl);
        if (cl.link_note.getText().toString().equals("") || main.reward_type != 2 && isSched_state_0()) {
            Log.i("state_0", true + " ");
            AlertDialog state_0 = new AlertDialog.Builder(this)   //schedule db에 state = 0로 저장, 즉 사용자에게 보이지 않는 스케줄이 됨
                    .setTitle(R.string.state_0_title).setMessage(R.string.state_0_content)
                    //.setIcon(R.drawable.androboy)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int b) {
                            saveSc();

                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
            // checkReward();

        } else {
            sd.state = 1;
            saveSc();
        }
    }

    void saveSc() {
        //saveContent_data(main, 0, cl);
        Schedule.saveSc(page_mode, sd, selected_mans, week_select_state, this);

        if (page_mode == 0)
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(this, R.string.modified, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, A_ScListActivity.class);
        startActivity(intent);
    }

    public void onDelSc(View v) {
        Schedule.delSched(this, s_id);
        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
        listScheds();
        //listMans();

    }

    public void onBack(View v) {
        finish();
        Intent intent = new Intent(this, A_ScListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setMans() {
        man.fillSpinner(this, man_spi, null);
        man_lay.setVisibility(man.mans.size() == 0 ? View.GONE : View.VISIBLE);
    }

    public void showLinkView() {
        /*
        if(sd.reward==-1) {   // general case.
            title.setText(sd.title);
            rew_spi.setSelection(0);
        }
        else {   //in case one-day-reward.
            //int sel_j=0;
            TextView imsi = new TextView(this);
            imsi.setText(sd.rew_desc);
            rew_s = imsi.getText().toString();*/
          //  title.setText(sd.title);
        //}

        if( sd.lds.size() > 1 ) {
            Util.setLinearWeightParam(3, getWindow().getDecorView().getRootView(), R.id.content_layout);
            dellink.setVisibility(View.VISIBLE);
        }
        else {
            Util.setLinearWeightParam(5, getWindow().getDecorView().getRootView(), R.id.content_layout);
            dellink.setVisibility(View.GONE);
        }

        linkedl.removeAllViews();  //Log.i("rbs.size", rbs.size()+" "+sd.lds.size());
        rbs.clear();// rbs.add(srb); srb.setChecked(false);
       // if(sd.lds.size()==0)  bListener.onClick(srb);
        for(int i=1; i<sd.lds.size();i++) {
            LinearLayout dl = makeLinkItemView(this, sd.lds.get(i));
            //LinearLayout dl = Schedule.makeView(link_datas.get(i), this, one_day_rew);
            linkedl.addView(dl);

            RadioButton rb = dl.findViewById(R.id.radiob); L.NewSchedActivity_onSaveLinkClicked("find-radiob");
            rb.setVisibility(View.VISIBLE);
            rb.setOnClickListener(bListener);
            rbs.add(rb);
            if(i==(sd.lds.size()-1))   bListener.onClick(rb);

        }
        Log.i("link_data.size()",sd.lds.size() + " " );
      //  if(sd.lds.size()>0&&title.getText().toString().length()>0) save.setVisibility(View.VISIBLE);
      //  else save.setVisibility(View.GONE);

    }

    void showWeekView(){
        LinearLayout week = findViewById(R.id.day_time);
        sd.week_select_state.setLayout(week);
       // Day_state[] ds = sd.week_select_state.week_state;
        week_select_state = sd.week_select_state;
        for(int i=0; i<7; i++){
            Day_state ds = week_select_state.week_state[i];Log.i(i+"ds.state = ", ds.state + " ");
            //ds.state++;   modify to change when time is added
            if(ds.state == 0) {
                ds.v_day.setBackground(getResources().getDrawable(R.drawable.picture));
                ds.v_time.setText("");
            }
            if(ds.state == 1 ) {
                ds.v_day.setBackground(getResources().getDrawable(R.drawable.day_click));
                ds.v_time.setText("");
                //   Schedule.onDayClicked(this, ds.v_day);
            }
            if(ds.state==2)
                ds.v_day.setBackground(getResources().getDrawable(R.drawable.day_click));
                ds.v_time.setText(ds.time);
        }

    }


    public void onDayClicked(View v) {

        switch (v.getId()){
            case R.id.mon:  manageClickDay(0);  break;
            case R.id.tue:  manageClickDay(1);  break;

            case R.id.wed:  manageClickDay(2);  break;
            case R.id.thu:  manageClickDay(3);  break;
            case R.id.fri:    manageClickDay(4);  break;
            case R.id.sat:    manageClickDay(5);  break;
            case R.id.sun:    manageClickDay(6);  break;

        }
        //Schedule.onDayClicked(this, (TextView)v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void manageClickDay(int day){
        ((TextView)findViewById(R.id.day)).setText(day+"");
        Day_state ds = week_select_state.week_state[day];
        //ds.state++;   modify to change when time is added
        if(ds.state == 0) {
            ds.v_day.setBackground(getResources().getDrawable(R.drawable.day_click));
            ds.state++;
            return;
        }
        if(ds.state == 1 || ds.state == 2) {
            ds.state = 0;
            ds.v_day.setBackground(getResources().getDrawable(R.drawable.picture));
            ds.time = null;
            ds.v_time.setText("");
         //   Schedule.onDayClicked(this, ds.v_day);

        }

    }

    public void ontimeClicked(View v) {
        ((TextView)v).setText("");
        for(int i=0; i<7; i++) {
            if(v == week_select_state.week_state[i].v_time){
                week_select_state.week_state[i].state = 1;
            }
        }

    }
/*
    public void onSaveDayTime(View v) {
        if(dt_datas_length==0){
            Toast.makeText(this, R.string.dt_again, Toast.LENGTH_LONG).show();
            return;}
        //if(dt_input_state) {
            /*for (int i = 0; i < 7; i++) {
                if (dt_dates_imsi[i] != null) { Log.i("dt_imsi[i] != null", i+" ");
                    day_time_datas[i] = new Day_time_data();
                    day_time_datas[i].v_day = dt_dates_imsi[i].v_day;
                    day_time_datas[i].v_time = dt_dates_imsi[i].v_time;
                    day_time_datas[i].day = dt_dates_imsi[i].day; Log.i("day", day_time_datas[i].day+" ");
                    day_time_datas[i].time = dt_dates_imsi[i].time;Log.i("time", day_time_datas[i].time+" ");
                }
                else day_time_datas[i] = null;
            }
            //day_time_datas = dt_dates_imsi;
        //}
        //dt_input_state = true;
        //sel_daytime.setTextColor(Color.RED);
        Schedule.onSaveDayTime(this);
        //if(dt_input_state) {
            dtsl.removeAllViews();
            for (int i = 0; i < 7; i++) {
                if (day_time_datas[i] != null) {
                    LinearLayout dt_date = (LinearLayout) View.inflate(this, R.layout.dt_date, null);
                    ((TextView) dt_date.findViewById(R.id.time)).setText(day_time_datas[i].time);
                    String day = day_time_datas[i].day;
                    switch (day) {
                        case "0":
                            ((TextView) dt_date.findViewById(R.id.day)).setText(R.string.mon);
                            break;
                        case "1":
                            ((TextView) dt_date.findViewById(R.id.day)).setText(R.string.tue);
                            break;
                        case "2":
                            ((TextView) dt_date.findViewById(R.id.day)).setText(R.string.wed);
                            break;
                        case "3":
                            ((TextView) dt_date.findViewById(R.id.day)).setText(R.string.thu);
                            break;
                        case "4":
                            ((TextView) dt_date.findViewById(R.id.day)).setText(R.string.fri);
                            break;
                        case "5":
                            ((TextView) dt_date.findViewById(R.id.day)).setText(R.string.sat);
                            break;
                        case "6":
                            ((TextView) dt_date.findViewById(R.id.day)).setText(R.string.sun);
                            break;
                    }
                    dtsl.addView(dt_date);

                }
            }*/
       // }


   // }*/

    public void onSaveTimeClicked(View v) {
     //   ++dt_datas_length;
        //if(dt_input_state)
            Schedule.onSaveTimeClicked(this);
        //else Schedule.onSaveTimeClicked(this, day_time_datas);
    }

    TextWatcher searchWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if (s.length() > 0) {
                String st = s.toString();
                String ts = st.trim();
                if (!ts.equals(st))
                    man_search.setText(ts);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        if(from!=null && from.equals("new_man")) {
            setMans();
        }
        if(from!=null && from.equals("new_rew")) {
            setRewSpi();
        }
    }

    class SelectedMansAdapter extends BaseAdapter {

        Context context;
        ArrayList<Man_data> items = new ArrayList<Man_data>();

        public SelectedMansAdapter(Context c, ArrayList<Man_data> it) {
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
                convertView = inflater.inflate(R.layout.selected_m_list, null);

            }
            Man_data ld = (Man_data) items.get(i);

            ((TextView) convertView.findViewById(R.id.title)).setText(ld.name);
            ((TextView) convertView.findViewById(R.id.i)).setText(i + "");
            Man.setImage(ld.img, (ImageView) convertView.findViewById(R.id.img), context);

            return convertView;

        }

        public void addItem(Man_data item) {
            items.add(item);
        }
    }

    class SearchAdapter extends BaseAdapter {

        Context context;
        ArrayList<Man_data> items = new ArrayList<Man_data>();

        public SearchAdapter(Context c, ArrayList<Man_data> it) {
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
                convertView = inflater.inflate(R.layout.search_man, null);

            }
            Man_data ld = (Man_data) items.get(i);

            ((TextView) convertView.findViewById(R.id.name)).setText(ld.name);

           // ((TextView) convertView.findViewById(R.id._id)).setText(ld._id + "");
            Man.setImage(ld.img, (ImageView) convertView.findViewById(R.id.img), context);
           // ((TextView) convertView.findViewById(R.id.login_id)).setText(ld.login_id);
            search_cbs.add((CheckBox)convertView.findViewById(R.id.cb));
            //((TextView)convertView.findViewById(R.id.i)).setText(i+"");

            return convertView;

        }

        public void addItem(Man_data item) {
            items.add(item);
        }
    }
    View.OnClickListener  bListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i=0; i<rbs.size();i++){
                rbs.get(i).setChecked(false);
                if(rbs.get(i)==v) {
                    Log.i("rbs.get(i)==v", i+" "); /*
                   TextView t = (TextView)(((LinearLayout)v.getParent()).findViewById(R.id.seq));
                    if(t==null) Log.i("t=null", " "); */
                        String s = ((TextView) (((LinearLayout) v.getParent()).findViewById(R.id.seq)))
                                .getText().toString();
                        Log.i("seq", s + " ");

                        sel_seq = Integer.parseInt(s);

                }
            }
            ((RadioButton)v).setChecked(true);

        }
    };

    void allocate_variables(){

        man_spi = findViewById(R.id.man_spi);
        sched_spi =  findViewById(R.id.sched_spi);

        // schedl =  findViewById(R.id.sched);
        linkedl = findViewById(R.id.linked);
        //dtsl =  findViewById(R.id.dt);
        img =  findViewById(R.id.img);

        man_list = findViewById(R.id.man_list);
        man_search =  findViewById(R.id.man_search);
        man_search.addTextChangedListener(searchWatch);
        //rew_spi =  findViewById(R.id.rew_spi) ;
        save =  findViewById(R.id.save);

        man_lay = findViewById(R.id.man_lay);
        new_man = findViewById(R.id.new_man);
       // new_rew = findViewById(R.id.new_rew);

        //rew =  findViewById(R.id.rew);
        //num_lay = findViewById(R.id.num_lay);

        day_time = findViewById(R.id.day_time);
        man_regb = findViewById(R.id.man_regb);

        addlink = findViewById(R.id.addlink);
        dellink = findViewById(R.id.dellink);

    }

    void initiate(){
     //  Log.i("initiate", sd.lds.size() + " ");
     //   Intent intent = getIntent();
     //   if ( intent.getStringExtra("s_id") == null) {

       // cl = new Schedule_content_layout(getWindow().getDecorView().getRootView(), sd, 0, this); //}
        //sched = new Schedule(this);
        man = new Man();
        rbs = new ArrayList<RadioButton>();

        setMans();
        //setCoupons();

        attachListeners();

    }

    void attachListeners(){

        man_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(man_search.getWindowToken(), 0);    //hide keyboard
                    searchByName();
                    man_search.setText("");

                    return true;
                }
                return false;
            }
        });

        man_spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Man.setImage(man.mans.get(i).img, img, ScheduleActivity.this);
                selected_i = i;
                //showSchedule();

                //scheduleList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

}
