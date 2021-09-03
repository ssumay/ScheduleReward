package sugan.org.schedulereward;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-16.
 */

public class DoSchedActivity extends AppCompatActivity {

    LinearLayout fragment_layout;
    LinearLayout login_layout;

    LoginFragment lf;
    DoSchedFragment dsf;
    FragmentManager fm ;
    FragmentTransaction tr ;
    boolean login_frag = false;   //로긴프레그가 있는지를 나타냄.   로긴아웃시 로긴프레그가 없으면 로긴프레그를 추가하기 위함.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_sched);
        fragment_layout = (LinearLayout) findViewById(R.id.fragment_layout);
        Log.i("doschedactivity", "oncreate");

        LinearLayout linear1 = new LinearLayout(this);
        int i=1;
        linear1.setId(i);
        fragment_layout.addView(linear1);

        fm = getFragmentManager();
        tr = fm.beginTransaction();
        SFDoSchedFragment.mds = Man.getManTotal(this);
        SFDoSchedFragment.ids = new ArrayList<String>();
        SFDoSchedFragment.myFragments = new ArrayList<MyFragment>();
        SFDoSchedFragment.frag_linears = new ArrayList<LinearLayout>();
        SFDoSchedFragment.next_id = 2;

        Intent intent = getIntent();
        Man_data md = (Man_data) intent.getSerializableExtra("man_data");
        //String m_id = intent.getStringExtra("m_id");
        //String name = intent.getStringExtra("name");
        //String s_id = intent.getStringExtra("s_id");
        Sched_data sd = (Sched_data) intent.getSerializableExtra("sched");
        //String seq_num = intent.getStringExtra("seq_num");
        SFDoSchedFragment.ids.add(md.name);

        //MyPageFragment mpf =  MyPageFragment.newInstance(m_id, this);
        dsf = DoSchedFragment.newInstance(md, sd, 0, this); // --seq 0으로 수정
        MyFragment myf = new MyFragment();
        myf.m_name = md.name;
        myf.fragment = dsf;
        myf.layout_id = i;
        SFDoSchedFragment.myFragments.add(myf);

        //lf = LoginFragment.newInstance(this);
        //tr.add(i, mpf);
        SFDoSchedFragment.setHeight(linear1, 400);
        SFDoSchedFragment.frag_linears.add(linear1);
        tr.add(i, dsf);
        //tr.add(R.id.fragment_layout, dsf, m_id);
        getLoginFrag(i);

        tr.commit();


    }
    void getLoginFrag(int i){
        if(SFDoSchedFragment.mds.size() > SFDoSchedFragment.ids.size())  {
            login_layout = new LinearLayout(this);
            login_layout.setId(++i);
            fragment_layout.addView(login_layout);
            SFDoSchedFragment.setHeight(login_layout, 120);
            SFDoSchedFragment.frag_linears.add(login_layout);
            a: for(int j=0; j<SFDoSchedFragment.mds.size(); j++){
                for(int k=0;k < SFDoSchedFragment.ids.size(); k++){
                    if(SFDoSchedFragment.mds.get(j).name.equals( SFDoSchedFragment.ids.get(k)))
                        continue a;
                }
                lf = LoginFragment.newInstance(this, SFDoSchedFragment.mds.get(j).name );
                tr.add(i, lf);  // i++ error
                login_frag = true;

                Log.i("login_user", SFDoSchedFragment.mds.get(j).name );

            }

            //tr.add(i, lf);

            //login_frag = true;

            //tr.add(R.id.fragment_layout, lf);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fm = getFragmentManager();
        tr = fm.beginTransaction();

        Man_data md = (Man_data)intent.getSerializableExtra("man_data");
        Sched_data sd = (Sched_data) intent.getSerializableExtra("sched");
        String name = md.name;

        String s_id = intent.getStringExtra("s_id");
        String add_id = intent.getStringExtra("add_id");
        if(add_id!=null && add_id.equals("y")) {
            //tr.remove(lf);

            //LinearLayout linear1 = new LinearLayout(this);
            //linear1.setId(SFDoSchedFragment.next_id); Log.i("next_id",SFDoSchedFragment.next_id+ " " );
            //fragment_layout.removeView();
            //fragment_layout.addView(linear1);

            SFDoSchedFragment.ids.add(name);
            MyFragment mf = new MyFragment();
            mf.fragment = MyPageFragment.newInstance(md, this);
            mf.layout_id = SFDoSchedFragment.next_id;
            SFDoSchedFragment.myFragments.add(mf);
            //SFDoSchedFragment.next_id = SFDoSchedFragment.next_id +
            int last_l = SFDoSchedFragment.frag_linears.size();
            int hei = last_l>2? 220: 300;  // 로긴이 두명이면 300, 더 많으면 220 , 한명이면 400 (위에서 첨에 지정)
            SFDoSchedFragment.setHeight(SFDoSchedFragment.frag_linears.get(last_l - 1), hei);
            if (last_l > 1)
                SFDoSchedFragment.setHeight(SFDoSchedFragment.frag_linears.get(last_l - 2), hei);
            if (last_l > 2)
                SFDoSchedFragment.setHeight(SFDoSchedFragment.frag_linears.get(last_l - 3), hei);

            tr.replace(SFDoSchedFragment.next_id, mf.fragment);
            login_frag = false;
            //tr.add(mf.layout_id, mf.fragment, m_id);
            if (SFDoSchedFragment.mds.size() > SFDoSchedFragment.ids.size()) {
                Log.i(" size>", true + " ");
                getLoginFrag(SFDoSchedFragment.next_id + 1);
                /*
                SFDoSchedFragment.next_id = SFDoSchedFragment.next_id +1;
                LinearLayout linear2 = new LinearLayout(this);
                linear2.setId(SFDoSchedFragment.next_id);  Log.i("next_id",SFDoSchedFragment.next_id+ " " );
                fragment_layout.addView(linear2);
                lf = LoginFragment.newInstance(this);
                SFDoSchedFragment.setHeight(linear2, 120);
                SFDoSchedFragment.frag_linears.add(linear2);
                tr.add(SFDoSchedFragment.next_id, lf);*/
               // login_frag = true;
            }
            tr.commit();
        }
        else {
            String page = intent.getStringExtra("page");

            FragmentManager fm = getFragmentManager();
            FragmentTransaction tr = fm.beginTransaction();
            if (page.equals(MyFragment.MYPAGE)) {
                Log.i("onnewintent", "MyFragment.MYPAGE");
                MyPageFragment mf = MyPageFragment.newInstance(md, this);

                for(int i = 0; i< SFDoSchedFragment.ids.size(); i++ ) {
                    if(SFDoSchedFragment.ids.get(i).equals(name)){  //transferred from different fragment except loginfrag.
                       // Log.i("SFDoSchedFragment.ids.get(i).equals(m_id)", " ");
                        tr.replace(SFDoSchedFragment.myFragments.get(i).layout_id, mf);
                        SFDoSchedFragment.myFragments.get(i).fragment = mf;
                        tr.commit();
                        return;
                    }
                }
                //tr.replace(SFDoSchedFragment.next_id, mf);
                //tr.add( ++SFDoSchedFragment.next_id, LoginFragment.newInstance(this));
                /*
                tr.remove(lf);    //from login fragment
                tr.add(R.id.fragment_layout, mf, m_id);
                if (SFDoSchedFragment.mds.size() > SFDoSchedFragment.ids.size()) {
                    Log.i(" size>", true + " ");
                    lf = LoginFragment.newInstance(this);
                    tr.add(R.id.fragment_layout, lf);
                }
                tr.commit();
                */
            } else if (page.equals(MyFragment.DOSCHED)) {
                Log.i("page.equals(0)", "bbb");
                int seq = Integer.parseInt(intent.getStringExtra("seq"));Log.i("dosched seq = ", seq + " ");
                String seq_num = intent.getStringExtra("seq_num");
                String s_title = intent.getStringExtra("s_title");
                DoSchedFragment fragment = DoSchedFragment.newInstance( md, sd, seq,  s_title, this);

                int layout_id = 0;
                for(int i = 0; i< SFDoSchedFragment.myFragments.size(); i++) {
                    if(SFDoSchedFragment.myFragments.get(i).m_name.equals(name)) {
                        layout_id = SFDoSchedFragment.myFragments.get(i).layout_id;
                        SFDoSchedFragment.myFragments.get(i).fragment = fragment;
                        break;
                    }
                }

                tr.replace(layout_id, fragment);

                tr.commit();
            }
            else if (page.equals(MyFragment.SUM)) {
                Log.i("page.equals(0)", "bbb");
                //int seq = Integer.parseInt(intent.getStringExtra("seq"));
                //String seq_num = intent.getStringExtra("seq_num");
                //String s_title = intent.getStringExtra("s_title");
                SumFragment fragment = SumFragment.newInstance( md,  this);

                int layout_id = 0;
                for(int i = 0; i< SFDoSchedFragment.ids.size(); i++ ) {
                    if(SFDoSchedFragment.ids.get(i).equals(name)){  //transferred from different fragment except loginfrag.
                        Log.i("SFDoSchedFragment.id", i+" ");
                        tr.replace(SFDoSchedFragment.myFragments.get(i).layout_id, fragment);
                        SFDoSchedFragment.myFragments.get(i).fragment = fragment;
                        tr.commit();
                        return;
                    }
                }

            }
            else if(page.equals(MyFragment.LOGOUT)) {
                Log.i("LOGOUT","BEFORE SIZE()==1" );
                if(SFDoSchedFragment.ids.size()==1) {
                    Log.i("LOGOUT","IF SIZE()==1" );

                    SFDoSchedFragment.ids.remove(0);
                    SFDoSchedFragment.myFragments.remove(0);
                    Intent intent1 = new Intent(this, EntranceActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    finish(); return;
                }
                Log.i("LOGOUT","AFTER SIZE()==1" );

                //String _id = intent.getStringExtra("m_id");
                Log.i("m_name", name+" " + SFDoSchedFragment.myFragments.size());
                for(int i = 0; i< SFDoSchedFragment.myFragments.size(); i++) {
                    Log.i("aaa", i + " " + SFDoSchedFragment.myFragments.get(i).m_name);
                    if(SFDoSchedFragment.myFragments.get(i).m_name.equals(name)) {
                        tr.remove(SFDoSchedFragment.myFragments.get(i).fragment);Log.i("tr.remove", "ccc");
                        //tr.commit();
                        fragment_layout.removeView(SFDoSchedFragment.frag_linears.get(i));
                        SFDoSchedFragment.ids.remove(i);
                        SFDoSchedFragment.myFragments.remove(i);
                        SFDoSchedFragment.frag_linears.remove(i);
                        //if (SFDoSchedFragment.mds.size() > SFDoSchedFragment.ids.size()) {
                           Log.i(" login_frag", login_frag + " ");
                        if(login_frag==false) {
                            //getLoginFrag();  왜 함수호출로 하면 안되는지 모르겠네

                            SFDoSchedFragment.next_id = SFDoSchedFragment.next_id + 1;
                            LinearLayout linear2 = new LinearLayout(this);
                            linear2.setId(SFDoSchedFragment.next_id);
                            Log.i("next_id", SFDoSchedFragment.next_id + " ");
                            fragment_layout.addView(linear2);
                            getLoginFrag(SFDoSchedFragment.next_id);
                           /* lf = LoginFragment.newInstance(this,SFDoSchedFragment.next_id+1  );
                            SFDoSchedFragment.setHeight(linear2, 120);
                            SFDoSchedFragment.frag_linears.add(linear2);
                            tr.add(SFDoSchedFragment.next_id, lf);
                            login_frag = true;*/
                        }
                        tr.commit();

                        int last_l = SFDoSchedFragment.frag_linears.size()-1;  //끝에는 로긴프레그 last_l은 1이상
                        int hei = last_l>2? 220:( last_l==2?300: 400);  // 로긴이 두명이면 300, 더 많으면 220 , 한명이면 400 (위에서 첨에 지정)
                        SFDoSchedFragment.setHeight(SFDoSchedFragment.frag_linears.get(last_l - 1), hei);
                        if (last_l > 1)
                            SFDoSchedFragment.setHeight(SFDoSchedFragment.frag_linears.get(last_l - 2), hei);
                        if (last_l > 2)
                            SFDoSchedFragment.setHeight(SFDoSchedFragment.frag_linears.get(last_l - 3), hei);



                    }

                }
            }
        }

    }

    public static class SFDoSchedFragment {
        static ArrayList<Man_data> mds;  //men in db except admin.
        static ArrayList<String> ids;   //connected man_id
        static ArrayList<MyFragment> myFragments;
        static int next_id = 2;   //fragment layout id
        static ArrayList<LinearLayout> frag_linears;

        static void setHeight(LinearLayout linear1, int height_dpi) {
            LinearLayout.LayoutParams pr = (LinearLayout.LayoutParams) linear1.getLayoutParams();
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height_dpi,
                    linear1.getResources().getDisplayMetrics());
            //기존 - 220
            pr.height = height;
            linear1.setLayoutParams(pr);
        }
    }
}



