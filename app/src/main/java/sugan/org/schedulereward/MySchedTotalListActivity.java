package sugan.org.schedulereward;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-14.
 * updated by eunsoo on summer 2021
 */

public class MySchedTotalListActivity extends AppCompatActivity {
    //String m_id;
    Man_data md;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sched_total);

        Intent intent = getIntent();
        md= (Man_data) intent.getSerializableExtra("man_data");
        getSchedList();
    }

    public void onSum(View v) {
        Intent intent = new Intent(MySchedTotalListActivity.this, SumActivity.class);
        intent.putExtra("man_data", md);
        startActivity(intent);

    }

    public void onMainClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("man_data", md );
        startActivity(intent);
    }
    public void onLogoutClicked(View v) {
        Intent intent = new Intent(this, EntranceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void getSchedList() {
        ListView listView = (ListView) findViewById(R.id.list);
        SchedListAdapter_base adapter = Schedule.getList(this, md.name);
        listView.setAdapter(adapter);

    }
}

class SchedListAdapter_base extends BaseAdapter {

    Context context;
    ArrayList<Sched_data> items = new ArrayList<Sched_data>();

    public SchedListAdapter_base(Context c, ArrayList<Sched_data> it) {
        context = c;
        items = it;
    }

    @Override
    public int getCount() {
        return items.size();

    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.my_sched_list, null);

        }
        Sched_data ld = (Sched_data) items.get(i);

        ((TextView) convertView.findViewById(R.id.title)).setText(ld.title);

        ((TextView) convertView.findViewById(R.id._id)).setText(ld._id + "");

        return convertView;

    }

    public void addItem(Sched_data item) {
        items.add(item);
    }
}