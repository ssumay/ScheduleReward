package sugan.org.schedulereward;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EntranceActivity extends AppCompatActivity {

    Man      man;
    Man_data selected_man;
    EditText pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // deleteDatabase("schedreward.db");

        setContentView(R.layout.activity_entrance);
       // TextView formula_test_r = findViewById(R.id.formula_test_r);

        //formula_test_r.setText(Util.getResult(21, 0, 19, 30, 1.5f)+"");
        //Util.calcul_r(paren);

        Coupon.givePeriodicCoupons(this);   //주기적으로 발행되는 쿠폰 체크하고 발행한다

        man = new Man();
        LinearLayout man_list = findViewById(R.id.man_list);

        SchedDBHelper sHelper= new SchedDBHelper(this);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        Cursor cursor;
        String sql;
            sql = "select name, pwd, pwd_on, img from man ";

            cursor = db.rawQuery(sql, null);

        cursor.moveToNext();
        Man_data admin_data = getMan(cursor);
        TextView admin = findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enterMain(admin_data);
            }
        });



        while(cursor.moveToNext())  {

            TextView man_for_click = (TextView)View.inflate(this, R.layout.man_for_click, null);
            man_list.addView(man_for_click);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(man_for_click.getLayoutParams());
            param.setMargins(0,0,0,90);
            param.width = Util.dip2px(this, 200);
            param.height = Util.dip2px(this, 40);
            man_for_click.setLayoutParams(param);
            man_for_click.setText(cursor.getString(0));
            Man_data man_data = getMan(cursor);

            man_for_click.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                   // selected_man = man_data;

                    Log.i("onClick -",  man_data.pwd_on+" ");
                    enterMain(man_data);
                }

            });

        }
        cursor.close();
        sHelper.close();

      //  LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);



    }

    void enterMain(Man_data sel_man){

        selected_man = sel_man;
        if(selected_man.pwd_on== 1)
            pwdDialog();

        else
            gotoMainActivity();

    }

    void pwdDialog(){
        LinearLayout pwd_l = (LinearLayout)View.inflate(EntranceActivity.this, R.layout.pwd_input, null);
        pwd = pwd_l.findViewById(R.id.pwd);
        pwd.addTextChangedListener(pwdWatch);
        // AlertDialog dialog2 =
        new AlertDialog.Builder(EntranceActivity.this)
                 .setTitle(R.string.enter_pwd)
                //.setIcon(R.drawable.androboy)
                .setView(pwd_l)
                //.setCancelable(false)
                .show();
    }

        Man_data getMan(Cursor cursor) {
            Man_data man_data = new Man_data();
            man_data.name =  cursor.getString(0);
            man_data.pwd = cursor.getString(1);
            man_data.pwd_on = cursor.getInt(2);
            man_data.img = cursor.getString(3);
            return man_data;
        }

        TextWatcher pwdWatch =  new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if(s.length()==4 ) {
                    if( pwd.getText().toString().equals(selected_man.pwd) ) {
                        gotoMainActivity();
                    }
                    else {
                        Toast.makeText(EntranceActivity.this, R.string.wrong_pwd, Toast.LENGTH_SHORT).show();
                        pwd.setText("");
                        pwd.requestFocus();

                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

    void gotoMainActivity() {
        Intent intent;
        if(selected_man.name.equals("admin"))
            intent = new Intent(this, AdminActivity.class);
        else
            intent = new Intent(this, MainActivity.class);
       // Log.i("man_data", selected_man.name + " " + selected_man.pwd);
        intent.putExtra("man_data", selected_man);
        startActivity(intent);
    }

}