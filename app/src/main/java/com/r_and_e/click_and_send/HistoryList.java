package com.r_and_e.click_and_send;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import Obj.Function;
import Obj.userData;


public class HistoryList extends ActionBarActivity implements Serializable {



    userData ud1;
    ListView lv;
    MyCustomListAdapter customListAdapter;
    ArrayList<String> myList;
    Messenger messenger;
    static int FromeHistory = 2;




    //tag compunent
    private static final String TAG = "HistoryList";
    private static final boolean D = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        Intent i = getIntent();
        messenger =((Messenger) i.getExtras().get("messenger"));
        ud1 = ((MyData) this.getApplication()).getUd();
      //  userData ud = Function.LoadObj(getApplicationContext());
        if (MainActivity.receivedProfileTrans != null){
            ud1.ReceivedList.addProrile(MainActivity.receivedProfileTrans);
            MainActivity.receivedProfileTrans = null;
            ((MyData) getApplication()).setData(ud1);//saving userData to memory
            Function.saveObj(ud1, getApplicationContext());

        }


        lv=(ListView)findViewById(R.id.allHistory);
        myList =ud1.getProfileHistoryTitles();






        customListAdapter = new MyCustomListAdapter(this,R.layout.activity_history_list,myList);
        lv.setAdapter(customListAdapter);

    }
    public void MoveToHistoryProfileView(int index){
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra("History",""+index);
        intent.putExtra("Name",  "-2");
        intent.putExtra("messenger", messenger);
        startActivity(intent);
        finish();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit__profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void MoveToClickAndSend(int index){
        Intent intent = new Intent(this, ClickAndSend.class);
        intent.putExtra("Name", "" + index);
        intent.putExtra("messenger", messenger);
        intent.putExtra("whereFrom" , ""+FromeHistory);

        startActivity(intent);
        finish();
    }










    //*********************inner class start**********************************

    class MyCustomListAdapter extends ArrayAdapter<String> implements android.view.View.OnClickListener
    {
        ArrayList<String> mItems ;

        String g;
        TextView TitleName;
        ImageButton delete , send;


        public MyCustomListAdapter( HistoryList theActivity, int viewResourceId, ArrayList<String> objects)
        {
            super((Context) theActivity, viewResourceId, objects);
            mItems =ud1.getProfileHistoryTitles();
            g="";
        }

        @Override
        public int getCount()
        {
            return mItems.size();
        }
        @Override
        public String getItem(int position)
        {
            return mItems.get(position);
        }
        @Override
        public int getPosition(String item)
        {
            return mItems.indexOf(item);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent)
        {

            String title = mItems.get(position);





            LayoutInflater inflater = getLayoutInflater();
            View row=inflater.inflate(R.layout.activity_history_list__row, parent, false);

            TitleName = (TextView) row.findViewById(R.id.history_row_title);
            TitleName.setText(title);
            TitleName.setOnClickListener(this);
            TitleName.setTag(title);
            delete=(ImageButton)row.findViewById(R.id.delete_history_profile);
            delete.setFocusableInTouchMode(false);
            delete.setFocusable(false);
            delete.setOnClickListener(this);
            delete.setTag(title);

            send=(ImageButton)row.findViewById(R.id.history_profile_send);
            send.setFocusableInTouchMode(false);
            send.setFocusable(false);
            send.setOnClickListener(this);
            send.setTag(title);




            return row;
        }

        @Override
        public void onClick(View v) {
            final String entry = (String) v.getTag();
            final int pos = getPosition(entry);

            //      int s=entry.getCount();
            switch(v.getId()){

                case R.id.history_row_title:
                    MoveToHistoryProfileView(pos);
                    break;
                case R.id.delete_history_profile:

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setMessage("Delete Profile "+entry.toString()+" ? ");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            mItems.remove(entry);
                            ud1.ReceivedList.removeProfile(pos);
                            Function.saveObj(ud1, getApplicationContext());
                            notifyDataSetChanged();
                        }
                    });
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();

                    break;
                case R.id.history_profile_send:
                    MoveToClickAndSend(pos);
                    break;
            }
        }
    }

    //******************************************************

    @Override
    public void onBackPressed() {

            Intent i = new Intent(this, ProfilesPage.class);
            i.putExtra("messenger" , messenger);
            startActivity(i);
            finish();
    }

}
