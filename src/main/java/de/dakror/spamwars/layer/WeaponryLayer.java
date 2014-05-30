package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import de.dakror.dakrorbin.Launch;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.layer.Confirm;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.ui.weaponry.WeaponryWeaponButton;

/**
 * @author Dakror
 */
public class WeaponryLayer extends MPLayer
{
	int goingto = 0;
	boolean build;
	TextButton delete, edit;
	
	public WeaponryLayer(boolean build)
	{
		modal = true;
		this.build = build;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/weaponry.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawContainer(Game.getWidth() / 2 - TextButton.WIDTH - 15, Game.getHeight() - TextButton.HEIGHT * 2 - 30, TextButton.WIDTH * 2 + 30, TextButton.HEIGHT * 3, false, false, g);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		
		if (build)
		{
			boolean sel = false;
			for (Component c : components)
				if (c instanceof WeaponryWeaponButton && ((WeaponryWeaponButton) c).selected) sel = true;
			
			delete.enabled = sel;
			edit.enabled = sel;
		}
		
		if (Game.currentFrame.alpha == 1 && enabled)
		{
			Game.currentFrame.fadeTo(0, 0.05f);
			new Thread()
			{
				@Override
				public void run()
				{
					if (goingto == 1) Game.currentGame.removeLayer(WeaponryLayer.this);
					else if (goingto == 2) Game.currentGame.addLayer(new BuildWeaponLayer(null, 0));
					else if (goingto == 3 && edit.enabled)
					{
						for (Component c : components)
						{
							if (c instanceof WeaponryWeaponButton && ((WeaponryWeaponButton) c).selected)
							{
								Game.currentGame.addLayer(new BuildWeaponLayer(((WeaponryWeaponButton) c).data, ((WeaponryWeaponButton) c).id));
								break;
							}
						}
					}
					
					goingto = 0;
				}
			}.start();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void init()
	{
		components.clear();
		Game.pullWeapons();
		int mw = Game.getWidth() - 200;
		int space = 20;
		int inRow = Math.round(mw / (float) (WeaponryWeaponButton.WIDTH + space));
		
		for (int i = 0; i < Game.weapons.length(); i++)
		{
			try
			{
				final WeaponData wd = WeaponData.load(Game.weapons.getJSONObject(i).getString("WEAPONDATA"));
				final WeaponryWeaponButton wwb = new WeaponryWeaponButton((i % inRow) * (WeaponryWeaponButton.WIDTH + space), i / inRow * (WeaponryWeaponButton.HEIGHT + space) + Game.getHeight() / 4, wd);
				wwb.id = Game.weapons.getJSONObject(i).getInt("ID");
				wwb.addClickEvent(new ClickEvent()
				{
					@Override
					public void trigger()
					{
						for (Component c : components)
						{
							if (c instanceof WeaponryWeaponButton) ((WeaponryWeaponButton) c).selected = false;
						}
						if (!build) Game.activeWeapon = wd;
						
						wwb.selected = true;
					}
				});
				
				if (!build)
				{
					if (Game.activeWeapon != null && Game.activeWeapon.equals(wd)) wwb.selected = true;
				}
				
				components.add(wwb);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		
		TextButton back = new TextButton(Game.getWidth() / 2 - (int) (TextButton.WIDTH * (!build ? 0.5f : 1)), Game.getHeight() - TextButton.HEIGHT - 10, "Zurück");
		back.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				goingto = 1;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(back);
		TextButton build = new TextButton(Game.getWidth() / 2, Game.getHeight() - TextButton.HEIGHT - 10, "Neu");
		build.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				goingto = 2;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		if (this.build) components.add(build);
		
		edit = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() - TextButton.HEIGHT * 2 - 10, "Ändern");
		edit.enabled = false;
		edit.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				goingto = 3;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		if (this.build) components.add(edit);
		
		delete = new TextButton(Game.getWidth() / 2, Game.getHeight() - TextButton.HEIGHT * 2 - 10, "Löschen");
		delete.enabled = false;
		delete.addClickEvent(new ClickEvent()
		{
			
			@Override
			public void trigger()
			{
				Game.currentGame.addLayer(new Confirm("Bist du sicher, dass du diese Waffe unwiderruflich löschen willst?", new ClickEvent()
				{
					@Override
					public void trigger()
					{
						int id = -1;
						for (Component c : components)
						{
							if (c instanceof WeaponryWeaponButton && ((WeaponryWeaponButton) c).selected) id = ((WeaponryWeaponButton) c).id;
						}
						if (id == -1) return;
						
						try
						{
							final String response = Helper.getURLContent(new URL("http://dakror.de/spamwars/api/weapons?username=" + Game.user.getUsername() + "&password=" + Launch.pwdMd5 + "&removeweapon=" + id));
							Game.currentGame.addLayer(new Alert("Deine Waffe wurde " + (response.contains("true") ? "" : " nicht") + " erfolgreich gelöscht.", null));
							init();
						}
						catch (MalformedURLException e)
						{
							e.printStackTrace();
						}
					}
				}, null));
			}
		});
		if (this.build) components.add(delete);
	}
}
