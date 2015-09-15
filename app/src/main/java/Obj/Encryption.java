package Obj;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Vector;

import javax.crypto.Cipher;

/**
 * Created by roy on 5/16/15.
 */
public class Encryption {
    final  int blockLen = 53;
    final  String xform = "RSA/ECB/PKCS1Padding";
    final  int keySize = 2048;
    private PublicKey pubk;
    private PrivateKey prvk;

    public PublicKey getPubk() {
        return pubk;
    }

    public Encryption(){

    }




    private  byte[] encrypt(byte[] inpBytes, PublicKey key, String xform) throws Exception {
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }
    private  byte[] decrypt(byte[] inpBytes, PrivateKey key,   String xform) throws Exception{
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }
    private  Vector<byte[]> blockDev1(byte[] data ){
        Vector <byte[]> ans = new Vector<byte[]>();
        for (int i = 0 ; i < data.length ; i = i + blockLen){
            byte[] row = new byte[blockLen];
            for (int j = 0 ; j < blockLen && i+j < data.length; j++){
                row[j] = data[i+j];
            }
            ans.add(row);
        }


        return ans;
    }
    private  Vector<byte[]> EnctryptVector(Vector<byte[]> data , PublicKey key,   String xform) throws Exception{
        Vector <byte[]> ans = new Vector<byte[]>();
        for (int i = 0 ; i < data.size() ; i++){
            ans.add(encrypt(data.get(i), key, xform));
        }
        return ans;
    }
    private  Vector<byte[]> DecryptVector(Vector<byte[]> data , PrivateKey key,   String xform) throws Exception {
        Vector<byte[]> ans = new Vector<byte[]>();
        for (int i = 0; i < data.size(); i++) {
            ans.add(decrypt(data.get(i), key, xform));
        }


        return ans;

    }
    private  byte[] Vec2byte(Vector<byte[]> data , int padding){
        int size = (data.size()*data.get(0).length)-padding;
        byte[] ans = new byte[size];
        int c = 0;
        for (int i = 0 ; i < data.size() ; i++){
            for (int j = 0 ; j < data.get(i).length && c < size ; j++){
                ans[c++] = data.get(i)[j];
            }
        }

        return ans;
    }
    private void keyGen(){
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        kpg.initialize(keySize);
        KeyPair kp = kpg.generateKeyPair();
         pubk = kp.getPublic();
         prvk = kp.getPrivate();
    }
    public  Vector <byte[]> EncryptProfile(Profile p){
        keyGen();
        byte[] objData = null;
        try {
            objData = p.serialize();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Vector <byte[]>dataBlocks = blockDev1(objData);
        int padding = objData.length%blockLen;
        Vector <byte[]>EnctyptedBlocks = null;
        try {
            EnctyptedBlocks = EnctryptVector(dataBlocks, pubk, xform);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return EnctyptedBlocks;
    }
    public  Profile DeccryptProfile(PrivateKey Pkey ,Vector <byte[]> EnctyptedBlocks , int padding){
        Vector <byte[]>dectyptedBlocks = null;
        try {
            dectyptedBlocks = DecryptVector(EnctyptedBlocks, Pkey, xform);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] DecryptedByte = null;
        DecryptedByte = Vec2byte(dectyptedBlocks, padding);
        Profile ans = null;
        try {
            ans = Function.deserialize(DecryptedByte);
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ans;

    }

}
