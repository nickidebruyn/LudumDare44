/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.screens;

import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Label;
import com.jme3.math.ColorRGBA;
import za.co.bruynhuis.ld44.MainApplication;
import za.co.bruynhuis.ld44.game.Game;
import za.co.bruynhuis.ld44.game.Player;

/**
 *
 * @author nicki
 */
public class MenuScreen extends AbstractScreen {

    public static final String NAME = "MenuScreen";
    private float scale = 0.02f;
    private Label title;
    private Game game;
    private Player player;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Paper Boy");
        title.centerTop(0, 0);

    }

    @Override
    protected void load() {

        game = new Game((MainApplication) baseApplication, rootNode, "Scenes/menu.j3o");
        game.load();

        player = new Player(game, ColorRGBA.Black, ColorRGBA.Pink, 1);
        player.load();

    }

    @Override
    protected void show() {
        game.start(player);
        setPreviousScreen(null);
    }

    @Override
    protected void exit() {
        game.close();
    }

    @Override
    protected void pause() {
    }

}
