package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Graphics2D;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class PurchaseWeaponLayer extends MPLayer
{
	WeaponData data;
	int costs;
	
	public PurchaseWeaponLayer(WeaponData data)
	{
		modal = true;
		this.data = data;
		
		costs = data.getPrice();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		
		Helper.drawContainer(GameFrame.getWidth() / 2 - 310, Game.getHeight() / 2 - 125, 620, 250, true, false, g);
		Helper.drawHorizontallyCenteredString("Bezahlung", Game.getWidth(), Game.getHeight() / 2 - 75, g, 40);
		Helper.drawHorizontallyCenteredString("Das Bauen deiner Waffe kostet", Game.getWidth(), Game.getHeight() / 2 - 50, g, 20);
		
		Color c = g.getColor();
		g.setColor(Color.darkGray);
		Helper.drawHorizontallyCenteredString(costs == 0 ? "Nichts" : costs + "$", Game.getWidth(), Game.getHeight() / 2 + 20, g, 80);
		g.setColor(c);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void init()
	{
		
		TextButton cnc = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() / 2 + 30, "Abbruch");
		cnc.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.removeLayer(PurchaseWeaponLayer.this);
			}
		});
		components.add(cnc);
		
		TextButton lgn = new TextButton(Game.getWidth() / 2, Game.getHeight() / 2 + 30, "Bezahlen");
		lgn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{}
		});
		components.add(lgn);
	}
}
