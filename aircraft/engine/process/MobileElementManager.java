package engine.process;

import java.util.ArrayList;
import java.util.List;

import config.GameConfiguration;
import engine.map.Block;
import engine.map.Map;
import engine.mobile.Aircraft;
import engine.mobile.Bomb;
import engine.mobile.Enemy;
import engine.mobile.Missile;

/**
 * Copyright SEDAMOP - Software Engineering
 * 
 * @author tianxiao.liu@cyu.fr
 *
 */
public class MobileElementManager implements MobileInterface {
	private Map map;

	private Aircraft aircraft;
	private List<Enemy> enemies = new ArrayList<Enemy>();
	private List<Missile> missiles = new ArrayList<Missile>();

	private Bomb bomb = null;
	private int bombCount = 0;
	private int bombDelay = GameConfiguration.BOMB_EXPLOSION_DELAY;
	private List<Block> bombZone = new ArrayList<Block>();

	public MobileElementManager(Map map) {
		this.map = map;
	}

	@Override
	public void set(Aircraft aircraft) {
		this.aircraft = aircraft;
	}

	@Override
	public void add(Enemy enemy) {
		enemies.add(enemy);
	}

	@Override
	public void add(Missile missile) {
		missiles.add(missile);
	}

	@Override
	public void moveLeftAirCraft() {
		Block position = aircraft.getPosition();

		if (position.getColumn() > 0) {
			Block newPosition = map.getBlock(position.getLine(), position.getColumn() - 1);
			aircraft.setPosition(newPosition);
		}

	}

	@Override
	public void moveRightAirCraft() {
		Block position = aircraft.getPosition();

		if (position.getColumn() < GameConfiguration.COLUMN_COUNT - 1) {
			Block newPosition = map.getBlock(position.getLine(), position.getColumn() + 1);
			aircraft.setPosition(newPosition);
		}
	}

	@Override
	public void generateMissile() {
		Block position = aircraft.getPosition();
		Missile missile = new Missile(position);
		add(missile);

	}

	@Override
	public void putBomb(Block position) {
		if (bombCount < GameConfiguration.MAX_BOMB_COUNT && bomb == null) {

			// Can not release a bomb on the border of the map.
			if (!map.isOnBorder(position)) {
				bomb = new Bomb(position);
				int line = position.getLine();
				int column = position.getColumn();

				// The four blocks around
				bombZone.add(map.getBlock(line, column + 1));
				bombZone.add(map.getBlock(line, column - 1));
				bombZone.add(map.getBlock(line - 1, column));
				bombZone.add(map.getBlock(line + 1, column));
			}

			bombCount++;
		}
	}

	@Override
	public void nextRound() {
		moveEnemies();
		generateEnemy();
		moveMissiles();
		missileImpact();
		bombExplosion();
	}

	private void generateEnemy() {
		int randomColumn = getRandomNumber(0, GameConfiguration.COLUMN_COUNT - 1);
		Block position = map.getBlock(0, randomColumn);
		Enemy enemy = new Enemy(position);
		add(enemy);
	}

	private void moveEnemies() {

		List<Enemy> outOfBoundEnemies = new ArrayList<Enemy>();

		for (Enemy enemy : enemies) {
			Block position = enemy.getPosition();

			if (!map.isOnBottom(position)) {
				Block newPosition = map.getBlock(position.getLine() + 1, position.getColumn());
				enemy.setPosition(newPosition);
			} else {
				outOfBoundEnemies.add(enemy);
			}

		}

		for (Enemy enemy : outOfBoundEnemies) {
			enemies.remove(enemy);
		}

	}

	private void moveMissiles() {
		List<Missile> outOfBoundMissiles = new ArrayList<Missile>();

		for (Missile missile : missiles) {
			Block position = missile.getPosition();

			if (!map.isOnTop(position)) {
				Block newPosition = map.getBlock(position.getLine() - 1, position.getColumn());
				missile.setPosition(newPosition);
				map.getMissiles().remove(position);
				map.getMissiles().put(newPosition, missile);
				
			} else {
				outOfBoundMissiles.add(missile);
			}

		}

		for (Missile missile : outOfBoundMissiles) {
			missiles.remove(missile);
		}
	}

	private void missileImpact() {
		List<Enemy> eliminatedEnemies = new ArrayList<Enemy>();
		for (Missile missile : missiles) {
			Block missilePosition = missile.getPosition();
			for (Enemy enemy : enemies) {
				if (enemy.getPosition().equals(missilePosition)) {
					eliminatedEnemies.add(enemy);
				}
			}
		}
		for (Enemy enemy : eliminatedEnemies) {
			enemies.remove(enemy);
		}
	}

	private void bombExplosion() {
		if (bomb != null) {
			if (bombDelay == 0) {
				List<Enemy> eliminatedEnemies = new ArrayList<Enemy>();
				Block bombPosition = bomb.getPosition();

				for (Block block : bombZone) {
					for (Enemy enemy : enemies) {
						Block enemyPosition = enemy.getPosition();
						if (enemyPosition.equals(block) || enemyPosition.equals(bombPosition)) {
							eliminatedEnemies.add(enemy);
						}
					}
				}
				for (Enemy enemy : eliminatedEnemies) {
					enemies.remove(enemy);
				}

				bomb = null;
				bombZone.clear();
				bombDelay = GameConfiguration.BOMB_EXPLOSION_DELAY;
			} else {
				bombDelay--;
			}
		}
	}

	@Override
	public Aircraft getA() {
		return aircraft;
	}

	@Override
	public List<Enemy> getEnemies() {
		return enemies;
	}

	@Override
	public List<Missile> getMissiles() {
		return missiles;
	}

	@Override
	public Bomb getBomb() {
		return bomb;
	}

	@Override
	public List<Block> getBombZone() {
		return bombZone;
	}

	private static int getRandomNumber(int min, int max) {
		return (int) (Math.random() * (max + 1 - min)) + min;
	}
}
