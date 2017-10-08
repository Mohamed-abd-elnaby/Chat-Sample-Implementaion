package com.example.mohamed.mahamoud.UI.Actvities;

import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.mahamoud.Adapters.AdapterContacts;
import com.example.mohamed.mahamoud.Adapters.ChatAdapter;
import com.example.mohamed.mahamoud.Clients.Clients_Massage;
import com.example.mohamed.mahamoud.Interfaces.ChatInterface;
import com.example.mohamed.mahamoud.Interfaces.ContactsInterface;
import com.example.mohamed.mahamoud.Interfaces.Eventfinshed;
import com.example.mohamed.mahamoud.Models.ModelContact;
import com.example.mohamed.mahamoud.Models.ModelMassage;
import com.example.mohamed.mahamoud.R;
import com.example.mohamed.mahamoud.Servers.Server_chat;
import com.example.mohamed.mahamoud.Servers.Server_contacts;
import com.example.mohamed.mahamoud.UI.Application.ApplactionCalss;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.mohamed.mahamoud.Others.Constant.Contacts_Port;
import static com.example.mohamed.mahamoud.Others.Constant.CoresAvaliable;

public class MainActivity extends AppCompatActivity implements ContactsInterface{
    EditText Ename;
    ListView contactslist;
    ProgressBar progressBar;
    Server_contacts server_contacts;
    Button goOnline;
    Handler handler=new Handler();
    LinearLayout linearLayout;
    TextView counter;
    boolean notify=true;
    int count=0;
    ArrayList<ModelContact>contacts=new ArrayList<>();
    AdapterContacts adapterContacts;
    Server_chat server_chat;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    public void setEventfinshed1(Eventfinshed eventfinshed1) {
        this.eventfinshed1 = eventfinshed1;
    }

    Eventfinshed eventfinshed1;


    @Override
    protected void onResume() {
        builder = new NotificationCompat.Builder(getApplication());
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intionalizecoponnet();

    }
    ChatInterface chatInterface=new ChatInterface() {
        int count=0;
        @Override
        public void onMassageCome(final String massage, final boolean me, final String ip) {

            Log.d("massage come",""+massage);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(!me&&notify){
                        Toast.makeText(ApplactionCalss.getInstance().getApplicationContext(),massage,Toast.LENGTH_LONG).show();
                        count++;
                        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplication());
                        builder.setSmallIcon(R.drawable.ic_add_black_48dp);
                        builder.setContentTitle(ip);
                        builder.setContentText(massage);
                        builder.setSound(soundUri);
                        notificationManager = (NotificationManager) ApplactionCalss.getInstance().getSystemService(
                                NOTIFICATION_SERVICE);
                        notificationManager.notify(count, builder.build());
                    }
                }
            }) ;
        }
    };


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);


    }
    String get_iplocal() {
        String ip;
        WifiManager wifimanger = (WifiManager) MainActivity.this.getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifimanger.getConnectionInfo();
        int iputip = wifiinfo.getIpAddress();
        ip = Formatter.formatIpAddress(iputip);
        return ip;
    }

    @Override
    protected void onDestroy() {
        if(server_chat!=null){
            server_chat.dissconeccted();

        }
        if(server_contacts!=null){
            server_contacts.disconnect();

        }
        super.onDestroy();
    }

    void intionalizecoponnet(){
        linearLayout=(LinearLayout)findViewById(R.id.container2);

        counter=(TextView)findViewById(R.id.counter);
        progressBar =(ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        contactslist=(ListView)findViewById(R.id.contectlist);
        goOnline=(Button)findViewById(R.id.go_online);
        Ename=(EditText)findViewById(R.id.name);
        goOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter.setText("0");
                if(goOnline.getText().equals(getString(R.string.go_online))){

                    if(Ename.getText().toString().trim().length()>0){
                            server_contacts=new Server_contacts();
                            server_contacts.setContactsInterface(MainActivity.this);
                            server_contacts.start();
                            server_chat=new Server_chat();
                            server_chat.setChatInterface(chatInterface);
                            server_chat.start();

                        Ename.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        goOnline.setVisibility(View.GONE);
                        goOnline.setText(getString(R.string.search));
                        Clients_Massage clients_massage=new Clients_Massage(Ename.getText().toString()+"$",goOnline,MainActivity.this,progressBar,counter);
                        clients_massage.start();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Never Let Name Empty",Toast.LENGTH_LONG).show();

                    }
                }
                else{
                    if(server_contacts!=null){
                        server_contacts.disconnect();

                    }
                    if(server_chat!=null){
                        server_chat.dissconeccted();

                    }

                    ExecutorService executorService= Executors.newFixedThreadPool(CoresAvaliable);
                    adapterContacts.notifyDataSetChanged();
                    if(Ename.getText().toString().trim().length()>0){
                        progressBar.setVisibility(View.VISIBLE);
                        goOnline.setVisibility(View.GONE);
                        goOnline.setText(getString(R.string.search));

                        Eventfinshed eventfinshed=new Eventfinshed() {
                            @Override
                            public void onEvientFinshed() {
                                Toast.makeText(MainActivity.this,String.valueOf(contacts.size()),Toast.LENGTH_LONG).show();
                                count++;
                                counter.setText(String.valueOf(contacts.size()));
                                if(count==contacts.size()){

                                    contacts.clear();
                                    adapterContacts.notifyDataSetChanged();
                                }
                            }
                        };
                        for(int i=0;i<contacts.size();i++){
                            executorService.submit(new Sendrequest(contacts.get(i).getFriend_ip(),eventfinshed,contacts.get(i).getFriend_Name()));
                            contacts.remove(i);

                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapterContacts.notifyDataSetChanged();
                                Ename.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                goOnline.setVisibility(View.VISIBLE);
                                goOnline.setText(getString(R.string.go_online));
                            }
                        });
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Never Let Name Empty",Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
        contactslist.post(new Runnable() {
            @Override
            public void run() {
                setAdapterContacts();

            }
        });
    }


    @Override
    public void onBackPressed() {

        if(getFragmentManager().popBackStackImmediate()){
            notify=true;
        }
        else
            super.onBackPressed();
    }

    Eventfinshed eventfinshed=new Eventfinshed() {
        @Override
        public void onEvientFinshed() {
            notify=false;
        }
    };
    void setAdapterContacts(){

        adapterContacts=new AdapterContacts(MainActivity.this,contacts,linearLayout);
        adapterContacts.setEventfinshed(eventfinshed);
        contactslist.setAdapter(adapterContacts);
    }

    @Override
    public void onContactCome(final String ip, final String name) {

         if(ip!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,ip,Toast.LENGTH_LONG).show();
                    for(int i=0;i<contacts.size();i++){
                        if(ip.equals(contacts.get(i).getFriend_ip())){
                            contacts.remove(i);
                        }
                    }
                    adapterContacts.notifyDataSetChanged();
                    if(name.endsWith("$")){
                        final Eventfinshed eventfinshed=new Eventfinshed() {
                            @Override
                            public void onEvientFinshed() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String newName=name.replace('$',' ');
                                        ModelContact modelContact=new ModelContact();
                                        modelContact.setFriend_ip(ip);
                                        modelContact.setFriend_Name(newName);
                                        contacts.add(modelContact);
                                        adapterContacts.notifyDataSetChanged();
                                    }
                                });

                            }
                        };
                        Sendrequest sendrequest=new Sendrequest(ip,eventfinshed,Ename.getText().toString()+"&");
                        sendrequest.start();

                    }
                    else if(name.endsWith("&")){
                        String newName=name.replace('&',' ');
                        ModelContact modelContact=new ModelContact();
                        modelContact.setFriend_ip(ip);
                        modelContact.setFriend_Name(newName);
                        contacts.add(modelContact);
                        adapterContacts.notifyDataSetChanged();
                    }



                }

            });

        }

    }

    class Sendrequest extends Thread{
        Socket socket=null;
        String ip;
        Eventfinshed eventfinshed;
        String myname;
        ObjectOutputStream objectOutputStream=null;
        public Sendrequest (String ip,Eventfinshed eventfinshed,String myname){
            this.ip=ip;
            this.myname=myname;
            this.eventfinshed=eventfinshed;
        }
        @Override
        public void run() {
            try{
                socket=new Socket(ip,Contacts_Port);
                socket.setSoTimeout(5000);
                if(socket!=null&&socket.isConnected()){
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

}
