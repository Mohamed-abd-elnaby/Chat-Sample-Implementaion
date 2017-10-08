package com.example.mohamed.mahamoud.UI.Actvities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.mahamoud.Adapters.ChatAdapter;
import com.example.mohamed.mahamoud.Clients.Client_Chat;
import com.example.mohamed.mahamoud.Interfaces.ChatInterface;
import com.example.mohamed.mahamoud.Interfaces.Eventfinshed;
import com.example.mohamed.mahamoud.Models.ModelContact;
import com.example.mohamed.mahamoud.Models.ModelMassage;
import com.example.mohamed.mahamoud.R;
import com.example.mohamed.mahamoud.Servers.Server_chat;
import com.example.mohamed.mahamoud.Servers.Server_contacts;

import java.net.ServerSocket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by mohamed on 1/12/17.
 */

public class ChatActivity extends Fragment implements ChatInterface{
    String freindip,friendname;
    EditText Emassage;
    ListView contactslist;
    ImageView sendBtn;
    ChatAdapter adapter;
    ArrayList<ModelMassage> chatHistory;
    Handler handler=new Handler();

    TextView friend;

    @Override
    public void onDetach() {

        super.onDetach();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat_activity,container,false);

        Emassage=(EditText)view.findViewById(R.id.massgetext);
        sendBtn=(ImageButton)view.findViewById(R.id.sendbtn);
        friend=(TextView)view.findViewById(R.id.friendname);
        contactslist=(ListView)view.findViewById(R.id.list_item);
        chatHistory=new ArrayList<>();
        Bundle  bundle=getArguments();
        freindip=bundle.getString("ip");
        friendname=bundle.getString("name");
        Server_chat.getInstance().setChatInterface(this);
        if(friendname!=null){
            friend.setText(friendname);
        }
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Emassage.getText().toString().trim().length()>0&&freindip!=null){
                    sendmassage();
                }
            }
        });

        MainActivity mainActivity=new MainActivity();
        mainActivity.setEventfinshed1(eventfinshed);

        return view;
    }
    Eventfinshed eventfinshed=new Eventfinshed() {
        @Override
        public void onEvientFinshed() {

            finshit();
        }
    };
    void finshit(){
        getFragmentManager().beginTransaction().remove(this).commit();

    }
    void sendmassage(){
        Client_Chat client_chat=new Client_Chat(freindip,Emassage.getText().toString());
        client_chat.setChatInterface(this);
        client_chat.start();
    }
    public void displayMessage(ModelMassage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    private void scroll() {
        contactslist.setSelection(contactslist.getCount() - 1);
    }
    private void addmassage(String messagesend,boolean me){
        ModelMassage msg = new ModelMassage();
        msg.setId(1);
        msg.setMe(me);
        msg.setMessage(messagesend);
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        adapter = new ChatAdapter(getActivity(), new ArrayList<ModelMassage>());
        contactslist.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ModelMassage message = chatHistory.get(i);
            displayMessage(message);
        }

    }

    @Override
    public void onMassageCome(final String massage, final boolean me,String ip) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(massage!=null){
                    if(me){
                        Emassage.setText("");
                    }
                    addmassage(massage,me);

                }
                else {
                    Toast.makeText(getActivity(),"some error happen",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
