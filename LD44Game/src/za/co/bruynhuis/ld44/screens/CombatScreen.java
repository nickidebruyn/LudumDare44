/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import com.bruynhuis.galago.games.blender3d.Blender3DGameListener;
import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickInputListener;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.field.ProgressBar;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import za.co.bruynhuis.ld44.MainApplication;
import za.co.bruynhuis.ld44.game.AttackCallback;
import za.co.bruynhuis.ld44.game.EnemyAIControl;
import za.co.bruynhuis.ld44.game.Game;
import za.co.bruynhuis.ld44.game.Player;
import za.co.bruynhuis.ld44.game.Player2;

/**
 *
 * @author nicki
 */
public class CombatScreen extends AbstractScreen implements Blender3DGameListener, KeyboardControlListener, JoystickListener {

    public static final String NAME = "CombatScreen";
    private Label title;
    private Label fightLabel;
    private ProgressBar progressBarPlayer1;
    private ProgressBar progressBarPlayer2;

    private MainApplication mainApplication;
    private Game game;
    private Player player;
    private Player player2;

    private KeyboardControlInputListener keyboardControlInputListener;
    private JoystickInputListener joystickInputListener;

    private boolean player1Left = false;
    private boolean player1Right = false;

    private boolean player2Left = false;
    private boolean player2Right = false;

    private int round = 0;
    private int player1Wins = 0;
    private int player2Wins = 0;

    @Override
    protected void init() {

        mainApplication = (MainApplication) baseApplication;
        
        progressBarPlayer1 = new ProgressBar(hudPanel, "Interface/progress-out.png", "Interface/progress-in.png", 300, 30);
        progressBarPlayer1.leftTop(10, 10);
        hudPanel.add(progressBarPlayer1);
        progressBarPlayer1.setProgress(0.5f);        
        
        progressBarPlayer2 = new ProgressBar(hudPanel, "Interface/progress-out.png", "Interface/progress-in.png", 300, 30);
        progressBarPlayer2.rightTop(10, 10);
        progressBarPlayer2.rotate(180*FastMath.DEG_TO_RAD);
        hudPanel.add(progressBarPlayer2);
        progressBarPlayer2.setProgress(0.5f);

        title = new Label(hudPanel, "Round 1", 58);
        title.centerAt(0, 200);
        title.setTextColor(ColorUtils.rgb(5, 5, 5));

        fightLabel = new Label(hudPanel, "Fight", 600, 50, new FontStyle(56, 6));
        fightLabel.centerAt(0, 200);
        fightLabel.setTextColor(ColorRGBA.Red);
        fightLabel.setOutlineColor(ColorUtils.rgb(5, 5, 5));

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);

        joystickInputListener = new JoystickInputListener();
        joystickInputListener.addJoystickListener(this);
    }

    @Override
    protected void load() {

        player1Left = false;
        player1Right = false;

        player2Left = false;
        player2Right = false;

        game = new Game(mainApplication, rootNode, "Scenes/level1.j3o");
        game.load();

        player = new Player(game, ColorUtils.rgb(83, 82, 237), ColorUtils.rgb(236, 204, 104), 1);
        player.load();
        player.setAttackCallback(new AttackCallback() {
            @Override
            public void finishedAttack(int damage) {
                boolean inRange = player.isInDamageRange(player2, 1.2f);
//                log("Attack damage amount " + damage);
//                log("Distance damage amount " + distanceDamage);
                if (inRange) {
                    player2.hit(damage, player.getMovementDirection());
                    showResults();

                }

            }
        });

        player2 = new Player2(game, null, null, -1);
        player2.load();
        player2.warp(game.getPlayer2Start());
        player2.setAttackCallback(new AttackCallback() {
            @Override
            public void finishedAttack(int damage) {
                boolean inRange = player2.isInDamageRange(player, 1.2f);
//                log("Attack damage amount " + damage);
//                log("Distance damage amount " + distanceDamage);
                if (inRange) {
                    player.hit(damage, player2.getMovementDirection());
                    showResults();

                }

            }
        });
        
        player2.getPlayerNode().addControl(new EnemyAIControl(game, player2, player, 0));

        game.addGameListener(this);
        
        updateScore();

//        chaseCamera = new ChaseCamera(camera, player.getPlayerNode(), inputManager);
//        chaseCamera.setDefaultHorizontalRotation(FastMath.DEG_TO_RAD*90);
//        chaseCamera.setDefaultVerticalRotation(FastMath.DEG_TO_RAD*10);
//        chaseCamera.setDefaultDistance(10);
        camera.setLocation(new Vector3f(0, 5, 15));
        camera.lookAt(new Vector3f(0, 4.5f, 0), Vector3f.UNIT_Y);

    }

    @Override
    protected void show() {
//        mainApplication.showDebuging();
        setPreviousScreen(MenuScreen.NAME);

        round++;

        game.start(player);

        fightLabel.hide();
        fightLabel.setScale(0);

        title.setText("Round " + round);
        title.show();
        title.setScale(1);
        title.scaleFromTo(1, 1, 0.3f, 0.3f, 0.2f, 1f, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                title.hide();
                keyboardControlInputListener.registerWithInput(inputManager);
                joystickInputListener.registerWithInput(inputManager);
                showMainMessage("FIGHT");
            }
        });
    }
    
    private void updateScore() {
        progressBarPlayer1.setProgress((float)player.getHealth()/(float)Player.MAX_HEALTH);
        progressBarPlayer2.setProgress((float)player2.getHealth()/(float)Player.MAX_HEALTH);
        
    }

    protected void showMainMessage(String message) {
        fightLabel.setText(message);
        fightLabel.setScale(0);
        fightLabel.show();
        fightLabel.scaleFromTo(0, 0, 1, 1, 0.4f, 0.1f, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                fightLabel.setScale(1);
                fightLabel.scaleFromTo(1, 1, 0, 0, 0.3f, 1, new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> bt) {
                        fightLabel.setScale(0);
                        fightLabel.hide();
                    }
                });

            }
        });

    }

    protected void showWinMessage(String message) {
        fightLabel.setText(message);
        fightLabel.setScale(0);
        fightLabel.show();
        fightLabel.scaleFromTo(0, 0, 1, 1, 1f, 0.5f, Bounce.OUT, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {                
                showScreen(CombatScreen.NAME);
            }
        });

    }

    @Override
    protected void exit() {
        game.close();
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

            if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !player.isDead() && !player2.isDead()) {
                if (player1Left) {
                    
                    player.setWalkDirection(-1);
                    player.move(true);
                    
                    if (player.isInRange(player2, 1, 4f)) {
                        player.setLookDirection(1);
                        player.moveBack(true);
                        
                    } else {
                        player.setLookDirection(-1);
                        player.moveBack(false);
                    }

                } else if (player1Right) {
                    
                    player.setWalkDirection(1);
                    player.move(true);
                    
                    if (player.isInRange(player2, -1, 4f)) {
                        player.setLookDirection(-1);
                        player.moveBack(true);
                        
                    } else {
                        player.setLookDirection(1);
                        player.moveBack(false);
                    }

                } else {
                    player.move(false);
                }

                if (player2Left) {
                    player2.setWalkDirection(-1);
                    player2.move(true);
                    
                    if (player2.isInRange(player, 1, 4f)) {
                        player2.setLookDirection(1);
                        player2.moveBack(true);
                        
                    } else {
                        player2.setLookDirection(-1);
                        player2.moveBack(false);
                    }

                } else if (player2Right) {
                    player2.setWalkDirection(1);
                    player2.move(true);
                    
                    if (player2.isInRange(player, -1, 4f)) {
                        player2.setLookDirection(-1);
                        player2.moveBack(true);
                        
                    } else {
                        player2.setLookDirection(1);
                        player2.moveBack(false);
                    }

                } else {
                    player2.move(false);
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
        
        player1Left = false;
        player1Right = false;
        player2Left = false;
        player2Right = false;
        
        keyboardControlInputListener.unregisterInput();
        joystickInputListener.unregisterInput();

        showResults();

    }

    private void showResults() {
        if (player2.isDead()) {
            player.win();
            showWinMessage("Player 1 Wins");

        } else if (player.isDead()) {
            player2.win();
            showWinMessage("Player 2 Wins");
        }

    }

    @Override
    public void doGameCompleted() {
    }

    @Override
    public void doScoreChanged(int score) {

//        log("Player 2 health = " + player2.getHealth());
        updateScore();

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

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !player.isDead() && !player2.isDead()) {
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
//                log("Box");
                player.box();
            }

            if (keyboardControlEvent.isButton2() && keyboardControlEvent.isKeyDown()) {
//                log("Kick");
                player.kick();
            }

        }

    }

    @Override
    public void stick(JoystickEvent joystickEvent, float fps) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !player.isDead() && !player2.isDead()) {
            if (joystickEvent.isLeft()) {
                player2Left = joystickEvent.isAxisDown();
            }

            if (joystickEvent.isRight()) {
                player2Right = joystickEvent.isAxisDown();
            }

            if (joystickEvent.isButton5() && joystickEvent.isButtonDown()) {
                player2.jump();
            }

            if (joystickEvent.isButton1() && joystickEvent.isButtonDown()) {
//                log("Box");
                player2.box();
            }

            if (joystickEvent.isButton2() && joystickEvent.isButtonDown()) {
//                log("Kick");
                player2.kick();
            }

        }

    }

}
