package com.mrshiehx.mschatroom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class AboutListView extends ListView {
    public AboutListView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public AboutListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public AboutListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    //解决listview高度问题；
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int me = MeasureSpec.makeMeasureSpec(600, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, me);
    }*/

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
