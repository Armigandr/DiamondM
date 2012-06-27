package com.diamond;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.*;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.SequenceModifier;
import org.anddev.andengine.util.modifier.ease.*;


public class SquareManager extends Sprite {

	
	int TotalScore = 0;
	float FirstTouchPointX = 0;
	float FirstTouchPointY = 0;
	float SecondTouchPointX = 0;
	float SecondTouchPointY = 0;
	int [] MovementAxis = new int[2];
	int iElement = 0;
	int jElement = 0;
	Element elementMoved = null;
	Element elementMirrored = null;
	boolean MoveDetected = false;
	boolean AlreadyMoved = false;
	boolean CanMove = true;
	boolean WasFirstTouch = false;
	boolean WasSecondTouch = false;
	
	public boolean CanClean = false;
	
	int CleanListCounter = 0;
	
	public float MoveLength = 0;
	private int MaxElements = 6;
	private static final int ROW_COUNT = 8;
	private static final int COLUMN_COUNT = 8;
	private static final float ELEMENT_HEIGHT = 60;
	private static final float ELEMENT_WIDTH = 60;
	
	public ArrayList <Element> ListToDelete = new ArrayList<Element>();
	public ArrayList <Element> ListAddedOnTop = new ArrayList<Element>();
	
	public static Element [][] matrix;
	
			
	public SquareManager(float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion) {
		super(pX, pY, pWidth, pHeight, pTextureRegion);
		// TODO Auto-generated constructor stub
		matrix = new Element[ROW_COUNT][COLUMN_COUNT];
		this.Create();
		
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		// TODO Auto-generated method stub

		
		//Обрабатывается касание к экрану и перемещение в одном из четырех возможных направлений
		
		if(pSceneTouchEvent.isActionDown())
		{
			
			//Определяется элемент, который будет перемещаться
			if (CanMove && !MoveDetected && !WasFirstTouch)
			{
				
				WasFirstTouch = true;
				iElement = (int) (pTouchAreaLocalX/ELEMENT_HEIGHT);
				jElement = (int) (pTouchAreaLocalY/ELEMENT_WIDTH);
				
				FirstTouchPointX = pTouchAreaLocalX;
				FirstTouchPointY = pTouchAreaLocalY;
				
			}
			else if (CanMove && !MoveDetected && WasFirstTouch)
			{
				int iTemp = (int) (pTouchAreaLocalX/ELEMENT_HEIGHT);
				int jTemp = (int) (pTouchAreaLocalY/ELEMENT_WIDTH);
				
				if((iElement - iTemp == -1 && jElement - jTemp ==  0) ||
				   (iElement - iTemp ==  1 && jElement - jTemp ==  0) ||
				   (iElement - iTemp ==  0 && jElement - jTemp ==  1) ||
				   (iElement - iTemp ==  0 && jElement - jTemp == -1))
				{
					MoveDetected = GetMovementAxis(FirstTouchPointX, FirstTouchPointY, pTouchAreaLocalX, pTouchAreaLocalY);
					InterchangeElementsForward();	
				}
				else
				{
					iElement = (int) (pTouchAreaLocalX/ELEMENT_HEIGHT);
					jElement = (int) (pTouchAreaLocalY/ELEMENT_WIDTH);
					
					FirstTouchPointX = pTouchAreaLocalX;
					FirstTouchPointY = pTouchAreaLocalY;
				}
			}
		}
		
		if(pSceneTouchEvent.isActionMove() && WasFirstTouch)
		{
			//Фильтрует движения по экрану. Достаточно, чтобы разница между смещением по Х и по У была больше 4. 
			//Удобно если пользователь двинул элемент по диагонали. 	
			if(MoveDetected && CanMove) 
			{ 
				//Один из четырех вариантов перемещения элемента, которые выглядят одинаково, только в 
				//зависимости от направления движения выбирается соседний элемент, который будет
				//перемещаться зеркально.
					InterchangeElementsForward();	
				
			}
			else if(!MoveDetected && CanMove)
			{

				MoveDetected = GetMovementAxis(FirstTouchPointX, FirstTouchPointY, pTouchAreaLocalX, pTouchAreaLocalY);

			}
		}
		else if(pSceneTouchEvent.isActionUp())
		{
				 
			//AlreadyMoved = false;
			//WasFirstTouch = false;
			
		}
				        
		return super
				.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	
	public void Create()
	{
		Calendar datetime = Calendar.getInstance();
		Random random = new Random(datetime.getTimeInMillis());
		
		int index = 0;
		
		for (int i = 0; i < ROW_COUNT; i++)
			for(int j = 0; j < COLUMN_COUNT; j++)
			{
				boolean validIndexInRow = true;
				boolean validIndexInColumn = true;
				boolean CanGo = false;
				
				index = random.nextInt(MaxElements);
				do
				{
					if(validIndexInColumn)
					{
						int counter = 0;
						for (int k = i-1; k >= i-2 && k >= 0; k--)
						{	
							if(matrix[k][j] != null)
							if(matrix[k][j].colorIndex == index) counter++;
							
						}
						if (counter >= 2) validIndexInColumn = false; 
					}
					if(validIndexInRow)
					{
						int counter = 0;
						for (int l = j-1; l >= j-2 && l >= 0; l--)
						{
							
							if(matrix[i][l] != null)
							if(matrix[i][l].colorIndex == index) counter++;
							
						}	
						if (counter >= 2) validIndexInRow = false;
						
					}
						
					
					if (validIndexInRow && validIndexInColumn) CanGo = true;
					else 
					{
						validIndexInRow = true;
						validIndexInColumn = true;
						index = (index+1)%MaxElements;
					}
				}
				while(!CanGo);
				
				float X = this.getX() + ELEMENT_WIDTH*i;
				float Y = this.getY() + ELEMENT_HEIGHT*j;
				matrix[i][j] = new Element(X, Y, ELEMENT_WIDTH, ELEMENT_HEIGHT, DiamondMActivity.ElementTRegion[index], j, i, index);
				matrix[i][j].setZIndex(2);
				DiamondMActivity.scene.attachChild(matrix[i][j]);
				//this.attachChild(matrix[i][j]);
				
			}

	}
	
	public void AddOnTop()
	{
		Calendar datetime = Calendar.getInstance();
		Random random = new Random(datetime.getTimeInMillis());
		
		int index = 0;
		
		for (int i = 0; i < ROW_COUNT; i++)
			for(int j = 0; j < COLUMN_COUNT; j++)
			{
				boolean validIndexInRow = true;
				boolean validIndexInColumn = true;
				boolean CanGo = false;
				
				index = random.nextInt(MaxElements);
				if(matrix[i][j] == null)
				{
					
				
				do
				{
					if(validIndexInColumn)
					{
						int counter = 0;
						for (int k = i-1; k >= i-2 && k >= 0; k--)
						{	
							if(matrix[k][j] != null)
							if(matrix[k][j].colorIndex == index) counter++;
						}
						if (counter >= 2) validIndexInColumn = false; 
					}
					if(validIndexInRow)
					{
						int counter = 0;
						for (int l = j-1; l >= j-2 && l >= 0; l--)
						{
							
							if(matrix[i][l] != null)
							if(matrix[i][l].colorIndex == index) counter++;
							
						}	
						if (counter >= 2) validIndexInRow = false;
						
					}
						
					
					if (validIndexInRow && validIndexInColumn) CanGo = true;
					else 
					{
						validIndexInRow = true;
						validIndexInColumn = true;
						index = (index+1)%MaxElements;
					}
				}
				while(!CanGo);
				
//				float X = this.getX() + ELEMENT_WIDTH * i;
//				float Y = this.getY() - ELEMENT_HEIGHT * ROW_COUNT + ELEMENT_HEIGHT * j;
//				matrix[i][j] = new Element(X, Y, ELEMENT_WIDTH, ELEMENT_HEIGHT, DiamondMActivity.ElementTRegion[index], j, i, index);
//				matrix[i][j].setZIndex(2);
//				DiamondMActivity.scene.attachChild(matrix[i][j]);
//				ListAddedOnTop.add(matrix[i][j]);
				
				float X = this.getX() + ELEMENT_WIDTH * i;
				float Y = this.getY() - (ELEMENT_HEIGHT * (ROW_COUNT - 1) + ELEMENT_HEIGHT * j);
				matrix[i][j] = new Element(X, Y, ELEMENT_WIDTH, ELEMENT_HEIGHT, DiamondMActivity.ElementTRegion[index], j, i, index);
				matrix[i][j].setZIndex(2);
				DiamondMActivity.scene.attachChild(matrix[i][j]);
				ListAddedOnTop.add(matrix[i][j]);
				
				}
								
			}
		
	}
	
	
	public boolean FindCoinsidence()
	{

		for(int i = 0; i < COLUMN_COUNT; i++)
			for(int j = 0; j < ROW_COUNT; j++)
			{
				matrix[i][j].CoinsV = false;
				matrix[i][j].CoinsH = false;
				matrix[i][j].FallLength = 0;
			}
		
		int LineLength = 0;
		boolean HaveCoinsidence = false;
		

		//Просматривается массив в двух направления и помечаются элементы на удаление при совпадении по 3 подряд и больше
		for (int index = 0; index < MaxElements; index++)
		{
			for (int i = 0; i < ROW_COUNT; i++)
			{
				LineLength = 0;
				
				for (int j = 0; j < COLUMN_COUNT; j++)
				{
					if (matrix[i][j].colorIndex == index)
					{
						LineLength++;
						if (LineLength > 2)
						{
							for(int k = j; k > j - LineLength && k >= 0; k--)
							{
								matrix[i][k].CoinsV = true;
								if(LineLength > 5)
								{
									matrix[i][k].CoinsLength = 5;
								}
								else matrix[i][k].CoinsLength = LineLength;
								HaveCoinsidence = true;
							}
						}
					}
					else if (matrix[i][j].colorIndex != index)
					{
						LineLength = 0;
					}
				}
					
			}
		}
		
		
		for (int index = 0; index < MaxElements; index++)
		{
			for (int j = 0; j < ROW_COUNT; j++)
			{
				LineLength = 0;
				
				for (int i = 0; i < COLUMN_COUNT; i++)
				{
					if (matrix[i][j].colorIndex == index)
					{
						LineLength++;
						
						if (LineLength > 2)
						{
							for(int k = i; k > i - LineLength && k >= 0; k--)
							{
								matrix[k][j].CoinsH = true;
								if(LineLength > 5)
								{
									matrix[k][j].CoinsLength = 5;
								}
								else matrix[k][j].CoinsLength = LineLength;
								HaveCoinsidence = true;
							}
						}
					}
					else if (matrix[i][j].colorIndex != index)
					{
						LineLength = 0;
					}
				}
					
			}
		}
		
		for(int i = 0; i < COLUMN_COUNT; i++)
			for(int j = 0; j < ROW_COUNT; j++)
			{
				if (matrix[i][j].CoinsH == true && matrix[i][j].CoinsV == true)
				{
										
					for (int k = i - 2; k < i + 2 && k < COLUMN_COUNT && k >= 0; k++)
					{
						if (matrix[k][j].CoinsH == true)
							matrix[k][j].CoinsLength = 5;
					}
					
					for (int k = j - 2; k < j + 2 && k < COLUMN_COUNT && k >= 0; k++)
					{
						if (matrix[i][k].CoinsV == true)
							matrix[i][k].CoinsLength = 5;
					}
				}
				
			}
		

		
		LineLength = 0;

		
		
		return HaveCoinsidence;
	}
	
	public void CollectToRemove()
	{
		for(int i = 0; i < COLUMN_COUNT; i++)
			for(int j = 0; j < ROW_COUNT; j++)
			{
				if (matrix[i][j].CoinsH == true || matrix[i][j].CoinsV == true)
					ListToDelete.add(matrix[i][j]);
			}
	}
	
	public void CalculateScore()
	{ 
		@SuppressWarnings("unused")
		int Coins3Count = 0;
		int Coins4Count = 0;
		int Coins5Count = 0;
		
	}
	
	public synchronized void CleanGame()
	{	
		
		for(Element temp : ListToDelete)
		{
			AlphaModifier alphaModifier = new AlphaModifier(0.3f, 1.0f, 0.0f, new IEntityModifierListener() {
				@Override
				public void onModifierFinished(
						IModifier<IEntity> arg0, final IEntity arg1) {
						
					DiamondMActivity.mEngine.runOnUpdateThread(new Runnable() {
					
						        @Override
						        public synchronized void run() {             
					
						        	arg1.detachSelf();
									
						        }
							});
					
					
				}

				@Override
				public void onModifierStarted(
						IModifier<IEntity> arg0, IEntity arg1) {
			
				}
			});

			temp.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			temp.registerEntityModifier(alphaModifier);
			matrix[temp.colNumber][temp.rowNumber] = null;
			//temp.detachSelf();
		}
		ListToDelete.clear();
		
		
		//После удаления элементов массив сортируется так чтобы пустые ячейки оказались вверху
		//и попутно определяется расстояние на которое должны сместиться визуально элементы потолка
		for(int i = 0; i < COLUMN_COUNT; i++)
       	{
       		for (int k = 0; k < ROW_COUNT; k++)
       		{
       			for (int j = 1; j < ROW_COUNT; j++)
       			{
       				if(matrix[i][j] == null && matrix[i][j-1] != null)
       				{
       					matrix[i][j] = matrix[i][j-1];
       					matrix[i][j].colNumber = i;
       					matrix[i][j].rowNumber = j;
       					matrix[i][j].FallLength++;
       					matrix[i][j-1] = null; 
       					
       				}
       			}
       		}
       	}
		
		//Визуально смещаем вниз элементы которые обсыпались вниз
		for(int j = 0; j < COLUMN_COUNT; j++)
			for(int i = 0; i < ROW_COUNT; i++)
			{
				if(matrix[i][j] != null)
				{
					if(matrix[i][j].FallLength > 0)
					{
						float FromY = matrix[i][j].getY();
						float ToY = matrix[i][j].getY() + matrix[i][j].FallLength*ELEMENT_WIDTH;
						//float duration = matrix[i][j].FallLength * 0.24f - matrix[i][j].FallLength * 0.04f;
						
						float duration = matrix[i][j].FallLength * 0.19f;
						
						MoveYModifier moveModifier1 =  new MoveYModifier(duration, FromY, ToY, EaseQuadOut.getInstance());
						DelayModifier delayModifier =  new DelayModifier(0.4f-0.08f*j);
						SequenceEntityModifier seModifier = new SequenceEntityModifier(new IEntityModifierListener() {
							@Override
							public void onModifierFinished(
									IModifier<IEntity> arg0, IEntity arg1) {
								
								//Осуществляется физическая перемена местами перемещаемых элементов в массиве
							int a = 6;
					
						
							}

							@Override
							public void onModifierStarted(
									IModifier<IEntity> arg0, IEntity arg1) {
								//CleanListCounter++;
							}
						}, delayModifier,moveModifier1);		
						matrix[i][j].registerEntityModifier(seModifier);
						matrix[i][j].FallLength = 0;
						//CleanListCounter++;
					}
				}
			}
		
		
		
		AddOnTop();
		
		//Заполняем пустоту сверху
		for(Element temp : ListAddedOnTop)
		{
		
			float FromY = temp.getY();
			float ToY = this.getY() + temp.rowNumber*ELEMENT_HEIGHT;
			float duration = (ROW_COUNT - temp.rowNumber) * 0.19f;
			
			MoveYModifier moveModifier1 = new MoveYModifier(duration, FromY, ToY, EaseSineOut.getInstance());
			
			DelayModifier delayModifier = new DelayModifier(0.30f);
			SequenceEntityModifier seModifier = new SequenceEntityModifier(new IEntityModifierListener() {
				@Override
				public void onModifierFinished(
						IModifier<IEntity> arg0, IEntity arg1) {
							
					CleanListCounter--;
			
				}

				@Override
				public void onModifierStarted(
						IModifier<IEntity> arg0, IEntity arg1) {
					//CleanListCounter++;
				}
			}, delayModifier, moveModifier1);
			temp.registerEntityModifier(seModifier);
			temp.FallLength = 0;
			CleanListCounter++;
		}

	}
 
	/**При касании и перетаскивании вычисляется направление движения.
	Разница между местом первого касания и очередным сообщением о перемещении с разницей больше 5*/
	public boolean GetMovementAxis(float FirstTouchPointX, float FirstTouchPointY, float SecondTouchPointX, float SecondTouchPointY)
	{
		boolean GotMove = false;
		
			if (Math.abs(FirstTouchPointX - SecondTouchPointX) > 5 || Math.abs(FirstTouchPointY - SecondTouchPointY) > 5)
			{
			//Разница между кординатами в виде отрезков для уверенного определения направления перетаскивания
				if (Math.abs(FirstTouchPointX - SecondTouchPointX) - Math.abs(FirstTouchPointY - SecondTouchPointY) > 3)
				{
					if (FirstTouchPointX - SecondTouchPointX > 0 && iElement > 0)
						{GotMove = true; MovementAxis[0] = -1; MovementAxis[1] = 0; }
					else if (FirstTouchPointX - SecondTouchPointX < 0 && iElement <  COLUMN_COUNT - 1)
						{GotMove = true; MovementAxis[0] = 1; MovementAxis[1] = 0; }
					else 
						{WasFirstTouch = false; }
				
				}
			
				else if (Math.abs(FirstTouchPointY - SecondTouchPointY) - Math.abs(FirstTouchPointX - SecondTouchPointX) > 3)
				{
					if (FirstTouchPointY - SecondTouchPointY > 0 && jElement > 0)
						{GotMove = true; MovementAxis[0] = 0; MovementAxis[1] = -1; }
					else if (FirstTouchPointY - SecondTouchPointY < 0 && jElement < ROW_COUNT - 1)
						{GotMove = true; MovementAxis[0] = 0; MovementAxis[1] = 1; }
					else 
						{WasFirstTouch = false; }
				
				}
		
			}
			return GotMove;
	}

	public void InterchangeElementsForward()
	{
		
		CanMove = false;
		WasFirstTouch = false;
		elementMoved = matrix[iElement][jElement];
		elementMirrored = matrix[iElement + MovementAxis[0]][jElement + MovementAxis[1]];
		
		elementMoved.setZIndex(3);
		DiamondMActivity.scene.sortChildren();
		
		final float FromX = elementMoved.getX();
		final float ToX = elementMirrored.getX();
		final float FromY = elementMoved.getY();
		final float ToY = elementMirrored.getY();
		
		int colNumberTemp = elementMirrored.colNumber;
		int rowNumberTemp = elementMirrored.rowNumber;
		
		elementMirrored.colNumber = elementMoved.colNumber;
		elementMirrored.rowNumber = elementMoved.rowNumber;
		
		elementMoved.colNumber = colNumberTemp;
		elementMoved.rowNumber = rowNumberTemp;
		
		
		matrix[iElement][jElement] = elementMirrored;
		matrix[iElement + MovementAxis[0]][jElement + MovementAxis[1]] = elementMoved;
		
																		
		elementMoved.setZIndex(2);
		DiamondMActivity.scene.sortChildren();
		
	MoveModifier moveModifier1 =  new MoveModifier(0.5f, FromX, ToX, FromY, ToY, new IEntityModifierListener() {
		@Override
		public void onModifierFinished(
				IModifier<IEntity> arg0, IEntity arg1) {
			
			//Осуществляется физическая перемена местами перемещаемых элементов в массиве
			if (FindCoinsidence() == true)
			{			
				CanClean = true;
			}
			else 
			{
				InterchangeElementsBack();
				
				CanMove = true;
				MoveDetected = false;
				WasFirstTouch = false;
				CanClean = false;
			}

	
		}

		@Override
		public void onModifierStarted(
				IModifier<IEntity> arg0, IEntity arg1) {
		}
	});
	MoveModifier moveModifier2 = new MoveModifier(0.5f, ToX, FromX, ToY, FromY);
	
	elementMoved.registerEntityModifier(moveModifier1);
	elementMirrored.registerEntityModifier(moveModifier2);
		
		
				
		
    
		
	}
	
	public void InterchangeElementsBack()
	{
		CanMove = false;
		WasFirstTouch = false;
		elementMoved = matrix[iElement][jElement];
		elementMirrored = matrix[iElement + MovementAxis[0]][jElement + MovementAxis[1]];
		
		elementMoved.setZIndex(3);
		DiamondMActivity.scene.sortChildren();
		
		final float FromX = elementMoved.getX();
		final float ToX = elementMirrored.getX();
		final float FromY = elementMoved.getY();
		final float ToY = elementMirrored.getY();
		
		int colNumberTemp = elementMirrored.colNumber;
		int rowNumberTemp = elementMirrored.rowNumber;
		
		elementMirrored.colNumber = elementMoved.colNumber;
		elementMirrored.rowNumber = elementMoved.rowNumber;
		
		elementMoved.colNumber = colNumberTemp;
		elementMoved.rowNumber = rowNumberTemp;
		
		
		matrix[iElement][jElement] = elementMirrored;
		matrix[iElement + MovementAxis[0]][jElement + MovementAxis[1]] = elementMoved;
		
																		
		elementMoved.setZIndex(2);
		DiamondMActivity.scene.sortChildren();
				
		
		MoveModifier moveModifier3 =  new MoveModifier(0.5f, FromX, ToX, FromY, ToY, new IEntityModifierListener() {
				@Override
				public void onModifierFinished(
						IModifier<IEntity> arg0, IEntity arg1) {
					
					//Осуществляется физическая перемена местами перемещаемых элементов в массиве
					
		
			
				}

				@Override
				public void onModifierStarted(
						IModifier<IEntity> arg0, IEntity arg1) {
				}
			});

		MoveModifier moveModifier4 = new MoveModifier(0.5f, ToX, FromX, ToY, FromY);
		
		elementMoved.registerEntityModifier(moveModifier3);
		elementMirrored.registerEntityModifier(moveModifier4);
	}
		
}

