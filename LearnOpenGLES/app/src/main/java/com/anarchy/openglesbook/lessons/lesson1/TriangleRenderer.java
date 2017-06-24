package com.anarchy.openglesbook.lessons.lesson1;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 一月/14/2017  19:00.
 * Description:
 */

public class TriangleRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = TriangleRenderer.class.getSimpleName();
    private int mProgramObject;
    private FloatBuffer mVertices;
    private final float[] mVerticesData =
            { 0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f };
    public TriangleRenderer() {
        mVertices = ByteBuffer.allocateDirect ( mVerticesData.length * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mVertices.put ( mVerticesData ).position ( 0 );
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vShaderStr =
                "#version 300 es 			  \n"
                        +   "in vec4 vPosition;           \n"
                        + "void main()                  \n"
                        + "{                            \n"
                        + "   gl_Position = vPosition;  \n"
                        + "}                            \n";

        String fShaderStr =
                "#version 300 es		 			          	\n"
                        + "precision mediump float;					  	\n"
                        + "out vec4 fragColor;	 			 		  	\n"
                        + "void main()                                  \n"
                        + "{                                            \n"
                        + "  fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );	\n"
                        + "}                                            \n";
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER,vShaderStr);
        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER,fShaderStr);
        programObject = GLES30.glCreateProgram();
        if(programObject == 0) return;
        GLES30.glAttachShader(programObject,vertexShader);
        GLES30.glAttachShader(programObject,fragmentShader);

        GLES30.glBindAttribLocation(programObject,0,"vPosition");
        GLES30.glLinkProgram(programObject);
        GLES30.glGetProgramiv(programObject,GLES30.GL_LINK_STATUS,linked,0);
        if(linked[0] == 0){
            Log.e(TAG,"Error linking program:");
            Log.e(TAG,GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteShader(programObject);
            return;
        }

        mProgramObject = programObject;
        GLES30.glClearColor(1.0f,1.0f,1.0f,0.0f);
    }


    private int loadShader(int type,String shaderSrc){
        int shader;
        int[] compiled = new int[1];
        shader = GLES30.glCreateShader(type);
        if(shader == 0){
            return 0;
        }

        GLES30.glShaderSource(shader,shaderSrc);
        GLES30.glCompileShader(shader);
        GLES30.glGetShaderiv(shader,GLES30.GL_COMPILE_STATUS,compiled,0);
        if(compiled[0] == 0){
            Log.e(TAG,GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgramObject);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,0,mVertices);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,3);
    }
}
