package Obj;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;

import com.r_and_e.click_and_send.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by roy on 5/5/15.
 */
public class Transmission {
    private static final String TAG = "Transmition";
    private static final boolean D = true;

    userData ud1;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChat";

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Member fields
    public final BluetoothAdapter mAdapter;
    private final Handler mHandler;




    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private String deviceBTNAme = "NA";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public Transmission(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        deviceBTNAme = mAdapter.getName();
    }
    public String getDiviceName(){
        return this.deviceBTNAme;
    }
    public AcceptThread getmAcceptThread() {
        return mAcceptThread;
    }


    public void setmAcceptThread(AcceptThread mAcceptThread) {
        this.mAcceptThread = mAcceptThread;
    }
    public void cancellAcceptThread(){
        if (this.mAcceptThread!= null)
            this.mAcceptThread.cancel();
    }

    public synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState()MESSAGE_WRITE " + mState + " -> " + state);
        mState = state;
        mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE , state , -1,null).sendToTarget(); //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

        // Give the new state to the Handler so the UI Activity can update

    }
    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
         //   mHandler.obtainMessage(MainActivity.MESSAGE_TOAST , MainActivity.LongToastDuration , -1,"Your device name is:\n"+this.deviceBTNAme).sendToTarget();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device ,byte[] SerializedProfile , Messenger messenger , String pos , boolean containsFiles) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device , SerializedProfile , messenger , pos , containsFiles);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }


    private class AcceptThread extends Thread {
        // The local server socket
        public final BluetoothServerSocket mmServerSocket;


        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
           // while (mState != STATE_CONNECTED) {
            while(mState != STATE_CONNECTED){
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                    Log.e(TAG,socket.toString()+ "  has arrived");
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (Transmission.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                                // listening
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);

            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
     //   mHandler.obtainMessage(MainActivity.MESSAGE_READ , bytes ,-1 , receivedProfileName).sendToTarget();

        setState(STATE_NONE);//qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq

    }

    public boolean write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return false;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);

        return true;
    }
    private void connectionFailed(String diveceNameAdress) {
        setState(STATE_LISTEN);
        mHandler.obtainMessage(MainActivity.MESSAGE_TOAST , -1 , -1,"couldent connect to\n"+diveceNameAdress).sendToTarget();


        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
    }
    private void connectionLost() {
        setState(STATE_NONE);
        stop();



        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final byte[] SerializedProfile;
        private final String pos;
        private final Messenger messenger;
        private boolean containsFiles;

        public ConnectThread(BluetoothDevice device ,byte[] SerializedProfile , Messenger messenger , String pos , boolean containsFiles) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            this.SerializedProfile = SerializedProfile;
            this.pos = pos;
            this.messenger = messenger;
            this.containsFiles = containsFiles;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            MainActivity.isVisible = false;
            mHandler.obtainMessage(MainActivity.StateDiscoveryChange , -1 ,-1 , null).sendToTarget();



            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                // when connected seccesfuly , start writing process via handler
              //  mHandler.obtainMessage(MainActivity.ConnectedSeccesfuly , -1,-1,null);
                Thread connector = new Thread(new connctorRunnable(SerializedProfile,messenger , pos , containsFiles));
                connector.start();


            } catch (IOException e) {
                connectionFailed(mmDevice.getName());
                Log.e(TAG, " connectionFailed()", e);

                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                Transmission.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (Transmission.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        Profile receivedProfile = null;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                //    ProfilesPage.readMessage = new String(buffer, 0, bytes);
                   // Log.i(TAG, ProfilesPage.readMessage+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    Log.i(TAG,"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    String receivedProfileName ="";
                    try {
                        // receivedProfile = Function.deserialize(buffer);
                        MainActivity.receivedProfileTrans = Function.deserialize(buffer);
                        MainActivity.receivedProfileTrans.updateDate();
                        receivedProfileName = MainActivity.receivedProfileTrans.getName();
                        cancel();//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }



                   // mHandler.obtainMessage(MainActivity.MESSAGE_READ ,bytes , -1 , buffer).sendToTarget();
                    mHandler.obtainMessage(MainActivity.MESSAGE_READ , bytes ,-1 , receivedProfileName).sendToTarget();
                //    ProfilesPage.startHistory = true;

                //    ProfilesPage.makeToastProfilePage(readMessage);
                 //   Profile recived =(Profile)

                    // Send the obtained bytes to the UI Activity
//                    mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
//                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "f", e);
                    mHandler.obtainMessage(MainActivity.MESSAGE_TOAST , -1 ,-1 , "disconnected").sendToTarget();
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
                mHandler.obtainMessage(MainActivity.StateListenChange ,MainActivity.SetStateListenOff ,-1 , null).sendToTarget();


            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
