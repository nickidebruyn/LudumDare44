/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.games.blender3d.Blender3DGameListener;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Spatial;
import za.co.bruynhuis.ld44.MainApplication;
import za.co.bruynhuis.ld44.game.AttackCallback;
import za.co.bruynhuis.ld44.game.Game;
import za.co.bruynhuis.ld44.game.Player;
import za.co.bruynhuis.ld44.game.Player2;

/**
 *
 * @author nicki
 */
public class PlayScreen extends AbstractScreen implements Blender3DGameListener, KeyboardControlListener {

    public static final String NAME = "PlayScreen";
    private Label title;

    private MainApplication mainApplication;
    private Game game;
    private Player player;
    private Player player2;

    private CartoonEdgeProcessor cep;
    private FilterPostProcessor fpp;

    private ChaseCamera chaseCamera;
    private KeyboardControlInputListener keyboardControlInputListener;
    private boolean player1Left = false;
    private boolean player1Right = false;

    @Override
    protected void init() {

        mainApplication = (MainApplication) baseApplication;

        title = new Label(hudPanel, "Round 1", 50);
        title.centerTop(0, 50);
        title.setTextColor(ColorRGBA.DarkGray);

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);
    }

    @Override
    protected void load() {

        game = new Game(mainApplication, rootNode, "Scenes/menu.j3o");
        game.load();

        player = new Player(game, ColorUtils.rgb(83, 82, 237), ColorUtils.rgb(236, 204, 104), 1);
        player.load();
        player.setAttackCallback(new AttackCallback() {
            @Override
            public void finishedAttack(int damage) {
                int distanceDamage = player.getDamageRange(player2);
                log("Attack damage amount " + damage);
                log("Distance damage amount " + distanceDamage);
                if (distanceDamage > 0) {
                    player2.hit(damage * distanceDamage);
                    
                    if (player2.isDead()) {
                        player.win();
                    }

                }

            }
        });

        player2 = new Player2(game, null, null, -1);
        player2.load();
        player2.warp(game.getPlayer2Start());

        game.addGameListener(this);

        loadFX();

//        chaseCamera = new ChaseCamera(camera, player.getPlayerNode(), inputManager);
//        chaseCamera.setDefaultHorizontalRotation(FastMath.DEG_TO_RAD*90);
//        chaseCamera.setDefaultVerticalRotation(FastMath.DEG_TO_RAD*10);
//        chaseCamera.setDefaultDistance(10);
        camera.setLocation(new Vector3f(0, 5, 15));
        camera.lookAt(new Vector3f(0, 4.5f, 0), Vector3f.UNIT_Y);

    }

    private void loadFX() {
        if (cep == null) {
            cep = new CartoonEdgeProcessor();
                        

            fpp = new FilterPostProcessor(baseApplication.getAssetManager());
//            baseApplication.getViewPort().addProcessor(fpp);

            FXAAFilter fXAAFilter = new FXAAFilter();
            fpp.addFilter(fXAAFilter);

//        ArtFilter bleachFilter = new ArtFilter();
//        fpp.addFilter(bleachFilter);
//        SSAOFilter ssaof = new SSAOFilter();        
//        fpp.addFilter(ssaof);
        }

        baseApplication.getViewPort().addProcessor(cep);
    }

    @Override
    protected void show() {
//        mainApplication.showDebuging();
        setPreviousScreen(MenuScreen.NAME);

        game.start(player);

        title.show();
        title.setScale(0);
        title.scaleFromTo(0, 0, 1, 1, 1f, 0.5f, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                title.hide();
                keyboardControlInputListener.registerWithInput(inputManager);
            }
        });

    }

    @Override
    protected void exit() {
        game.close();
        baseApplication.getViewPort().removeProcessor(cep);
    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {
            if (keyboardControlEvent.isLeft()) {
                player1Left = keyboardControlEvent.isKeyDown();
            }

            if (keyboardControlEvent.isRight()) {
                player1Right = keyboardControlEvent.isKeyDown();
            }

            if (keyboardControlEvent.isUp() && keyboardControlEvent.isKeyDown()) {
                player.jump();
            }

            if (keyboardControlEvent.isButton4() && keyboardControlEvent.isKeyDown()) {
                log("Box");
                player.box();
            }

            if (keyboardControlEvent.isButton2() && keyboardControlEvent.isKeyDown()) {
                log("Kick");
                player.kick();
            }

        }

    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

            if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {
                if (player1Left) {
                    player.setDirection(-1);
                    player.walk(true);

                } else if (player1Right) {
                    player.setDirection(1);
                    player.walk(true);

                } else {
                    player.walk(false);
                }
            }

        }
    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {
        log("Game over");
        keyboardControlInputListener.unregisterInput();
//        showScreen(PlayScreen.NAME);
    }

    @Override
    public void doGameCompleted() {
    }

    @Override
    public void doScoreChanged(int score) {

        log("Player 2 health = " + player2.getHealth());

    }

    @Override
    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {
        log("Player and enemy collision");

    }

    @Override
    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {
    }

}
