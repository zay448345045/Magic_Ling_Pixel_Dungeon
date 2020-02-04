/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Group;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class NewCityBossLevel extends Level {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	private static final Rect entry = new Rect(1, 37, 14, 48);
	private static final Rect arena = new Rect(1, 25, 14, 38);
	private static final Rect end = new Rect(0, 0, 15, 22);

	private static final int bottomDoor = 7 + (arena.bottom-1)*15;
	private static final int topDoor = 7 + arena.top*15;

	private ImpShopRoom impShop;

	@Override
	public String tilesTex() {
		return Assets.TILES_CITY;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_CITY;
	}

	private static final String IMP_SHOP = "imp_shop";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( IMP_SHOP, impShop );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		impShop = (ImpShopRoom) bundle.get( IMP_SHOP );
		if (map[topDoor] != Terrain.LOCKED_DOOR) impShop.onLevelLoad(this);
	}

	@Override
	protected boolean build() {

		setSize(15, 48);

		//entrance room
		Painter.fill(this, entry, Terrain.WALL);
		Painter.fill(this, entry, 1, Terrain.BOOKSHELF);
		Painter.fill(this, entry, 2, Terrain.EMPTY);

		Painter.fill(this, entry.left+3, entry.top+3, 1, 5, Terrain.BOOKSHELF);
		Painter.fill(this, entry.right-4, entry.top+3, 1, 5, Terrain.BOOKSHELF);

		Point c = entry.center();

		Painter.fill(this, c.x-1, c.y-2, 3, 1, Terrain.STATUE);
		Painter.fill(this, c.x-1, c.y, 3, 1, Terrain.STATUE);
		Painter.fill(this, c.x-1, c.y+2, 3, 1, Terrain.STATUE);
		Painter.fill(this, c.x, entry.top+1, 1, 6, Terrain.EMPTY_SP);

		Painter.set(this, c.x, entry.top, Terrain.DOOR);

		entrance = c.x + (c.y+2)*width();
		Painter.set(this, entrance, Terrain.ENTRANCE);

		//DK's throne room
		Painter.fillDiamond(this, arena, 1, Terrain.EMPTY);

		Painter.fill(this, arena, 5, Terrain.EMPTY_SP);
		Painter.fill(this, arena, 6, Terrain.STATUE_SP);

		c = arena.center();
		Painter.set(this, c.x-3, c.y, Terrain.STATUE);
		Painter.set(this, c.x-4, c.y, Terrain.STATUE);
		Painter.set(this, c.x+3, c.y, Terrain.STATUE);
		Painter.set(this, c.x+4, c.y, Terrain.STATUE);

		Painter.set(this, c.x-3, c.y-3, Terrain.PEDESTAL);
		Painter.set(this, c.x+3, c.y-3, Terrain.PEDESTAL);
		Painter.set(this, c.x+3, c.y+3, Terrain.PEDESTAL);
		Painter.set(this, c.x-3, c.y+3, Terrain.PEDESTAL);

		Painter.set(this, c.x, arena.top, Terrain.LOCKED_DOOR);

		//exit hallway
		Painter.fill(this, end, Terrain.CHASM);
		Painter.fill(this, end.left+4, end.top+5, 7, 18, Terrain.EMPTY);
		Painter.fill(this, end.left+4, end.top+5, 7, 3, Terrain.EXIT);
		exit = end.left+7 + (end.top+7)*width();

		impShop = new ImpShopRoom();
		impShop.set(end.left+3, end.top+12, end.left+11, end.top+20);
		if (impShop.itemCount() > (7*7)){
			impShop.bottom += 2;
		}
		Painter.set(this, impShop.center(), Terrain.PEDESTAL);

		Painter.set(this, impShop.left+2, impShop.top, Terrain.STATUE);
		Painter.set(this, impShop.left+6, impShop.top, Terrain.STATUE);

		Painter.fill(this, end.left+5, end.bottom+1, 5, 1, Terrain.EMPTY);
		Painter.fill(this, end.left+6, end.bottom+2, 3, 1, Terrain.EMPTY);

		new CityPainter().paint(this, null);

		//pillars last, no deco on these
		Painter.fill(this, end.left+1, end.top+2, 2, 2, Terrain.WALL);
		Painter.fill(this, end.left+1, end.top+7, 2, 2, Terrain.WALL);
		Painter.fill(this, end.left+1, end.top+12, 2, 2, Terrain.WALL);
		Painter.fill(this, end.left+1, end.top+17, 2, 2, Terrain.WALL);

		Painter.fill(this, end.right-3, end.top+2, 2, 2, Terrain.WALL);
		Painter.fill(this, end.right-3, end.top+7, 2, 2, Terrain.WALL);
		Painter.fill(this, end.right-3, end.top+12, 2, 2, Terrain.WALL);
		Painter.fill(this, end.right-3, end.top+17, 2, 2, Terrain.WALL);

		CustomTilemap customVisuals = new CustomGroundVisuals();
		customVisuals.setRect(0, 0, width(), height());
		customTiles.add(customVisuals);

		customVisuals = new CustomWallVisuals();
		customVisuals.setRect(0, 0, width(), height());
		customWalls.add(customVisuals);

		return true;
	}

	@Override
	protected void createMobs() {
	}

	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos = randomRespawnCell(null);
			} while (pos == entrance);
			drop( item, pos ).setHauntedIfCursed().type = Heap.Type.REMAINS;
		}
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		int cell;
		do {
			cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (!passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
				|| Actor.findChar(cell) != null);
		return cell;
	}

	@Override
	public void occupyCell( Char ch ) {

		super.occupyCell( ch );

		if (map[bottomDoor] == Terrain.DOOR && map[topDoor] == Terrain.LOCKED_DOOR
				&& ch.pos < bottomDoor && ch == Dungeon.hero) {

			seal();

		}
	}

	@Override
	public void seal() {
		super.seal();

		for (Mob m : mobs){
			//bring the first ally with you
			if (m.alignment == Char.Alignment.ALLY && !m.properties().contains(Char.Property.IMMOVABLE)){
				m.pos = Dungeon.hero.pos + (Random.Int(2) == 0 ? +1 : -1);
				m.sprite.place(m.pos);
				break;
			}
		}

		DwarfKing boss = new DwarfKing();
		boss.state = boss.WANDERING;
		boss.pos = pointToCell(arena.center());
		GameScene.add( boss );

		if (heroFOV[boss.pos]) {
			boss.notice();
			boss.sprite.alpha( 0 );
			boss.sprite.parent.add( new AlphaTweener( boss.sprite, 1, 0.1f ) );
		}

		set( bottomDoor, Terrain.LOCKED_DOOR );
		GameScene.updateMap( bottomDoor );
		Dungeon.observe();
	}

	@Override
	public void unseal() {
		super.unseal();

		set( bottomDoor, Terrain.DOOR );
		GameScene.updateMap( bottomDoor );

		set( topDoor, Terrain.DOOR );
		GameScene.updateMap( topDoor );

		if (Imp.Quest.isCompleted()) {
			impShop.spawnShop(this);
		}
		Dungeon.observe();
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(CityLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Messages.get(CityLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CityLevel.class, "exit_desc");
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				return Messages.get(CityLevel.class, "deco_desc");
			case Terrain.EMPTY_SP:
				return Messages.get(CityLevel.class, "sp_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(CityLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals( ) {
		super.addVisuals();
		CityLevel.addCityVisuals(this, visuals);
		return visuals;
	}

	//TODO need to change text for some of these tiles
	public static class CustomGroundVisuals extends CustomTilemap {

		{
			texture = Assets.CITY_BOSS;
			tileW = 15;
			tileH = 48;
		}

		private static final int STAIR_ROWS = 8;

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];

			int[] map = Dungeon.level.map;

			int stairsTop = -1;

			//upper part of the level, mostly demon halls tiles
			for (int i = tileW; i < tileW*22; i++){

				if (map[i] == Terrain.EXIT && stairsTop == -1){
					stairsTop = i - tileW;
				}

				//pillars
				if (map[i] == Terrain.WALL && map[i-tileW] == Terrain.CHASM){
					data[i] = 13*8 + 6;
					data[++i] = 13*8 + 7;
				} else if (map[i] == Terrain.WALL && map[i-tileW] == Terrain.WALL){
					data[i] = 14*8 + 6;
					data[++i] = 14*8 + 7;
				} else if (i > tileW && map[i] == Terrain.CHASM && map[i-tileW] == Terrain.WALL) {
					data[i] = 15*8 + 6;
					data[++i] = 15*8 + 7;

					//imp's pedestal
				} else if (map[i] == Terrain.PEDESTAL) {
					data[i] = 12*8 + 5;

					//skull piles
				} else if (map[i] == Terrain.STATUE) {
					data[i] = 13*8 + 5;

					//ground tiles
				} else if (map[i] == Terrain.EMPTY || map[i] == Terrain.EMPTY_DECO){

					//final ground stiching with city tiles
					if (i/tileW == 21){
						data[i] = 11*8 + 0;
						data[++i] = 11*8 + 1;
						data[++i] = 11*8 + 2;
						data[++i] = 11*8 + 3;
						data[++i] = 11*8 + 4;
						data[++i] = 11*8 + 5;
						data[++i] = 11*8 + 6;
					} else {

						//regular ground tiles
						if (map[i - 1] == Terrain.CHASM) {
							data[i] = 12 * 8 + 1;
						} else if (map[i + 1] == Terrain.CHASM) {
							data[i] = 12 * 8 + 3;
						} else if (map[i] == Terrain.EMPTY_DECO) {
							data[i] = 12 * 8 + 4;
						} else {
							data[i] = 12 * 8 + 2;
						}
					}

					//otherwise no tile here
				} else {
					data[i] = -1;
				}
			}

			//custom for stairs
			int[] rowData = null;
			for (int i = 0; i < STAIR_ROWS; i++){
				if (i == 0){
					rowData = new int[]{-1, -1, 7*8+2, 7*8+3, 7*8+4, -1, -1};
				} else if (i == 1){
					rowData = new int[]{8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6};
				} else if (i < STAIR_ROWS-2){
					rowData = new int[]{9*8+0, 8*8+3, 8*8+3, 8*8+3, 8*8+3, 8*8+3, 9*8+6};
				} else if (i == STAIR_ROWS-2){
					rowData = new int[]{9*8+0, 9*8+1, 9*8+2, 9*8+3, 9*8+4, 9*8+5, 9*8+6};
				} else {
					rowData = new int[]{10*8+0, 10*8+1, 10*8+2, 10*8+3, 10*8+4, 10*8+5, 10*8+6};
				}
				for (int j = 0; j < rowData.length; j++){
					data[stairsTop+j] = rowData[j];
				}
				stairsTop += tileW;
			}

			//lower part: statues, pedestals, and carpets
			for (int i = tileW*22; i < tileW * tileH; i++){

				//pedestal spawners
				if (map[i] == Terrain.PEDESTAL){
					data[i] = 13*8 + 4;

					//statues that should face left instead of right
				} else if (map[i] == Terrain.STATUE && i%tileW > 7) {
					data[i] = 15 * 8 + 4;

					//carpet tiles
				} else if (map[i] == Terrain.EMPTY_SP) {
					//top row of DK's throne
					if (map[i + 1] == Terrain.EMPTY_SP && map[i + tileW] == Terrain.EMPTY_SP) {
						data[i] = 13 * 8 + 1;
						data[++i] = 13 * 8 + 2;
						data[++i] = 13 * 8 + 3;

						//mid row of DK's throne
					}else if (map[i + 1] == Terrain.STATUE_SP) {
						data[i] = 14 * 8 + 1;
						data[++i] = 15 * 8 + 5;
						data[++i] = 14 * 8 + 3;

						//bottom row of DK's throne
					} else if (map[i+1] == Terrain.EMPTY_SP && map[i-tileW] == Terrain.EMPTY_SP){
						data[i] = 15*8 + 1;
						data[++i] = 15*8 + 2;
						data[++i] = 15*8 + 3;

						//otherwise entrance carpet
					} else if (map[i-tileW] != Terrain.EMPTY_SP){
						data[i] = 13*8 + 0;
					} else if (map[i+tileW] != Terrain.EMPTY_SP){
						data[i] = 15*8 + 0;
					} else {
						data[i] = 14*8 + 0;
					}

					//otherwise no tile here
				} else {
					data[i] = -1;
				}
			}

			v.map( data, tileW );
			return v;
		}
	}

	public static class CustomWallVisuals extends CustomTilemap {
		{
			texture = Assets.CITY_BOSS;
			tileW = 15;
			tileH = 48;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];

			int[] map = Dungeon.level.map;

			int stairsTop = -1;

			//upper part of the level, mostly demon halls tiles
			for (int i = tileW; i < tileW*21; i++) {

				if (map[i] == Terrain.EXIT && stairsTop == -1){
					stairsTop = i - tileW;
				}

				//pillars
				if (map[i] == Terrain.CHASM && map[i+tileW] == Terrain.WALL) {
					data[i] = 12*8 + 6;
					data[++i] = 12*8 + 7;
				} else if (map[i] == Terrain.WALL && map[i-tileW] == Terrain.CHASM) {
					data[i] = 13*8 + 6;
					data[++i] = 13*8 + 7;

					//otherwise no tile here
				} else {
					data[i] = -1;
				}
			}

			//custom shadow (and skull tops) for stairs
			//TODO this doesn't look so great. Should try baking some of the shadow into the stairs themselves
			for (int i = 0; i < CustomGroundVisuals.STAIR_ROWS; i++){
				if (i == CustomGroundVisuals.STAIR_ROWS-1){
					data[stairsTop] = i*8 + 0;
					data[stairsTop+1] = i*8 + 1;
					data[stairsTop+2] = data[stairsTop+3] = data[stairsTop+4] = -1;
					data[stairsTop+5] = i*8 + 5;
					data[stairsTop+6] = i*8 + 6;
				} else {
					for (int j = 0; j < 7; j++) {
						data[stairsTop + j] = i*8 + j;
					}
				}
				stairsTop += tileW;
			}

			//lower part. Just need to handle statue tiles here
			for (int i = tileW*21; i < tileW * tileH; i++){

				//DK's throne
				if (map[i] == Terrain.STATUE_SP){
					data[i-tileW] = 14*8 + 5;

					//Statues that need to face left instead of right
				} else if (map[i] == Terrain.STATUE && i%tileW > 7){
					data[i-tileW] = 14*8 + 4;
				}

				//always no tile here (as the above statements are modifying previous tiles)
				data[i] = -1;
			}

			v.map( data, tileW );
			return v;
		}
	}
}