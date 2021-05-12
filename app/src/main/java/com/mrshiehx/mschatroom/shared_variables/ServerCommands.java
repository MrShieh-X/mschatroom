package com.mrshiehx.mschatroom.shared_variables;

import com.mrshiehx.mschatroom.beans.Command;

public class ServerCommands {
    public static Command downloadFile(long millis){
        return downloadFile(String.valueOf(millis));
    }
    public static Command downloadFile(String millis){
        return new Command("downloadFile",millis);
    }
}
