package com.arrow.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/**
 * Created by matt on 2016-10-16.
 */
public class GameScreen implements Screen {
    final MyGame game;
    private Texture monsterImage;
    private Texture bowImage;
    private Texture arrowImage;
    private Texture forestImage;
    public OrthographicCamera camera;
    private Rectangle bow;
    private Vector3 touchPos = new Vector3();
    private Array<Rectangle> monsters;
    private Array<Rectangle> arrows;
    private Array<Rectangle> forests;
    private Array<Rectangle> monstersToRemove;
    private Array<Rectangle> arrowsToRemove;
    private long lastDropTime;
    private long lastArrowTime;
    private long levelTime;
    private int exp;
    private int health;
    private boolean dmg = false;
    private int mazeLevel;
    private boolean nextLevel = false;


    public GameScreen(final MyGame g) {
        this.game = g;

        monsterImage = new Texture(Gdx.files.internal("swordman.png"));
        bowImage = new Texture(Gdx.files.internal("bowman.png"));
        arrowImage = new Texture(Gdx.files.internal("arrow.png"));
        forestImage = new Texture(Gdx.files.internal("forest.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);

        bow = new Rectangle();
        bow.x = 480 / 2 - 64 / 2;
        bow.y = 20;
        bow.width = 64;
        bow.height = 64;

        forests = new Array<Rectangle>();
        newForests();

        monsters = new Array<Rectangle>();
        arrows = new Array<Rectangle>();
        monstersToRemove = new Array<Rectangle>();
        arrowsToRemove = new Array<Rectangle>();
        exp = 0;
        health = 100;
        mazeLevel = 1;
        spawnMonster();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Flash screen red for damage
        if (dmg) {
            Gdx.gl.glClearColor(0.3f, 0, 0, 1);
            dmg = false;
        }
        else {
            Gdx.gl.glClearColor(0, 0.5f, 0, 1);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        //Transition screen between levels
        if (nextLevel) {
            game.font.draw(game.batch, "Completed Level " + Integer.toString(mazeLevel - 1), 200, 400);
            game.font.draw(game.batch, "Beginning Level " + Integer.toString(mazeLevel), 200, 300);
            game.batch.end();
            monsters.clear();
            arrows.clear();
            forests.clear();
            newForests();

            if (TimeUtils.nanoTime() - levelTime > 1000000000) {
                nextLevel = false;
            }
        }
        //Normal tick
        else {
            game.batch.draw(bowImage, bow.x, bow.y);
            for (Rectangle forest : forests) {
                game.batch.draw(forestImage, forest.x, forest.y);
            }
            for (Rectangle monster : monsters) {
                game.batch.draw(monsterImage, monster.x, monster.y);
            }
            for (Rectangle arrow : arrows) {
                game.batch.draw(arrowImage, arrow.x, arrow.y);
            }
            game.font.draw(game.batch, "EXP: " + Integer.toString(exp), 380, 700);
            game.font.draw(game.batch, "HEALTH: " + Integer.toString(health), 380, 650);
            game.batch.end();

            if (Gdx.input.isTouched()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                //Recovery time between arrows
                if (TimeUtils.nanoTime() - lastArrowTime > 1000000000) {
                    spawnArrow(touchPos);
                }
            }

            if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnMonster();

            Iterator<Rectangle> iter = monsters.iterator();
            while (iter.hasNext()) {
                Rectangle monster = iter.next();
                //Speed monsters move
                monster.y -= (100 + (5 * mazeLevel)) * Gdx.graphics.getDeltaTime();
                if (monster.y + 64 < 0) {
                    iter.remove();
                    health -= 10;
                    if (health <= 0) {
                        game.setScreen(new RetryScreen(game));
                    } else {
                        dmg = true;
                    }
                }
                else {
                    //Dodge forests
                    for (Rectangle f : forests) {
                        if (monster.overlaps(f)) {
                            if (Math.abs(monster.x - f.x) < Math.abs(monster.x + 64 - f.x) &&
                                    (monster.x += Math.abs(monster.x - f.x)) < 480) {
                                monster.x += Math.abs(monster.x - f.x);
                            }
                            else {
                                if ((monster.x -= Math.abs(monster.x + 64 - f.x)) > 0) {
                                    monster.x -= Math.abs(monster.x + 64 - f.x);
                                } else {
                                    monster.x += Math.abs(monster.x - f.x);
                                }
                            }
                        }
                    }
                }
            }
            for (Rectangle arrow : arrows) {
                //Speed arrows move
                arrow.y += 300 * Gdx.graphics.getDeltaTime();
                if (arrow.y + 40 > 800) arrowsToRemove.add(arrow);
                for (int i = 0; i < monsters.size; i++) {
                    //Hit a monster
                    if (arrow.overlaps(monsters.get(i))) {
                        if (!arrowsToRemove.contains(arrow, false)) {
                            arrowsToRemove.add(arrow);
                        }
                        monstersToRemove.add(monsters.get(i));
                        exp += 20 + (10 * mazeLevel - 1);
                        if (exp >= 1000 * mazeLevel) {
                            mazeLevel++;
                            nextLevel = true;
                            levelTime = TimeUtils.nanoTime();
                        }
                        break;
                    }
                }
                for (Rectangle forst : forests) {
                    if (arrow.overlaps(forst)) {
                        arrowsToRemove.add(arrow);
                    }
                }
                monsters.removeAll(monstersToRemove, false);
            }
            arrows.removeAll(arrowsToRemove, false);
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
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
        arrow.width = 10;
        arrow.height = 40;
        arrows.add(arrow);
        lastArrowTime = TimeUtils.nanoTime();
    }

    private void newForests() {
        for (int i = 0; i < 3; i++) {
            Rectangle f = new Rectangle();
            f.x = MathUtils.random(0, 480 - 64);
            f.y = MathUtils.random(100, 800 - 64);
            f.width = 64;
            f.height = 64;
            forests.add(f);
        }
    }
}
