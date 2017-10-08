package com.example.mohamed.mahamoud.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mohamed.mahamoud.Interfaces.ChatInterface;
import com.example.mohamed.mahamoud.Interfaces.Eventfinshed;
import com.example.mohamed.mahamoud.Models.ModelContact;
import com.example.mohamed.mahamoud.R;
import com.example.mohamed.mahamoud.UI.Actvities.ChatActivity;
import com.example.mohamed.mahamoud.UI.Application.ApplactionCalss;

import java.util.ArrayList;

/**
 * Created by mohamed on 1/12/17.
 */

public class AdapterContacts extends BaseAdapter{
    Activity context;
    ArrayList<ModelContact>contacts=new ArrayList<>();
    LinearLayout linearLayout;
    Eventfinshed eventfinshed;

    public void setEventfinshed(Eventfinshed eventfinshed) {
        this.eventfinshed = eventfinshed;
    }

    public AdapterContacts(Activity context , ArrayList<ModelContact>contacts, LinearLayout linearLayout){
        this.contacts=contacts;
        this.context=context;
        this.linearLayout=linearLayout;
    }
    @Override
    public int getCount() {
        if(contacts!=null){
            return contacts.size();

        }else
            return 0;
    }

    @Override
    public ModelContact getItem(int position) {
        if (contacts!=null){
            return contacts.get(position);
        }else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView=vi.inflate(R.layout.model_contact,null);
            holder=new Holder();
            holder.friendname=(TextView) convertView.findViewById(R.id.friendname);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.container);
            convertView.setTag(holder);

        }else
        {
            holder=(Holder)convertView.getTag();
        }
        holder.friendname.setText(contacts.get(position).getFriend_Name());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity chatActivity=new ChatActivity();
                Bundle bundle=new Bundle();
                bundle.putString("ip",contacts.get(position).getFriend_ip());
                bundle.putString("name",contacts.get(position).getFriend_Name());
                chatActivity.setArguments(bundle);
                context.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.activity_main,chatActivity).commit();
                eventfinshed.onEvientFinshed();
            }
        });
        return convertView;
    }
    class Holder{
        TextView friendname;
        RelativeLayout relativeLayout;

    }
}
