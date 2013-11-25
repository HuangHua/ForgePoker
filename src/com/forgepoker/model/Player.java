package com.forgepoker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents game player
 * @author zhanglo
 *
 */
public class Player {
	
	/** basic attributes */
	private String mName;
	private int mAvatar;
	private int mScore;	
	private int mBid;
	private boolean mHasBid = false;
	
	/** cards */
	private List<Card> mCards = new ArrayList<Card>();
	private Suit mCurPlayedSuit;
	
	private boolean mIsLord = false;
	private int mSeatIndex = 1;
	private boolean mIsRobot = true;
	
	public Player(String name, int avatar, int score, boolean isRobot) {
		mName = name;
		mAvatar = avatar;
		mScore = score;
		mIsRobot = isRobot;
	}
	
	public boolean isLord() {
		return mIsLord;
	}
	
	public void isLord(boolean val) {
		mIsLord = val;
	}
	
	public String name() {
		return mName;
	}
	
	public int avatar() {
		return mAvatar;
	}
	
	public int score() {
		return mScore;
	}
	
	public void score(int val) {
		mScore = val;
	}
	
	public int bid() {
		return mBid;
	}
	
	public void bid(int val) {
		assert(mHasBid == false);
		mBid = val;
		mHasBid = true;
	}
	
	public boolean hasBid() {
		return mHasBid;
	}
	
	public Suit curPlayedSuit() {
		return mCurPlayedSuit;
	}
	
	public List<Card> cards() {
		return mCards;
	}
	
	public void cards(List<Card> cards) {
		mCards.clear();
		mCards.addAll(cards);
		sortCards();
	}
	
	public void sortCards() {
		Collections.sort(mCards);
	}

	/** Produce/play cards */
	public boolean playCards(Suit playedSuit) {
		mCurPlayedSuit = playedSuit;
		
		mCards.remove(playedSuit.cards());
		return true;
	}
	
	public int seatIndex() {
		return mSeatIndex;
	}
	
	public void seatIndex(int val) {
		mSeatIndex = val;
	}
	
	public boolean isRobot() {
		return mIsRobot;
	}
}
