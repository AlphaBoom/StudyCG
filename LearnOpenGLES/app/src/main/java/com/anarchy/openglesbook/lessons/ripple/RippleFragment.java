package com.anarchy.openglesbook.lessons.ripple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.openglesbook.R;
import com.anarchy.openglesbook.lessons.base.BaseFragment;

import java.io.IOException;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 二月/25/2017  11:54.
 * Description:
 */

public class RippleFragment extends BaseFragment {
    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getResources().getAssets().open("kv_bg.jpg"));
            return new RippleRenderer(getActivity(),bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    ((RippleRenderer) mRenderer).onMove(event);
                return false;
            }
        });
        return view;
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        /*if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }*/

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
