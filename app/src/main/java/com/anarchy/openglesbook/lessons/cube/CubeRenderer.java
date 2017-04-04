package com.anarchy.openglesbook.lessons.cube;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.anarchy.openglesbook.R;
import com.anarchy.openglesbook.utils.OpenGLHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 四月/04/2017  16:12.
 * Description:
 */

class CubeRenderer implements GLSurfaceView.Renderer {
    private final Context mContext;
    private int mProgram;
    private float[] a_vertices = new float[]{
            -0.5f, -0.5f, 0.5f, 1.0f,
            -0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f,
            -0.5f, -0.5f, -0.5f, 1.0f,
            -0.5f, 0.5f, -0.5f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f
    };
    private float[] a_colors = new float[]{
            0, 0, 0, 1f,
            1f, 0, 0, 1f,
            1f, 1f, 0, 1f,
            0, 1f, 0, 1f,
            0, 0, 1f, 1f,
            1f, 0, 1f, 1f,
            1f, 1f, 1f, 1f,
            0, 1f, 1f, 1f
    };
    private float[] vertices = new float[36 * 4];
    private float[] colors = new float[36 * 4];
   /* private short[] indices = new short[]{
            1, 0, 3, 1, 3, 2,
            2, 3, 7, 2, 7, 6,
            3, 0, 4, 3, 4, 7,
            6, 5, 1, 6, 1, 2,
            4, 5, 6, 4, 6, 7,
            5, 4, 0, 5, 0, 1
    };*/
    private FloatBuffer mVertices;
    private FloatBuffer mColors;
    private int mThetaHandle;
    private float[] mTheta = new float[]{0f, 0f, 0f};
    private ShortBuffer mIndices;
    private int index = 0;

    CubeRenderer(Context context) {
        mContext = context;
        colorcube();
        mVertices = ByteBuffer.allocateDirect(4 * vertices.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(vertices);
        mVertices.position(0);
        mColors = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColors.put(colors);
        mColors.position(0);
        /*mIndices = ByteBuffer.allocateDirect(2 * indices.length).order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(indices);
        mIndices.position(0);*/
    }
 private void colorcube(){
     quad(1,0,3,2);
     quad(2,3,7,6);
     quad(3,0,4,7);
     quad(6,5,1,2);
     quad(4,5,6,7);
     quad(5,4,0,1);
 }
    private void quad(int a,int b,int c,int d){
        a *= 4;b*=4;c*=4;d*=4;
        colors[index] = a_colors[a];vertices[index] = a_vertices[a];index++;
        colors[index] = a_colors[a + 1];vertices[index] = a_vertices[a + 1];index++;
        colors[index] = a_colors[a + 2];vertices[index] = a_vertices[a + 2];index++;
        colors[index] = a_colors[a + 3];vertices[index] = a_vertices[a + 3];index++;
        colors[index] = a_colors[b];vertices[index] = a_vertices[b];index++;
        colors[index] = a_colors[b + 1];vertices[index] = a_vertices[b + 1];index++;
        colors[index] = a_colors[b + 2];vertices[index] = a_vertices[b + 2];index++;
        colors[index] = a_colors[b + 3];vertices[index] = a_vertices[b + 3];index++;
        colors[index] = a_colors[c];vertices[index] = a_vertices[c];index++;
        colors[index] = a_colors[c + 1];vertices[index] = a_vertices[c + 1];index++;
        colors[index] = a_colors[c + 2];vertices[index] = a_vertices[c + 2];index++;
        colors[index] = a_colors[c + 3];vertices[index] = a_vertices[c + 3];index++;
        colors[index] = a_colors[a];vertices[index] = a_vertices[a];index++;
        colors[index] = a_colors[a + 1];vertices[index] = a_vertices[a + 1];index++;
        colors[index] = a_colors[a + 2];vertices[index] = a_vertices[a + 2];index++;
        colors[index] = a_colors[a + 3];vertices[index] = a_vertices[a + 3];index++;
        colors[index] = a_colors[c];vertices[index] = a_vertices[c];index++;
        colors[index] = a_colors[c + 1];vertices[index] = a_vertices[c + 1];index++;
        colors[index] = a_colors[c + 2];vertices[index] = a_vertices[c + 2];index++;
        colors[index] = a_colors[c + 3];vertices[index] = a_vertices[c + 3];index++;
        colors[index] = a_colors[d];vertices[index] = a_vertices[d];index++;
        colors[index] = a_colors[d + 1];vertices[index] = a_vertices[d + 1];index++;
        colors[index] = a_colors[d + 2];vertices[index] = a_vertices[d + 2];index++;
        colors[index] = a_colors[d + 3];vertices[index] = a_vertices[d + 3];index++;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgram = OpenGLHelper.createProgram(mContext, R.raw.cube_vertex, R.raw.cube_fragment);
        GLES30.glUseProgram(mProgram);
        //创建顶点数组对象
        int[] vertexArray = new int[1];
        GLES30.glGenVertexArrays(1, vertexArray, 0);
        GLES30.glBindVertexArray(vertexArray[0]);
        //创建缓冲区对象
        int[] buffers = new int[1];
        GLES30.glGenBuffers(1, buffers, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4*mVertices.capacity() + 4*mColors.capacity(), null, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, 4*mVertices.capacity(), mVertices);
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 4*mVertices.capacity(), 4*mColors.capacity(), mColors);
        int vPosition = GLES30.glGetAttribLocation(mProgram,"vPosition");
        GLES30.glEnableVertexAttribArray(vPosition);
        GLES30.glVertexAttribPointer(vPosition, 4, GLES30.GL_FLOAT, false, 0, 0);
        int vColor = GLES30.glGetAttribLocation(mProgram,"vColor");
        GLES30.glEnableVertexAttribArray(vColor);
        GLES30.glVertexAttribPointer(vColor, 4, GLES30.GL_FLOAT, false, 0, 4*mVertices.capacity());
        mThetaHandle = GLES30.glGetUniformLocation(mProgram, "theta");
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glClearColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mTheta[0] += 0.1f;
        if(mTheta[0] > 360){
            mTheta[0] -= 360;
        }
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glUniform3fv(mThetaHandle, 1, mTheta, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,36);
//        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mIndices.capacity(), GLES30.GL_UNSIGNED_SHORT, mIndices);
    }
}
