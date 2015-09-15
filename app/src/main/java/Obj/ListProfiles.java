package Obj;

import android.widget.ArrayAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roy on 4/1/15.
 */
public class ListProfiles implements Serializable {
    public ArrayList<Profile> profiles;


    public ListProfiles(ArrayList<Profile> profiles ) {
        this.profiles = profiles;

    }
    public ListProfiles(){
        this.profiles = new ArrayList<Profile>();

    }
    public void addProrile(Profile profile){
        this.profiles.add(profile);
    }

    public void setProfileByIndex(int index,Profile profile){
        this.profiles.set(index , profile);
    }
    public void removeProfile(int index){
        this.profiles.remove(index);
    }
    public int size(){
        return this.profiles.size();
    }
    public ArrayList<Profile> getProfiles() {
        return profiles;
    }
    public Profile getProfilebyIndex(int index){
        Profile ans;
        ans = profiles.get(index);
        return ans;
    }
    public void setProfiles(ArrayList<Profile> profiles) {
        this.profiles = profiles;
    }
}
