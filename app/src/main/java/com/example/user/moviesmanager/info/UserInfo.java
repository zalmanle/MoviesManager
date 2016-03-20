package com.example.user.moviesmanager.info;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.moviesmanager.R;

/**
 * Created by User on 15/03/2016.
 */
public class UserInfo {

    //region Constants
    private static final String TAG = "TEST";
    //endregion
    //region Instance Variables
    protected Context context;
    //endregion

    //region Constructor
    UserInfo(Context context) {
        this.context = context;
    }
    //endregion
    //region Public Methods
    public void displayInfoMessage(String message){
        makeImageToast(context, R.drawable.info_icon,message,Toast.LENGTH_SHORT).show();
    }
    public void displayWarningMessage(String message){
        makeImageToast(context, R.drawable.red_warning_icon,message, Toast.LENGTH_SHORT).show();
    }
    public void displayLogMessage(String message){
        Log.d(TAG,message);
    }
    //endregion
    //region service methods
    //region CREATE TOAST WITH IMAGE

    //region CREATE TOAST WITH IMAGE
    protected static Toast makeImageToast(Context context, int imageResId,String message, int length) {
        Toast toast = Toast.makeText(context,message, length);

        View rootView = toast.getView();
        LinearLayout linearLayout = null;
        View messageTextView = null;

        // check (expected) toast layout
        if (rootView instanceof LinearLayout) {
            linearLayout = (LinearLayout) rootView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

            if (linearLayout.getChildCount() == 1) {
                View child = linearLayout.getChildAt(0);

                if (child instanceof TextView) {
                    messageTextView = child;
                }
            }
        }

        // cancel modification because toast layout is not what we expected
        if (linearLayout == null || messageTextView == null) {
            return toast;
        }

        ViewGroup.LayoutParams textParams = messageTextView.getLayoutParams();
        ((LinearLayout.LayoutParams) textParams).gravity = Gravity.CENTER_VERTICAL;

        // convert dip dimension
        float density = context.getResources().getDisplayMetrics().density;
        int imageSize = (int) (density * 25 + 0.5f);
        int imageMargin = (int) (density * 15 + 0.5f);

        // setup image view layout parameters
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageParams.setMargins(0, 0, imageMargin, 0);
        imageParams.gravity = Gravity.CENTER_VERTICAL;

        // setup image view
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(imageResId);
        imageView.setLayoutParams(imageParams);

        // modify root layout
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(imageView, 0);

        return toast;
    }
    //endregion
    //endregion
}
