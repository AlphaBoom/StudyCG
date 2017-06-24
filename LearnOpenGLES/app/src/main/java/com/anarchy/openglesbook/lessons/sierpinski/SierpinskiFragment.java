package com.anarchy.openglesbook.lessons.sierpinski;

import android.opengl.GLSurfaceView;

import com.anarchy.openglesbook.lessons.base.BaseFragment;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 三月/05/2017  16:01.
 * Description:
 */

public class SierpinskiFragment extends BaseFragment {
    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new SierpinskiRenderer(getActivity());
    }
}
