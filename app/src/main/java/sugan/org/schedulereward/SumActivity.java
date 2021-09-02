package sugan.org.schedulereward;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by eunsoo on 2017-12-12.
 */

public class SumActivity extends AppCompatActivity {

    Man_data md ;
    Man_sum ms;

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
    int per_score_i;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum);
        Intent intent = getIntent();
        md = (Man_data) intent.getSerializableExtra("man_data");

        img =  findViewById(R.id.img);
        name = findViewById(R.id.name);
        t1 = findViewById(R.id.t1);
        t2 =  findViewById(R.id.t2);
        t3 =  findViewById(R.id.t3);
        b2 = findViewById(R.id.b2);
        b1 = findViewById(R.id.b1);
        list1 =  findViewById(R.id.list1);
        list2 = findViewById(R.id.list2);
        b2 =  findViewById(R.id.b2);
        b3 =  findViewById(R.id.b3);

        per_s_lay = findViewById(R.id.per_s_lay);
        fillSum();

    }

    void fillSum(){

        ms = Sum.fillSumByMan(this, md);
        per_score_i = ms.man.per_score; Log.i("per_score", per_score_i+" ");

        name.setText(md.name);
        Man.setImage(md.img, img, this);
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
            //  t2.setText(ms.man.reward + "");
        }
        //t3.setText(((int) (ms.sum / ms.man.per_score)) + "");
        t3.setText(((int) (ms.sum / per_score_i)) + "");
/*
        if(ms.used_scores.size()>1) {
            UsedSumListAdapter adapter1 = new UsedSumListAdapter(SumActivity.this, ms.used_scores);
            list2.setAdapter(adapter1);
            b2.setVisibility(View.VISIBLE);
            A_SumActivity.setListViewHeightBasedOnChildren(list2);
        }
        else b2.setVisibility(View.GONE);*/
        if(ms.cp_datas.size()>0) {
            b3.setVisibility(View.VISIBLE);
        }
        else b3.setVisibility(View.GONE);
        if(ms.sum_datas.size()>1) {
            SumListAdapter adapter = new SumListAdapter(SumActivity.this, ms.sum_datas);
            list1.setAdapter(adapter);
            A_SumActivity.setListViewHeightBasedOnChildren(list1);
        }
        else  {
            list1.setAdapter(null);
        }
        if(ms.cp_datas.size()>1){
            CpListAdapter adapter = new CpListAdapter(this, ms.cp_datas);
            list2.setAdapter(adapter);
            A_SumActivity.setListViewHeightBasedOnChildren(list2);

        }

    }

    public void onMyCoupons(View v) {
        Coupon.onMyCoupons(this, md.name);

    }

    public void onB1Clicked(View v) {   //점수내역 sum history
        list1.setVisibility(list1.getVisibility() == View.VISIBLE? View.GONE:View.VISIBLE);
        list2.setVisibility(View.GONE);
    }

    public void onB2Clicked(View v) {   //쿠폰내역 coupon history
        list2.setVisibility(list2.getVisibility() == View.VISIBLE? View.GONE:View.VISIBLE);
        list1.setVisibility(View.GONE);
    }

    public void onHome(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("man_data", md);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
