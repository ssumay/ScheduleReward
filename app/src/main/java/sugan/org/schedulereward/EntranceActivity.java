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
    //LinearLayout pwd_l;
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
            sql = "select  name, pwd, pwd_on  from man ";

            cursor = db.rawQuery(sql, null);

        cursor.moveToNext();
        Man_data admin_data = getMan(cursor);
        TextView admin = findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntranceActivity.this, AdminActivity.class);
                intent.putExtra("man_data", admin_data);
                startActivity(intent);
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

                    selected_man = man_data;

                    Log.i("onClick -",  man_data.pwd_on+" ");
                    if(man_data.pwd_on== 1) {
                        LinearLayout pwd_l = (LinearLayout)View.inflate(EntranceActivity.this, R.layout.pwd_input, null);
                        pwd = (EditText) pwd_l.findViewById(R.id.pwd);
                        pwd.addTextChangedListener(pwdWatch);
                       // AlertDialog dialog2 =
                        new AlertDialog.Builder(EntranceActivity.this)
                               // .setTitle(R.string.select_time)
                                //.setIcon(R.drawable.androboy)
                                .setView(pwd_l)
                                //.setCancelable(false)
                                .show();

                    }
                    else {
                        gotoMainActivity();
                    }
                }

            });

        }
        cursor.close();
        sHelper.close();

      //  LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);



    }

        Man_data getMan(Cursor cursor) {
            Man_data man_data = new Man_data();
            man_data.name =  cursor.getString(0);
            man_data.pwd = cursor.getString(1);
            man_data.pwd_on = cursor.getInt(2);
            return man_data;
        }

    public void onAdmin(View v){
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

        TextWatcher pwdWatch =  new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                Log.i("pwdWatcher", " ");
                //if(s.length()==4 && !_id.getText().toString().equals("")) {
                /*if(!_id.getText().toString().equals("admin")) {
                    pwdl.setVisibility(View.GONE);
                    _id.requestFocus();
                    return;
                }*/
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
        Intent intent = new Intent(this, MainActivity.class);
        Log.i("man_data", selected_man.name + " " + selected_man.pwd);
        intent.putExtra("man_data", selected_man);
        startActivity(intent);
    }

}