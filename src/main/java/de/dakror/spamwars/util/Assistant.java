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


package de.dakror.spamwars.util;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.security.MessageDigest;

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
	
	public static String getSha1Hex(String clearString) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(clearString.getBytes("UTF-8"));
			byte[] bytes = messageDigest.digest();
			StringBuilder buffer = new StringBuilder();
			for (byte b : bytes) {
				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return buffer.toString();
		} catch (Exception ignored) {
			ignored.printStackTrace();
			return null;
		}
	}
}
