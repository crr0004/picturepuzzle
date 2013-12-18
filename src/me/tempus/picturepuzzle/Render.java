package me.tempus.picturepuzzle;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import me.tempus.interfaces.RenderHost;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;



public class Render implements Renderer{

	private final int DEFAULT_SIZE = 25;
	
	private int screenHeight;
	private int screenWidth;
	private int rawScreenHeight;
	
	private RenderHost host;
	private Drawable[] drawables;
	private int bufferIndex = 0;
	private int currentPos = 0;
	private int currentOffSet = 0;
	private boolean canDraw = false;

	private int lastBoundTexture;
	
	public Render(){
		drawables  = new Drawable[DEFAULT_SIZE * 2];
	}
	
	public Render(int drawingBufferSize){
		drawables = new Drawable[drawingBufferSize * 2];
	}
	
	public Render(RenderHost host){
		this.host = host;
		drawables  = new Drawable[DEFAULT_SIZE * 2];
	}
	
	public Render(RenderHost host, int drawingBufferSize){
		this.host = host;
		drawables = new Drawable[drawingBufferSize * 2];
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		
		if(!canDraw){
			this.waitThis();
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		canDraw = false;
		
		host.frameStarted(gl);
		final int size = currentPos;
		final int offset = bufferIndex * (drawables.length/2);
		bufferIndex = 1 - bufferIndex;
		currentOffSet = bufferIndex * (drawables.length/2);
		currentPos = 0;
		for(Drawable d : drawables){
			if(d != null){
				d.preDraw(gl);
				d.draw(gl);
				d.postDraw(gl);
			}
		}
		
		host.frameEnded(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);

		gl.glShadeModel(GL10.GL_FLAT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(0, 0, 0, 1);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		screenHeight = height;
		screenWidth = width;
		
		host.screenChanged(gl);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		// TODO Auto-generated method stub
		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0.66f, 0.69f, 0.71f, 1);
		gl.glShadeModel(GL10.GL_FLAT);
		//gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_LIGHTING);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		host.screenCreated(gl);
	}
	
	/**
	 * Loads a texture
	 * @param gl The OpenGL context
	 * @param resourceId The resource ID according to the generated resource IDs (tested against png)
	 * @param Context The activity context used for loading the bitmap out of assets
	 * @return Texture containing ID, width and height
	 */
	private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options(); //Used in texture loading
	public static Texture loadBitmap(GL10 gl, Context context, int resourceId) { // Texture Name
		int height = 0;
		int width = 0;

		sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		int textureName = -1;
		int[] textureNames = new int[1];
		if (context != null && gl != null) {
			gl.glGenTextures(1, textureNames, 0);

			textureName = textureNames[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

			InputStream is = context.getResources().openRawResource(resourceId);
			Bitmap bitmap;

			try {
				bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
				height = bitmap.getHeight();
				width = bitmap.getWidth();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

			bitmap.recycle();
			((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
					GL11Ext.GL_TEXTURE_CROP_RECT_OES, new int[]{0, height, width, -height}, 0);

			int error = gl.glGetError();
			if (error != GL10.GL_NO_ERROR) {
				Log.e("TextureLoading", "Texture Load GLError: " + error);
			}

		}
		return new Texture(textureName, width, height);
	}
	
	public int getRawScreenHeight() {
		return rawScreenHeight;
	}

	public void setRawScreenHeight(int rawScreenHeight) {
		this.rawScreenHeight = rawScreenHeight;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setHost(RenderHost host){
		this.host = host;
	}
	
	/**
	 * A sync and catch way of waiting the thread. Just cleaner code
	 */
	public void waitThis(){
		try{
			synchronized (this) {
				this.wait();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * A sync and catch way of notifying the thread. Just cleaner code
	 */
	public void notifyThis(){
		try{
			synchronized (this) {
				this.notify();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * A way of a adding a whole list of drawables.
	 * This way there is no way of knowing if one of the drawables didn't get added.
	 * @param d
	 */
	public void addDrawables(List<Piece> pieces){
		for(Drawable i : pieces){
			addDrawable(i);
		}
	}
	
	/**
	 * 
	 * @param d Drawable to add to list
	 * @return Returns the index in the array, -1 if it wasn't added
	 */
	public int addDrawable(Drawable d){
		if(currentPos < drawables.length/2){
			drawables[currentPos + currentOffSet] = d;
			currentPos++;
			return (currentPos -1) + currentOffSet;
		}
		return -1;
	}

	public void beginFrame() {
		
		canDraw = true;
		notifyThis();
	}

	/**
	 * A checked way of binding a texture, just prevents re-binding the same texture 
	 * @param gl GL context
	 * @param target The same as bindTexture's target
	 * @param id ID of the texture
	 */
	public void bindTexture(GL10 gl, int id){
		if(lastBoundTexture != id){
			gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
			lastBoundTexture = id;
		}else{
			lastBoundTexture = id;
		}
	}

}
