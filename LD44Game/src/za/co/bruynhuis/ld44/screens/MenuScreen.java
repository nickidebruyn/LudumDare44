/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.screens;

import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.effect.TextWriteEffect;
import com.bruynhuis.galago.ui.effect.WobbleEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import za.co.bruynhuis.ld44.MainApplication;
import za.co.bruynhuis.ld44.game.Game;
import za.co.bruynhuis.ld44.game.Player;
import za.co.bruynhuis.ld44.ui.LargeButton;

/**
 *
 * @author nicki
 */
public class MenuScreen extends AbstractScreen implements KeyboardControlListener {

    public static final String NAME = "MenuScreen";
    private float scale = 0.02f;
    private Label title;
    private LargeButton playButton;
    private LargeButton combatButton;
    private LargeButton settingsButton;
    private LargeButton creditsButton;
    private LargeButton exitButton;
    private Game game;
    private Player player;
    private KeyboardControlInputListener keyboardControlInputListener;
    private boolean firstLoad = true;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Paper Combat", 58, 460, 50);
        title.setTextColor(ColorUtils.rgb(5, 5, 5));
        title.setAlignment(TextAlign.LEFT);
        title.centerTop(0, 50);
        title.setAnimated(true);
        title.addEffect(new TextWriteEffect(title, 14));

        playButton = new LargeButton(hudPanel, "playbutton", "Campaign >>");
        playButton.rightBottom(100, 340);
        playButton.addEffect(new WobbleEffect(playButton, 1.05f, 0.1f));
        playButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen(PlayScreen.NAME);

                }
            }

        });

        combatButton = new LargeButton(hudPanel, "combatButton", "1 on 1 Combat");
        combatButton.rightBottom(100, 340);
        combatButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen(CombatScreen.NAME);

                }
            }

        });

        settingsButton = new LargeButton(hudPanel, "settingsbutton", "Settings");
        settingsButton.rightBottom(100, 270);
        settingsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen(SettingsScreen.NAME);

                }
            }

        });

        creditsButton = new LargeButton(hudPanel, "creditsButton", "Credits");
        creditsButton.rightBottom(100, 200);
        creditsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen(CreditsScreen.NAME);

                }
            }

        });

        exitButton = new LargeButton(hudPanel, "exitButton", "Exit");
        exitButton.rightBottom(100, 130);
        exitButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    exitScreen();

                }
            }

        });

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);

    }

    @Override
    protected void load() {

        game = new Game((MainApplication) baseApplication, rootNode, "Scenes/menu.j3o");
        game.load();

        player = new Player(game, ColorRGBA.Black, ColorRGBA.White, 1);
        player.load();

        camera.setLocation(new Vector3f(0, 5, 15));
        camera.lookAt(new Vector3f(0, 4.5f, 0), Vector3f.UNIT_Y);

    }

    @Override
    protected void show() {
        game.start(player);
        setPreviousScreen(null);

        if (firstLoad) {
            title.setVisible(false);
            title.show();

            playButton.moveFromToCenter(1280, 0, 400, 0, 1, 1);
            combatButton.moveFromToCenter(1280, -70, 400, -70, 1, 1.5f);
            settingsButton.moveFromToCenter(1280, -140, 400, -140, 1, 2f);
            creditsButton.moveFromToCenter(1280, -210, 400, -210, 1, 2.5f);
            exitButton.moveFromToCenter(1280, -280, 400, -280, 1, 3f);
            firstLoad = false;
        }

        keyboardControlInputListener.registerWithInput(inputManager);
    }

    @Override
    protected void exit() {
        keyboardControlInputListener.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {

        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isButton1()) {
            showScreen(PlayScreen.NAME);

        }

    }

}
