/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

package de.dakror.spamwars.ui.weaponry;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class WeaponryGroup extends Component {
	CopyOnWriteArrayList<WeaponryButton> buttons;
	
	public ClickEvent onUnselect;
	
	public boolean extending;
	
	public WeaponryGroup(int x, int y) {
		super(x, y, WeaponryButton.SIZE, 0);
		buttons = new CopyOnWriteArrayList<>();
		extending = false;
	}
	
	public void addButton(WeaponryButton b) {
		b.x = 0;
		b.y = y + height;
		buttons.add(b);
		height += WeaponryButton.SIZE;
	}
	
	public int length() {
		return buttons.size();
	}
	
	public WeaponryButton getButton(int index) {
		return buttons.get(index);
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.translate(x, y);
		WeaponryButton hov = null;
		WeaponryButton sel = null;
		for (WeaponryButton b : buttons) {
			b.draw(g);
			if (b.state != 0) hov = b;
			if (b.selected) sel = b;
		}
		
		g.translate(-x, -y);
		if (sel != null && extending) Helper.drawImage(Game.getImage("gui/gui.png"), x + sel.width, y + sel.y + (sel.height - 40) / 2, 38, 40, 539, 255, 38, 40, g);
		
		if (hov != null) hov.drawTooltip(Game.currentGame.mouse.x, Game.currentGame.mouse.y, g);
	}
	
	@Override
	public void update(int tick) {
		for (WeaponryButton b : buttons) {
			if (!enabled) {
				b.state = 0;
				b.selected = false;
			}
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!enabled) return;
		
		if (height < Game.getHeight()) return;
		
		if (e.getX() < x || e.getX() > x + width) return;
		
		int y = this.y + e.getScrollAmount() * 15 * -e.getWheelRotation();
		
		if (y > 0) y = 0;
		if (Math.abs(y) > height - Game.getHeight()) y = -(height - Game.getHeight());
		
		this.y = y;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (!enabled) return;
		
		e.translatePoint(-x, -y);
		
		for (Component c : buttons)
			c.mouseMoved(e);
		e.translatePoint(x, y);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (!enabled) return;
		
		e.translatePoint(-x, -y);
		for (Component c : buttons)
			c.mousePressed(e);
		e.translatePoint(x, y);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (!enabled) return;
		
		e.translatePoint(-x, -y);
		for (WeaponryButton c : buttons) {
			if (contains(e.getX(), e.getY()) && e.getButton() == MouseEvent.BUTTON1) c.selected = false;
			c.mouseReleased(e);
		}
		
		boolean unselect = true;
		for (WeaponryButton c : buttons) {
			if (c.selected) {
				unselect = false;
				break;
			}
		}
		if (unselect && onUnselect != null) onUnselect.trigger();
		
		e.translatePoint(x, y);
	}
	
	public void deselectAll() {
		for (WeaponryButton c : buttons) {
			c.selected = false;
			c.state = 0;
		}
	}
}
