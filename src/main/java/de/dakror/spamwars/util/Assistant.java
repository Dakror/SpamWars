package de.dakror.spamwars.util;

import java.awt.Polygon;
import java.awt.geom.Line2D;

/**
 * @author Dakror
 */
public class Assistant {
	public static boolean intersection(Polygon p, Line2D line) {
		for (int i = 0; i < p.npoints; i++) {
			for (int j = 0; j < p.npoints; j++) {
				if (i == j) continue;
				
				Line2D l = new Line2D.Float(p.xpoints[i], p.ypoints[i], p.xpoints[j], p.ypoints[j]);
				if (l.intersectsLine(line)) return true;
			}
		}
		
		return false;
	}
	
	public static boolean isBetween(int x, int min, int max) {
		return x >= min && x <= max;
	}
}
