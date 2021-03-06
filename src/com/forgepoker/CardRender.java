package com.forgepoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.forgepoker.model.Card;
import com.forgepoker.model.Deck;
import com.forgepoker.model.Player;

/** Render poker card
 * 
 * @author zhanglo
 *
 */
public class CardRender {
	
	private GameController mGameController;
	private GameViewRender mViewRender;
	private Canvas mCanvas;
	private Context mContext;	
	private Bitmap mCardsImage;						// Image sprite contains all cards
	
	/** Card layout attributes 
	 */
	static double mCardTotalWidthRate =  2 / 3.0; 	// (Width of all current player's cards) / screenWidth
	static double mCardTotalHeightRate = 1 / 6.0;	// (Height of left/right player's cards) / screenHeight
	static int mCardHeight = 110;					// Single card height render in canvas
	static int mCardWidth = 80;						// Single card width render in canvas
	private int mCardHeightImage = 110;				// Single card height in image
	private int mCardWidthImage = 80;				// Single card width in image
	static int mCardSelectedPopupHeight = 20; 		// Height of selected card jumps
	private int mCardsOffset = 20;
	
	public CardRender(GameViewRender viewRender, Context context) {
		mViewRender = viewRender;
		mContext = context;
		mGameController = GameController.get();
		mCardsImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cards);
		
		if(mGameController.rule().showRivalCards())
			mCardTotalHeightRate = 2/3.0;
	}
	
	public void init() {
		initCardNodes();
	}
	
	public void render(Canvas canvas) {
		mCanvas = canvas;
		
		Player thisJoinedPlayer = mGameController.ThisJoinedPlayer();
		for (Player p: mGameController.players()) {
			if (p == thisJoinedPlayer)
				renderCurrentPlayer(p);
			else
				renderOtherPlayer(p);
		}
		
		// Draw the current played cards
		renderCurPlayedCards();
	}
	
	private void renderCurrentPlayer(Player p) {
		int totalCardWidth = (int)(mGameController.mScreenWidth * mCardTotalWidthRate);
		int indexOfCard = 0;
		int top = mGameController.mScreenHeight - mCardHeight - GameViewRender.mBottomSideMargin;
		for (Card c: p.cards()) {
			indexOfCard++;
			Rect des = getCardPosition(indexOfCard, p.cards().size(), totalCardWidth, top);
			renderCard(p, c, des);
		}
	}
	
	private void renderOtherPlayer(Player p) {
		
		int totalCardHeight = (int)(mGameController.mScreenHeight * mCardTotalHeightRate);
	
		boolean isLeftOrRight = true;
		if (p.seatIndex() != 2) {
			isLeftOrRight = false;
		}
		int indexOfCard = 0;
		for (Card c: p.cards()) {
			indexOfCard++;
			Rect des = getCardPositionLeftOrRight(indexOfCard, p.cards().size(), isLeftOrRight, totalCardHeight);
			if(mGameController.rule().showRivalCards())
				renderCard(p, c, des);
			else
				renderCardBack(des);
		}
	}
	
	private void renderCurPlayedCards()
	{
		List<Card> playedCards = mGameController.CurrentPlayedCards();
		if(playedCards != null)
		{
			int left = (mGameController.mScreenWidth - (mCardWidth + mCardsOffset)*playedCards.size())/2;
			int top = (mGameController.mScreenHeight - mCardHeight)/2;
			int bottom = top + mCardHeight;
			for (Card c : playedCards)
			{
				left = left + mCardsOffset;
				int right = left + mCardWidth;
				Rect des = new Rect(left, top, right, bottom);
				CardSceneNode cnode = mCardNodes.get(c);
				cnode.desRect(des);	
				mCanvas.drawBitmap(mCardsImage, cnode.srcRect(), des, null);
			}
		}
	}
	
	private Rect getCardPosition(int indexOfCard, int numOfCards, int totalWidth, int top) {
		
		int eachCardWidthOverlap = totalWidth / numOfCards;		
		int start = (mGameController.mScreenWidth - totalWidth) / 2;
		int left = start + eachCardWidthOverlap * indexOfCard;
		return new Rect(left, top, left + mCardWidth, top + mCardHeight);
	}
	
	private void renderCard(Player player, Card c, Rect des) {
		CardSceneNode cnode = mCardNodes.get(c);
		
		if (cnode.isSelected()) {
			if(player.isRobot()) {
				des.offset(player.seatIndex() == 2 ? mCardSelectedPopupHeight : (-mCardSelectedPopupHeight), 0);
			} else {
				des.offset(0, -mCardSelectedPopupHeight);
			}
		}
		
		cnode.desRect(des);		
		mCanvas.drawBitmap(mCardsImage, cnode.srcRect(), des, null);		
	}
	
	private void renderCardBack(Rect des) {
		mCanvas.drawBitmap(mCardsImage, mCardBackPos, des, null);		
	}

	/** Draw poker cards
	 * 
	 */
	private Map<Card, CardSceneNode> mCardNodes = new HashMap<Card, CardSceneNode>();
//	private List<CardSceneNode> mCardNodes = new ArrayList<CardSceneNode>();
	private Rect mCardBackPos;
	private void initCardNodes() {
		if (mCardNodes.size() > 0) {
			return;
		}
		
		final int numOfCol = 13;
		
		Deck deck = mGameController.deck();
		
		for (Card c: deck.cards()) {
			
			int col = c.imageIndex() % numOfCol;
			int row = c.imageIndex() / numOfCol;
			int left = mCardWidthImage * col;
			int top = mCardHeightImage * row;
			int right = left + mCardWidthImage;
			int bottom = top + mCardHeightImage;			
			Rect r = new Rect(left, top, right, bottom);
			
			CardSceneNode cnode = new CardSceneNode(c);
			cnode.srcRect(r);
			mCardNodes.put(c,  cnode);
		}
		
		// Card back is at 55th
		int col = 2 % numOfCol;
		int row = 54 / numOfCol;
		int left = mCardWidthImage * col;
		int top = mCardHeightImage * row;
		int right = left + mCardWidthImage;
		int bottom = top + mCardHeightImage;			
		mCardBackPos = new Rect(left, top, right, bottom);
	}
	
	private Rect getCardPositionLeftOrRight(int indexOfCard, int numOfCards, boolean isLeftOrRight, int totalHeight) {
		
		int eachCardOverlap = totalHeight / numOfCards;		
		int start = (mGameController.mScreenHeight - totalHeight) / 2;
		int left = GameViewRender.mLeftOrRightSideMargin;
		int top = start + eachCardOverlap * indexOfCard;
		
		if (!isLeftOrRight) {
			// right
			left = mGameController.mScreenWidth - left - mCardWidth;
		}
		
		return new Rect(left, top, left + mCardWidth, top + mCardHeight);
	}
	
	public boolean OnTouch(int x, int y) {
		
		boolean showRivalCards = mGameController.rule().showRivalCards();
		Player thisJoinedPlayer = mGameController.ThisJoinedPlayer();
		for (Player p: mGameController.players()) {
			if (!showRivalCards && p != thisJoinedPlayer)
				continue;
			
			for (int i=p.cards().size()-1; i>=0; i--) {
				Card card = p.cards().get(i);
				CardSceneNode cnode = mCardNodes.get(p.cards().get(i));
				if (cnode.isHit(x, y)) {
					card.selected(!card.selected());
					return true;
				}
			}
		}
		
		return false;
	}
}
