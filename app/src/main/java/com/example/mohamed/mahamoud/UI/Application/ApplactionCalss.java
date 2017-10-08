package com.example.mohamed.mahamoud.UI.Application;

import android.app.Application;

/**
 * Created by mohamed on 1/11/17.
 */

public  class ApplactionCalss extends Application{
    private static ApplactionCalss instance;

    public  ApplactionCalss(){

        instance=this;
    }

    public static ApplactionCalss getInstance() {
        if(instance==null){
            instance=new ApplactionCalss();
        }
        return instance;
    }
}
