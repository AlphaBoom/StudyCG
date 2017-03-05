package com.anarchy.openglesbook.utils;

import android.content.Context;
import android.opengl.GLES30;
import android.support.annotation.IntDef;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * CreateTime: 三月/05/2017  15:42.
 * Description:
 */

public class OpenGLHelper {
    private static final String TAG = "OpenGLHelper";

    @IntDef({GLES30.GL_VERTEX_SHADER, GLES30.GL_FRAGMENT_SHADER})
    @interface ShaderType {

    }


    /**
     * 创建program
     *
     * @param context       Android上下文
     * @param vertexRawId   放在 raw 目录下的顶点glsl文件
     * @param fragmentRawId 放在 raw 目录下片段glsl文件
     * @return 返回程序句柄
     */
    public static int createProgram(Context context, @RawRes int vertexRawId, @RawRes int fragmentRawId) {
        String vertexShaderSource = readTextFileFromRawResource(context, vertexRawId);
        String fragmentShaderSource = readTextFileFromRawResource(context, fragmentRawId);
        //编译shader
        int vertexShader = createShader(GLES30.GL_VERTEX_SHADER,vertexShaderSource);
        int fragmentShader = createShader(GLES30.GL_FRAGMENT_SHADER,fragmentShaderSource);
        //创建程序
        int program = GLES30.glCreateProgram();
        if(program == 0) return 0;
        GLES30.glAttachShader(program,vertexShader);
        GLES30.glAttachShader(program,fragmentShader);
        GLES30.glLinkProgram(program);
        int[] linked = new int[1];
        GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS,linked,0);
        if(linked[0] == 0){
            Log.e(TAG,GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program);
            return 0;
        }
        return program;
    }

    /**
     * 创建shader数据
     *
     * @param type         顶点或者片段
     * @param shaderSource shader代码
     * @return shader句柄
     */
    public static int createShader(@ShaderType int type, String shaderSource) {
        int[] complied = new int[1];
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderSource);
        GLES30.glCompileShader(shader);
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, complied, 0);
        if (complied[0] == 0) {
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }


    /**
     * 从raw 目录下读取glsl的文件内容
     *
     * @param context Android上下文
     * @param resId   文件id
     * @return 源文件信息
     */
    public static String readTextFileFromRawResource(Context context, @RawRes int resId) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resId)));
        String line;
        StringBuilder body = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                body.append(line).append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return body.toString();
    }
}
