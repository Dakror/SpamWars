package de.dakror.spamwars.game.world;

import java.awt.Rectangle;

/**
 * @author Dakror
 */
public enum Tile {
	air(null),
	box(new Rectangle()),
	boxAlt(new Rectangle()),
	boxCoin(new Rectangle()),
	
	/**
	 * ammo box
	 */
	boxCoinAlt(null),
	boxCoinAlt_disabled(new Rectangle()),
	boxCoin_disabled(new Rectangle()),
	boxEmpty(new Rectangle()),
	
	/**
	 * health box
	 */
	boxExplosive(null),
	boxExplosiveAlt(new Rectangle()),
	boxExplosive_disabled(new Rectangle()),
	boxItem(new Rectangle()),
	boxItemAlt(new Rectangle()),
	boxItemAlt_disabled(new Rectangle()),
	boxItem_disabled(new Rectangle()),
	boxWarning(new Rectangle()),
	brickWall(new Rectangle()),
	
	/**
	 * spawnpoint
	 */
	bridge(null),
	bridgeLogs(new Rectangle(0, 45, 70, 25)),
	castle(new Rectangle()),
	castleCenter(new Rectangle()),
	castleCenter_rounded(new Rectangle()),
	castleCliffLeft(new Rectangle()),
	castleCliffLeftAlt(new Rectangle()),
	castleCliffRight(new Rectangle()),
	castleCliffRightAlt(new Rectangle()),
	castleHalf(new Rectangle(0, 0, 70, 40)),
	castleHalfLeft(new Rectangle(0, 0, 70, 40)),
	castleHalfMid(new Rectangle(0, 0, 70, 40)),
	castleHalfRight(new Rectangle(0, 0, 70, 40)),
	castleHillLeft(null, 0, 1),
	castleHillLeft2(new Rectangle()),
	castleHillRight(null, 1, 0),
	castleHillRight2(new Rectangle()),
	castleLeft(new Rectangle()),
	castleMid(new Rectangle()),
	castleRight(new Rectangle()),
	dirt(new Rectangle()),
	dirtCenter(new Rectangle()),
	dirtCenter_rounded(new Rectangle()),
	dirtCliffLeft(new Rectangle()),
	dirtCliffLeftAlt(new Rectangle()),
	dirtCliffRight(new Rectangle()),
	dirtCliffRightAlt(new Rectangle()),
	dirtHalf(new Rectangle(0, 0, 70, 40)),
	dirtHalfLeft(new Rectangle(0, 0, 70, 40)),
	dirtHalfMid(new Rectangle(0, 0, 70, 40)),
	dirtHalfRight(new Rectangle(0, 0, 70, 40)),
	dirtHillLeft(null, 0, 1),
	dirtHillLeft2(new Rectangle()),
	dirtHillRight(null, 1, 0),
	dirtHillRight2(new Rectangle()),
	dirtLeft(new Rectangle()),
	dirtMid(new Rectangle()),
	dirtRight(new Rectangle()),
	door_closedMid(null),
	door_closedTop(null),
	door_openMid(null),
	door_openTop(null),
	fence(null),
	fenceBroken(null),
	grass(new Rectangle()),
	grassCenter(new Rectangle()),
	grassCenter_rounded(new Rectangle()),
	grassCliffLeft(new Rectangle()),
	grassCliffLeftAlt(new Rectangle()),
	grassCliffRight(new Rectangle()),
	grassCliffRightAlt(new Rectangle()),
	grassHalf(new Rectangle(0, 0, 70, 40)),
	grassHalfLeft(new Rectangle(0, 0, 70, 40)),
	grassHalfMid(new Rectangle(0, 0, 70, 40)),
	grassHalfRight(new Rectangle(0, 0, 70, 40)),
	grassHillLeft(null, 0, 1),
	grassHillLeft2(new Rectangle()),
	grassHillRight(null, 1, 0),
	grassHillRight2(new Rectangle()),
	grassLeft(new Rectangle()),
	grassMid(new Rectangle()),
	grassRight(new Rectangle()),
	ladder_mid(null),
	ladder_top(null),
	liquidLava(null),
	liquidLavaTop(null),
	liquidLavaTop_mid(null),
	liquidWater(null),
	liquidWaterTop(null),
	liquidWaterTop_mid(null),
	lock_blue(null),
	lock_green(null),
	lock_red(null),
	lock_yellow(null),
	rockHillLeft(null, 0, 1),
	rockHillRight(null, 1, 0),
	ropeAttached(null),
	ropeHorizontal(null),
	ropeVertical(null),
	sand(new Rectangle()),
	sandCenter(new Rectangle()),
	sandCenter_rounded(new Rectangle()),
	sandCliffLeft(new Rectangle()),
	sandCliffLeftAlt(new Rectangle()),
	sandCliffRight(new Rectangle(0, 0, 70, 40)),
	sandCliffRightAlt(new Rectangle()),
	sandHalf(new Rectangle(0, 0, 70, 40)),
	sandHalfLeft(new Rectangle(0, 0, 70, 40)),
	sandHalfMid(new Rectangle(0, 0, 70, 40)),
	sandHalfRight(new Rectangle(0, 0, 70, 40)),
	sandHillLeft(null, 0, 1),
	sandHillLeft2(new Rectangle()),
	sandHillRight(null, 1, 0),
	sandHillRight2(new Rectangle()),
	sandLeft(new Rectangle()),
	sandMid(new Rectangle()),
	sandRight(new Rectangle()),
	sign(null),
	signExit(null),
	signLeft(null),
	signRight(null),
	snow(new Rectangle()),
	snowCenter(new Rectangle()),
	snowCenter_rounded(new Rectangle()),
	snowCliffLeft(new Rectangle()),
	snowCliffLeftAlt(new Rectangle()),
	snowCliffRight(new Rectangle()),
	snowCliffRightAlt(new Rectangle()),
	snowHalf(new Rectangle(0, 0, 70, 40)),
	snowHalfLeft(new Rectangle(0, 0, 70, 40)),
	snowHalfMid(new Rectangle(0, 0, 70, 40)),
	snowHalfRight(new Rectangle(0, 0, 70, 40)),
	snowHillLeft(null, 0, 1),
	snowHillLeft2(new Rectangle()),
	snowHillRight(null, 1, 0),
	snowHillRight2(new Rectangle()),
	snowLeft(new Rectangle()),
	snowMid(new Rectangle()),
	snowRight(new Rectangle()),
	stone(new Rectangle()),
	stoneCenter(new Rectangle()),
	stoneCenter_rounded(new Rectangle()),
	stoneCliffLeft(new Rectangle()),
	stoneCliffLeftAlt(new Rectangle()),
	stoneCliffRight(new Rectangle()),
	stoneCliffRightAlt(new Rectangle()),
	stoneHalf(new Rectangle(0, 0, 70, 40)),
	stoneHalfLeft(new Rectangle(0, 0, 70, 40)),
	stoneHalfMid(new Rectangle(0, 0, 70, 40)),
	stoneHalfRight(new Rectangle(0, 0, 70, 40)),
	stoneHillLeft2(new Rectangle()),
	stoneHillRight2(new Rectangle()),
	stoneLeft(new Rectangle()),
	stoneMid(new Rectangle()),
	stoneRight(new Rectangle()),
	stoneWall(new Rectangle()),
	tochLit(null),
	tochLit2(null),
	torch(null),
	window(null),
	
	;
	
	private Rectangle bump;
	private float lY, rY;
	
	private Tile(Rectangle bump, float... tY) {
		this.bump = bump;
		
		if (tY.length == 2) {
			lY = tY[1];
			rY = tY[0];
		} else lY = rY = -1;
	}
	
	public Rectangle getBump() {
		if (bump == null) return null;
		
		if (bump.width + bump.height == 0) return new Rectangle(0, 0, SIZE, SIZE);
		
		return bump;
	}
	
	public int getLeftY() {
		return Math.round(lY * SIZE);
	}
	
	public int getRightY() {
		return Math.round(rY * SIZE);
	}
	
	public static final int SIZE = 70;
}
