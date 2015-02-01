package de.dakror.spamwars.game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.world.Tile;

/**
 * @author Dakror
 */
public class HealthBox extends Entity {
	public static final int HEALTH = 30;
	public static final int TIMEOUT = 60 * 30; // 30 secs
	int tick;
	int startTick;
	int random;
	
	public HealthBox(float x, float y) {
		super(x, y, Tile.SIZE, Tile.SIZE);
		update = false;
		bump = new Rectangle(0, 0, Tile.SIZE, Tile.SIZE);
		random = (int) (Math.random() * 1000);
		massive = false;
	}
	
	@Override
	protected void tick(int tick) {
		this.tick = tick;
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (Game.world == null) return;
		
		g.drawImage(Game.getImage("tile/boxExplosive.png"), (int) (x + Game.world.x) + 10, (int) (y + Game.world.y + 5 * Math.sin((tick + random) / 13f) + 10), width - 20,
								height - 20, Game.w);
	}
	
	@Override
	public void updateServer(int tick) {
		if (!isEnabled()) {
			if (startTick == 0) {
				startTick = tick;
				return;
			}
			
			if (tick - startTick >= TIMEOUT) {
				setEnabled(true, true, true);
				startTick = 0;
			}
		} else startTick = 0;
	}
}
