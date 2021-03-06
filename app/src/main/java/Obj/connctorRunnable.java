package Obj;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.r_and_e.click_and_send.MainActivity;

/**
 * Created by roy on 7/2/15.
 */

public class connctorRunnable implements Runnable {
    byte[] SerializedProfile = null;
    boolean containsFiles;
    Messenger messenger;
    String pos;

    public connctorRunnable(byte[] SerializedProfile, Messenger messenger , String pos , boolean containsFiles) {
        this.SerializedProfile = SerializedProfile;
        this.messenger = messenger;
        this.pos = pos;
        this.containsFiles = containsFiles;

    }

    public void run() {
        String success = "connected to";
        boolean writeCheck = false;
        long startTime = System.currentTimeMillis();
        while (!writeCheck) {
            writeCheck = MainActivity.mTransService.write(SerializedProfile);
            long estimatedTime = System.currentTimeMillis() - startTime;
            if (estimatedTime > 4000) {
                writeCheck = true;
                success = "send failed \ncould not connect to ";
                return;

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendTohandler(MainActivity.MESSAGE_TOAST , -1,-1,success+" "+pos);
        if (containsFiles == true) {
            sendTohandler(MainActivity.proceed_To_Send_Files, -1, -1, success + " " + pos);
        }
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
}
