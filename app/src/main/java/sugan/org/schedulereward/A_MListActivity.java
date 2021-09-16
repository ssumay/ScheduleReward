package sugan.org.schedulereward;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by eunsoo on 2017-11-03.
 * updated by eunsoo on summer 2021
 */

public class A_MListActivity extends AppCompatActivity {
    private AlertDialog dialog;
    LinearLayout linear;
    EditText name;
    EditText login;
    EditText per_score;
    EditText rew_desc;
    boolean inp_n =false;
    boolean inp_l =false;
    private  Uri mImageCaptureUri;
    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int CROP_FROM_IMAGE = 2;
    String img;
    ImageView ch_img;
    TextView save;
    int mode;   // 0 - newman, 1 - modify
    String m_name;
    String from;

    Man_data admin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if(from!=null && (from.equals("schedA") || from.equals("couponA"))) {
            onNewMan(null);
        }
        else {
            setContentView(R.layout.activity_a_man_list);

            getManList();
        }

        admin = (Man_data)intent.getSerializableExtra("man_data");

        //.setIcon(R.drawable.androboy)
    }

    public void onSaveMan(View v) {
/*
        Log.i("per_score",per_score.getText().toString() + "-" + rew_desc.getText().toString() );


        if(!checkLoginId()){
            Toast.makeText(this, R.string.check_name, Toast.LENGTH_LONG).show();
            name.setText("");
            name.setFocusable(true);
            return;
        }
        if(!(per_score.getText().toString().equals("") && rew_desc.getText().toString().equals(""))) {
            Log.i("aaa", "aaaaa");
            Toast.makeText(this, R.string.input_rew, Toast.LENGTH_SHORT).show();
            //rew_desc.setFocusedByDefault(true);
            return;
        }
*/
        SchedDBHelper sHelper= new SchedDBHelper(this);
        SQLiteDatabase db = sHelper.getWritableDatabase();
        String i_img;
        String score_s;
        String reward_s;

        if(img==null)  i_img="null"; else i_img= "'" + img + "'";
       /* score_s = per_score.getText().toString();
        reward_s = "'" + rew_desc.getText().toString() + "'" ;
        if(score_s.equals("")) {
            score_s = "null";
            reward_s = "null";
        }
        */
        String sql;
        if(mode == 0)

            sql = "insert into man ( name, pwd, pwd_on, img ) values ('" +
                    name.getText().toString() +"', '1111', 0 , " + i_img +" )";

        else
            sql = "update man set  img= "+ i_img+ " where name='" + m_name + "'";
        try {
            Log.i("sql", sql);
            db.execSQL(sql);

            if (from == null) {  //스케줄페이지에서 온 경우 토스트메시지를 보이지 않는다. 저장메시지가 혼동을 줄 수 있으므로
                if (mode == 0)
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();

                else Toast.makeText(this, R.string.modified, Toast.LENGTH_SHORT).show();
            }
            img = null;
            dialog.dismiss();
            if(from!=null && (from.equals("schedA") || from.equals("couponA"))) {
                finish();

            }
            else
                getManList();

        }catch (Exception e){
            Log.i("exception", e.getMessage() + " ");
            Toast.makeText(this, R.string.check_name, Toast.LENGTH_LONG).show();
            //name.setText("");
            name.setFocusable(true);
        }finally {
            sHelper.close();

        }

    }



    public void onDialogCancel(View v) {
        dialog.dismiss();
    }
    public void onCameraClicked(View v) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + System.currentTimeMillis() + ".jpg";

        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        Log.i("mImageCaptureUri = " , mImageCaptureUri.getPath());


        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    public void onAlbumClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    public void  onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!= RESULT_OK) {
            return;
        }

        switch(requestCode)
        {
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                Log.i("pick_from_album = " , mImageCaptureUri.getPath().toString());


                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 180);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 9);
                intent.putExtra("aspectY", 10);
                intent.putExtra("scale", true);

                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);

            }
            case PICK_FROM_CAMERA: {
                Log.i("pick_from_camera  " , mImageCaptureUri.getPath().toString());




                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 180);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 9);
                intent.putExtra("aspectY", 10);
                intent.putExtra("scale", true);

                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
/*
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(mImageCaptureUri.getPath().toString(), options);
                ch_img.setImageBitmap(bitmap);
                */
                break;

            }
            case CROP_FROM_IMAGE: {
                Bundle extras = data.getExtras();
                img = System.currentTimeMillis()+"";


                //String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                //        + "/scheduleReward/" + img + ".jpg";
                //String filePath = "/storage/Android/data/data/sugan.org.schedulereward/"+ img + ".jpg";
                String filePath = getCacheDir()+"/" + img + ".jpg";
                Log.i("CROP_FROM_IMAGE", filePath + " ");
                if(extras != null){
                    Bitmap photo = extras.getParcelable("data");
                    Log.i("photo", photo + " ");
                    ch_img.setImageBitmap(photo);

                    Util.storeCropImage(this, photo, filePath);

                }

                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()) {
                    f.delete();
                    Log.i("f.exist()", "f.delete()");
                }
                else {

                    Log.i("f.exist() not", "aaa");
                }


            }
        }
    }


    public void onName(View v) {
        mode = 1;

        m_name = ((TextView)v).getText().toString();
        Man_data md = Man.getManData( m_name, this);

       // if(dialog==null)
            getMDiaLayout(R.string.mod_man);
       // else {
        //    dialog.setTitle(R.string.mod_man);
        //    dialog.show();
       // }
        name.setText(md.name);
        Man.setImage(md.img, ch_img, this);
        img = md.img;
       // if(md.per_score==0)  per_score.setText("");
       // else  per_score.setText(md.per_score+"");
        save.setText(R.string.modify);
    }

    public void getMDiaLayout(int title_res){
        linear = (LinearLayout)View.inflate(this, R.layout.new_man, null);
        name = (EditText)linear.findViewById(R.id.name);
        if(mode!=0) name.setFocusable(false);

        ch_img = linear.findViewById(R.id.ch_img);
        login = (EditText)linear.findViewById(R.id.login);
       // per_score = (EditText)linear.findViewById(R.id.per_score);
        //rew_desc = (EditText)linear.findViewById(R.id.rew_desc);
        //ch_img = linear.findViewById(R.id.ch_img);
        //Log.i("name", "" + name.getText().toString()+" ");
        save = (TextView)linear.findViewById(R.id.save);

        TextWatcher nameWatch =  new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length()>0) {
                    String st = s.toString();
                    String ts=st.trim();
                    if(!ts.equals(st))
                        name.setText(ts);
                    else inp_n = true;
                    if(inp_n)// if(inp_n &&inp_l)
                        save.setVisibility(View.VISIBLE);

                }else {inp_n = false; save.setVisibility(View.GONE);          }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        TextWatcher logWatch =  new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length()>0) {
                    String st = s.toString();
                    String ts=st.trim();
                    if(!ts.equals(st))
                        login.setText(ts);
                    else inp_l = true;
                    if(inp_n &&inp_l)
                        save.setVisibility(View.VISIBLE);



                }else {inp_l = false; save.setVisibility(View.GONE);}


            }



            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        name.addTextChangedListener(nameWatch);
       // login.addTextChangedListener(logWatch);

        dialog = new AlertDialog.Builder(this)
                .setView(linear)
                .setTitle(title_res)
                .show();


    }
    public void onNewMan(View v){
        mode = 0;
        img = null;
      //  if(dialog==null) {Log.i("dialog", "null");
        getMDiaLayout(R.string.input_man); //}

       // else {
        //    dialog.setTitle(R.string.input_man);
        //    dialog.show();
            name.setText("");
            ch_img.setImageResource(R.drawable.singer2);
            login.setText("");
            name.requestFocus();

            //per_score.setText("");
           // rew_desc.setText("");

       // }
        save.setText(R.string.save);

    }
    public void onHomeClicked(View v){
        Intent intent = new Intent(this, AdminActivity.class);
        intent.putExtra("man_data", admin);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void onDelMan(final View v) {
        new AlertDialog.Builder(this).setMessage(R.string.del_man).setPositiveButton(R.string.y,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int b) {
                        String s_id = ((TextView)(((LinearLayout)v.getParent()).findViewById(R.id.name))).getText().toString();

                        if(Man.delMan(s_id, A_MListActivity.this)){
                            Toast.makeText(A_MListActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(A_MListActivity.this, R.string.fail, Toast.LENGTH_SHORT).show();

                        getManList();
                    }
                }).setNegativeButton(R.string.n, null)
                .show();


    }
/*
    public void onSchedTitleClicked(View v) {
        String s_id = ((TextView)(((LinearLayout)v.getParent()).findViewById(R.id._id))).getText().toString();
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("s_id", s_id);
        startActivity(intent);
    }
*/
    public void onDelSc(final View v) {
        new AlertDialog.Builder(this).setMessage(R.string.del_ms).setPositiveButton(R.string.y,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int b) {
                        String s_id = ((TextView)(((LinearLayout)v.getParent().getParent()).findViewById(R.id._id))).getText().toString();
                        String m_id = ((TextView)(((LinearLayout)v.getParent().getParent()).findViewById(R.id.m_name))).getText().toString();

                        if(Schedule.delMSc(m_id, s_id, A_MListActivity.this))
                            Toast.makeText(A_MListActivity.this, R.string.deleted_sch, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(A_MListActivity.this, R.string.fail, Toast.LENGTH_SHORT).show();

                        getManList();
                    }
                }).setNegativeButton(R.string.n, null)
                .show();


        //getSchedList();

    }
    public void getManList() {
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.list);
        ManListAdapter adapter = Man.getList(this);
        listView.setAdapter(adapter);

    }
}


class ManListAdapter extends BaseExpandableListAdapter {
    Context context;
    //ArrayList<SchedItem>  items = new ArrayList<SchedItem>();

    private ArrayList<Man_data> groups;

    private ArrayList<ArrayList<Sched_data>> children;

    public ManListAdapter(Context context, ArrayList<Man_data> groups,
                            ArrayList<ArrayList<Sched_data>> children){

        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public ArrayList<Sched_data> getChildList(int groupPostion)    {
        return children.get(groupPostion);
    }



    @Override

    public Object getChild(int groupPosition, int childPosition)    {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)  {
        return childPosition;
    }
    @Override

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        if (convertView == null)  {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sched_list, null);

        }
        Sched_data d = (Sched_data) getChild(groupPosition, childPosition);

        ((TextView)convertView.findViewById(R.id.title)).setText(d.title);

        ((TextView)convertView.findViewById(R.id._id)).setText(d._id+"");

        ((TextView)convertView.findViewById(R.id.m_name)).setText(((Man_data)getGroup(groupPosition)).name);
        //if(childPosition%2==1) convertView.setBackgroundColor(0x208f8c8f);

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

    public long getGroupId(int groupPosition)

    {

        return groupPosition;

    }



    // 그룹 뷰 반환. 그룹 뷰의 레이아웃을 load

    @Override

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,

                             ViewGroup parent)

    {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.man_list_group, null);
        }
        Man_data d = (Man_data) getGroup(groupPosition);
        ((TextView)convertView.findViewById(R.id.name)).setText(d.name);
        ImageView img = (ImageView) convertView.findViewById(R.id.img);
        Man.setImage(d.img, img, context);
        return convertView;

    }



    @Override

    public boolean hasStableIds()

    {

        return true;

    }



    @Override

    public boolean isChildSelectable(int arg0, int arg1)

    {

        return true;

    }

}
