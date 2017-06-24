package com.anarchy.openglesbook.lessons.ripple;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.anarchy.openglesbook.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 二月/25/2017  11:56.
 * Description:
 */

public class RippleRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "RippleRenderer";
    private Context mContext;
    private int resolution = 512;
    private int dropRadius = 20;
    private float perturbance = 0.03f;
    private boolean interactive = true;
    private float[] textureDelta;
    private int[] textures = new int[2];
    private int[] frameBuffers = new int[2];
    private int[] quad = new int[1];
    private int[] backgroundTexture = new int[1];
    private int dropProgram;
    private int updateProgram;
    private int renderProgram;
    private Bitmap mBackground;
    private FloatBuffer topLeft;
    private FloatBuffer rightBottom;
    private FloatBuffer containerRatio;
    private int bufferWriteIndex = 0;
    private int bufferReadIndex = 1;
    private boolean running = false;
    int width;
    int height;
    int centerHandle;
    int radiusHandle;
    int strengthHandle;

    private float[] mVerticesData = new float[]{
            -1,-1,
            +1,-1,
            +1,+1,
            -1,+1
    };
    private FloatBuffer mVertices;

    public RippleRenderer(Context context,Bitmap bitmap) {
        mContext = context;
        textureDelta = new float[]{1f / resolution, 1f / resolution};
        mBackground = bitmap;

        mVertices  = ByteBuffer.allocateDirect(mVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glGenTextures(2, textures, 0);
        GLES30.glGenFramebuffers(2, frameBuffers, 0);
        for (int i = 0; i < 2; i++) {
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,frameBuffers[i]);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, resolution, resolution, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            Log.d(TAG,"texture:" + textures[i]);
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textures[i], 0);
        }
        GLES30.glGenBuffers(1, quad, 0);
        Log.d(TAG,"quad:"+ quad[0]);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, quad[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,mVerticesData.length * 4,mVertices, GLES30.GL_STATIC_DRAW);
        initShaders();
        initTexture();
        setTransparentTexture();
        loadImage();
        GLES30.glClearColor(0,0,0,0);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
        computeTextureBoundaries();
    }

    private int perturbanceHandle;
    private int topLeftHandle;
    private int rightBottomHandle;
    private int containRateHandle;
    private int backgroundHandle;
    private int sampleHandle;

    private void initShaders(){
        dropProgram = createProgram(loadShader(GLES30.GL_VERTEX_SHADER,R.raw.vertex_drop),loadShader(GLES30.GL_FRAGMENT_SHADER,R.raw.fragment_drop));
        updateProgram = createProgram(loadShader(GLES30.GL_VERTEX_SHADER,R.raw.vertex_drop),loadShader(GLES30.GL_FRAGMENT_SHADER,R.raw.fragment_update));
        GLES30.glUniform2fv(GLES30.glGetUniformLocation(updateProgram,"delta"),1,textureDelta,0);
        renderProgram = createProgram(loadShader(GLES30.GL_VERTEX_SHADER,R.raw.vertex_render),loadShader(GLES30.GL_FRAGMENT_SHADER,R.raw.fragment_render));
        GLES30.glUniform2fv(GLES30.glGetUniformLocation(renderProgram,"delta"),1,textureDelta,0);
        Log.d(TAG,"program:"+dropProgram + "," + updateProgram + "," + renderProgram);
        Log.d(TAG,"delta:"+GLES30.glGetUniformLocation(updateProgram,"delta") + "," + GLES30.glGetUniformLocation(renderProgram,"delta"));
        centerHandle = GLES30.glGetUniformLocation(dropProgram,"center");
        radiusHandle = GLES30.glGetUniformLocation(dropProgram,"radius");
        strengthHandle = GLES30.glGetUniformLocation(dropProgram,"strength");
        perturbanceHandle = GLES30.glGetUniformLocation(renderProgram,"perturbance");
        topLeftHandle = GLES30.glGetUniformLocation(renderProgram,"topLeft");
        rightBottomHandle = GLES30.glGetUniformLocation(renderProgram,"bottomRight");
        containRateHandle = GLES30.glGetUniformLocation(renderProgram,"containerRatio");
        backgroundHandle = GLES30.glGetUniformLocation(renderProgram,"samplerBackground");
        sampleHandle = GLES30.glGetUniformLocation(renderProgram,"samplerRipples");
    }


    private int createProgram(int vertexShader,int fragmentShader){
        Log.d(TAG,"vertex:" + vertexShader+" fragment:" + fragmentShader);
        int[] linked = new int[1];
        int program = GLES30.glCreateProgram();
        if(program == 0) return 0;
        GLES30.glAttachShader(program,vertexShader);
        GLES30.glAttachShader(program,fragmentShader);
        GLES30.glLinkProgram(program);
        GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS,linked,0);
        if(linked[0] == 0){
            Log.e(TAG,GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program);
            return 0;
        }
        GLES30.glUseProgram(program);
        GLES30.glEnableVertexAttribArray(0);
        return program;
    }

    private int loadShader(int type,int resId){
        int[] compiled = new int[1];
        int shader = GLES30.glCreateShader(type);
        if (shader == 0) return 0;
        GLES30.glShaderSource(shader, readTextFileFromRawResource(mContext, resId));
        GLES30.glCompileShader(shader);
        GLES30.glGetShaderiv(shader,GLES30.GL_COMPILE_STATUS,compiled,0);
        if(compiled[0] == 0){
            Log.e(TAG,GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }


    private void initTexture(){
        GLES30.glGenTextures(1,backgroundTexture,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,backgroundTexture[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
    }


    private void setTransparentTexture(){
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,backgroundTexture[0]);
//        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGBA,0,0,0,GLES30.GL_RGBA,GLES30.GL_UNSIGNED_BYTE,IntBuffer.wrap(new int[32*32]).position(0));
    }


    private void loadImage(){
        if(mBackground == null) return;
        int wrapping = (isPowerOfTwo(mBackground.getWidth()) && isPowerOfTwo(mBackground.getHeight())) ? GLES30.GL_REPEAT : GLES30.GL_CLAMP_TO_EDGE;
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,backgroundTexture[0]);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,mBackground,0);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_S,wrapping);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,wrapping);
        Log.d(TAG,"load image:("+mBackground.getWidth()+","+mBackground.getHeight()+") id:" + backgroundTexture[0]);
    }



    private boolean isPowerOfTwo(int x){
        return (x & (x -1 )) == 0;
    }


    private static String readTextFileFromRawResource(Context context, int resId){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resId)));
        String line;
        StringBuilder body = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null){
                body.append(line).append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return body.toString();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }
    private long lastTime;

    @Override
    public void onDrawFrame(GL10 gl) {
        computeTextureBoundaries();
        if(running){
            update();
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastTime > 1000) {
                drop((float) Math.random() * 1000, (float) Math.random() * 1000, dropRadius, (float) (Math.random() * 0.1));
                lastTime = currentTime;
            }
        }
        render();
    }


    private void computeTextureBoundaries(){
        topLeft = FloatBuffer.wrap(new float[]{0.4f,0.4f});
        topLeft.position(0);
        rightBottom = FloatBuffer.wrap(new float[]{0.8f,0.8f});
        rightBottom.position(0);
        float maxSide = Math.max(width,height);
        containerRatio = FloatBuffer.wrap(new float[]{width/maxSide,height/maxSide});
        containerRatio.position(0);
    }


    private void update(){
        GLES30.glViewport(0,0,resolution,resolution);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,frameBuffers[bufferWriteIndex]);
        GLES30.glClearColor(0,0,0,0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if(status != GLES30.GL_FRAMEBUFFER_COMPLETE){
            Log.e(TAG,"framebuffer error:"+status);
            return;
        }
        bindTexture(textures[bufferReadIndex],0);
        GLES30.glUseProgram(updateProgram);
        drawQuad();
        swapBufferIndices();
    }

    private void swapBufferIndices(){
        bufferWriteIndex = 1 - bufferWriteIndex;
        bufferReadIndex = 1 - bufferReadIndex;
    }


    private void render(){
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
        GLES30.glViewport(0,0,width,height);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glClearColor(0,0,0,0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glUseProgram(renderProgram);
        bindTexture(backgroundTexture[0],0);
        bindTexture(textures[0],1);
        GLES30.glUniform1f(perturbanceHandle,perturbance);
        GLES30.glUniform2fv(topLeftHandle,1,topLeft);//error
        GLES30.glUniform2fv(rightBottomHandle,1,rightBottom);//error
        GLES30.glUniform2fv(containRateHandle,1,containerRatio);
        GLES30.glUniform1i(backgroundHandle,0);
        GLES30.glUniform1i(sampleHandle,1);
        drawQuad();
        GLES30.glDisable(GLES30.GL_BLEND);
    }

    private void  drawQuad(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,quad[0]);
        GLES30.glVertexAttribPointer(0,2,GLES30.GL_FLOAT,false,0,0);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN,0,4);
//        GLES30.glVertexAttribPointer(0,2,GLES30.GL_FLOAT,false,0,mVertices);
//        GLES30.glEnableVertexAttribArray(0);
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN,0,4);
    }

    private void bindTexture(int texture,int unit){
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + unit);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture);
    }

    public void onMove(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            running = !running;
//            drop(200, 200, dropRadius, 0.01f);
//            Log.d(TAG,"down");
        }
    }



    private void drop(float x,float y,float radius,float strength){
        int longestSide = Math.max(width,height);
        radius = radius/longestSide;
        float[] dropPositionData = new float[]{
                (2 * x - width)/longestSide,
                (height - 2 * y)/longestSide
        };
        FloatBuffer dropPosition = ByteBuffer.allocateDirect(dropPositionData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        dropPosition.put(dropPositionData).position(0);
        GLES30.glViewport(0,0,resolution,resolution);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,frameBuffers[bufferWriteIndex]);
        GLES30.glClearColor(0,0,0,0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        bindTexture(textures[bufferReadIndex],0);
        GLES30.glUseProgram(dropProgram);
        GLES30.glUniform2fv(centerHandle,1,dropPosition);
        GLES30.glUniform1f(radiusHandle,radius);
        GLES30.glUniform1f(strengthHandle,strength);
        drawQuad();
        swapBufferIndices();
    }


}
