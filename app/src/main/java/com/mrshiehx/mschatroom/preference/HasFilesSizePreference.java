package com.mrshiehx.mschatroom.preference;

import android.content.Context;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.mrshiehx.mschatroom.R;

public class HasFilesSizePreference extends Preference {
    private CharSequence filesSize;
    private TextView filesSizeTV;

    public HasFilesSizePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HasFilesSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HasFilesSizePreference(Context context) {
        super(context);
    }


    public void setFilesSize(CharSequence filesSizeText) {
        filesSize = filesSizeText;
        notifyChanged();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        filesSizeTV = view.findViewById(R.id.textview_files_size);
        if (TextUtils.isEmpty(filesSize)) {
            filesSizeTV.setVisibility(View.GONE);
        } else {
            filesSizeTV.setText(filesSize);
        }
    }

}
