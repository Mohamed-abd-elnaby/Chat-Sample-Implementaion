package com.example.mohamed.mahamoud.Servers;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.mohamed.mahamoud.Interfaces.ContactsInterface;
import com.example.mohamed.mahamoud.UI.Application.ApplactionCalss;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static com.example.mohamed.mahamoud.Others.Constant.Contacts_Port;

/**
 * Created by mohamed on 1/11/17.
 */

public class Server_contacts extends Thread{

    private ServerSocket server=null;
    Handler handler=new Handler();
    Socket socket=null;
    boolean still_connect=true;
    ContactsInterface contactsInterface;

    public void setContactsInterface(ContactsInterface contactsInterface) {
        this.contactsInterface = contactsInterface;
    }

    @Override
    public void run() {
        startServer();
    }
    void startServer(){
        try {
            server=new ServerSocket(Contacts_Port);
            while (still_connect){
                if((socket=server.accept())!=null){

                    GetMassage getMassage=new GetMassage(socket);
                    getMassage.start();
                }
                else {
                    Log.d("socket","is null");
                }
            }

        }catch (SocketException ex){
            ex.printStackTrace();

        }
        catch (Exception ex){
            ex.printStackTrace();

        }
    }

    class GetMassage extends Thread{
        Socket socket;
        String ip;
        public GetMassage(Socket socket){
            this.socket=socket;
        }

        String massage;
        ObjectInputStream objectInputStream=null;
        @Override
        public void run() {
            StartGet();
        }

        void StartGet(){
            try {
                objectInputStream=new ObjectInputStream(socket.getInputStream());
                if(objectInputStream!=null){
                    massage=(String)objectInputStream.readObject();
                    ip=socket.getInetAddress().getHostAddress().toString();
                }else{
                    Log.d("object error","object stream is null");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            finally {
                if(massage!=null&&!(massage.isEmpty())){
                    contactsInterface.onContactCome(ip,massage);
                }
                else {
                    Log.d("massage","is nul or empty");
                }
            }
        }
    }

    public  void disconnect(){
        try {
            still_connect=false;
            socket.close();
            server.close();


        }catch (Exception ex){
            ex.printStackTrace();
        }

    }



}
