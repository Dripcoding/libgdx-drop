package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

// Texture(FileHandle) - loaded image stored in video ram
	// internal files - assets
// Sound(FileHandle) - load sounds less than 10 seconds
// Music(FileHandle) - load sounds greater than 10 seconds

// camera - uses a matrix to set up coordinate system for rendering
	// matrices need to be recomputed when camera property changes (ie. position)
	// good practice to update camera once per frame

// libgdx provides gc aware collections - minimize gc

// represents the game play area
public class GameScreen implements Screen {

	private final Drop game;
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private int dropsGathered;

	public GameScreen(final Drop game) {
		this.game = game;

		// load images for the droplet and the bucket
		dropImage = new Texture(Gdx.files.internal("drop.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load drop sound effect and rain background sound
		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterDrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("underTreeRain.mp3"));

		// load background music
		rainMusic.setLooping(true);

		// create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// create bucket
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2; // centers bucket horizontally
		bucket.y = 20; // bottom left corner of bucket is 20 pixels above bottom screen edge
		bucket.width = 64;
		bucket.height = 64;

		// create raindrop
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}

	@Override
	public void render (float delta) {
		// clear screen with dark blue color
		ScreenUtils.clear(0, 0, 0.2f, 1);

		// update camera's matrices
		camera.update();

		// tell the SpriteBatch to render coordinate system of camera
		game.batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw bucket and drops
		// SpriteBatch records all drawing commands between begin() and end()
		game.batch.begin();
		game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 400);
		game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
		for(Rectangle raindrop : raindrops) {
			game.batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		game.batch.end();

		// make bucket move - touched or mouse btn pressed
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3(); // usually want this in game object so GC doesn't kick in frequently causing stutters
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos); // transform input coordinates to camera's coordinate system
			bucket.x = touchPos.x - 62 / 2; // center bucket around input position
		}

		// make bucket move - keyboard input
			// move without acceleration 200 pixels/second
			// need time passed between last and the current rendering frame
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		// ensure bucket stays within screen limits
		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		// render rain drop
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		// move rain drops at constant speed
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 62 < 0) {
				iter.remove();
			}

			if (raindrop.overlaps(bucket)) {
				dropsGathered++;
				dropSound.play();
				iter.remove();
			}
		}
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void show() {
		// start background music when screen is shown
		rainMusic.play();
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

	// clean up objects made during game execution
	// Disposable interface provides dispose()
	// Disposables are not handled by Java garbage collector -> need to be manually disposed
	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}
}
