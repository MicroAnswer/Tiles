package pit.opengles;

import android.content.Context;
import android.content.res.Configuration;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLESPlaneAnimatedRenderer implements GLSurfaceView.Renderer {

    private Context _mContext;
    private Shader _mShader;
    private Camera _mCamera;
    private Vector3f _mPlanePosition = new Vector3f(0, 0, 0);
    private Transform _mPlaneTransform = new Transform(
            _mPlanePosition.x,
            _mPlanePosition.y,
            _mPlanePosition.z,
            1f,
            1f,
            1f,
            0.646875f,
            1.15f,
            1,
            0
    );

    private int _mAutumn = 0;
    private int _mPink = 0;
    private int _mWinterWonderland = 0;
    private int _mColorful = 0;
    private int _mRed = 0;
    private int _mGreen = 0;
    private int _mBlue = 0;
    private int _mTexture = 0;
    private int _photo = 0;

    private boolean red = false, blue = false, green = false, colorful = true, pink = false, autumn = false, winterwonderland = false, photo = false;

    private Vector2f _mOffset = new Vector2f(0, 0);

     private Plane plane;

    public GLESPlaneAnimatedRenderer(Context context) {
        _mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed, EGLConfig config) {
        _mShader = new Shader(_mContext);
        _mCamera = new Camera();
        plane = new Plane();


        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        createVisuals();
    }

    private void createVisuals() {
         // vertices
         float[] vertices = plane.vertices;
         FloatBuffer _mVertexBuffer = floatToBuffer(vertices);
         // texCoords
         float[] texCoords = plane.texCoords;
         FloatBuffer _mTexCoordBuffer = floatToBuffer(texCoords);
         _mVertexBuffer.position(0);
         GLES20.glEnableVertexAttribArray(0);
         GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, _mVertexBuffer);

         _mTexCoordBuffer.position(0);
         GLES20.glEnableVertexAttribArray(1);
         GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, _mTexCoordBuffer);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);

        _mRed              = ResourceLoader.loadTexture(_mContext, R.drawable.red);
        _mGreen            = ResourceLoader.loadTexture(_mContext, R.drawable.green);
        _mBlue             = ResourceLoader.loadTexture(_mContext, R.drawable.blue);
        _mColorful         = ResourceLoader.loadTexture(_mContext, R.drawable.colorful);
        _mPink             = ResourceLoader.loadTexture(_mContext, R.drawable.pink);
        _mAutumn           = ResourceLoader.loadTexture(_mContext, R.drawable.autumn);
        _mWinterWonderland = ResourceLoader.loadTexture(_mContext, R.drawable.winterwonderland);
        _photo             = ResourceLoader.loadTexture(_mContext, R.drawable.photo);

        if (red) {
            _mTexture = _mRed;
        } else if (green) {
            _mTexture = _mGreen;
        } else if (blue) {
            _mTexture = _mBlue;
        } else if (colorful) {
            _mTexture = _mColorful;
        } else if (pink) {
            _mTexture = _mPink;
        } else if (autumn) {
            _mTexture = _mAutumn;
        } else if (winterwonderland) {
            _mTexture = _mWinterWonderland;
        } else if (photo) {
            _mTexture = _photo;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        _mCamera.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 notUsed) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        DrawModel();
    }

    private void DrawModel() {
        GLES20.glUseProgram(_mShader.getMainProgram());

         float[] MVPMatrix = new float[16];
         android.opengl.Matrix.multiplyMM(MVPMatrix, 0, _mCamera.getProjectionMatrix(), 0, _mCamera.getViewMatrix(), 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "MVMatrix"), 1, false, MVPMatrix, 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "modelMatrix"), 1, false, _mPlaneTransform.getModelMatrix(), 0);

        GLES20.glUniform2fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "offset"), 1, _mOffset.get(), 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _mTexture);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "texture"), 1);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    public void parallaxMove(float x, float y, boolean reversed, boolean touch) {
        if (_mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!touch) {
                float temp;
                if (reversed) {
                    temp = y;
                    y = -x;
                } else {
                    temp = -y;
                    y = x;
                }
                x = temp;
            } else {
                float temp;
                if (reversed) {
                    temp = y;
                    y = -x;
                } else {
                    temp = y;
                    y = x;
                }
                x = temp;
            }
        }

        _mOffset.x += (x * (0.0005f));
        _mOffset.y -= (y * (0.0005f));
        float MAX_OFFSET = .1f;
        _mOffset.x = clampf(_mOffset.x, -MAX_OFFSET, MAX_OFFSET);
        _mOffset.y = clampf(_mOffset.y, -MAX_OFFSET, MAX_OFFSET);
    }

    private float clampf(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    private FloatBuffer floatToBuffer(float[] array) {
        int sizeOfFloat = 4;
        FloatBuffer fb = ByteBuffer.allocateDirect(array.length * sizeOfFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);

        return fb;
    }

    public void switchColors(String newColor) {
        switch (newColor) {
            case "RED":
                _mTexture = _mRed;
                red = true;
                blue = green = colorful = winterwonderland = pink = autumn = photo = false;
                break;
            case "BLUE":
                _mTexture = _mBlue;
                blue = true;
                red = green = colorful = winterwonderland = pink = autumn = photo = false;
                break;
            case "GREEN":
                _mTexture = _mGreen;
                green = true;
                red = blue = colorful = winterwonderland = pink = autumn = photo = false;
                break;
            case "COLORFUL":
                _mTexture = _mColorful;
                colorful = true;
                red = green = blue = winterwonderland = pink = autumn = photo = false;
                break;
            case "PINK":
                _mTexture = _mPink;
                pink = true;
                red = green = blue = colorful = winterwonderland = autumn = photo = false;
                break;
            case "AUTUMN":
                _mTexture = _mAutumn;
                autumn = true;
                red = green = blue = colorful = winterwonderland = pink = photo = false;
                break;
            case "WINTER WONDERLAND":
                _mTexture = _mWinterWonderland;
                winterwonderland = true;
                red = green = blue = colorful = pink = autumn = photo = false;
                break;
            case "PHOTO":
                _mTexture = _photo;
                photo = true;
                red = green = blue = colorful = pink = autumn = winterwonderland = false;
                break;
        }
    }
}