package com.mrshiehx.mschatroom.items;

import java.io.InputStream;
import java.io.Serializable;

public class InputStreamItem implements Serializable {
    InputStream inputStream;
    public InputStreamItem(InputStream inputStream){
        this.inputStream=inputStream;
    }
    public void setInputStream(InputStream inputStream){
        this.inputStream=inputStream;
    }
    public InputStream getInputStream(){
        return inputStream;
    }
}
