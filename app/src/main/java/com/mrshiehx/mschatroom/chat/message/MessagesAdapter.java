package com.mrshiehx.mschatroom.chat.message;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.beans.UserInformation;
import com.mrshiehx.mschatroom.picture_viewer.screen.PictureViewerScreen;
import com.mrshiehx.mschatroom.shared_variables.DataFiles;
import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.ImageFormatConverter;
import com.mrshiehx.mschatroom.utils.Utils;
import com.mrshiehx.mschatroom.utils.UserInformationUtils;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private final List<MessageItem> mMsgList;
    private final Context context;
    private final MessageDeleter deleter;
    private final SaveFileToLocal saveFileToLocal;
    private final SavePictureToLocal savePictureToLocal;
    private final ShowFileDetailsDialog showFileDetailsDialog;
    private final ShowDownloadFileDialog showDownloadFileDialog;
    Drawable avatar;
    Drawable avatarR;
    String emailOrAccountOfChattingWithManEncrypted;
    /**
     * ChattingWithMan
     */
    String nickname;
    String gender;
    String whatsup;

    public MessagesAdapter(Context context,
                           List<MessageItem> msgList,
                           String receiverAvatarPath,
                           String emailOrAccountOfChattingWithManEncrypted,
                           MessageDeleter messageDeleter,
                           SaveFileToLocal saveFileToLocal,
                           SavePictureToLocal savePictureToLocal,
                           ShowDownloadFileDialog showDownloadFileDialog,
                           ShowFileDetailsDialog showFileDetailsDialog) {
        this.context = context;
        this.mMsgList = msgList;
        this.emailOrAccountOfChattingWithManEncrypted = emailOrAccountOfChattingWithManEncrypted;
        this.deleter = messageDeleter;
        this.saveFileToLocal = saveFileToLocal;
        this.savePictureToLocal = savePictureToLocal;
        this.showDownloadFileDialog = showDownloadFileDialog;
        this.showFileDetailsDialog = showFileDetailsDialog;


        if (MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true)) {
            File file = new File(receiverAvatarPath);
            if (file.exists()) {
                try {
                    avatar = ImageFormatConverter.bytes2Drawable(FileUtils.toByteArray(file));
                    //avatarR = FormatTools.bytes2Drawable(FileUtils.toByteArray(new File(Utils.getDataFilesPath(MSCRApplication.getContext()), "avatar_" + Utils.getAccountInformation().getAccountE())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            File file1 = new File(Utils.getDataFilesPath(MSChatRoom.getContext()), "avatar_" + Utils.getAccountInformation().getAccountE());
            if (file1.exists()) {
                try {
                    //avatar = FormatTools.bytes2Drawable(FileUtils.toByteArray(new File(receiverAvatarPath)));
                    avatarR = ImageFormatConverter.bytes2Drawable(FileUtils.toByteArray(file1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            boolean closed=true;
            if(Variables.ACCOUNT_UTILS != null && Variables.ACCOUNT_UTILS.getConnection() != null) {
                try {
                    closed=Variables.ACCOUNT_UTILS.getConnection().isClosed();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (Utils.isNetworkConnected(context) && Variables.ACCOUNT_UTILS != null && Variables.ACCOUNT_UTILS.getConnection() != null&&!closed) {
                boolean b = true;
                if (Variables.ACCOUNT_UTILS != null) {
                    try {
                        b = Variables.ACCOUNT_UTILS.getConnection().isClosed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!b) {
                    new Thread(() -> {
                        Looper.prepare();
                        try {
                            final String eoaClean;
                            eoaClean = EnDeCryptTextUtils.decrypt(emailOrAccountOfChattingWithManEncrypted);
                            String by = AccountUtils.BY_ACCOUNT;
                            if (Utils.isEmail(eoaClean)) {
                                by = AccountUtils.BY_EMAIL;
                            }
                            byte[] info = Variables.ACCOUNT_UTILS.getBytes(context, "information", by, emailOrAccountOfChattingWithManEncrypted);
                            UserInformation information = UserInformationUtils.read(context, info);
                            nickname = information.nameContent;
                            gender = information.genderContent;
                            whatsup = information.whatsupContent;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.exceptionDialog(context, e, context.getString(R.string.dialog_exception_failed_get_user_information));
                        }
                        Looper.loop();
                    }).start();
                }
            } else {
                File file2 = new File(DataFiles.INFORMATION_DIR, emailOrAccountOfChattingWithManEncrypted+".json");
                if (file2.exists()) {
                    try {
                        UserInformation list = UserInformationUtils.read(context, FileUtils.getFileBytes(file2));
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

        ImageView leftMsgPicture;
        ImageView rightMsgPicture;

        LinearLayout leftMsgFile;
        LinearLayout rightMsgFile;

        LinearLayout leftLayoutInsideSpec;
        LinearLayout rightLayoutInsideSpec;

        TextView left_file_type_name;
        TextView left_file_name;
        TextView left_file_size_tip;
        TextView left_file_size;
        Button left_file_download;
        Button left_file_details;

        TextView right_file_type_name;
        TextView right_file_name;
        TextView right_file_size_tip;
        TextView right_file_size;
        Button right_file_download;
        Button right_file_details;


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

            leftLayoutInsideSpec = view.findViewById(R.id.left_layout_inside_spec);
            rightLayoutInsideSpec = view.findViewById(R.id.right_layout_inside_spec);

            leftMsgPicture = view.findViewById(R.id.left_msg_picture);
            rightMsgPicture = view.findViewById(R.id.right_msg_picture);
            leftMsgFile = view.findViewById(R.id.left_msg_file);
            rightMsgFile = view.findViewById(R.id.right_msg_file);

            left_file_type_name = view.findViewById(R.id.left_file_type_name);
            left_file_name = view.findViewById(R.id.left_file_name);
            left_file_size_tip = view.findViewById(R.id.left_file_size_tip);
            left_file_size = view.findViewById(R.id.left_file_size);
            left_file_download = view.findViewById(R.id.left_file_download);
            left_file_details = view.findViewById(R.id.left_file_details);
            right_file_type_name = view.findViewById(R.id.right_file_type_name);
            right_file_name = view.findViewById(R.id.right_file_name);
            right_file_size_tip = view.findViewById(R.id.right_file_size_tip);
            right_file_size = view.findViewById(R.id.right_file_size);
            right_file_download = view.findViewById(R.id.right_file_download);
            right_file_details = view.findViewById(R.id.right_file_details);

            leftAvatar.setVisibility((!MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true)) ? View.GONE : View.VISIBLE);
            rightAvatarLayout.setVisibility((!MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true)) ? View.GONE : View.VISIBLE);


            if (MSChatRoom.getSharedPreferences().getString(Variables.SHARED_PREFERENCE_THEME, "dark").equals("light")) {
                int black = Color.parseColor("#000000");
                leftMsg.setTextColor(black);

                left_file_type_name.setTextColor(black);
                right_file_type_name.setTextColor(black);
                left_file_name.setTextColor(black);
                right_file_name.setTextColor(black);
                left_file_size_tip.setTextColor(black);
                right_file_size_tip.setTextColor(black);
                left_file_size.setTextColor(black);
                right_file_size.setTextColor(black);

                tipLayout.setBackgroundColor(Color.parseColor("#DCDCDC"));
                leftLayoutInside.setBackgroundResource(R.drawable.message_receiver_light);
                leftLayoutInsideSpec.setBackgroundResource(R.drawable.message_receiver_light);
            } else {
                leftLayoutInside.setBackgroundResource(R.drawable.message_receiver_dark);
                leftLayoutInsideSpec.setBackgroundResource(R.drawable.message_receiver_dark);
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
                long t = msg.getTime();
                String timeText = Utils.formatTime(t);
                if (!TextUtils.isEmpty(timeText)) {
                    holder.tip.setText(timeText);
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.tipLayout.setVisibility(View.GONE);
            }
        } else if (msg.getType() == MessageItem.TYPE_RECEIVER) {
            holder.tipLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);

            /**code for send types*/
            if (msg.getContentType() == MessageTypes.PICTURE.code) {
                holder.leftMsgFile.setVisibility(View.GONE);
                holder.leftLayoutInside.setVisibility(View.GONE);
                File file = new File(DataFiles.IMAGES_DIR, msg.getContent());
                try {
                    holder.leftMsgPicture.setImageDrawable(ImageFormatConverter.bytes2Drawable(FileUtils.toByteArray(file)));
                    //holder.rightMsgPicture.setImageBitmap(FormatTools.bytes2Bitmap(FileUtils.toByteArray(new File(Utils.getDataFilesPath(context), "images" + File.separator + msg.getContent()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.leftMsgPicture.setOnClickListener((v) -> viewPicture(context, file.getAbsolutePath()));
                holder.leftMsgPicture.setOnLongClickListener((var0) -> {
                    more(msg, msg.getContentType());
                    return true;
                });
            } else if (msg.getContentType() == MessageTypes.FILE.code) {
                holder.leftMsgPicture.setVisibility(View.GONE);
                holder.leftLayoutInside.setVisibility(View.GONE);

                FileMessageItem fileMessageItem = (FileMessageItem) msg;
                holder.left_file_name.setText(fileMessageItem.getFileName());
                holder.left_file_size.setText(fileMessageItem.getFormattedFileSize());
                String content = fileMessageItem.getContent();
                File file = new File(DataFiles.FILES_DIR, content);
                if (file.exists()) {
                    holder.left_file_download.setVisibility(View.GONE);
                    holder.left_file_download.setEnabled(false);
                } else {
                    holder.left_file_details.setVisibility(View.GONE);
                    holder.left_file_details.setEnabled(false);
                }
                holder.left_file_download.setOnClickListener((var) -> {
                    showDownloadFileDialog.show(content);
                });
                holder.left_file_details.setOnClickListener((var) -> {
                    showFileDetailsDialog.show(fileMessageItem);
                });
                holder.leftMsgFile.setOnLongClickListener((var0) -> {
                    more(msg, msg.getContentType());
                    return true;
                });
            } else {
                holder.leftLayoutInsideSpec.setVisibility(View.GONE);
                holder.leftMsg.setText(msg.getContent());
                holder.leftLayoutInside.setOnLongClickListener((var0) -> {
                    more(msg, msg.getContentType());
                    return true;
                });
            }

            if (MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true) && avatar != null && holder.leftAvatar.getVisibility() == View.VISIBLE) {
                holder.leftAvatar.setImageDrawable(avatar);
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
        } else if (msg.getType() == MessageItem.TYPE_SELF) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.GONE);
            /**code for send types*/
            if (msg.getContentType() == MessageTypes.PICTURE.code) {
                holder.rightMsgFile.setVisibility(View.GONE);
                holder.rightLayoutInside.setVisibility(View.GONE);
                File file = new File(DataFiles.IMAGES_DIR, msg.getContent());
                try {
                    holder.rightMsgPicture.setImageDrawable(ImageFormatConverter.bytes2Drawable(FileUtils.toByteArray(file)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.rightMsgPicture.setOnClickListener((v) -> viewPicture(context, file.getAbsolutePath()));
                holder.rightMsgPicture.setOnLongClickListener((var0) -> {
                    more(msg, msg.getContentType());
                    return true;
                });
            } else if (msg.getContentType() == MessageTypes.FILE.code) {
                holder.rightMsgPicture.setVisibility(View.GONE);
                holder.rightLayoutInside.setVisibility(View.GONE);


                FileMessageItem fileMessageItem = (FileMessageItem) msg;
                holder.right_file_name.setText(fileMessageItem.getFileName());
                holder.right_file_size.setText(fileMessageItem.getFormattedFileSize());
                String content = fileMessageItem.getContent();
                File file = new File(DataFiles.FILES_DIR, content);
                if (file.exists()) {
                    holder.right_file_download.setVisibility(View.GONE);
                    holder.right_file_download.setEnabled(false);
                } else {
                    holder.right_file_details.setVisibility(View.GONE);
                    holder.right_file_details.setEnabled(false);
                }
                holder.right_file_download.setOnClickListener((var) -> {
                    showDownloadFileDialog.show(content);
                });
                holder.right_file_details.setOnClickListener((var) -> {
                    showFileDetailsDialog.show(fileMessageItem);
                });
                holder.rightMsgFile.setOnLongClickListener((var0) -> {
                    more(msg, msg.getContentType());
                    return true;
                });
            } else {
                holder.rightLayoutInsideSpec.setVisibility(View.GONE);
                holder.rightMsg.setText(msg.getContent());
                holder.rightLayoutInside.setOnLongClickListener((var0) -> {
                    more(msg, msg.getContentType());
                    return true;
                });
            }
            View.OnClickListener var = v -> {
                try {
                    String eoa = "";
                    if (Utils.getAccountInformation() != null)
                        eoa = EnDeCryptTextUtils.decrypt((String) Utils.getAccountInformation().getAccountE());
                    showInformationDialog(context, eoa, avatarR, !TextUtils.isEmpty(Utils.getAccountInformation().getNickname()) ? Utils.getAccountInformation().getNickname() : (
                            EnDeCryptTextUtils.decrypt(Utils.getAccountInformation().getAccountE().toString().toUpperCase(), Variables.TEXT_ENCRYPTION_KEY)), Utils.getAccountInformation().getGender(), Utils.getAccountInformation().getWhatsup());
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
            };
            holder.rightAvatar.setOnClickListener(var);

            if (MSChatRoom.getSharedPreferences().getBoolean(Variables.SHARED_PREFERENCE_SHOW_AVATARS_WHEN_CHATTING, true) && avatarR != null && holder.rightAvatarLayout.getVisibility() == View.VISIBLE && holder.rightAvatar.getVisibility() == View.VISIBLE) {
                holder.rightAvatar.setImageDrawable(avatarR);

            }

        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND || msg.getType() == MessageItem.TYPE_FAILED_SEND_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            String showTip;
            /**code for send types*/
            if (msg.getContentType() == MessageTypes.PICTURE.code) {
                showTip = context.getString(R.string.message_type_picture_lower);
            } else if (msg.getContentType() == MessageTypes.FILE.code) {
                showTip = context.getString(R.string.message_type_file_lower);
            } else {
                showTip = msg.getContent();
            }

            holder.tip.setText(String.format(msg.getType() == MessageItem.TYPE_FAILED_SEND_SO ? context.getString(R.string.chat_tip_failed_send_so) : context.getString(R.string.chat_tip_failed_send), showTip));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_OFFLINE || msg.getType() == MessageItem.TYPE_FAILED_SEND_OFFLINE_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayoutInsideSpec.setVisibility(View.GONE);
            holder.leftLayoutInsideSpec.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_offline));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED || msg.getType() == MessageItem.TYPE_FAILED_SEND_NOT_LOGGINED_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayoutInsideSpec.setVisibility(View.GONE);
            holder.leftLayoutInsideSpec.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_not_loggined));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED || msg.getType() == MessageItem.TYPE_FAILED_SEND_LOGIN_FAILED_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayoutInsideSpec.setVisibility(View.GONE);
            holder.leftLayoutInsideSpec.setVisibility(View.GONE);
            holder.tipLayout.setVisibility(View.VISIBLE);
            holder.tip.setText(context.getString(R.string.chat_tip_failed_logined));
        } else if (msg.getType() == MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED || msg.getType() == MessageItem.TYPE_FAILED_SEND_CONNECT_FAILED_SO) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayoutInsideSpec.setVisibility(View.GONE);
            holder.leftLayoutInsideSpec.setVisibility(View.GONE);
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
            alertDialog.setNeutralButton(R.string.dialog_view_or_modify_avatar_operation_view, (var0, var1) -> context.startActivity(new Intent(context, PictureViewerScreen.class).putExtra("bytes", ImageFormatConverter.drawable2Bytes(avatar))));
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

    /*void more(MessageItem messageItem){
        final CharSequence[] items = {context.getString(R.string.message_options_delete)};
        AlertDialog.Builder listDialog=new AlertDialog.Builder(context);
        listDialog.setItems(items, (dialog, which) -> {
            switch (which){
                case 0:
                    deleter.delete(messageItem);
                    break;
            }
        });
        listDialog.show();
    }

    void moreForImages(MessageItem messageItem){
        final CharSequence[] items = {context.getString(R.string.message_options_delete),context.getString(R.string.message_options_save_to_local)};
        AlertDialog.Builder listDialog=new AlertDialog.Builder(context);
        listDialog.setItems(items, (dialog, which) -> {
            switch (which){
                case 0:
                    deleter.delete(messageItem);
                    break;
                case 1:
                    File file=new File(DataFiles.IMAGES_DIR,messageItem.getContent());
                    if(file.exists()){
                        try {
                            File to = Utils.createLocalPictureFileAndCreate();
                            StreamUtils.copy(file,to);
                            Toast.makeText(context, String.format(context.getString(R.string.toast_successfully_save_picture), to.getAbsolutePath()), Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context,R.string.toast_failed_to_save_file, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, R.string.toast_target_file_not_found, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
        listDialog.show();
    }

    void moreForFile(MessageItem messageItem){
         CharSequence[] items = {context.getString(R.string.message_options_delete),context.getString(R.string.message_options_save_to_local)};
        AlertDialog.Builder listDialog=new AlertDialog.Builder(context);
        listDialog.setItems(items, (dialog, which) -> {
            switch (which){
                case 0:
                    deleter.delete(messageItem);
                    break;
                case 1:
                    File file=new File(DataFiles.IMAGES_DIR,messageItem.getContent());
                    if(file.exists()){
                        try {
                            File to = Utils.createLocalPictureFileAndCreate();
                            StreamUtils.copy(file,to);
                            Toast.makeText(context, String.format(context.getString(R.string.toast_successfully_save_picture), to.getAbsolutePath()), Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context,R.string.toast_failed_to_save_file, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, R.string.toast_target_file_not_found, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
        listDialog.show();
    }*/

    void more(MessageItem messageItem, int type) {
        CharSequence[] items;
        /**code for send types*/
        if (type == MessageTypes.PICTURE.code || type == MessageTypes.FILE.code) {
            items = new CharSequence[]{context.getString(R.string.message_options_delete), context.getString(R.string.message_options_save_to_local)};
        } else/* if(type==MessageTypes.TEXT.code)*/ {
            items = new CharSequence[]{context.getString(R.string.message_options_delete)};
        }

        AlertDialog.Builder listDialog = new AlertDialog.Builder(context);
        listDialog.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    deleter.delete(messageItem);
                    break;
                case 1:
                    /**code for send types*/
                    if (type == MessageTypes.PICTURE.code) {
                        savePictureToLocal.save(messageItem);
                    } else if (type == MessageTypes.FILE.code) {
                        saveFileToLocal.save(messageItem);
                    }
                    break;
            }
        });
        listDialog.show();
    }


    public interface MessageDeleter {
        void delete(MessageItem messageItem);
    }

    public interface SavePictureToLocal {
        void save(MessageItem messageItem);
    }

    public interface SaveFileToLocal {
        void save(MessageItem messageItem);
    }

    public interface ShowDownloadFileDialog {
        void show(String content);
    }

    public interface ShowFileDetailsDialog {
        void show(MessageItem messageItem);
    }
}