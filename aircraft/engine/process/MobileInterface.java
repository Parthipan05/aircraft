package engine.process;

import java.util.List;

import engine.map.Block;
import engine.mobile.Aircraft;
import engine.mobile.Bomb;
import engine.mobile.Enemy;
import engine.mobile.Missile;

public interface MobileInterface {

	void set(Aircraft aircraft);

	void add(Enemy enemy);

	void add(Missile missile);

	void moveLeftAirCraft();

	void moveRightAirCraft();

	void generateMissile();

	void putBomb(Block position);

	void nextRound();

	Aircraft getA();

	List<Enemy> getEnemies();

	List<Missile> getMissiles();

	Bomb getBomb();

	List<Block> getBombZone();

}