package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// screens contain many methods from ApplicationListener
    // show() - invoked when screen gains focus
    // hide() - invoked when screen loses focus
    // responsible for handling 1 aspect of a game
        // menu screen
        // settings screen
        // game screen

// Game.class responsible for handling multiple screens
    // provides helper methods
    // provides ApplicationListener impl

// SpriteBatch - used to draw 2D images (ie. Textures)

public class Drop extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    // entry point to the game
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); // renders the set screen
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
