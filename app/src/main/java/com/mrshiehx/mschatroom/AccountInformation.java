package com.mrshiehx.mschatroom;

import android.widget.Toast;

import com.mrshiehx.mschatroom.utils.XMLUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.InputStream;
import java.util.List;

/**
 * 当离线模式时，全部界面使用这个账户信息
 */
public class AccountInformation {
    boolean isNetworkConnected;
    boolean canLogin;
    boolean logined;
    boolean canConnectToServer;
    CharSequence accountE;
    CharSequence emailE;
    CharSequence nickname;
    CharSequence whatsup;
    CharSequence gender;
    //InputStream avatar;
    int accountNameIndex = 0;
    int accountGenderIndex = 1;
    int accountWhatSUpIndex = 2;
    //InputStream avatar;
    //InputStream information;

    /*public AccountInformation(boolean logined){
        this.logined=logined;
    }

    public AccountInformation(boolean logined){
        this.isNetworkConnected=isNetworkConnected;
        this.logined=logined;
    }*/

    public AccountInformation() {

    }

    public AccountInformation(boolean isNetworkConnected,boolean logined,boolean canLogin, CharSequence accountE, CharSequence emailE, InputStream information/*, InputStream avatar*/){
        this.isNetworkConnected=isNetworkConnected;
        this.logined=logined;
        this.canLogin=canLogin;
        this.accountE=accountE;
        this.emailE=emailE;
        //this.avatar=avatar;
        if(information!=null) {
            try {
                List<UserInformation> list = XMLUtils.readXmlBySAX(information);
                this.nickname = list.get(accountNameIndex).getNameContent();
                this.gender = list.get(accountGenderIndex).getGenderContent();
                this.whatsup = list.get(accountWhatSUpIndex).getWhatsupContent();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MSCRApplication.getContext(), MSCRApplication.getContext().getString(R.string.toast_failed_to_get_information), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setIsNetworkConnected(boolean isNetworkConnected){
        this.isNetworkConnected=isNetworkConnected;
    }
    public void setLogined(boolean logined){
        this.logined=logined;
    }
    public void setCanLogin(boolean canLogin){
        this.canLogin=canLogin;
    }
    public void setAccountE(CharSequence accountE) {
        this.accountE = accountE;
    }
    public void setEmailE(CharSequence emailE){
        this.emailE=emailE;
    }
    public void setInformation(InputStream information){
        if(information!=null) {
            try {
                List<UserInformation> list = XMLUtils.readXmlBySAX(information);
                this.nickname = list.get(accountNameIndex).getNameContent();
                this.gender = list.get(accountGenderIndex).getGenderContent();
                this.whatsup = list.get(accountWhatSUpIndex).getWhatsupContent();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MSCRApplication.getContext(), MSCRApplication.getContext().getString(R.string.toast_failed_to_get_information), Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*public void setAvatar(InputStream avatar){
        this.avatar=avatar;
    }*/
    public void setNickname(CharSequence nickname){
        this.nickname=nickname;
    }

    public void setGender(CharSequence gender){
        this.gender=gender;
    }

    public void setWhatsup(CharSequence whatsup){
        this.whatsup=whatsup;
    }

    public void setCanConnectToServer(boolean canConnectToServer) {
        this.canConnectToServer = canConnectToServer;
    }

    /**
     * waitting for write
     */
    /*public AccountInformation(Context context, CharSequence account, CharSequence email, InputStream avatar, InputStream information) {
        this.context=context;
        this.account=account;
        this.email=email;
        this.avatar=avatar;
        this.information=information;
        /*this.nickname=nickname;
        this.whatsup=whatsup;
        this.gender=gender;*/
    //}*/



    public boolean isNetworkConnected(){
        return isNetworkConnected;
    }
    public boolean isCanLogin(){
        return canLogin;
    }
    public boolean isLogined(){
        return logined;
    }
    public CharSequence getAccountE(){
        return accountE;
    }
    public CharSequence getEmailE(){
        return emailE;
    }
    public CharSequence getNickname(){
        return nickname;
    }
    public CharSequence getWhatsup(){
        return whatsup;
    }
    public CharSequence getGender(){
        return gender;
    }
    /*public InputStream getAvatar(){
        return avatar;
    }*/
    public boolean isCanConnectToServer() {
        return canConnectToServer;
    }

}
