package com.logicdojo.arrowwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by matt on 2016-10-16.
 */
public class GameScreen implements Screen {
    final MyGame game;
    private Texture monsterImage;
    private Texture bowImage;
    private Texture bowYellow;
    private Texture bowOrange;
    private Texture bowRed;
    private Texture arrowImage;
    private Texture forestImage;
    private Texture bonus1Image;
    private Texture fireArrowImage;
    private Texture bombImage;
    private Texture mushroomCloudImage;
    private Texture bullseyeImage;
    private Texture crystalImage;
    public OrthographicCamera camera;
    private Rectangle bow;
    private Vector3 touchPos = new Vector3();
    private Array<Rectangle> monsters;
    private Array<Arrow> arrows;
    private Array<Rectangle> forests;
    private Array<Rectangle> forestsToRemove;
    private Array<Rectangle> monstersToRemove;
    private Array<Arrow> arrowsToRemove;
    private Array<BonusImage> bonuses;
    private Array<BonusImage> bonusesToRemove;
    private long lastDropTime;
    private long lastArrowTime;
    private long levelTime;
    private long chargeTime;
    private int exp;
    private int health;
    private boolean dmg = false;
    private int mazeLevel;
    private boolean nextLevel = false;
    private int bowColor;
    private Random rand = new Random();
    private int bombCount;
    private boolean explosion = false;

    Vector3 tp = new Vector3();
    boolean dragging;
    private int downx;
    private int downy;

    public GameScreen(final MyGame g) {
        this.game = g;
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override public boolean touchDown (int screenX, int screenY, int pointer, int button) {
                // ignore if its not left mouse button or first touch pointer
                if (button != Input.Buttons.LEFT || pointer > 0) return false;
                camera.unproject(tp.set(screenX, screenY, 0));
                downx = screenX / 4;
                downy = screenY / 4;
                dragging = true;
                bowColor = 0;
                chargeTime = TimeUtils.millis();
                return true;
            }

            @Override public boolean touchDragged (int screenX, int screenY, int pointer) {
                if (!dragging) return false;
                camera.unproject(tp.set(screenX, screenY, 0));
                if (TimeUtils.millis() - chargeTime > 900) {
                    bowColor = 3;
                }
                else if (TimeUtils.millis() - chargeTime > 600) {
                    bowColor = 2;
                }
                else if (TimeUtils.millis() - chargeTime > 300) {
                    bowColor = 1;
                }
                return true;
            }

            @Override public boolean touchUp (int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT || pointer > 0) return false;
                camera.unproject(tp.set(screenX, screenY, 0));
                int x = screenX / 4;
                int y = screenY / 4;
                if (TimeUtils.millis() - lastArrowTime > 400) {
                    newArrow(downx, x, downy, y, bowColor);
                    bowColor = 0;
                }
                dragging = false;
                return true;
            }
        });

        monsterImage = new Texture(Gdx.files.internal("swordman.png"));
        bowImage = new Texture(Gdx.files.internal("bowman.png"));
        bowYellow = new Texture(Gdx.files.internal("bowyellow.png"));
        bowOrange = new Texture(Gdx.files.internal("boworange.png"));
        bowRed = new Texture(Gdx.files.internal("bowred.png"));
        arrowImage = new Texture(Gdx.files.internal("arrow.png"));
        forestImage = new Texture(Gdx.files.internal("forest.png"));
        bonus1Image = new Texture(Gdx.files.internal("bonus1.png"));
        fireArrowImage = new Texture(Gdx.files.internal("splinter.png"));
        bombImage = new Texture(Gdx.files.internal("bolt.png"));
        mushroomCloudImage = new Texture(Gdx.files.internal("mushroom-cloud.png"));
        bullseyeImage = new Texture(Gdx.files.internal("bullseye.png"));
        crystalImage = new Texture(Gdx.files.internal("crytsal.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
        bowColor = 0;

        bow = new Rectangle();
        bow.x = 480 / 2 - 64 / 2;
        bow.y = 20;
        bow.width = 64;
        bow.height = 64;

        forests = new Array<Rectangle>();
        forestsToRemove = new Array<Rectangle>();
        newForests();

        monsters = new Array<Rectangle>();
        arrows = new Array<Arrow>();
        monstersToRemove = new Array<Rectangle>();
        arrowsToRemove = new Array<Arrow>();
        bonuses = new Array<BonusImage>();
        bonusesToRemove = new Array<BonusImage>();
        exp = 0;
        health = 100;
        bombCount = 3;
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
        else if (mazeLevel > 3) {
            Gdx.gl.glClearColor(244, 164, 96, 1);
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
            Gdx.gl.glClearColor(0, 0.8f, 0, 1);
            game.font.draw(game.batch, "Completed Level " + Integer.toString(mazeLevel - 1), 100, 600);
            game.font.draw(game.batch, "+ 1 Bomb", 180, 500);
            game.font.draw(game.batch, "Beginning Level " + Integer.toString(mazeLevel), 100, 300);
            game.batch.end();
            monsters.clear();
            arrows.clear();
            forests.clear();
            newForests();

            if (TimeUtils.millis() - levelTime > 3000) {
                bombCount++;
                nextLevel = false;
            }
        }
        //Normal tick
        else {
            switch (bowColor) {
                case 0:
                    game.batch.draw(bowImage, bow.x, bow.y);
                    break;
                case 1:
                    game.batch.draw(bowYellow, bow.x, bow.y);
                    break;
                case 2:
                    game.batch.draw(bowOrange, bow.x, bow.y);
                    break;
                case 3:
                    game.batch.draw(bowRed, bow.x, bow.y);
                    break;
                default:
                    game.batch.draw(bowImage, bow.x, bow.y);
                    break;
            }

            for (Rectangle forest : forests) {
                if (mazeLevel > 3) {
                    game.batch.draw(crystalImage, forest.x, forest.y);
                }
                else {
                    game.batch.draw(forestImage, forest.x, forest.y);
                }
            }
            for (Rectangle monster : monsters) {
                game.batch.draw(monsterImage, monster.x, monster.y);
            }
            for (Arrow arrow : arrows) {
                arrow.step();
                Rectangle rect = arrow.getRectangle();
                if (arrow.getPowerScore() == 3 && bombCount > 0) {
                    game.batch.draw(bombImage, rect.x, rect.y);
                }
                else if (arrow.getPowerScore() == 1 || arrow.getPowerScore() == 2) {
                    game.batch.draw(fireArrowImage, rect.x, rect.y);
                }
                else {
                    game.batch.draw(arrowImage, rect.x, rect.y);
                }
            }
            for (BonusImage bi : bonuses) {
                game.batch.draw(bi.getImage(), bi.getRect().x, bi.getRect().y);
                if (TimeUtils.millis() - bi.getTime() > 1000) {
                    bonusesToRemove.add(bi);
                }
                if (bi.getType() == 2) {
                    for (Rectangle m : monsters) {
                        if (bi.getRect().overlaps(m)) {
                            monstersToRemove.add(m);
                        }
                    }
                    monsters.removeAll(monstersToRemove, false);
                    for (Rectangle f : forests) {
                        if (bi.getRect().overlaps(f)) {
                            forestsToRemove.add(f);
                        }
                    }
                    forests.removeAll(forestsToRemove, false);
                }
            }
            bonuses.removeAll(bonusesToRemove, false);

            game.expFont.draw(game.batch, "EXP: " + Integer.toString(exp), 360, 700);
            game.expFont.draw(game.batch, "HEALTH: " + Integer.toString(health), 360, 650);
            game.expFont.draw(game.batch, "BOMBS: " + Integer.toString(bombCount), 360, 600);
            game.batch.end();

            if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnMonster();

            Iterator<Rectangle> iter = monsters.iterator();
            while (iter.hasNext()) {
                Rectangle monster = iter.next();
                //Speed monsters move
                monster.y -= (100 + (5 * mazeLevel)) * Gdx.graphics.getDeltaTime();
                if (monster.y - 64 < 0) {
                    iter.remove();
                    health -= 10;
                    if (health <= 0) {
                        game.setScreen(new RetryScreen(game));
                    } else {
                        dmg = true;
                    }
                }
            }
            for (Arrow arrow : arrows) {
                if (TimeUtils.millis() - arrow.getTimeAlive() > 1000) {
                    arrowsToRemove.add(arrow);
                }

                if (arrow.getRectangle().y + 64 > 800) {
                    arrowsToRemove.add(arrow);
                }

                for (int i = 0; i < monsters.size; i++) {
                    //Hit a monster
                    if (arrow.getRectangle().overlaps(monsters.get(i))) {
                        if (!arrowsToRemove.contains(arrow, false)) {
                            if (arrow.getPowerScore() == 1 || arrow.getPowerScore() == 2) {
                                BonusImage combo = new BonusImage(bullseyeImage, arrow.getRectangle(), 1);
                                bonuses.add(combo);
                                exp += 50;
                            }
                            else {
                                arrowsToRemove.add(arrow);
                            }
                        }
                        monstersToRemove.add(monsters.get(i));
                        if (arrow.getBonus1Hit()) {
                            int randomNum = rand.nextInt((10 - 1) + 1) + 1;
                            if (randomNum >= 8) {
                                BonusImage bi = new BonusImage(bonus1Image, arrow.getRectangle(), 0);
                                bonuses.add(bi);
                                exp += 100;
                            }
                        }
                        if (arrow.getPowerScore() == 3) {
                            BonusImage tempB = new BonusImage(mushroomCloudImage, arrow.getRectangle(), 2);
                            bombCount--;
                            bonuses.add(tempB);
                            explosion = true;
                        }
                        exp += 20 + (10 * mazeLevel - 1);
                        if (exp >= 1000 * mazeLevel) {
                            mazeLevel++;
                            nextLevel = true;
                            levelTime = TimeUtils.millis();
                        }
                        break;
                    }
                }
                for (Rectangle forst : forests) {
                    if (arrow.getRectangle().overlaps(forst)) {
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

    private void newForests() {
        for (int i = 0; i < 6; i++) {
            Rectangle f = new Rectangle();
            f.x = MathUtils.random(0, 480-64);
            f.y = MathUtils.random(300, 800 - 64);
            f.width = 64;
            f.height = 64;
            forests.add(f);
        }
    }

    private void newArrow(int startx, int endx, int starty, int endy, int bowColor) {
        Arrow arrow = new Arrow(Math.abs(startx - endx), Math.abs(starty - endy), startx - endx < 0, bowColor);
        lastArrowTime = TimeUtils.millis();
        arrows.add(arrow);
    }

}
