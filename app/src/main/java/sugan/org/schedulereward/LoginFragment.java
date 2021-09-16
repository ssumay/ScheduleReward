package sugan.org.schedulereward;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by eunsoo on 2017-11-17.
 * updated by eunsoo on summer 2021
 */

public class LoginFragment extends Fragment {

    Context context;
    Man_data md;
    EditText pwd;
    LinearLayout linear;
    //String name;

    //EditText _id ;

    public  static LoginFragment newInstance(Context context, Man_data md, LinearLayout linear) {
        LoginFragment fragment = new LoginFragment();
        fragment.context = context;
        fragment.md = md;
        fragment.linear = linear;
        return fragment;
    }

        @Nullable
    @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_entrance, container, false);
            TextView man_for_click = root.findViewById(R.id.user);
            man_for_click.setText(md.name);
            man_for_click.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i("user-clicked", md.pwd_on + " " );
                    // selected_man = man_data;
                    if(md.pwd_on== 1){
                        LinearLayout pwd_l = (LinearLayout)View.inflate(context, R.layout.pwd_input, null);
                        pwd = pwd_l.findViewById(R.id.pwd);
                        pwd.addTextChangedListener(pwdWatch);
                        // AlertDialog dialog2 =
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.enter_pwd)
                                //.setIcon(R.drawable.androboy)
                                .setView(pwd_l)
                                //.setCancelable(false)
                                .show();
                    }

                    else
                    //Log.i("onClick -",  md.pwd_on+" ");
                    gotoMain();
                }

            });
        return root;
    }
    void gotoMain(){
        Intent intent = new Intent(context, DoSchedActivity.class);
        intent.putExtra("man_data", md);
        //intent.putExtra("m_id", m_id);
        intent.putExtra("add_id", "y");
        intent.putExtra("page", MyFragment.MYPAGE);
        context.startActivity(intent);
    }
    TextWatcher pwdWatch =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            if(s.length()==4 ) {
                if( pwd.getText().toString().equals(md.pwd) ) {
                    gotoMain();
                }
                else {
                    Toast.makeText(context, R.string.wrong_pwd, Toast.LENGTH_SHORT).show();
                    pwd.setText("");
                    pwd.requestFocus();

                }

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
