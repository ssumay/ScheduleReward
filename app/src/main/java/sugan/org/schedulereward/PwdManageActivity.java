package sugan.org.schedulereward;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PwdManageActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_manage);

        listView = findViewById(R.id.list);
    }

    @Override
     protected void onStart() {
        super.onStart();
        listView.setAdapter( Man.getPwdList(this));

    }

    public void onHome(View v){
        Intent intent = new Intent(this, AdminActivity.class);
       // intent.putExtra("man_data", admin);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

class PwdListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    ArrayList<Man_data> mds;

    PwdListAdapter(Context context, ArrayList<Man_data> mds) {

        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mds = mds;


    }

    @Override
    public int getCount() {
        return mds.size();
    }

    @Override
    public Object getItem(int position) {
        return mds.get(position).name;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         if(convertView==null)
             convertView = inflater.inflate(R.layout.pwd_listview, parent, false);

         Man_data md =  mds.get(position);
        ImageView img = convertView.findViewById(R.id.img);
        Man.setImage(md.img, img, context);
        TextView name = convertView.findViewById(R.id.name);
        name.setText(md.name);
        TextView pwd = convertView.findViewById(R.id.pwd);
        pwd.setText(md.pwd);
        TextView pwd_on = convertView.findViewById(R.id.pwd_on);
        pwd_on.setText(md.pwd_on == 0 ? R.string.pwd_on: R.string.pwd_off);

        TextView pwd_ch = convertView.findViewById(R.id.pwd_ch);

        pwd_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView imsi = new TextView(context);
                imsi.setText(R.string.state_pwd);
                String s = imsi.getText().toString() + "  " + md.name;

                new AlertDialog.Builder(context)   //schedule db에 state = 0로 저장, 즉 사용자에게 보이지 않는 스케줄이 됨
                        .setTitle(R.string.t_state_pwd)
                        .setMessage(s)
                        //.setIcon(R.drawable.androboy)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int b) {
                                if(Man.changePwdState(md.pwd_on==0? 1: 0 , md.name, context)){  //change에 성공시

                                    Toast.makeText(context, md.pwd_on==0?R.string.pwd_oned : R.string.pwd_offed, Toast.LENGTH_SHORT).show();
                                    md.pwd_on = md.pwd_on==0? 1 : 0;
                                    pwd_on.setText(md.pwd_on == 0 ? R.string.pwd_on: R.string.pwd_off);
                                }
                                else{  //실패시
                                    Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton(R.string.cancle, null)
                        .show();
            }
        });

        pwd_ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView imsi = new TextView(context);
                imsi.setText(R.string.q_pwd_ch);
                String s = imsi.getText().toString() + md.name;

                LinearLayout pwd_l = (LinearLayout)View.inflate(context, R.layout.pwd_input, null);
                EditText pwd = pwd_l.findViewById(R.id.pwd);
                TextView title = pwd_l.findViewById(R.id.title);
                title.setText(R.string.new_pwd);

                AlertDialog dialog = new AlertDialog.Builder(context)   //schedule db에 state = 0로 저장, 즉 사용자에게 보이지 않는 스케줄이 됨
                        .setTitle(R.string.pwd_ch)
                        .setMessage(s)
                        .setView(pwd_l)
                        //.setIcon(R.drawable.androboy)
                        .setNegativeButton(R.string.cancle, null)
                        .show();

                pwd.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                        if(s.length()==4 ) {

                            if(Man.changePwd(md.name, pwd.getText().toString(), context)){
                                Toast.makeText(context, R.string.modified, Toast.LENGTH_SHORT).show();
                                ((PwdManageActivity)context).onStart();
                            }
                            else {
                                Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();

                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }
        });

        return convertView;
    }

}