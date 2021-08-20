package sugan.org.schedulereward;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-14.
 */

public class MainActivity extends AppCompatActivity {
    //String m_id;
    //String name;
    Man_data md;
    Sched_data clicked_s;
    ArrayList<Sched_data> group ;
    ArrayList<ArrayList<Linked_sched_data>> child ;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
Log.i("Mainac", "ONCREATE");
        Intent intent = getIntent();
        md = (Man_data)intent.getSerializableExtra("man_data");
        //m_id = intent.getStringExtra("m_id");
        //name = intent.getStringExtra("name");
        getSchedList();


    }
    public void getSchedList() {
        group = new ArrayList<Sched_data>();
        child = new ArrayList<ArrayList<Linked_sched_data>>();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.list);
        //Log.i("md.id", md._id + " " + md.pwd);
        CurrentSchedListAdapter adapter = Schedule.getRightNowList(this, group, child, md.name );
        listView.setAdapter(adapter);

    }

    public void onSum(View v) {
        Intent intent = new Intent(MainActivity.this, SumActivity.class);
        intent.putExtra("man_data", md);
        startActivity(intent);

    }
    public void onSchedTitleClicked(View v) {
        Intent intent = new Intent(MainActivity.this, DoSchedActivity.class);
        TextView i = (TextView)((LinearLayout) v.getParent()).findViewById(R.id.i);
        //TextView seq_num = (TextView)((LinearLayout) v.getParent()).findViewById(R.id.seq_num);

        intent.putExtra("man_data", md);
        clicked_s = group.get(Integer.parseInt(i.getText().toString()));
       // Log.i("skip_num", clicked_s.skip_num+" ");
        intent.putExtra("sched", clicked_s);
        //intent.putExtra("seq_num", seq_num.getText().toString());
        startActivity(intent);
    }

    public void onMySchedCliked(View v) {
        Intent intent = new Intent(MainActivity.this, MySchedTotalListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("man_data", md);
        //intent.putExtra("m_id", m_id );
        startActivity(intent);
    }

    public void onLogoutClicked(View v) {
        Intent intent = new Intent(this, EntranceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

}

