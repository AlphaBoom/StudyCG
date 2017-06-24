package com.anarchy.openglesbook.lessons.cube;

import android.opengl.GLSurfaceView;

import com.anarchy.openglesbook.lessons.base.BaseFragment;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 四月/04/2017  16:12.
 * Description:
 */

public class CubeFragment extends BaseFragment {
    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new CubeRenderer(getActivity());
    }
}
