package com.example.mohamed.mahamoud.Servers;

import android.util.Log;

import com.example.mohamed.mahamoud.Interfaces.ChatInterface;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static com.example.mohamed.mahamoud.Others.Constant.Chat_Port;

/**
 * Created by mohamed on 1/11/17.
 */

public class Server_chat extends Thread{


    private static Server_chat instance;

    public static Server_chat getInstance() {
        if(instance==null){
            instance=new Server_chat();
        }
        return instance;
    }
    public Server_chat(){

        instance=this;
    }

    ServerSocket serverChat=null;
    Socket socket=null;
    ChatInterface chatInterface;

    public void setChatInterface(ChatInterface chatInterface) {
        this.chatInterface = chatInterface;
    }



    boolean still_connect=true;
    @Override
    public void run() {
        startServer();
    }
    public void dissconeccted(){
        try {
            still_connect=false;
            socket.close();
            serverChat.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    void startServer(){
        Log.d("chat server is run","yes");
        try {
            serverChat=new ServerSocket(Chat_Port);
            while (still_connect){
                if(serverChat!=null){
                    if((socket=serverChat.accept())!=null){
                        GetMassage getMassage=new GetMassage(socket);
                        getMassage.start();
                    }
                }
            }

        }catch (SocketException ex){
            ex.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class GetMassage extends Thread{
        Socket socket=null;
        ObjectInputStream objectInputStream=null;
        String massage;
        public GetMassage(Socket socket){
            this.socket=socket;
        }

        @Override
        public void run() {
            try {
                if(socket!=null){
                    objectInputStream=new ObjectInputStream(socket.getInputStream());
                    massage=(String)objectInputStream.readObject();
                    if (massage!=null){
                        chatInterface.onMassageCome(massage,false,socket.getInetAddress().getHostAddress().toString());
                    }
                }
            }catch (SocketException ex){
                ex.printStackTrace();
                chatInterface.onMassageCome(null,false,null);
                this.interrupt();
            }
            catch (Exception ex){
                ex.printStackTrace();
                chatInterface.onMassageCome(null,false,null);
                this.interrupt();
            }finally {
                Log.d("massage come","yes");

            }

        }
    }
}
