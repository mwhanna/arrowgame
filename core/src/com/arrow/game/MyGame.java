package com.arrow.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGame extends ApplicationAdapter {

	private Texture monsterImage;
	private Texture bowImage;
	private Texture arrowImage;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bow;
	private Vector3 touchPos = new Vector3();
	private Array<Rectangle> monsters;
	private Array<Rectangle> arrows;
	private long lastDropTime;

	@Override
	public void create () {
		monsterImage = new Texture(Gdx.files.internal("swordman.png"));
		bowImage = new Texture(Gdx.files.internal("bowman.png"));
		arrowImage = new Texture(Gdx.files.internal("arrow.png"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);

		batch = new SpriteBatch();

		bow = new Rectangle();
		bow.x = 480 / 2 - 64 / 2;
		bow.y = 20;
		bow.width = 64;
		bow.height = 64;

		monsters = new Array<Rectangle>();
		spawnMonster();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0.5f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bowImage, bow.x, bow.y);
		for(Rectangle monster: monsters) {
			batch.draw(monsterImage, monster.x, monster.y);
		}
		for(Rectangle arrow: arrows) {
			batch.draw(arrowImage, arrow.x, arrow.y);
		}
		batch.end();

		if(Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			spawnArrow(touchPos);
		}

		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnMonster();

		Iterator<Rectangle> iter = monsters.iterator();
		while(iter.hasNext()) {
			Rectangle monster = iter.next();
			monster.y -= 100 * Gdx.graphics.getDeltaTime();
			if(monster.y + 64 < 0) iter.remove();
			if(monster.overlaps(bow)) {
				iter.remove();
			}
		}
		if (arrows.size >= 1) {
			Iterator<Rectangle> arrowIter = arrows.iterator();
			while (arrowIter.hasNext()) {
				Rectangle arrow = iter.next();
				arrow.y += 400 * Gdx.graphics.getDeltaTime();
				if (arrow.y - 40 > 800) iter.remove();
				for (int i = 0; i < monsters.size; i++) {
					if (arrow.overlaps(monsters.get(i))) {
						iter.remove();
						monsters.removeIndex(i);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		monsterImage.dispose();
		bowImage.dispose();
	}

	private void spawnMonster() {
		Rectangle monster = new Rectangle();
		monster.x = MathUtils.random(0, 480 - 64);
		monster.y = 800;
		monster.width = 64;
		monster.height = 64;
		monsters.add(monster);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void spawnArrow(Vector3 startPos) {
		Rectangle arrow = new Rectangle();
		arrow.x = startPos.x;
		arrow.y = 0;
		arrow.width = 20;
		arrow.height = 40;
		arrows.add(arrow);
	}
}
