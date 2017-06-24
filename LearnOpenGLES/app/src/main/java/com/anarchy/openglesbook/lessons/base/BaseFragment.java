package com.anarchy.openglesbook.lessons.base;

import android.app.Fragment;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.openglesbook.R;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 一月/14/2017  18:17.
 * Description:
 */

public abstract class BaseFragment extends Fragment {
    protected GLSurfaceView mGLSurfaceView;
    protected GLSurfaceView.Renderer mRenderer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGLSurfaceView = (GLSurfaceView) inflater.inflate(R.layout.fragment_bsae,container,false);
        mGLSurfaceView.setEGLContextClientVersion(3);
        init(mGLSurfaceView,savedInstanceState);
        mRenderer = getRenderer();
        mGLSurfaceView.setRenderer(mRenderer);
        return mGLSurfaceView;
    }

    protected void init(GLSurfaceView glSurfaceView,Bundle savedInstanceState){

    }

    protected abstract GLSurfaceView.Renderer getRenderer();


    @Override
    public void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
