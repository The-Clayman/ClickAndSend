package Obj;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.roy.clicksend.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by roy on 4/1/15.
 */
public class Function implements Serializable {
    final public static String fileName = "save";



    public static void saveObj(userData ud , Context context) {//"myarray.ser"
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(ud);
            os.close();
            fos.close();
        }
        catch (Exception e){

        }
    }
    public static userData LoadObj (Context context) {
        userData obj;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            obj = (userData) is.readObject();
            is.close();
            fis.close();

        }
        catch (Exception e){
            obj = new userData();
        }
        return obj;
    }
    public static Profile deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        Profile ans = (Profile) o.readObject();
        if (ans.filesIndexes.size()>0) {
            for (int i = 0; i < ans.filesIndexes.size();i++) {
                ans.dataList.get(ans.filesIndexes.get(i)).deSerializeFile();

            }
        }
        return ans;
    }
}