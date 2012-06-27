package com.diamond;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.WakeLockOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.util.DisplayMetrics;

//import android.os.Bundle;

public class DiamondMActivity extends BaseGameActivity {
    /** Called when the activity is first created. */
	
	   private static final int CAMERA_WIDTH = 800;
	   private static final int CAMERA_HEIGHT = 480;
	   public static final float ELEMENT_HEIGHT = 64;
	   
	   public static Context context;
	    // ===========================================================
	    // Fields
	    // ===========================================================
	   public static Scene scene;
//	    private Camera mCamera;
	   
	   public static BitmapTextureAtlas ElementsTextureAtlas;

	   private BitmapTextureAtlas mAutoParallaxBackgroundTexture;

	   private TextureRegion mParallaxLayerBack;
	   private TextureRegion mParallaxLayerBack1;

	   public static TextureRegion ElementTRegion1;
	   public static TextureRegion ElementTRegion2;
	   public static TextureRegion ElementTRegion3;
	   public static TextureRegion ElementTRegion4;
	   public static TextureRegion ElementTRegion[];

	   public static SquareManager GameManager;
	   
	   public static Sprite steklo;
	    // ===========================================================
	    // Constructors
	    // ===========================================================
	   public static Engine mEngine;

	    // ===========================================================
	    // Getter & Setter
	    // ===========================================================


	    // ===========================================================
	    // Methods for/from SuperClass/Interfaces
	    // ===========================================================

	
	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Engine onLoadEngine() {
        //this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        //return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
		context = this.getApplicationContext();
		
		Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		final EngineOptions options = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
						(float) dm.widthPixels, (float) dm.heightPixels),
				camera).setWakeLockOptions(WakeLockOptions.SCREEN_ON);

		options.getRenderOptions().disableExtensionVertexBufferObjects();

		options.getTouchOptions().setRunOnUpdateThread(true);

		mEngine = new Engine(options);
		return mEngine;

	}

	@Override
	public void onLoadResources() {
		 BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
         
		 this.ElementsTextureAtlas = new BitmapTextureAtlas(512, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		 
         this.mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.DEFAULT);
         this.mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "Back1.png", 0, 0);
   //      this.mParallaxLayerBack1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_back1.png", 0, 481);
  
         ElementTRegion = new TextureRegion[6];
         for (int i = 0; i < 6; i++)
         {
        	  
        	 ElementTRegion[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset
                	 (ElementsTextureAtlas, this, "Element"+i+".png", i*70, 0);
        	 
         }
        	 
         
         this.mEngine.getTextureManager().loadTextures(this.mAutoParallaxBackgroundTexture, ElementsTextureAtlas);

	}

	@Override
	public Scene onLoadScene() {
	    this.mEngine.registerUpdateHandler(new FPSLogger());


        scene = new Scene();
        
        
        
//        GameManager = new SquareManager(CAMERA_WIDTH/2-CAMERA_HEIGHT/2, 0, 
//        		CAMERA_HEIGHT, CAMERA_HEIGHT, mParallaxLayerBack);
//        
        GameManager = new SquareManager(CAMERA_WIDTH-CAMERA_HEIGHT, 0, 
        		CAMERA_HEIGHT, CAMERA_HEIGHT, mParallaxLayerBack);
        //Sprite1.registerEntityModifier(new MoveModifier(30, 0, CAMERA_WIDTH - Sprite1.getWidth(), 0, CAMERA_HEIGHT - Sprite1.getHeight()));

    scene.registerTouchArea(GameManager);
    
    GameManager.setZIndex(0);
    scene.attachChild(GameManager);
    scene.sortChildren();
    
	scene.registerUpdateHandler(new TimerHandler(1f / 2f, true,
			new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler arg0) {
					
										
					if (GameManager.CanClean && GameManager.CleanListCounter == 0)
					{
						GameManager.CanMove = false;
						GameManager.MoveDetected = true;
						
						
						GameManager.CanClean = false;
						GameManager.CollectToRemove();
						GameManager.CalculateScore();
						GameManager.CleanGame();
						
							           	
				       	GameManager.ListAddedOnTop.clear();
				       	
						GameManager.CanMove = true;
						GameManager.MoveDetected = false;
						
						GameManager.CanClean = GameManager.FindCoinsidence();
						
					}
					
					
				}

			}));
     
        return scene;
	}
}