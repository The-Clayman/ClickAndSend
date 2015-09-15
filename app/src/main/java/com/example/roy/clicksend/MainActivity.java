package com.example.roy.clicksend;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import java.io.Serializable;

import Obj.Function;
import Obj.Profile;
import Obj.Transmission;
import Obj.userData;


public class MainActivity extends ActionBarActivity  implements Serializable{
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int FinishApp = 6;
    public static final int ConnectedSeccesfuly = 7;
    public static final int proceed_To_Send_Files = 8;

    public static final int LongToastDuration = 2;

    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public static Transmission mTransService = null;
    public static Profile receivedProfileTrans = null;
    public static Button listenButton = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
            userData ud = Function.LoadObj(getApplicationContext());
            ((MyData) this.getApplication()).setData(ud);
        mTransService = new Transmission(this,handler);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, ProfilesPage.class);
                Messenger messenger = new Messenger(handler);
                i.putExtra("messenger", messenger);
                startActivity(i);

            }
        }, 3000);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){ //override
            switch (msg.what){
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch(msg.arg1){
                        case STATE_CONNECTED:
                            break;
                        case STATE_CONNECTING:
                            break;
                        case STATE_LISTEN:
                            break;
                        case STATE_NONE:
                            if (MainActivity.listenButton!=null){
                                MainActivity.listenButton.setText("Listen");
                            }

//                            if (ProfilesPage.)
//                               ProfilesPage.refreshAll();
                            break;

                    }


                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:
                    String ProfileName =(String) msg.obj;
                    Toast.makeText(getApplicationContext(), ProfileName+ " received!", Toast.LENGTH_SHORT).show();



                    break;
                case MESSAGE_TOAST:
                    switch(msg.arg1) {
                        case LongToastDuration:
                            Toast.makeText(getApplicationContext(), (String) msg.obj,
                                    Toast.LENGTH_LONG).show();

                            break;
                        default:
                        Toast.makeText(getApplicationContext(), (String) msg.obj,
                                Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case FinishApp:
                    finish();
                    System.exit(0);
                    break;
                case ConnectedSeccesfuly:
                    break;
                case proceed_To_Send_Files:
                    ClickAndSend.sendFilesInit();


            }
        }
    };



}
