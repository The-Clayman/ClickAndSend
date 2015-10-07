package Obj;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by roy on 4/1/15.
 */
public class Profile implements Serializable {

    int serial;
    String name;
    String date = "00/00/00";
    String owner = "defalt";
    ArrayList<dataString> dataList;
    Vector<Integer> filesIndexes = new Vector<Integer>();
    boolean containsFiles = false;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getcontainsFiles(){
        return containsFiles;
    }


    public Profile(ArrayList<dataString> dataList, String name, int serial) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        this.date = df.format(c.getTime());
        this.dataList = dataList;
        this.name = name;
        this.serial = serial;
    }

    public Profile(int serial, String name) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        this.date = df.format(c.getTime());
        this.serial = serial;
        this.name = name;
        this.dataList = new ArrayList<dataString>();

    }
    public Profile(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        this.date = df.format(c.getTime());
        this.serial = -1;
        this.name = "null";
        this.dataList= new ArrayList<dataString>();

    }
    public Profile(Profile p) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        this.date = df.format(c.getTime());
        this.serial = -1;
        this.name = p.name;
        this.dataList = new ArrayList<dataString>();
        this.filesIndexes = new Vector<Integer>(p.filesIndexes);
        for (int i = 0; i < p.dataList.size(); i++) {
            this.dataList.add(i, new dataString(p.dataList.get(i)));
        }

    }
    public void addDataString(dataString ds){
        this.dataList.add(ds);
        if (ds.CheckisAFile()){
            this.filesIndexes.add(dataList.size()-1);
            this.containsFiles = true;
        }
    }
    public void addDataByIndex(dataString ds , int index){
        if (index <= dataList.size()-1){

            this.dataList.set(index , ds);
            if (ds.CheckisAFile()) this.filesIndexes.add(index);
        }
        else{
            this.dataList.add(index, ds);
            if (ds.CheckisAFile()) this.filesIndexes.add(index);
        }


    }
    public void updateDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        this.date = df.format(c.getTime());

    }
    public void removeByIndex(int index){
        if (dataList.get(index).CheckisAFile()) {
            int i = filesIndexes.indexOf(index);
            filesIndexes.remove(i);
        }
       //

        this.dataList.remove(index);

    }

    public int getSerial() {
        return serial;
    }
    public void RemoveDataString(int index){
        this.dataList.remove(index);
    }

    public String getName() {
        return name+'\n'+date;
    }

    public String getNameOnly(){
        return name;
    }


    public ArrayList<dataString> getDataList() {
        return dataList;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataList(ArrayList<dataString> dataList) {
        this.dataList = dataList;
    }
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(this);
        Log.d("TAG",this.getName()+" gets serialized()");
        return b.toByteArray();
    }
    public Vector<Integer> ContainsFiles(){
        return filesIndexes;
    }
//    public void SerializeFiles() throws dataString.noFilesExeption {
//        for (int i = 0 ; i < this.filesIndexes.size() ; i++){
//            this.dataList.get(this.filesIndexes.get(i)).FileSerialize();
//        }

  //  }
    public void removeAllFiles() {
        // for(int i = 0 ; i < this.filesIndexes.size() ; i++){
        while (!this.filesIndexes.isEmpty()) {
            this.removeByIndex(this.filesIndexes.lastElement());
            this.containsFiles = false;
        }
    }


    public ArrayList<Uri> extractFiles(){
        ArrayList<Uri> ans = new ArrayList<Uri>();
        for(int i = 0 ; i < this.filesIndexes.size();i++) {
         //   ans.add(this.dataList.get(this.filesIndexes.get(i)).uri);
            String a = this.dataList.get(this.filesIndexes.get(i)).getLine();
            ans.add(Uri.parse(a));

        }

        return ans;
    }
    public File WriteProfileToFile(File Dir){
        String stringToFile = "";
        stringToFile = stringToFile+"Profile name: "+name+'\n';
        stringToFile = stringToFile+"Profile date: "+date+'\n';
        stringToFile = stringToFile+"Fields:\n\n";
        for(int i = 0 ; i<this.dataList.size();i++){
            if (dataList.get(i).CheckisAFile())  stringToFile = stringToFile+"File: ";
            stringToFile = stringToFile+this.dataList.get(i).getLine()+'\n';
        }
        String filename = "Profile_"+this.name.toString()+".txt";
        File file = new File(Dir ,filename);
        boolean isPresent = true;
//        if (!file.exists()){
//            isPresent = Dir.mkdir();
//        }
        if (isPresent) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                fos.write(stringToFile.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


            return file;
        }
        else{
            return null;
        }
    }



}
