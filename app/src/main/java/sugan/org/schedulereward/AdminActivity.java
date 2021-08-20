package sugan.org.schedulereward;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent intent = getIntent();
        Man_data admin = (Man_data)intent.getSerializableExtra("man_data");


    }
    public void onAdMan(View v){
        Intent intent = new Intent(this, A_MListActivity.class);
        startActivity(intent);
    }

    public void onAdSc(View v){
        Intent intent = new Intent(this, A_ScListActivity.class);
        startActivity(intent);
    }

    public void onAdCoupon(View v){
        Intent intent = new Intent(this, A_CouponActivity.class);
        startActivity(intent);
    }

    public void onLogout(View v) {
        Intent intent = new Intent(this, EntranceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}