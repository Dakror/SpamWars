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
 

package de.dakror.spamwars.game.weapon;

/**
 * @author Dakror
 */
public class DataPart {
	public Part part;
	public double x, y;
	
	public DataPart(Part part, double x, double y) {
		this.part = part;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return part.id + ":" + x + ":" + y;
	}
	
	public static DataPart load(String s) {
		String[] parts = s.split(":");
		return new DataPart(Part.parts.get(Integer.parseInt(parts[0])), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
	}
}
