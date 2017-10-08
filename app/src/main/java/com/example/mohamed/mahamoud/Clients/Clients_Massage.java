package com.example.mohamed.mahamoud.Clients;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.mahamoud.Interfaces.ContactsInterface;
import com.example.mohamed.mahamoud.Interfaces.Eventfinshed;
import com.example.mohamed.mahamoud.R;
import com.example.mohamed.mahamoud.UI.Actvities.MainActivity;
import com.example.mohamed.mahamoud.UI.Application.ApplactionCalss;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Handler;

import static android.content.Context.WIFI_SERVICE;
import static com.example.mohamed.mahamoud.Others.Constant.Contacts_Port;
import static com.example.mohamed.mahamoud.Others.Constant.CoresAvaliable;

/**
 * Created by mohamed on 1/11/17.
 */

public class Clients_Massage extends Thread{

    int counter=0;
    ProgressBar progressBar;
    String myname;
    Socket socket=null;
    Button button;
    Context context;
    ExecutorService executorService;
    TextView countertext;
    android.os.Handler handler=new android.os.Handler();
    ContactsInterface contactsInterface;
    public Clients_Massage(String myname,Button button,Context context,ProgressBar progressBar,TextView countertext){
        this.countertext=countertext;
        this.progressBar=progressBar;
        this.context=context;
        this.button=button;
        this.myname=myname;
        executorService = Executors.newFixedThreadPool(CoresAvaliable);
    }

    public void setContactsInterface(ContactsInterface contactsInterface) {
        this.contactsInterface = contactsInterface;
    }

    @Override
    public void run() {

        Log.d("client","start sent");
        StartSend();
    }

    void StartSend(){

        String brodcast_ip=get_ip();
        Eventfinshed eventfinshed=new Eventfinshed() {
            @Override
            public void onEvientFinshed() {


                counter=counter+1;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        countertext.setText(String.valueOf(counter));
                        if(counter==254){
                            counter=0;
                            if(myname.endsWith("$")){
                                button.setText(context.getString(R.string.goOffline));
                                button.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }else if(myname.endsWith("#")){
                                button.setText(context.getString(R.string.go_online));
                                button.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    }
                });

            }
        };
        try {
            for( int i=0;i<255;i++){

                String ip=brodcast_ip+i;
                if(ip.equals(get_iplocal())){

                }else {
                    executorService.submit(new Sendrequest(ip,eventfinshed));

                }


            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    String get_iplocal() {
        String ip;
        WifiManager wifimanger = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifimanger.getConnectionInfo();
        int iputip = wifiinfo.getIpAddress();
        ip = Formatter.formatIpAddress(iputip);
        return ip;
    }

    class Sendrequest extends Thread{
        String ip;
        Eventfinshed eventfinshed;
        ObjectOutputStream objectOutputStream=null;
        public Sendrequest (String ip,Eventfinshed eventfinshed){
            this.ip=ip;
            this.eventfinshed=eventfinshed;
        }
        @Override
        public void run() {
            try{
                socket=new Socket(ip,Contacts_Port);
                socket.setSoTimeout(5000);
                if(socket!=null&&socket.isConnected()){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                          //  Toast.makeText(context,ip,Toast.LENGTH_LONG).show();

                        }
                    });
                    objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(myname);
                    objectOutputStream.flush();
                    objectOutputStream.close();


                }
                else{
                    Log.d("soket client","not connected");
                }
            }catch (Exception ex){
                ex.printStackTrace();
                Log.d("error",ip);
            }
            finally {
                eventfinshed.onEvientFinshed();
            }
        }
    }

    String get_ip() {
        int count=0;
        int postion;
        String ip;
        StringBuilder stringBuilder=new StringBuilder();
        WifiManager wifimanger = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifimanger.getConnectionInfo();
        int iputip = wifiinfo.getIpAddress();
        ip = Formatter.formatIpAddress(iputip);
        char[]m=ip.toCharArray();
        for (int i=0;i<m.length;i++){
            stringBuilder.append(m[i]);
            if(m[i]=='.'){
                count++;
                if(count==3){
                    break;
                }

            }

        }

        ip=stringBuilder.toString();
        return ip;
    }
}
