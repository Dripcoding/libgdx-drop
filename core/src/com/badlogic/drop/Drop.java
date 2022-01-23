package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class Drop extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;

	@Override
	public void create () {
		// load images for the droplet and the bucket
		dropImage = new Texture(Gdx.files.internal("drop.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load drop sound effect and rain background sound
		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterDrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("underTreeRain.mp3"));

		// start playback of the music background music
		rainMusic.setLooping(true);
		rainMusic.play();

		// create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// create spritebatch
		batch = new SpriteBatch();

		// create bucket
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		// update camera
		camera.update();

		// draw bucket
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		batch.end();

		// make bucket move - touched or mouse btn pressed
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos); // transform input coordinates
			bucket.x = touchPos.x - 62 / 2; // center bucket around input  position
		}

		// make bucket move - keyboard input
			// move without acceleration 200 pixels/second
			// need time passed between last and the current rendering frame
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		// ensure bucket stays within screen limits
		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

	}
	
	@Override
	public void dispose () {
//		batch.dispose();
//		img.dispose();
	}
}
