package com.example.mohamed.mahamoud.Clients;

import android.util.Log;
import android.widget.TableRow;

import com.example.mohamed.mahamoud.Interfaces.ChatInterface;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import static com.example.mohamed.mahamoud.Others.Constant.Chat_Port;

/**
 * Created by mohamed on 1/14/17.
 */

public class Client_Chat extends Thread {

    boolean stillconnect=true;
    Socket socket=null;
    String ip;
    String massage;
    ChatInterface chatInterface;

    public Client_Chat(String ip,String massage){
        this.ip=ip;
        this.massage=massage;
    }
    public void setChatInterface(ChatInterface chatInterface) {
        this.chatInterface = chatInterface;
    }

    @Override
    public void run() {
        startConnect();
    }
    public void dissconnected(){
        try {
            stillconnect=false;
            socket.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void startConnect(){
        SendMassage sendMassage=null;
        try {
            socket=new Socket(ip,Chat_Port);
            if (socket!=null&&socket.isConnected()){
                sendMassage=new SendMassage(socket,massage);
                sendMassage.start();
            }
        }catch (SocketException ex){
            ex.printStackTrace();
            if(sendMassage!=null){
                sendMassage.interrupt();
            }
            this.interrupt();
        }
        catch (Exception ex){
            ex.printStackTrace();
            if(sendMassage!=null){
                sendMassage.interrupt();
            }
            this.interrupt();
        }
    }
    class  SendMassage extends Thread{
        Socket socket=null;
        String massage;
        ObjectOutputStream objectOutputStream=null;
        public SendMassage(Socket socket,String massage){
            this.socket=socket;
            this.massage=massage;
        }

        @Override
        public void run() {
         try {
             if(socket!=null){
                 Log.d("massage send","start");
                 objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
                 if(massage!=null){
                     objectOutputStream.writeObject(massage);
                     objectOutputStream.flush();
                     objectOutputStream.close();
                     chatInterface.onMassageCome(massage,true,null);
                 }
             }
         }catch (SocketException ex){
             ex.printStackTrace();
             chatInterface.onMassageCome(null,true,null);
         }
         catch (Exception e){
             e.printStackTrace();
             chatInterface.onMassageCome(null,true,null);

         }

        }
    }
}
