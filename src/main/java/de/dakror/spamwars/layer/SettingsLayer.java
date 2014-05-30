package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.Checkbox;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class SettingsLayer extends Layer
{
	public SettingsLayer()
	{
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		
		Helper.drawContainer(GameFrame.getWidth() / 2 - 310, Game.getHeight() / 2 - 125, 620, 250, true, false, g);
		Helper.drawHorizontallyCenteredString("Optionen", Game.getWidth(), Game.getHeight() / 2 - 75, g, 40);
		
		Helper.drawString("autom. Nachladen:", Game.getWidth() / 2 - 290, Game.getHeight() / 2 - 20, g, 30);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void init()
	{
		final Checkbox reload = new Checkbox(Game.getWidth() / 2 + 230, Game.getHeight() / 2 - 50);
		components.add(reload);
		
		TextButton cnc = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() / 2 + 30, "Abbruch");
		cnc.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.removeLayer(SettingsLayer.this);
			}
		});
		components.add(cnc);
		
		TextButton save = new TextButton(Game.getWidth() / 2, Game.getHeight() / 2 + 30, "Speichern");
		save.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				CFG.AUTO_RELOAD = reload.isSelected();
				CFG.saveSettings();
				Game.currentGame.removeLayer(SettingsLayer.this);
			}
		});
		components.add(save);
	}
}
