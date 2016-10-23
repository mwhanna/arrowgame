package com.logicdojo.arrowwar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

public class MyGame extends Game {

	public BitmapFont font;
	public BitmapFont expFont;
	public SpriteBatch batch;
	boolean dragging;
	Vector3 tp = new Vector3();
	OrthographicCamera camera;
	private FreeTypeFontGenerator generator;
	private FreeTypeFontGenerator generator2;

	@Override
	public void create () {
		batch = new SpriteBatch();

		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/alpha.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 22;
		parameter.borderWidth = 2;
		font = generator.generateFont(parameter);
		generator.dispose();

		generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Zygoth.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 9;
		parameter.borderWidth = 1;
		expFont = generator.generateFont(parameter2);
		generator2.dispose();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
	}

}
