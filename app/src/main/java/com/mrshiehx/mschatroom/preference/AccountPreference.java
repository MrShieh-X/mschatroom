package com.mrshiehx.mschatroom.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrshiehx.mschatroom.R;

import java.lang.reflect.Field;

//临时账户Preference
public class AccountPreference extends Preference {

    private PreferenceActivity parent;
    private ImageView accountIcon;
    private TextView accountName;
    private TextView accountSummary;

    private CharSequence mTitle;
    private int mTitleRes;
    private CharSequence mSummary;
    private int mIconResId;
    private Drawable mIcon;

    private boolean mSingleLineTitle = true;
    private boolean mHasSingleLineTitleAttr;

    private boolean mIconSpaceReserved;
    private boolean mShouldDisableView = true;

    public AccountPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AccountPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AccountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        return LayoutInflater.from(getContext()).inflate(R.layout.preference_account,
                parent, false);
    }
    /*void setActivity(PreferenceActivity parent) {
        this.parent = parent;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }
*/

    public void setAccountIcon(Drawable drawable) {
        mIcon = drawable;
        notifyChanged();
    }


    public void setAccountIcon(int iconIesId) {
        mIconResId = iconIesId;
        notifyChanged();
    }


    public void setAccountName(String name) {
        setTitle(name);
    }


    public void setAccountName(CharSequence name) {
        setTitle(name);
    }


    public void setAccountName(int nameResId) {
        setTitle(nameResId);
    }

    public void setAccountSummary(String summary) {
        setSummary(summary);
    }


    public void setAccountSummary(CharSequence summary) {
        setSummary(summary);
    }


    public void setAccountSummary(int summaryResId) {
        setSummary(summaryResId);
    }

    public Drawable getAccountIcon() {
        return getIcon();
    }

    public int getAccountTitleRes() {
        return getTitleRes();
    }

    public CharSequence getAccountTitle() {
        return getTitle();
    }

    public CharSequence getAccountSummary() {
        return getSummary();
    }

    /*@Override
    protected void onBindView(View view) {
        super.onBindView(view);
        //SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //int backgroundPosition = mPerferences.getInt("background", R.drawable.bg_1);
        ImageView accountIcon = (ImageView)view.findViewById(R.id.account_icon);
        TextView accountName = (TextView)view.findViewById(R.id.account_name);
        TextView accountSummary = (TextView)view.findViewById(R.id.account_summary);
        //preview_img.setImageResource(backgroundPosition);
    }*/


    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        final TextView titleView = (TextView) view.findViewById(R.id.account_name);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
                if (mHasSingleLineTitleAttr) {
                    titleView.setSingleLine(mSingleLineTitle);
                }
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        final TextView summaryView = (TextView) view.findViewById(R.id.account_summary);
        if (summaryView != null) {
            final CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            } else {
                summaryView.setVisibility(View.GONE);
            }
        }
        final ImageView imageView = (ImageView) view.findViewById(R.id.account_icon);
        if (imageView != null) {
            if (mIconResId != 0 || mIcon != null) {
                if (mIcon == null) {
                    mIcon = getContext().getDrawable(mIconResId);
                }
                if (mIcon != null) {
                    imageView.setImageDrawable(mIcon);
                }
            }
            if (mIcon != null) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(mIconSpaceReserved ? View.INVISIBLE : View.GONE);
            }
        }
        int id = 0;
        try {
            Class c = Class.forName("com.android.internal.R$id");
            Object obj = c.newInstance();
            Field field = c.getField("icon_frame");
            id = field.getInt(obj);
        } catch (Exception e) {

        }
        final View imageFrame = view.findViewById(id);
        if (imageFrame != null) {
            if (mIcon != null) {
                imageFrame.setVisibility(View.VISIBLE);
            } else {
                imageFrame.setVisibility(mIconSpaceReserved ? View.INVISIBLE : View.GONE);
            }
        }

        if (mShouldDisableView) {
            setEnabledStateOnViews(view, isEnabled());
        }
    }

    public void setEnabledStateOnViews(View v, boolean enabled) {
        v.setEnabled(enabled);

        if (v instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) v;
            for (int i = vg.getChildCount() - 1; i >= 0; i--) {
                setEnabledStateOnViews(vg.getChildAt(i), enabled);
            }
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
    }
}