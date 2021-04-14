package com.mrshiehx.mschatroom.chat.message;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mrshiehx.mschatroom.MSCRApplication;
import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.UserInformationUtils;
import com.mrshiehx.mschatroom.xml.user_information.UserInformation;

import java.io.File;
import java.io.FileInputStream;
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

    public MessagesAdapter(final Context context, List<MessageItem> msgList, String receiverAvatarPath, final String emailOrAccountOfChattingWithManEncrypted) {
        this.context = context;
        this.mMsgList = msgList;
        this.emailOrAccountOfChattingWithManEncrypted = emailOrAccountOfChattingWithManEncrypted;


        if (MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true)) {
            File file = new File(receiverAvatarPath);
            if (file.exists()) {
                try {
                    avatar = FormatTools.getInstance().Bytes2Drawable(FileUtils.toByteArray(file));
                    //avatarR = FormatTools.getInstance().Bytes2Drawable(FileUtils.toByteArray(new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "avatar_" + Variables.ACCOUNT_INFORMATION.getAccountE())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                //avatar = FormatTools.getInstance().Bytes2Drawable(FileUtils.toByteArray(new File(receiverAvatarPath)));
                avatarR = FormatTools.getInstance().Bytes2Drawable(FileUtils.toByteArray(new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "avatar_" + Variables.ACCOUNT_INFORMATION.getAccountE())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Utils.isNetworkConnected(context) && Variables.ACCOUNT_UTILS != null) {
                if (Utils.getAccountUtils().getConnection() != null) {
                    try {
                        final String eoaClean;
                        eoaClean = EnDeCryptTextUtils.decrypt(emailOrAccountOfChattingWithManEncrypted);
                        new Thread(() -> {
                            String by = AccountUtils.BY_ACCOUNT;
                            if (Utils.isEmail(eoaClean)) {
                                by = AccountUtils.BY_EMAIL;
                            }
                            byte[] info = Variables.ACCOUNT_UTILS.getBytes(context, "information", by, emailOrAccountOfChattingWithManEncrypted);
                            UserInformation information = UserInformationUtils.read(context, info);
                            nickname = information.nameContent;
                            gender = information.genderContent;
                            whatsup = information.whatsupContent;
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.exceptionDialog(context, e, context.getString(R.string.dialog_exception_failed_get_user_information));
                    }
                }
            } else {
                File file2 = new File(DataFiles.INFORMATION_DIR, emailOrAccountOfChattingWithManEncrypted + ".json");
                if (file2.exists()) {
                    try {
                        UserInformation list = UserInformationUtils.read(context, FileUtils.toByteArray(file));
                        nickname = list.nameContent;
                        gender = list.genderContent;
                        whatsup = list.whatsupContent;
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

        LinearLayout leftLayoutP;
        LinearLayout rightLayoutP;
        LinearLayout leftLayoutInsideP;
        LinearLayout rightLayoutInsideP;
        LinearLayout rightAvatarLayoutP;
        ImageView leftMsgP;
        ImageView rightMsgP;
        ImageView leftAvatarP;
        ImageView rightAvatarP;

        public ViewHolder(View view) {
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftLayoutInside = view.findViewById(R.id.left_layout_inside);
            rightLayoutInside = view.findViewById(R.id.right_layout_inside);
            tipLayout = view.findViewById(R.id.tip_layout);
            rightAvatarLayout = view.findViewById(R.id.right_avatar_layout);
            leftMsg = view.findViewById(R.id.left_msg);
            rightMsg = view.findViewById(R.id.right_msg);
            tip = view.findViewById(R.id.tip);
            leftAvatar = view.findViewById(R.id.left_avatar);
            rightAvatar = view.findViewById(R.id.right_avatar);


            leftLayoutP = view.findViewById(R.id.left_layout_picture);
            rightLayoutP = view.findViewById(R.id.right_layout_picture);
            leftLayoutInsideP = view.findViewById(R.id.left_layout_inside_picture);
            rightLayoutInsideP = view.findViewById(R.id.right_layout_inside_picture);
            rightAvatarLayoutP = view.findViewById(R.id.right_avatar_layout_picture);
            leftMsgP = view.findViewById(R.id.left_msg_picture);
            rightMsgP = view.findViewById(R.id.right_msg_picture);
            leftAvatarP = view.findViewById(R.id.left_avatar_picture);
            rightAvatarP = view.findViewById(R.id.right_avatar_picture);
            if (!MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true)) {
                leftAvatar.setVisibility(View.GONE);
                rightAvatarLayout.setVisibility(View.GONE);
                leftAvatarP.setVisibility(View.GONE);
                rightAvatarLayoutP.setVisibility(View.GONE);
            }

            if (MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_MODIFY_THEME, "dark").equals("light")) {
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
            holder.rightLayoutP.setVisibility(View.GONE);
            holder.leftLayoutP.setVisibility(View.GONE);
            try {
                long t = msg.getTime();
                /*String[] time = msg.getContent().split(";");
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
                }*/
                String timeText = Utils.formatTime(t);
                if (!TextUtils.isEmpty(timeText)) {
                    holder.tip.setText(timeText);
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.tipLayout.setVisibility(View.GONE);
            }
        } else if (msg.getType() == MessageItem.TYPE_RECEIVER) {
            holder.leftLayoutInside.setBackgroundResource(MSCRApplication.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_MODIFY_THEME, "dark").equals("light") ? R.drawable.message_receiver_light : R.drawable.message_receiver_dark);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.tipLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.rightLayoutP.setVisibility(View.GONE);

            if (msg.getContentType() == MessageTypes.PICTURE.code) {
                holder.leftLayoutP.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.GONE);
                File file = new File(DataFiles.IMAGES_DIR, msg.getContent());
                try {
                    holder.leftMsgP.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(FileUtils.toByteArray(file)));
                    //holder.rightMsgP.setImageBitmap(FormatTools.getInstance().Bytes2Bitmap(FileUtils.toByteArray(new File(Utils.getDataFilesPath(context), "images" + File.separator + msg.getContent()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.leftMsgP.setOnClickListener((v) -> viewPicture(context, file.getAbsolutePath()));
            } else {
                holder.leftLayoutP.setVisibility(View.GONE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftMsg.setText(msg.getContent());
            }

            if (MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true)) {
                if (msg.getContentType() == MessageTypes.PICTURE.code) {
                    holder.leftAvatarP.setVisibility(View.VISIBLE);
                    if (avatar != null) {
                        holder.leftAvatarP.setImageDrawable(avatar);
                    }
                } else {
                    holder.leftAvatar.setVisibility(View.VISIBLE);
                    if (avatar != null) {
                        holder.leftAvatar.setImageDrawable(avatar);
                    }
                }
            }
            View.OnClickListener var = v -> {
                try {
                    String eoa = "";
                    try {
                        eoa = EnDeCryptTextUtils.decrypt(emailOrAccountOfChattingWithManEncrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showInformationDialog(context, eoa, avatar, !TextUtils.isEmpty(nickname) ? nickname : EnDeCryptTextUtils.decrypt(emailOrAccountOfChattingWithManEncrypted), gender, whatsup);
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

            };
            holder.leftAvatar.setOnClickListener(var);
            holder.leftAvatarP.setOnClickListener(var);
        } else if (msg.getType() == MessageItem.TYPE_SELF) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.GONE);
            holder.leftLayoutP.setVisibility(View.GONE);
            if (msg.getContentType() == MessageTypes.PICTURE.code) {
                holder.rightLayoutP.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
                File file = new File(DataFiles.IMAGES_DIR, msg.getContent());
                try {
                    holder.rightMsgP.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(FileUtils.toByteArray(file)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.rightMsgP.setOnClickListener((v) -> viewPicture(context, file.getAbsolutePath()));
            } else {
                holder.rightLayoutP.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightMsg.setText(msg.getContent());
                holder.rightMsgP.setVisibility(View.GONE);
            }
            View.OnClickListener var = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String eoa = "";
                        if (Variables.ACCOUNT_INFORMATION != null)
                            eoa = EnDeCryptTextUtils.decrypt((String) Variables.ACCOUNT_INFORMATION.getAccountE());
                        showInformationDialog(context, eoa, avatarR, !TextUtils.isEmpty(Variables.ACCOUNT_INFORMATION.getNickname()) ? Variables.ACCOUNT_INFORMATION.getNickname() : (
                                EnDeCryptTextUtils.decrypt(Variables.ACCOUNT_INFORMATION.getAccountE().toString().toUpperCase(), Variables.TEXT_ENCRYPTION_KEY)), Variables.ACCOUNT_INFORMATION.getGender(), Variables.ACCOUNT_INFORMATION.getWhatsup());
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
            };
            holder.rightAvatar.setOnClickListener(var);
            holder.rightAvatarP.setOnClickListener(var);
            if (MSCRApplication.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true)) {
                if (msg.getContentType() == MessageTypes.PICTURE.code) {
                    holder.rightAvatarLayoutP.setVisibility(View.VISIBLE);
                    if (avatarR != null) {
                        holder.rightAvatarP.setImageDrawable(avatarR);
                    }
                } else {
                    holder.rightAvatarLayout.setVisibility(View.VISIBLE);
                    if (avatarR != null) {
                        holder.rightAvatar.setImageDrawable(avatarR);
                    }
                }
            }
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND || msg.getType() == MessageItem.TYPE_FAILED_SEND_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayoutP.setVisibility(View.GONE);
            holder.leftLayoutP.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            String showTip;
            if (msg.getContentType() == MessageTypes.PICTURE.code) {
                showTip = context.getString(R.string.message_type_picture_lower);
            } else {
                showTip = msg.getContent();
            }

            holder.tip.setText(String.format(msg.getType() == MessageItem.TYPE_FAILED_SEND_SO ? context.getString(R.string.chat_tip_failed_send_so) : context.getString(R.string.chat_tip_failed_send), showTip));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_OFFLINE || msg.getType() == MessageItem.TYPE_FAILED_SEND_OFFLINE_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftLayoutP.setVisibility(View.GONE);
            holder.rightLayoutP.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_offline));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED || msg.getType() == MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftLayoutP.setVisibility(View.GONE);
            holder.rightLayoutP.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_not_loggined));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED || msg.getType() == MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftLayoutP.setVisibility(View.GONE);
            holder.rightLayoutP.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_failed_logined));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED || msg.getType() == MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftLayoutP.setVisibility(View.GONE);
            holder.rightLayoutP.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_failed_connect));
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    void showInformationDialog(Context context, String eoa, Drawable avatar, CharSequence name, CharSequence gender, CharSequence whatsup) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (avatar != null) {
            alertDialog.setIcon(avatar);
        }
        alertDialog.setTitle(name);

        alertDialog.setMessage(String.format(context.getString(R.string.dialog_user_information_message), eoa, gender.equals("male") ? context.getString(R.string.preference_account_gender_male) : ((gender.equals("female") ? context.getString(R.string.preference_account_gender_female) : context.getString(R.string.preference_account_gender_summary))), !TextUtils.isEmpty(whatsup) ? whatsup : context.getString(R.string.preference_account_whatsup_summary)));
        alertDialog.show();
    }

    void viewPicture(Context context, String path) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mscr://picture_viewer/view?localPath=" + path));
        context.startActivity(intent);
    }
}