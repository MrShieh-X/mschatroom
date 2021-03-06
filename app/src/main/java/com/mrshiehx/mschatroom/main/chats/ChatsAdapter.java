package com.mrshiehx.mschatroom.main.chats;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import com.mrshiehx.mschatroom.R;
import com.mrshiehx.mschatroom.Variables;
import com.mrshiehx.mschatroom.utils.EnDeCryptTextUtils;
import com.mrshiehx.mschatroom.utils.FileUtils;
import com.mrshiehx.mschatroom.utils.ImageFormatConverter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ChatsAdapter extends ArrayAdapter {
    public ChatsAdapter(Context context, int resource, List<ChatItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatItem chatItem = (ChatItem) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_main_screen_chat, null);

        ImageView avatar = view.findViewById(R.id.avatar);
        TextView name = view.findViewById(R.id.name);
        TextView latestMsg = view.findViewById(R.id.latest_msg);
        TextView date = view.findViewById(R.id.date);


        File avatarFile = chatItem.getAvatarFile();

        if (avatarFile.exists()) {
            try {
                avatar.setImageDrawable(ImageFormatConverter.bytes2Drawable(FileUtils.toByteArray(avatarFile)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(chatItem.getName())) {
            try {
                name.setText(EnDeCryptTextUtils.decrypt(chatItem.getName(), Variables.TEXT_ENCRYPTION_KEY));
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
        } else {
            try {
                name.setText(EnDeCryptTextUtils.decrypt(chatItem.getEmailOrAccount(), Variables.TEXT_ENCRYPTION_KEY));
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
        if (!TextUtils.isEmpty(chatItem.getLatestMsg())) {
            latestMsg.setText(chatItem.getLatestMsg());
        }
        if (!TextUtils.isEmpty(chatItem.getLatestMsgDate())) {
            date.setText(chatItem.getLatestMsgDate());
        }

        return view;
    }
}