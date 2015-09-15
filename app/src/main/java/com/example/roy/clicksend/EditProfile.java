package com.example.roy.clicksend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

import Obj.Function;
import Obj.Profile;
import Obj.ProfileFile;
import Obj.dataString;
import Obj.userData;


public class EditProfile extends ActionBarActivity implements Serializable {
    TextView tv;
    userData ud1;
    Button save, editTitle, newField, addFile, exportProfile;
    ListView lv;
    ArrayList<dataString> myList;
    MyCustomListAdapter customListAdapter;
    String new_profile;
    Profile tempProfile;
    int ProfileIndex, HistoryProfileIndex;
    boolean isANewProfile;
    boolean fromHistory = false;
    Messenger messenger;
    private boolean isChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ud1 = ((MyData) this.getApplication()).getUd();
        setContentView(R.layout.activity_edit_profile);
        tv = (TextView) findViewById(R.id.Profile_Name);
        save = (Button) findViewById(R.id.save_button);
        editTitle = (Button) findViewById(R.id.edit_profile_name);
        newField = (Button) findViewById(R.id.new_field);
        addFile = (Button) findViewById(R.id.add_file);
        exportProfile = (Button) findViewById(R.id.export);
        tempProfile = new Profile();

        Intent i = getIntent();
        if (null != i) {
            messenger = ((Messenger) i.getExtras().get("messenger"));

            ProfileIndex = Integer.parseInt(i.getStringExtra("Name"));
            HistoryProfileIndex = Integer.parseInt(i.getStringExtra("History"));

            if (ProfileIndex == -1) {
                isANewProfile = true;
                //in case it's a new profile, aleady analized
                //     MainActivity.TempArrayList = new ArrayList<String>();
                myList = new ArrayList<dataString>();
                fromHistory = false;
                //    tempIndex = -1;


                //&&&& need to choose between Profile object and Array list #roy


            } else if (ProfileIndex == -2) {// from history list
                tempProfile = new Profile(ud1.ReceivedList.getProfilebyIndex(HistoryProfileIndex));//deep copy Constructor
                myList = tempProfile.getDataList();
                tv.setText(tempProfile.getName().toString());
                fromHistory = true;
                save.setVisibility(View.GONE);
                editTitle.setVisibility(View.GONE);
                newField.setVisibility(View.GONE);
                addFile.setVisibility(View.GONE);


            } else {
                isANewProfile = false;
                tempProfile = new Profile(ud1.UserList.getProfilebyIndex(ProfileIndex));//deep copy Constructor
                //in case it isn't a new Profile
                //     int pos = Integer.getInteger(ProfileIndex);
                myList = tempProfile.getDataList();

                tv.setText(tempProfile.getName().toString());
                fromHistory = false;

            }

        }

        //############### needs to add if case, for 2 possible cases. a new profile, and edit existting profile

        lv = (ListView) findViewById(R.id.all_data_string);
        customListAdapter = new MyCustomListAdapter(this, R.layout.activity_edit_profile, myList);
        lv.setAdapter(customListAdapter);


    }

    public void sendTohandler(int what, int arg1, int arg2, String obj) {
        Message msg = new Message();
        msg.obj = obj;
        msg.what = what;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void intentGenarator() {
        Intent i1 = new Intent(this, ProfilesPage.class);
        i1.putExtra("messenger", messenger);
        startActivity(i1);

        finish();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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

    public void onClickEditProfile(View v) {


        switch (v.getId()) {
            case R.id.edit_profile_name:
                isChanged = true;
                final EditText input = new EditText(EditProfile.this);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfile.this);
                alertDialog.setTitle("- Edit title -");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                String g = input.getEditableText().toString();
                                tv.setText(g);
                                tempProfile.setName(g);


                            }

                        });
                alertDialog.show();
                break;
            case R.id.save_button:


                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                dialog.setTitle("Save Profile");
                dialog.setMessage("Do you want to save " + tv.getText().toString() + " profile ? ");
                dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isANewProfile) {
                            ud1.addUserProfile(tempProfile);//saveing profile in ud

                        } else {
                            ud1.UserList.setProfileByIndex(ProfileIndex, tempProfile);
                        }
                        ((MyData) getApplication()).setData(ud1);
                        //  Function.saveObj(MainActivity.ud);


                        Function.saveObj(ud1, getApplicationContext());
                        intentGenarator();
                    }
                });
                dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        intentGenarator();
                    }
                });
                dialog.show();

                break;
            case R.id.new_field:
                isChanged = true;
                dataString newField = new dataString("________");
                tempProfile.addDataString(newField);
                if (isANewProfile) {
                    customListAdapter.mItems.add(newField);
                }
                customListAdapter.refresh();


                break;
            case R.id.add_file:
                isChanged = true;
                showFileChooser();


                break;
            case R.id.export:
                File dir = null;
                boolean ans = Environment.isExternalStorageEmulated();
                if (!ans) {
                 //   sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, tempProfile.getName() + ".txt\ncouldn't be created");
                    dir =Environment.getDownloadCacheDirectory();
                   // return;
                }
                else {

                    dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                }
                File succ = null;
                try {
                   // succ = tempProfile.WriteProfileToFile(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS));
                    succ = tempProfile.WriteProfileToFile(dir);
                }catch(Exception e){
                    sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "Storage not available");
                    return;
                }
                if (succ!= null) {
                    sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, succ.getName()+" was created");
                    openFolder(succ);
                } else {
                    sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, tempProfile.getName() + ".txt\ncouldn't be created");
                }

                break;

        }


    }

    public void openFolder(File resulteFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String [] splitpath = resulteFile.getAbsoluteFile().getPath().split("/");
        String path = "";
        for (int i = 0 ; i < splitpath.length-1 ; i++){
            path = path+'/'+splitpath[i];
        }
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri , "text/plain");
        startActivity(intent);


}
    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to add"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "Please install a File Manager.");

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                  //  Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = ProfileFile.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if (path == null) {
                        sendTohandler(MainActivity.MESSAGE_TOAST, -1, -1, "failed to choose a file");
                        return;
                    }
                    dataString ds = new dataString(uri);
                    tempProfile.addDataString(ds);
                   // Log.d(TAG, "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                   // customListAdapter.mItems.add(ds);

                    if(isANewProfile){
                        customListAdapter.mItems.add(ds);
                    }
                    customListAdapter.refresh();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    //**************************Start inner Class row*******************************



    public class MyCustomListAdapter extends ArrayAdapter<dataString> implements android.view.View.OnClickListener
    {
        ArrayList<dataString> mItems;
        boolean flag;
        String g;
        TextView info;
        Button DeleteInfo;
        String profileTitle=tv.toString();

        public MyCustomListAdapter( EditProfile theActivity, int viewResourceId, ArrayList<dataString> objects )
        {
            super((Context) theActivity, viewResourceId, objects);
            mItems =objects;

           // mItems.addAll(MainActivity.ud.getFieldsTitles(tempIndex));///#$%#$@%@#$%@need to get profile index from getExtra.
            info = (TextView)findViewById(R.id.info);
            DeleteInfo = (Button)findViewById(R.id.delete_field);

            flag=false;
            g="";

        }

        @Override
        public int getCount()
        {
            return mItems.size();
        }
        @Override
        public dataString getItem(int position) {
            return mItems.get(position);
        }
        @Override
        public int getPosition(dataString item) {
            return mItems.indexOf(item);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent)
        {

            dataString fieldName = mItems.get(position);
            LayoutInflater inflater = getLayoutInflater();
            View row=inflater.inflate(R.layout.edit_profile_row, parent, false);

            TextView fName = (TextView) row.findViewById(R.id.info);
            Button del=(Button)row.findViewById(R.id.delete_field);


            if (fromHistory){
                fName.setText(fieldName.getName());
                fName.setOnClickListener(this);
                fName.setTag(fieldName);
                fName.setTextSize(20);


                del.setFocusableInTouchMode(false);
                del.setFocusable(false);
               del.setVisibility(View.GONE);
                del.setOnClickListener(this);
                del.setTag(fieldName);

            }
            else{
                fName.setText(fieldName.getName());
                fName.setOnClickListener(this);
                fName.setTag(fieldName);

                del.setFocusableInTouchMode(false);
                del.setFocusable(false);
                del.setOnClickListener(this);
                del.setTag(fieldName);

            }

            return row;
        }

        @Override
        public void onClick(View v) {
            isChanged = true;
             final  dataString entry1 = (dataString) v.getTag();
            final int pos = getPosition(entry1);
           // int s=entry.getCount();
            switch(v.getId()){
                case R.id.info:
                    if (mItems.get(pos).CheckisAFile()){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(mItems.get(pos).getLine()), "image/*");
                        startActivity(intent);
                    }
                    else {
                        if (fromHistory && !mItems.get(pos).CheckisAFile()) return;
                        final EditText input = new EditText(EditProfile.this);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfile.this);
                        alertDialog.setTitle("- Edit field -");
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        alertDialog.setView(input);

                        alertDialog.setPositiveButton("save",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        g = input.getEditableText().toString();
                                        entry1.setLine(g);
                                        int ii = getPosition(entry1);
                                        getItem(ii).setLine(g);

                                        tempProfile.addDataByIndex(entry1, ii);
                                        mItems.set(ii, entry1);



                                    }

                                });

                        alertDialog.setNegativeButton("cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to execute after dialog
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.show();

                    }
                    break;
                case R.id.delete_field:
                    if(tempProfile.getDataList().size() > pos)
                        tempProfile.removeByIndex(pos);
                        if (isANewProfile) {
                             mItems.remove(pos);
                        }

                    notifyDataSetChanged();

                    break;

            }
        }
        public void refresh(){
            notifyDataSetChanged();
        }

    }
    //**************************End iner Class row*******************************


    @Override
    public void onBackPressed() {
        if (!fromHistory) {
            if (isChanged) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                dialog.setTitle("Save changes?");
                dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isANewProfile) {
                            ud1.addUserProfile(tempProfile);//saveing profile in ud

                        } else {
                            ud1.UserList.setProfileByIndex(ProfileIndex, tempProfile);
                        }
                        ((MyData) getApplication()).setData(ud1);
                        //  Function.saveObj(MainActivity.ud);

                        Function.saveObj(ud1, getApplicationContext());
                        intentGenarator();


                    }
                });
                dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        intentGenarator();
                    }
                });
                dialog.show();

            }
            else{
                intentGenarator();
            }


        }
        else{
            Intent i = new Intent(this, HistoryList.class);
            i.putExtra("messenger", messenger);
            startActivity(i);

            finish();
        }


    }
}
