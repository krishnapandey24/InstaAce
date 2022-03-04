package com.omnicoder.instaace;

public class letcheck {

    private static Object NullPointerException;
    public static final String clas="HEllow";

    public static void download(String url) throws Throwable {
        if(url.length()==0 || url.length()>2 ){
            throw (Throwable) NullPointerException;
        }

    }
}
