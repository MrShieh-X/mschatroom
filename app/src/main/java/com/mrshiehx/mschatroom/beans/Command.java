package com.mrshiehx.mschatroom.beans;

import java.util.Arrays;
import java.util.List;

public class Command {
    private String[] arguments;
    public Command(String... arguments){
        this.arguments=arguments;
    }

    public String[]getArguments(){
        return arguments;
    }

    public String getCommandText(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(arguments[0]);
        for(int i=1;i<arguments.length;i++){
            stringBuilder.append(" ");
            stringBuilder.append(arguments[i]);
        }
        return stringBuilder.toString();
    }

    public Command addArgument(String argument){
        List<String>list = Arrays.asList(arguments);
        list.add(argument);
        this.arguments = (String[]) list.toArray();
        return this;
    }

    @Override
    public String toString() {
        return getCommandText();
    }
}
