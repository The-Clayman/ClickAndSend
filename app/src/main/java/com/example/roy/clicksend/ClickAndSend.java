package com.example.roy.clicksend;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;


public class ClickAndSend extends ActionBarActivity {

    private static final int Discover_Duration = 300;
    private static final int Request_blu = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_and_send);
    }
    public void sendViaBluetooth(View v){
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (bta == null){
            Toast.makeText(this,"Bluetooth is no supported",Toast.LENGTH_LONG).show();
        }
        else{
            enableBluetooth();
        }
    }
    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION , Discover_Duration);
        startActivityForResult(discoveryIntent,Request_blu);

    }
    protected void onActivityResult(int requestCode , int resultCode, Intent data){
        if (resultCode == Discover_Duration && requestCode == Request_blu){
            Intent intent = new Intent();
            PackageManager pm = getPackageManager();
            List<ResolveInfo> appList = pm.queryIntentActivities(intent , 0);
            if (appList.size() > 0){
                String packageName = null;
                String className = null;
                boolean found = false;
                for(ResolveInfo info : appList){
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")){
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }

                }
                if (!found){
                    Toast.makeText(this,"Bluetooth haven't been found",Toast.LENGTH_LONG).show();
                }
                else{
                    intent.setClassName(packageName,className);
                    startActivity(intent);
                }
            }

        }
        else{
            Toast.makeText(this,"Bluetooth is cancelled",Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_click_and_send, menu);
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
}
