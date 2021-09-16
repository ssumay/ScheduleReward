package sugan.org.schedulereward;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-16.
 * updated by eunsoo on summer 2021
 */

public class DoSchedFragment extends Fragment {
    Man_data md;
    //String m_id;
    //String name;
    Sched_data sd;

    Linked_data_man ld;

    String s_title;
    //String s_id;
    int seq;
    //String seq_num;
    Context context;

    TextView  sched_title ;
    TextView  nameT ;
    TextView  link_note ;
    String ln;
    //LinearLayout score_layout;
    LinearLayout values_layout;
    ArrayList<TextView> variables; // values_layout에 있는 editText들을 저장.
   // TextView  score;
   // TextView  no ;
    TextView  done;
    TextView logout ;
    TextView logout1;
    TextView home;
    TextView home1;
    LinearLayout picture_layout ;
    TextView camera;
    ImageView picture;
    View root;
    String img;

    private final int PICK_FROM_CAMERA = 0;
    private final int CROP_FROM_IMAGE = 2;
    private Uri mImageCaptureUri;

    public  static DoSchedFragment newInstance(Man_data man, Sched_data sd, int seq, Context context) {
        return newInstance(man, sd, seq,  null, context);
    }
    public  static DoSchedFragment newInstance(Man_data man, Sched_data sd,  int seq,
                                               String s_title, Context context) {
        DoSchedFragment fragment = new DoSchedFragment();
        fragment.md = man;
        fragment.sd = sd;
        fragment.seq =  seq;
        //fragment.seq_num = seq_num;
        //fragment.s_title = s_title;
        fragment.context = context;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_do_sched, container, false);

        sched_title = root.findViewById(R.id.sched_title);
        nameT = root.findViewById(R.id.name);
        link_note =  root.findViewById(R.id.link_note);
        values_layout = root.findViewById(R.id.values_layout);
        /* score_layout = (LinearLayout) root.findViewById(R.id.score_layout);
        if(sd.reward!=-1) {
            score_layout.setVisibility(View.GONE);
        }*/
        //score  = (TextView) root.findViewById(R.id.score) ;
        //no = (TextView) root.findViewById(R.id.no);
        picture_layout =  root.findViewById(R.id.picture_layout);
        picture =  root.findViewById(R.id.picture);
        camera = root.findViewById(R.id.camera);
        done =  root.findViewById(R.id.done);
        logout = root.findViewById(R.id.logout);
        logout1 =  root.findViewById(R.id.logout1);
        home =  root.findViewById(R.id.home);
        home1 = root.findViewById(R.id.home1);
        Log.i("md.img", md.img + " ");
        Man.setImage(md.img, root.findViewById(R.id.img), context);

        showLinkContent();
        return root;
    }

    public void goToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("man_data", md );

        startActivity(intent);
    }

    public void showLinkContent() {
        //Linked_data_man ld = Link.linkContent(sd._id+"", md._id+"", seq,  context );
        ld = Link.linkContent(sd, seq, md,  context );
        if(ld!=null) ld.md = md;

        s_title = sd.title; //Log.i("sd.reward", sd.reward+ " ");

        if(ld==null) {  // all-done situation

            root.findViewById(R.id.scroll).setVisibility(View.GONE);
            root.findViewById(R.id.all_complete).setVisibility(View.VISIBLE);
            ((TextView)root.findViewById(R.id.c_name)).setText(md.name);
            String sum_res;

           /* if(sd.reward!=-1) {   //one-day reward case.
                sum_res = sd.rew_desc;
            }
            else */

            sum_res = Sum.getSumScore(md.name, sd._id+"" , context);
            ((TextView)root.findViewById(R.id.sum)).setText(sum_res);
            Log.i("s_title", s_title +" ");
            ((TextView)root.findViewById(R.id.c_title)).setText(s_title);
        }
        else {
            if (ld.state == 0) {
                Toast.makeText(context, R.string.time_over, Toast.LENGTH_LONG).show();
                goToMainActivity();

            }/*
        else if(ld.state == 2) {
            Toast.makeText(context, R.string.no_sched_link, Toast.LENGTH_LONG).show();
            goToMainActivity();
        }*/

        /*
        if(sd.reward!=-1) {
            TextView imsi = new TextView(context);
            imsi.setText(R.string.one_day_rew);
            s_title = s_title + " - " + imsi.getText().toString();
        }*/

                //ld.md = Man.getManData(m_id, context);
                //Log.i("ld.md.name", ld.md.name + " ");

                sched_title.setText(s_title);
                nameT.setText(md.name);
                link_note.setText(ld.ld.link_note);
                ln = link_note.getText().toString();
            /*score.setText(ld.score);
            if (ld.no == 0 || (sd.reward!=-1)&& sd.skip_num == 0) {
           // if (ld.no == 0 || (sd.reward!=-1)&& sd.skip_num >= (sd.seq_num-sd.score)) {
                no.setVisibility(View.GONE);
                Log.i("no버튼 사라짐", ld.no + " " + sd.skip_num + " " + sd.seq_num + " " + sd.score);
            }*/
                if (ld.ld.pic == 0) picture_layout.setVisibility(View.GONE);
                switch (ld.ld.reward_type) {
                    case 1:
                        //values_layout.setVisibility(View.VISIBLE);
                        variables = new ArrayList<>();
                        Schedule.arrangeValues_layout(context, values_layout, ld.ld.link_Atoms, values_layout, true, false, 25, variables);
                        break;
                    case 0:   //simple
                    case 2:   //no reward
                }/*
                if (ld.ld.reward_type == 1) {
                    values_layout.setVisibility(View.VISIBLE);
                    variables = new ArrayList<>();
                    Schedule.arrangeValues_layout(context, values_layout, ld.ld.link_Atoms, true, false, 25, variables);
                }*/

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        done.setOnClickListener( done_listener);

        camera.setOnClickListener( camera_listener );

        LogoutListener listener = LogoutListener.newInstance(md, context);
        logout.setOnTouchListener(listener);
        logout1.setOnTouchListener(listener);
        HomeListener listener1 = HomeListener.newInstance(md, context);
        home.setOnTouchListener(listener1);
        home1.setOnTouchListener(listener1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!= Activity.RESULT_OK) {
            return;
        }

        switch(requestCode)
        {

            case PICK_FROM_CAMERA: {
                Log.i("pick_from_camera  " , mImageCaptureUri.getPath().toString());




                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 100);
                intent.putExtra("outputY", 100);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);

                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);

                break;

            }
            case CROP_FROM_IMAGE: {
                Bundle extras = data.getExtras();
                img = System.currentTimeMillis()+"";
                String filePath = context.getCacheDir()+"/" + img + ".jpg";
                Log.i("CROP_FROM_IMAGE", filePath + " ");

               // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                  //      +"/scheduleReward/" +
                  //      img+".jpg";

                if(extras != null){
                    Bitmap photo = extras.getParcelable("data");
                    picture.setImageBitmap(photo);

                    Util.storeCropImage(context, photo, filePath);

                }

                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()) {
                    f.delete();
                    Log.i("f.exist()", "f.delete()");
                }
                else {

                    Log.i("f.exist() not", "aaa");
                }


            }
        }
    }

    Button.OnClickListener done_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Log.i("onclick", "aaa");
            Log.i("ld.ld.reward_type", ld.ld.reward_type + " ");
            //double result = 0;
            String result= "" ;

            switch (ld.ld.reward_type){
                case 2: //no reward
                    break;

                case 1: // formula
                    result = Sum.calculateIfFormulaLinkResult(variables, ld.ld)+"";
                    break;
                case 0: //simple
                    if(ld.ld.formula.startsWith("3")) { //cash
                        result = ld.ld.formula.substring(2); Log.i("cash_result = " , result + " ");

                    }
                    else { //coupon
                        result = ld.ld.formula.replaceFirst("4", "coupon");  Log.i("coupon_result = ", result+ " ");
                    }
                    break;
                case 3: //edt  execute_during_time
                    String[] form = ld.ld.formula.split("\\|");
                    result = Schedule.getEDTResult(Integer.parseInt(form[3]), Integer.parseInt(form[4]),
                            Integer.parseInt(form[1]), Integer.parseInt(form[2]), Integer.parseInt(form[5])) + "";

            }

            Sum.executeSchedLink(ld, context, result, img);


            Intent intent = new Intent(context, DoSchedActivity.class);
            intent.putExtra("man_data", md);
            intent.putExtra("sched", sd);
            //intent.putExtra("s_id", s_id);
            //intent.putExtra("s_title", s_title);
            intent.putExtra("page", MyFragment.DOSCHED);
            intent.putExtra("seq", (seq+1)+"");
            //intent.putExtra("seq_num", sd.seq_num+"");
            startActivity(intent);

        }
    };

    Button.OnClickListener camera_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            String url = "tmp_" + System.currentTimeMillis() + ".jpg";

            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
            Log.i("mImageCaptureUri = " , mImageCaptureUri.getPath());


            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);

        }
    };
}

