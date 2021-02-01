package com.mrshiehx.mschatroom.chat.message;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.XMLUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<MessageItem> mMsgList;
    //private static String receiverAvatarPath;
    private Context context;
    Drawable avatar;
    Drawable avatarR;
    String emailOrAccountOfChattingWithManEncrypted;
    /**
     * ChattingWithMan
     */
    String nickname;
    String gender;
    String whatsup;
    int accountNameIndex = 0;
    int accountGenderIndex = 1;
    int accountWhatSUpIndex = 2;

    public MessagesAdapter(final Context context, List<MessageItem> msgList, String receiverAvatarPath, final String emailOrAccountOfChattingWithManEncrypted) {
        this.context=context;
        mMsgList = msgList;
        this.emailOrAccountOfChattingWithManEncrypted=emailOrAccountOfChattingWithManEncrypted;


        if(MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING,true)) {

            try {
                avatar = FormatTools.getInstance().InputStream2Drawable(new FileInputStream(new File(receiverAvatarPath)));
                if (MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) != 1) {
                    avatarR = FormatTools.getInstance().InputStream2Drawable(new FileInputStream(new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "avatar_" + Variables.ACCOUNT_INFORMATION.getAccountE())));
                } else {
                    avatarR = FormatTools.getInstance().InputStream2Drawable(new FileInputStream(new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "avatar_" + Variables.ACCOUNT_INFORMATION.getEmailE())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Utils.isNetworkConnected(context) && Variables.ACCOUNT_UTILS != null) {
                if (Variables.ACCOUNT_UTILS.getConnection() != null) {
                    try {
                        final String eoaClean;
                        eoaClean = EnDeCryptTextUtils.decrypt(emailOrAccountOfChattingWithManEncrypted);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String by = AccountUtils.BY_ACCOUNT;
                                if (Utils.isEmail(eoaClean)) {
                                    by = AccountUtils.BY_EMAIL;
                                }
                                InputStream info = Variables.ACCOUNT_UTILS.getUserInformationWithoutPasswordNoThread(context, by, emailOrAccountOfChattingWithManEncrypted);
                                List<UserInformation> information = XMLUtils.readXmlBySAX(info);
                                nickname = information.get(accountNameIndex).getNameContent();
                                gender = information.get(accountGenderIndex).getGenderContent();
                                whatsup = information.get(accountWhatSUpIndex).getWhatsupContent();
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e, context.getString(R.string.dialog_exception_failed_get_user_information));
                    }
                }
            } else {
                File file = new File(Utils.getDataFilesPath(context), "information" + File.separator + emailOrAccountOfChattingWithManEncrypted + ".xml");
                if (file.exists()) {
                    try {
                        List<UserInformation> list = XMLUtils.readXmlBySAX(new FileInputStream(file));
                        nickname = list.get(accountNameIndex).getNameContent();
                        gender = list.get(accountGenderIndex).getGenderContent();
                        whatsup = list.get(accountWhatSUpIndex).getWhatsupContent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        LinearLayout leftLayoutInside;
        LinearLayout rightLayoutInside;
        LinearLayout tipLayout;
        LinearLayout rightAvatarLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView tip;
        ImageView leftAvatar;
        ImageView rightAvatar;

        public ViewHolder(View view) {
            super(view);
            leftLayout=view.findViewById(R.id.left_layout);
            rightLayout=view.findViewById(R.id.right_layout);
            leftLayoutInside=view.findViewById(R.id.left_layout_inside);
            rightLayoutInside=view.findViewById(R.id.right_layout_inside);
            tipLayout=view.findViewById(R.id.tip_layout);
            rightAvatarLayout=view.findViewById(R.id.right_avatar_layout);
            leftMsg=view.findViewById(R.id.left_msg);
            rightMsg=view.findViewById(R.id.right_msg);
            tip=view.findViewById(R.id.tip);
            leftAvatar=view.findViewById(R.id.left_avatar);
            rightAvatar=view.findViewById(R.id.right_avatar);
            if(!MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING,true)){
                leftAvatar.setVisibility(View.GONE);
                rightAvatarLayout.setVisibility(View.GONE);
            }

            if(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_MODIFY_THEME,"dark").equals("light")) {
                leftMsg.setTextColor(Color.parseColor("#000000"));
                tipLayout.setBackgroundColor(Color.parseColor("#DCDCDC"));
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_screen_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MessageItem msg = mMsgList.get(position);
        if (msg.getType() == MessageItem.TYPE_TIME) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            try {
                String[] time = msg.getContent().split(";");
                String[] date = time[0].split("-");
                int year = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]);
                int day = Integer.parseInt(date[2]);
                Time Time = new Time();
                Time.setToNow();
                int cYear = Time.year;
                int cMonth = Time.month + 1;
                int cDay = Time.monthDay;
                String timeText;
                if (year == cYear) {
                    if (month == cMonth && day == cDay) {
                        timeText = time[1];
                    } else {
                        timeText = month + "-" + day + " " + time[1];
                    }
                } else {
                    timeText = time[0] + " " + time[1];
                }
                if (!TextUtils.isEmpty(timeText)) {
                    holder.tip.setText(timeText);
                }
            }catch (Exception e){
                e.printStackTrace();
                holder.tipLayout.setVisibility(View.GONE);
            }
        } else if (msg.getType() == MessageItem.TYPE_RECEIVER) {
            holder.leftLayoutInside.setBackgroundResource(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_MODIFY_THEME,"dark").equals("light")?R.drawable.message_receiver_light:R.drawable.message_receiver_dark);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.tipLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            if(MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING,true)){
                holder.leftAvatar.setVisibility(View.VISIBLE);
                if (avatar!=null) {
                    holder.leftAvatar.setImageDrawable(avatar);
                }
            }
            holder.leftAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showInformationDialog(context, avatar, !TextUtils.isEmpty(nickname) ? nickname: EnDeCryptTextUtils.decrypt(emailOrAccountOfChattingWithManEncrypted), gender, whatsup);
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }

                }
            });
        } else if(msg.getType() == MessageItem.TYPE_SELF) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showInformationDialog(context, avatarR, !TextUtils.isEmpty(Variables.ACCOUNT_INFORMATION.getNickname()) ? Variables.ACCOUNT_INFORMATION.getNickname() : (
                                MSCRApplication.getSharedPreferences().getInt(Variables.SHARED_PREFERENCE_LOGIN_METHOD, -1) != 1 ?
                                        EnDeCryptTextUtils.decrypt(Variables.ACCOUNT_INFORMATION.getAccountE().toString(), Variables.TEXT_ENCRYPTION_KEY)
                                        : EnDeCryptTextUtils.decrypt(Variables.ACCOUNT_INFORMATION.getEmailE().toString(), Variables.TEXT_ENCRYPTION_KEY)), Variables.ACCOUNT_INFORMATION.getGender(), Variables.ACCOUNT_INFORMATION.getWhatsup());
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                }
            });
            if(MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING,true)){
                holder.rightAvatarLayout.setVisibility(View.VISIBLE);
                if(avatarR!=null) {
                    holder.rightAvatar.setImageDrawable(avatarR);
                }
            }
        }else if(msg.getType() == MessageItem.TYPE_FAILED_SEND) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(String.format(context.getString(R.string.chat_tip_failed_send),msg.getContent()));
        }else if(msg.getType()==MessageItem.TYPE_FAILED_SEND_OFFLINE){
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_offline));
        }else if(msg.getType()==MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED){
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_not_loggined));
        }else if(msg.getType()==MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED){
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_failed_logined));
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    void showInformationDialog(Context context, Drawable avatar, CharSequence name, CharSequence gender, CharSequence whatsup){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        if(avatar!=null) {
            alertDialog.setIcon(avatar);
        }
        alertDialog.setTitle(name);
        alertDialog.setMessage(String.format(context.getString(R.string.dialog_user_information_message),(gender.equals("male")?context.getString(R.string.preference_account_gender_male):(gender.equals("female")?context.getString(R.string.preference_account_gender_female):context.getString(R.string.preference_account_gender_summary))),!TextUtils.isEmpty(whatsup)?whatsup:context.getString(R.string.preference_account_whatsup_summary)));
        alertDialog.show();
    }
}