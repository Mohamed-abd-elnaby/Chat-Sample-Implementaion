package com.example.mohamed.mahamoud.Models;

import java.io.Serializable;

/**
 * Created by mohamed on 1/11/17.
 */

public class ModelContact implements Serializable {
    String friend_Name, friend_ip;

    public String getFriend_Name() {
        return friend_Name;
    }

    public void setFriend_Name(String friend_Name) {
        this.friend_Name = friend_Name;
    }

    public String getFriend_ip() {
        return friend_ip;
    }

    public void setFriend_ip(String friend_ip) {
        this.friend_ip = friend_ip;
    }
}

