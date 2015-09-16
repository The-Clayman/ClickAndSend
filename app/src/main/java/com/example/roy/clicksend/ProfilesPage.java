package com.example.roy.clicksend;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Handler;

import Obj.Function;
import Obj.Transmission;
import Obj.userData;


public class ProfilesPage extends ActionBarActivity implements Serializable {

    public static String readMessage="";

    userData ud1;
    boolean flag;
    public ImageButton newProfile , ListenBottun , SetVisibleButton;
    ListView lv;
    MyCustomListAdapter customListAdapter;
    ArrayList <String> myList;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "Profile page";
    private static final boolean D = true;
    private  Handler handler;
    private Messenger messenger;

    static Context contextProfilePage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flag = true;
        ud1 = ((MyData) this.getApplication()).getUd();
        Intent i = getIntent();
        messenger =((Messenger) i.getExtras().get("messenger"));

        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        try {
            registerReceiver(mReceiver, filter);
        }
        catch(Exception e){
            sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "Couldn't complete task");
        }
        setContentView(R.layout.profiles_page);
        newProfile=(ImageButton)findViewById(R.id.add_new_profile);
        ListenBottun=(ImageButton)findViewById(R.id.listen_button);
        if (MainActivity.isListen){
            sendTohandler(MainActivity.StateListenChange, MainActivity.SetStateListenOn, -1, null);
        }
        SetVisibleButton = (ImageButton)findViewById(R.id.set_visible);
        lv=(ListView)findViewById(R.id.allProfiles);
        myList =ud1.getProfileTitles();
        contextProfilePage = getApplicationContext();


        if (MainActivity.mTransService != null) {
            if (MainActivity.mTransService.getmAcceptThread() != null) {
                if (D) Log.e(TAG, "++ onCeate, acceptThead is running ++");
                //ListenBottun.setText("Listening");
               // ListenBottun = (ImageButton)findViewById(R.id.listen_button);
               // ListenBottun.setImageResource(R.mipmap.listen2);
               // sendTohandler(MainActivity.StateListenChange , -1 ,-1,null);

            }
        }





        customListAdapter = new MyCustomListAdapter(this,R.layout.profiles_page,myList);
        lv.setAdapter(customListAdapter);
        MainActivity.listenButton = ListenBottun;
        MainActivity.visibleBottun = SetVisibleButton;
        if (MainActivity.isVisible){
            SetVisibleButton.setImageResource(R.mipmap.visib);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode != RESULT_CANCELED)
            {
                sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "You went Visible");
                SetVisibleButton.setImageResource(R.mipmap.visib);
                MainActivity.isVisible = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SetVisibleButton.setImageResource(R.mipmap.invisib);
                        MainActivity.isVisible = false;
                    }
                }, 300000);
            }
        }
    }

    public  void sendTohandler(int what , int arg1 , int arg2 ,String obj){
        Message msg = new Message();
        msg.obj = obj; msg.what = what;
        msg.arg1 = arg1;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }



    }
    public void MoveToHistoryList( int index){
        Intent intent = new Intent(this, HistoryList.class);
        intent.putExtra("Name",""+index);
        intent.putExtra("messenger", messenger);
        startActivity(intent);
        finish();
    }




    public void MoveToEditProfile( int index){
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra("Name",""+index);
        intent.putExtra("History","-1");
        intent.putExtra("messenger", messenger);

        startActivity(intent);
        finish();

    }
    public void MoveToClickAndSend(int index){
        Intent intent = new Intent(this, ClickAndSend.class);
        intent.putExtra("Name", "" + index);
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
    public void onClick(View v) {
        flag = true;

        switch (v.getId()) {
            case R.id.add_new_profile:
                Intent i = new Intent(this, EditProfile.class);
                i.putExtra("Name", "-1");
                i.putExtra("History","-1");
                i.putExtra("messenger" , messenger);
                startActivity(i);
                finish();
                break;
            case R.id.history_button:
                MoveToHistoryList(-1);


                break;
            case R.id.set_visible:
                if (MainActivity.isVisible){
                    if (MainActivity.mTransService.mAdapter!= null)
                    MainActivity.mTransService.mAdapter.cancelDiscovery();
                    SetVisibleButton.setImageResource(R.mipmap.invisib);
                    MainActivity.isVisible = false;
                    sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "You went Invisible");

                }
                else {
                    Intent discoverableIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    // startActivity(discoverableIntent);
                    startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT);
                    //   Toast.makeText(getApplicationContext() , "Set Visible",Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.listen_button:
                MainActivity.mTransService.setState(Transmission.STATE_LISTEN);
              //  if (this.ListenBottun.getText().toString().equals("Listening")) {
                if (MainActivity.isListen) {
                 //   MainActivity.mTransService.cancellAcceptThread();
                    MainActivity.mTransService.stop();
                    if (D) Log.e(TAG, "++ Listening pressed, stop listening ++");
//
                    sendTohandler(MainActivity.StateListenChange , MainActivity.SetStateListenOff ,-1,null );
                    MainActivity.mTransService.setState(MainActivity.STATE_NONE);
                    return;
                }

                else if (MainActivity.mTransService.mAdapter == null) {
                 //   Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                    sendTohandler(MainActivity.MESSAGE_TOAST , -1,-1,"Bluetooth is not available");
                    finish();
                    return;
                }
                if (D) Log.e(TAG, "++ Listening pressed ++");
                if (!MainActivity.mTransService.mAdapter.isEnabled()) {
                    // if Bluetooth isn't enabled
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    return;
                }
                setupTrans();
                sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "Yout device name is:\n" + MainActivity.mTransService.getDiviceName());
               // this.ListenBottun.setText("Listening");
                sendTohandler(MainActivity.StateListenChange, MainActivity.SetStateListenOn, -1, null);
                MainActivity.mTransService.setState(Transmission.STATE_LISTEN);
           //     refreshAll();
            //    ListenBottun.setImageResource(R.mipmap.listen2);

                    break;
                }

        }
//    public void changeListenButonText(String state){
//        this.ListenBottun.setText(state);
//    }
    public  void setupTrans(){
        Log.d(TAG, "setupTrance()");
        if (MainActivity.mTransService.getmAcceptThread() != null) {
            MainActivity.mTransService.cancellAcceptThread();
        }
       // MainActivity.mTransService.setmAcceptThread(null);
        MainActivity.mTransService.start();
        //ListenBottun.setText("Listening");
       // this.changeListenButonText("listening");




    }








    //*********************inner class start**********************************

     class MyCustomListAdapter extends ArrayAdapter<String> implements android.view.View.OnClickListener
    {
        ArrayList<String> mItems ;
        boolean flag;
        String g;
        TextView TitleName;
        ImageButton edit,send;


        public MyCustomListAdapter( ProfilesPage theActivity, int viewResourceId, ArrayList<String> objects)
        {
            super((Context) theActivity, viewResourceId, objects);
            mItems =ud1.getProfileTitles();
            flag=false;
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
            View row=inflater.inflate(R.layout.profile_page_row, parent, false);


            TitleName = (TextView) row.findViewById(R.id.profile_name);
            TitleName.setText(title);
            TitleName.setOnClickListener(this);
            TitleName.setTag(title);

            edit=(ImageButton)row.findViewById(R.id.delete_profile);
            edit.setFocusableInTouchMode(false);
            edit.setFocusable(false);
            edit.setOnClickListener(this);
            edit.setTag(title);

            send=(ImageButton)row.findViewById(R.id.edit_profile_Send);
            send.setFocusableInTouchMode(false);
            send.setFocusable(false);
            send.setOnClickListener(this);
            send.setTag(title);

            return row;
        }

        @Override
        public void onClick(View v) {
            flag = true;
            final String entry = (String) v.getTag();
            final int pos = getPosition(entry);

            //      int s=entry.getCount();
            switch(v.getId()){
                case R.id.delete_profile:

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setMessage("Delete Profile "+entry.toString()+" ? ");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            mItems.remove(entry);
                            ud1.UserList.removeProfile(pos);
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
                case R.id.profile_name:
                    MoveToEditProfile(pos);
                    break;
                case R.id.edit_profile_Send:
                    MoveToClickAndSend(pos);
                    break;


            }





        }




    }

    //******************************************************

    @Override
    public void onBackPressed() {
        if (flag) {
            sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "press back again to exit");
            flag = false;
        }
        else{
            flag = true;
            sendTohandler(MainActivity.FinishApp , -1,-1,"");
            finish();
           // System.exit(0);


        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        // setButtonText("Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //    setButtonText("Turning Bluetooth off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        //   setButtonText("Bluetooth on");
                        if (MainActivity.mTransService.getState() == MainActivity.STATE_LISTEN)
                            setupTrans();
                        if (ClickAndSend.customListAdapter != null && ClickAndSend.customListAdapter2 != null){
                        //    ClickAndSend.refreshCustomListAdapters();


                        }
                        // ProfilesPage.ListenBottun.setText("Listening");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //  setButtonText("Turning Bluetooth on...");
                        if (ClickAndSend.customListAdapter != null && ClickAndSend.customListAdapter2 != null) {
                        //    ClickAndSend.refreshCustomListAdapters();
                        }
                        break;
                }
            }
        }
    };

    public  void refreshAll(){
        notify();
    }

}
