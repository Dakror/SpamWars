package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.net.InetAddress;
import java.net.URL;

import org.json.JSONObject;

import de.dakror.dakrorbin.Launch;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class PurchaseWeaponLayer extends MPLayer {
	WeaponData data;
	int costs;
	int id;
	
	public PurchaseWeaponLayer(WeaponData exisData, int id, WeaponData data) {
		modal = true;
		this.data = data;
		this.id = id;
		
		costs = data.getPrice();
		if (exisData != null) costs -= Math.round(exisData.getPrice() * 0.4f);
	}
	
	@Override
	public void draw(Graphics2D g) {
		drawModality(g);
		
		Helper.drawContainer(GameFrame.getWidth() / 2 - 310, Game.getHeight() / 2 - 125, 620, 250, true, false, g);
		Helper.drawHorizontallyCenteredString("Bezahlung", Game.getWidth(), Game.getHeight() / 2 - 75, g, 40);
		
		Helper.drawHorizontallyCenteredString("Das Bauen deiner Waffe kostet", Game.getWidth(), Game.getHeight() / 2 - 50, g, 20);
		
		Color c = g.getColor();
		if (Game.money >= costs) g.setColor(Color.darkGray);
		else g.setColor(Color.decode("#660a0a"));
		Helper.drawHorizontallyCenteredString(costs == 0 ? "Nichts" : costs + "$", Game.getWidth(), Game.getHeight() / 2 + 20, g, 80);
		g.setColor(c);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick) {
		updateComponents(tick);
	}
	
	@Override
	public void onPacketReceived(Packet p, InetAddress ip, int port) {}
	
	@Override
	public void init() {
		TextButton cnc = new TextButton(Game.getWidth() / 2 - (int) (TextButton.WIDTH * (Game.money >= costs ? 1 : 0.5f)), Game.getHeight() / 2 + 30, "Abbruch");
		cnc.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				Game.currentGame.removeLayer(PurchaseWeaponLayer.this);
			}
		});
		components.add(cnc);
		
		TextButton buy = new TextButton(Game.getWidth() / 2, Game.getHeight() / 2 + 30, "Bezahlen");
		buy.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				try {
					boolean success = false;
					if (!CFG.INTERNET) {
						JSONObject o = new JSONObject();
						o.put("ID", (int) (Math.random() * 1000));
						o.put("USERID", (int) (Math.random() * 1000));
						o.put("WEAPONDATA", data.getSortedData().toString());
						
						Game.weapons.put(o);
						success = true;
					} else if (Game.subMoney(costs)) {
						String response = Helper.getURLContent(new URL("http://dakror.de/spamwars/api/weapons?username=" + Game.user.getUsername() + "&password=" + Launch.pwdMd5 + "&addweapon=" + data.getSortedData() + "&setweapon=" + id)).trim();
						success = response.contains("true");
					}
					
					final boolean s = success;
					Game.currentGame.addLayer(new Alert("Deine Waffe wurde " + (success ? "" : " nicht") + " erfolgreich hergestellt.", new ClickEvent() {
						@Override
						public void trigger() {
							Game.currentGame.removeLayer(PurchaseWeaponLayer.this);
							if (s) {
								Game.currentGame.removeLayer(Game.currentGame.getActiveLayer());
								Game.currentGame.removeLayer(Game.currentGame.getActiveLayer()); // BuildWeaponLayer
								Game.currentGame.getActiveLayer().init();
							}
						}
					}));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		if (Game.money >= costs) components.add(buy);
	}
}
