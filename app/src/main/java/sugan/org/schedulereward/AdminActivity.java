package sugan.org.schedulereward;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {

    Man_data admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent intent = getIntent();
        admin = (Man_data)intent.getSerializableExtra("man_data");

    }

    public void onPwdManage(View v){
        Intent intent = new Intent(this, PwdManageActivity.class);
        intent.putExtra("man_data", admin);
        startActivity(intent);
    }

    public void onAdMan(View v){
        Intent intent = new Intent(this, A_MListActivity.class);
        intent.putExtra("man_data", admin);
        startActivity(intent);
    }

    public void onAdSc(View v){
        Intent intent = new Intent(this, A_ScListActivity.class);
        intent.putExtra("man_data", admin);
        startActivity(intent);
    }

    public void onAdSum(View v){
        Intent intent = new Intent(this, A_SumActivity.class);
        intent.putExtra("man_data", admin);
        startActivity(intent);
    }

    public void onAdCoupon(View v){
        Intent intent = new Intent(this, A_CouponActivity.class);
        intent.putExtra("man_data", admin);
        startActivity(intent);
    }

    public void onLogout(View v) {
        Intent intent = new Intent(this, EntranceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}