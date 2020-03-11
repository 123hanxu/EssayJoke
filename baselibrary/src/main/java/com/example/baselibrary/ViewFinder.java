package com.example.baselibrary;

import android.app.Activity;
import android.view.View;

public class ViewFinder {

    public Activity mActivity;
    public View mView;

    public ViewFinder(Activity activity){
        this.mActivity=activity;
    }

    public ViewFinder(View view) {
        this.mView=view;
    }

    public View findViewById(int viewId) {
        return mActivity!=null ? mActivity.findViewById(viewId):mView.findViewById(viewId);
    }
}
