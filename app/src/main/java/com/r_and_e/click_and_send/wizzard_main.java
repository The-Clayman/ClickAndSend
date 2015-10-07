package com.r_and_e.click_and_send;

import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.Vector;

public class wizzard_main extends FragmentActivity {
    private PagerAdapter mPagerAdapter;
    public static Messenger messenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        Intent i = getIntent();
        if (null != i) {
            messenger = ((Messenger) i.getExtras().get("messenger"));
        }
        initialisePaging();
    }
    private void initialisePaging() {
        // TODO Auto-generated method stub
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this,Fragment1.class.getName()));
        fragments.add(Fragment.instantiate(this,Fragment2.class.getName()));
        fragments.add(Fragment.instantiate(this,Fragment3.class.getName()));
        fragments.add(Fragment.instantiate(this,Fragment4.class.getName()));
        fragments.add(Fragment.instantiate(this,Fragment5.class.getName()));
        fragments.add(Fragment.instantiate(this,Fragment6.class.getName()));
        fragments.add(Fragment.instantiate(this, Fragment7.class.getName()));
        fragments.add(Fragment.instantiate(this, Fragment8.class.getName()));
        mPagerAdapter =new PagerAdapter(this.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) findViewById(R.id.view_page_example);
        pager.setAdapter(mPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wizzard_main, menu);
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
    public static void sendTohandler(int what , int arg1 , int arg2 ,String obj){
        Message msg = new Message();
        msg.obj = obj; msg.what = what; msg.arg1 = arg1;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }



    }
}
