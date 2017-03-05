package com.anarchy.openglesbook.lessons.lesson1;

import android.opengl.GLSurfaceView;
import com.anarchy.openglesbook.lessons.base.BaseFragment;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 一月/14/2017  18:37.
 * Description:使用OpenGL ES 3.0 入门绘制三角形
 */
public class Lesson1Fragment extends BaseFragment {

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new TriangleRenderer();
    }
}
