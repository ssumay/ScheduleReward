package sugan.org.schedulereward;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Created by eunsoo on 2017-11-17.
 */

public class LoginFragment extends Fragment {

    Context context;
    String name;
    //EditText _id ;

    public  static LoginFragment newInstance(Context context, String name) {
        LoginFragment fragment = new LoginFragment();
        fragment.context = context;
        fragment.name = name;

        return fragment;
    }

        @Nullable
    @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_entrance, container, false);
            TextView user = root.findViewById(R.id.user);
            user.setText(name);
           /* _id = (EditText) root.findViewById(R.id._id);
            Man.setSpaceWatch(_id);
            _id.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //Enter key Action
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(_id.getWindowToken(), 0);    //hide keyboard
                        String id = _id.getText().toString();
                    if( id.equals("admin")) {
                        _id.setText(""); //_id.requestFocus();
                        Toast.makeText(context, R.string.no_admin, Toast.LENGTH_SHORT).show();

                        //pwd.setFocusedByDefault(true);
                    }
                    else {
                        Man_data d = Man.checkManId(id, context);                    //search.setText("");
                        if (d == null) {
                            Toast.makeText(context, R.string.login_id_noexist, Toast.LENGTH_SHORT).show();
                            _id.setText("");
                        } else {
                            for(int i=0; i< DoSchedActivity.SFDoSchedFragment.ids.size(); i++) {
                                if (d.name.equals(DoSchedActivity.SFDoSchedFragment.ids.get(i).toString())) {
                                    Toast.makeText(context, R.string.login_yes, Toast.LENGTH_SHORT).show();
                                    _id.setText("");
                                    return true;
                                }
                            }

                            Intent intent = new Intent(context, DoSchedActivity.class);
                            intent.putExtra("man_data", d);
                            //intent.putExtra("page", "1");  // 1 - mypageFragment page
                            intent.putExtra("add_id", "y");
                            startActivity(intent);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
*/
        return root;
    }
}
