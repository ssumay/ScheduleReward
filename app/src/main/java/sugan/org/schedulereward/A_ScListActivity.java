package sugan.org.schedulereward;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-03.
 * updated by eunsoo on summer 2021
 */

public class A_ScListActivity extends AppCompatActivity {

    String s_id;   //for delete
    String _id;  //스케줄 보기 및 수정 버튼 클릭시 에러 처리를 위해
    Man_data admin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_sched_list);

        Intent intent = getIntent();
        admin = (Man_data)intent.getSerializableExtra("man_data");

        getSchedList();
    }
    public void onSched(View v) {  //스케줄 보기 및 수정 버튼
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("s_id", _id);
        startActivity(intent);
    }
    public void onNewSched(View v){
        //Intent intent = new Intent(this, NewSchedActivity.class);
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("page", "newSc");
        startActivity(intent);
    }
    public void onHome(View v){
        Intent intent = new Intent(this, AdminActivity.class);
        intent.putExtra("man_data", admin);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void onDelSc(View v) {
        s_id = ((TextView)(((RelativeLayout)v.getParent()).findViewById(R.id._id))).getText().toString();
        new AlertDialog.Builder(this).setMessage(R.string.del_sc).setPositiveButton(R.string.y,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int b) {
                        Schedule.delSched(A_ScListActivity.this, s_id);
                        getSchedList();
                    }
                }).setNegativeButton(R.string.n, null)
                .show();
       // Schedule.delSched(s_id, this);
       // getSchedList();

    }

    public void onSchedTitleClicked(View v) {
        String s_id = ((TextView)(((RelativeLayout)v.getParent()).findViewById(R.id._id))).getText().toString();
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("s_id", s_id);
        startActivity(intent);
        //Intent intent = new Intent(this, Admin_SchedActivity.class);
        //startActivity(intent);
    }

    public void getSchedList() {
        ExpandableListView listView = findViewById(R.id.list);
        SchedListAdapter adapter = Schedule.getList(this);
        listView.setAdapter(adapter);
        if(adapter != null){
            _id = adapter.groups.get(0)._id + "";
            findViewById(R.id.sched_mod).setVisibility(View.VISIBLE);
        }
    }
}


class SchedListAdapter extends BaseExpandableListAdapter {

    Context context;

     ArrayList<Sched_data> groups;

    private ArrayList<ArrayList<Linked_sched_data>> children;

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    public void registerDataSetObserver(DataSetObserver observer){}

    public void unregisterDataSetObserver(DataSetObserver observer){}

    public  long getGroupId(int a){return 0;}

    public boolean hasStableIds(){return false; }
    public long getChildId(int a, int b){return 0;}
    public boolean isChildSelectable(int a, int b){return false;}
    public boolean  areAllItemsEnabled(){return false;}

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    public SchedListAdapter(Context context, ArrayList<Sched_data> groups,
                            ArrayList<ArrayList<Linked_sched_data>> children){

        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    public ArrayList<Linked_sched_data> getChildList(int groupPostion)    {
        return children.get(groupPostion);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition)    {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        if (convertView == null)  {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.link_list, null);

        }
        Linked_sched_data ld = (Linked_sched_data) getChild(groupPosition, childPosition);

        ((TextView)convertView.findViewById(R.id.note)).setText(ld.link_note);

        ((TextView)convertView.findViewById(R.id.s_id)).setText(ld.s_id+"");

        ((TextView)convertView.findViewById(R.id.seq)).setText(ld.seq+"");

        return convertView;

    }



    @Override

    public int getChildrenCount(int groupPosition)   {
        return children.get(groupPosition).size();

    }



    @Override

    public Object getGroup(int groupPosition)  {
        return groups.get(groupPosition);
    }



    @Override

    public int getGroupCount() {
        return groups.size();
    }

    @Override

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,

                             ViewGroup parent)

    {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sched_list, null);
        }
        Sched_data sd = (Sched_data) getGroup(groupPosition);
        ((TextView)convertView.findViewById(R.id.title)).setText(sd.title);

        ((TextView)convertView.findViewById(R.id._id)).setText(sd._id+"");

        return convertView;

    }



}
