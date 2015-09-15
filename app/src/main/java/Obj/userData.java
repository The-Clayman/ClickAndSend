package Obj;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roy on 4/1/15.
 */
public class userData implements Serializable {
    int Serial;
    String name;
    public ListProfiles UserList;
    public ListProfiles ReceivedList;

    public userData(int serial, String name) {
        Serial = serial;
        this.name = name;
        this.UserList = new ListProfiles();
        this.ReceivedList = new ListProfiles();
    }
    public userData(){
        Serial=-1;
        this.name="null";
        this.UserList = new ListProfiles();
        this.ReceivedList = new ListProfiles();
    }
    public ArrayList<String> getProfileTitles(){
        ArrayList<String> ans = new ArrayList<String>();
        for (int i = 0 ; i < userListSize() ; i++){
            ans.add(this.UserList.profiles.get(i).getName());
        }

        return ans;
    }
    public ArrayList<String> getProfileHistoryTitles(){
        ArrayList<String> ans = new ArrayList<String>();
        for (int i = 0 ; i < ReceivedListSize() ; i++){
            ans.add(this.ReceivedList.profiles.get(i).getName());
        }

        return ans;
    }

    public ArrayList<String> getFieldsTitles(int index){
        ArrayList<String> ans = new ArrayList<String>();
        for (int i = 0 ; i < this.UserList.getProfilebyIndex(index).dataList.size() ; i++) {
            ans.add(this.UserList.getProfilebyIndex(index).dataList.get(i).getLine());
        }
        return ans;
    }


    public int userListSize(){
        return this.UserList.size();
    }
    public int ReceivedListSize(){
        return this.ReceivedList.size();
    }


    public void addUserProfile(Profile p){
        this.UserList.addProrile(p);
    }
    public void addRecivedProfile(Profile p){
        this.ReceivedList.addProrile(p);
    }



}
