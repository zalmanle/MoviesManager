package com.example.user.moviesmanager.info;

import android.content.Context;

/**
 * Created by User on 20/03/2016.
 */
public class InfoFactory {
    //region Constants
    public static final String USER_INFO = "user";

    public static final String MOVIES_USER_INFO = "movies user";

    public static final String ADVANCED_OPTIONS_USER_INFO = "advanced options user";
    //endregion

    public static UserInfo getInfo(String flag,Context context){
        UserInfo info = null;
        if(flag.equals(USER_INFO)){
            info = new UserInfo(context);
        }
        else if(flag.equals(MOVIES_USER_INFO)){
            info = new MoviesUserInfo(context);
        }
        else if(flag.equals(ADVANCED_OPTIONS_USER_INFO)){
            info = new AdvancedOptionsUserInfo(context);
        }
        return info;
    }
}
