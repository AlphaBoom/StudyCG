package com.anarchy.openglesbook.lessons.sierpinski;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.anarchy.openglesbook.R;
import com.anarchy.openglesbook.utils.OpenGLHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 三月/05/2017  16:01.
 * Description:
 */

public class SierpinskiRenderer implements GLSurfaceView.Renderer {
    private final Context mContext;
    private int mProgram;
    private FloatBuffer mFloatBuffer;
    int count;
    int mBuffer;
    float[] mPoints;
    int index = 0;

    public SierpinskiRenderer(Context context) {
        mContext = context;
        count = 729;
        mPoints = new float[count*3*2];
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgram = OpenGLHelper.createProgram(mContext, R.raw.sierpinski_vertex, R.raw.sierpinski_fragment);
        GLES30.glUseProgram(mProgram);
        //生成数据
        divideTriangle(new float[]{-1.0f,-1.0f},new float[]{0.0f,1.0f},new float[]{1.0f,-1.0f},5);
//        int[] vao = new int[1];
//        GLES30.glGenVertexArrays(1,vao,0);
//        GLES30.glBindVertexArray(vao[0]);
        int[] buffer = new int[1];
        GLES30.glGenBuffers(1,buffer,0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,buffer[0]);
        mBuffer = buffer[0];
//        mFloatBuffer = ByteBuffer.allocateDirect(mPoints.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mFloatBuffer.put(mPoints).position(0);
        mFloatBuffer = FloatBuffer.wrap(mPoints);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,mFloatBuffer.capacity()*4,mFloatBuffer,GLES30.GL_STATIC_DRAW);
        int loc = GLES30.glGetAttribLocation(mProgram,"vPosition");
        GLES30.glEnableVertexAttribArray(loc);
        GLES30.glVertexAttribPointer(loc,2,GLES30.GL_FLOAT,false,0,0);
        GLES30.glClearColor(1.0f,1.0f,1.0f,1.0f);

    }

    private void divideTriangle(float[] a, float[] b, float[] c, int k) {
        if (k > 0) {
            float[] ab = new float[2];
            float[] ac = new float[2];
            float[] bc = new float[2];
            ab[0] = (a[0] + b[0]) / 2;
            ab[1] = (a[1] + b[1]) / 2;
            ac[0] = (a[0] + c[0]) / 2;
            ac[1] = (a[1] + c[1]) / 2;
            bc[0] = (b[0] + c[0]) / 2;
            bc[1] = (b[1] + c[1]) / 2;
            divideTriangle(a, ab, ac, k - 1);
            divideTriangle(c, ac, bc, k - 1);
            divideTriangle(b, bc, ab, k - 1);
        } else {
            triangle(a, b, c);
        }
    }


    private void triangle(float[] a, float[] b, float[] c) {
        mPoints[index] = a[0];
        index++;
        mPoints[index] = a[1];
        index++;
        mPoints[index] = b[0];
        index++;
        mPoints[index] = b[1];
        index++;
        mPoints[index] = c[0];
        index++;
        mPoints[index] = c[1];
        index++;
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,count);
    }
}
