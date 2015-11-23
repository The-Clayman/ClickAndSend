package com.r_and_e.click_send;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import Obj.Profile;
import Obj.userData;

public class ClickAndSend extends Activity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /// Debugging
    public static ClickAndSend classRef = null;
    Set<BluetoothDevice> pairedDevices;
    userData ud1;
    static ArrayList<Uri> files2Send = null;

    ListView lv,lv2;
    public static MyCustomListAdapter customListAdapter=null,customListAdapter2=null;
    ArrayList <String> myList, myList2;

    Button scan,refreshButton;
    Profile profile2send;
    private Messenger messenger;
  //  private BluetoothAdapter mBluetoothAdapter = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classRef = this;

       // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_click_and_send);
        ud1 = ((MyData) this.getApplication()).getUd();
        Intent i = getIntent();
        messenger =((Messenger) i.getExtras().get("messenger"));
        if (null != i) {

            int ProfileIndex = Integer.parseInt(i.getStringExtra("Name"));
            int whereFrom = Integer.parseInt(i.getStringExtra("whereFrom"));
            if (whereFrom == ProfilesPage.FromeProfilePage){
                profile2send = new Profile(ud1.UserList.getProfilebyIndex(ProfileIndex));
        }
        else{
                profile2send = new Profile(ud1.ReceivedList.getProfilebyIndex(ProfileIndex));
            }
        }

        myList = new ArrayList<String>();
        myList2 = new ArrayList<String>();
        lv=(ListView)findViewById(R.id.paired_devises_list);

        lv2=(ListView)findViewById(R.id.available_devises_list);
        scan = (Button)findViewById(R.id.scan_button);
        refreshButton = (Button)findViewById(R.id.refresh);


        customListAdapter = new MyCustomListAdapter(this,R.layout.activity_click_and_send,myList , true);
        lv.setAdapter(customListAdapter);

        customListAdapter2 = new MyCustomListAdapter(this,R.layout.activity_click_and_send,myList2 , false);
        lv2.setAdapter(customListAdapter2);
        if (MainActivity.mTransService.mAdapter == null) {
            // Device does not support Bluetooth

            sendTohandler(MainActivity.MESSAGE_TOAST ,-1,-1,"Bluetooth is not available");
        }
        if (!MainActivity.mTransService.mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);//%@$#%@#$%@#$%@$#%#$@%@$#^%@TWREVSDVFBTGW
            customListAdapter.refresh();
            customListAdapter2.refresh();


        }
        pairedDevices = MainActivity.mTransService.mAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                myList.add(device.getName() + "\n" + device.getAddress());
            }
        }
        customListAdapter.refresh();
        customListAdapter2.refresh();
        final Vector<Integer> filesLoc = profile2send.ContainsFiles();
        if (filesLoc.size() > 0){
            AlertDialog.Builder dialog = new AlertDialog.Builder(ClickAndSend.this);
            dialog.setTitle("Include attached files?");
            dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    files2Send = profile2send.extractFiles();
                    profile2send.removeAllFiles();


                    dialog.cancel();
                }
            });
            dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    profile2send.removeAllFiles();
                    dialog.cancel();

                }
            });
            dialog.show();

        }



        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);



    }

    public void onClick(View v){

          //  final String entry = (String) v.getTag();
        switch(v.getId()){
            case R.id.scan_button:

                // Indicate scanning in the title
                setProgressBarIndeterminateVisibility(true);
                setTitle("scanning for devices...");
                // If we're already discovering, stop it
                if (MainActivity.mTransService.mAdapter.isDiscovering()) {
                    MainActivity.mTransService.mAdapter.cancelDiscovery();
                }
                // Request discover from BluetoothAdapter
                MainActivity.mTransService.mAdapter.startDiscovery();
                sendTohandler(MainActivity.MESSAGE_TOAST, MainActivity.LongToastDuration, -1, "Scanning..\nAsk your friend to go visible");


            break;

            case R.id.refresh:
               refreshCustomListAdapters();
            break;



        }
        }
    public  void sendTohandler(int what , int arg1 , int arg2 ,String obj){
        Message msg = new Message();
        msg.obj = obj; msg.what = what; msg.arg1 = arg1;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }



    }





    private void sendProfile(String message) {


        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            MainActivity.mTransService.write(send);


        }
    }
    public void refreshCustomListAdapters(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }



    //*********************inner class start**********************************

    public class MyCustomListAdapter extends ArrayAdapter<String> implements android.view.View.OnClickListener
    {
        ArrayList<String> mItems ;
        boolean isPairdDivices;
        String g;
        TextView deviceNameBT;



        public MyCustomListAdapter( ClickAndSend theActivity, int viewResourceId, ArrayList<String> objects , boolean isPairdDivicesin)
        {
            super((Context) theActivity, viewResourceId, objects);
            mItems = objects;
            isPairdDivices=isPairdDivicesin;
            g="";
            deviceNameBT = (TextView)findViewById(R.id.device_name_text);

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
            View row=inflater.inflate(R.layout.click_and_send_row, parent, false);

            deviceNameBT = (TextView) row.findViewById(R.id.device_name_text);
            deviceNameBT.setText(title);
            deviceNameBT.setOnClickListener(this);
            deviceNameBT.setTag(title);

            return row;
        }

        @Override
        public void onClick(View v) {
            final String entry = (String) v.getTag();
            final int pos = getPosition(entry);

            switch(v.getId()) {
                case R.id.device_name_text:
                    if (isPairdDivices || !isPairdDivices) {

                        String address = mItems.get(pos);
                        String name = "";
                        StringTokenizer st = new StringTokenizer(address, "\n");
                        name = st.nextToken();
                        address = st.nextToken();
                        sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "Trying to connect "+name);
                        BluetoothDevice device = MainActivity.mTransService.mAdapter.getRemoteDevice(address);
                        byte[] SerializedProfile = null;
                        try {
                            SerializedProfile = profile2send.serialize();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        MainActivity.mTransService.connect(device, SerializedProfile, messenger, mItems.get(pos) , profile2send.getcontainsFiles() );
                    }

                    else{

                    }


                    break;
            }
//



        }


        public void refresh(){
            notifyDataSetChanged();
        }

    }
    public static void sendFilesInit(){
        classRef.sendFiles();
    }

    public  void sendFiles() {
        if (files2Send == null) return;
        Intent intent = null;
        intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("*/*");
        PackageManager pm = getPackageManager();
        List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

        if (appsList.size() > 0) {
            String packageName = null;
            String className = null;
            boolean found = false;

            for (ResolveInfo info : appsList) {
                packageName = info.activityInfo.packageName;
                if (packageName.equals("com.android.bluetooth")) {
                    className = info.activityInfo.name;
                    found = true;
                    break;// found
                }
            }
            if (!found) {
                sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "Bluetooth not found\nFailed to send files");
                // exit
            }
            // proceed
            intent.setClassName(packageName, className);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files2Send);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(intent);



            }
        }



    //******************************************************


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (!myList2.contains(device.getName() + "\n" + device.getAddress())) {
                    myList2.add(device.getName() + "\n" + device.getAddress());
                 //   Toast.makeText(getApplicationContext(), device.getName() + "\n" + device.getAddress()+" found", Toast.LENGTH_SHORT).show();
                    sendTohandler(MainActivity.MESSAGE_TOAST , -1,-1,device.getName() + "\n" + device.getAddress()+" found");
                   // MainActivity.ha

                }
                customListAdapter2.refresh();

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
// Make sure we're not doing discovery anymore
        if (MainActivity.mTransService.mAdapter != null) {
            MainActivity.mTransService.mAdapter.cancelDiscovery();
        }
// Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
//Log.d("SCANDEVICES", "Unregister Receiver!!!");
    }



    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ProfilesPage.class);
        i.putExtra("messenger" , messenger);
        startActivity(i);
        finish();

    }
}
