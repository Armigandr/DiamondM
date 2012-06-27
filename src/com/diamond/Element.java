package com.diamond;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;



public class Element extends Sprite {

	
	public int rowNumber, colNumber;
	public int colorIndex = 0;
//	Хранит точку касания в координатах относительно нулевой точки элемента.
//	Используется для плавного перемещения картинки по экрану.
	public float TouchPointX = 0;
	public float TouchPointY = 0;
	
	public float TouchXGlobal = 0;
	public float TouchYGlobal = 0;
	
	public int FallLength = 0;
	
	boolean CoinsH = false;
	boolean CoinsV = false;
	
	public int CoinsLength = 0;
	
	public Element(float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion) {
		super(pX, pY, pWidth, pHeight, pTextureRegion);

	}

	public Element(float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion, int rowNumber, int colNumber,
			int colorIndex) {
		super(pX, pY, pWidth, pHeight, pTextureRegion);
		this.rowNumber = rowNumber;
		this.colNumber = colNumber;
		this.colorIndex = colorIndex;
		
		
	}

	public Element(float pX, float pY, TextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion);
		// TODO Auto-generated constructor stub
	}


	
	


}
