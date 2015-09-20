package Obj;

import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by roy on 4/1/15.
 */
public class dataString implements Serializable {
    String line;
    byte[] serializeFile;
    private boolean isAFile = false;

    public dataString(String s){
        this.line = s;
        this.isAFile = false;

    }
    public  dataString(Uri uri){
        String path = uri.getPath();
        String toSSS = uri.toString();
        this.line = uri.toString();
        this.isAFile = true;
    }
    public dataString(dataString ds){
        this.setLine(ds.getLine());
        if (ds.isAFile){
            this.isAFile = true;
        }
    }
    public dataString(){

        this.line = "";
    }
    public void setLine(String line){
        this.line = line;
    }

    public String getLine(){
        return this.line;
    }
    public String toString(){
        return line;
    }
    public boolean CheckisAFile(){
        return isAFile;
    }
    public void deSerializeFile(){
        String fileoldPath = this.getLine();
        String arr[] = fileoldPath.split("/");
        String filename = arr[arr.length-1];
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(this.serializeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.line = filename;
        this.serializeFile = null;

    }
    public String getName(){
        if (this.isAFile){
            String[] ans = this.getLine().split("/");
            if (ans.length == 0) return this.getLine();
            return "File: "+ans[ans.length-1];
        }
        else return this.getLine();
    }
//    public void FileSerialize() throws noFilesExeption {
//        if (!this.CheckisAFile()){
//            throw new noFilesExeption("Files not found");
//        }
//        File file = new File(uri.getPath());
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_STREAM,uri);
//
//
//
//
//
//
//
//
//        byte[] b = new byte[(int) file.length()];
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            fileInputStream.read(b);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.serializeFile = b;
//    }
//    public class noFilesExeption extends Exception{
//        public noFilesExeption(String msg) {
//            super(msg);
//        }
//    }

}
