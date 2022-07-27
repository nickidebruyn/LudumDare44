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
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
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
import za.co.bruynhuis.ld44.game.Level;
import za.co.bruynhuis.ld44.game.Player;
import za.co.bruynhuis.ld44.game.Player2;
import za.co.bruynhuis.ld44.input.AdvancedKeyboardControlEvent;
import za.co.bruynhuis.ld44.input.AdvancedKeyboardControlInputListener;
import za.co.bruynhuis.ld44.input.AdvancedKeyboardControlListener;

/**
 *
 * @author nicki
 */
public class PlayScreen extends AbstractScreen implements Blender3DGameListener, AdvancedKeyboardControlListener, JoystickListener {

    public static final String NAME = "PlayScreen";
    private Label title;
    private Label fightLabel;
    private ProgressBar progressBarPlayer1;
    private ProgressBar progressBarPlayer2;
    private Label player1Name;
    private Label player2Name;

    private MainApplication mainApplication;
    private Game game;
    private Player player;
    private Player player2;

    private Level level;

    private AdvancedKeyboardControlInputListener keyboardControlInputListener;
    private JoystickInputListener joystickInputListener;

    private boolean player1Left = false;
    private boolean player1Right = false;

    private int round = 0;
    private int player1Wins = 0;
    private int player2Wins = 0;
    
    private final float playerBlockRange = 2f;

    @Override
    protected void init() {

        mainApplication = (MainApplication) baseApplication;

        progressBarPlayer1 = new ProgressBar(hudPanel, "Interface/progress-out.png", "Interface/progress-in.png", 350, 40);
        progressBarPlayer1.leftTop(10, 10);
        hudPanel.add(progressBarPlayer1);
        progressBarPlayer1.setProgress(0.5f);

        progressBarPlayer2 = new ProgressBar(hudPanel, "Interface/progress-out.png", "Interface/progress-in.png", 350, 40);
        progressBarPlayer2.rightTop(10, 10);
        progressBarPlayer2.rotate(180 * FastMath.DEG_TO_RAD);
        hudPanel.add(progressBarPlayer2);
        progressBarPlayer2.setProgress(0.5f);

        player1Name = new Label(hudPanel, "Player", 16, 350, 40);
        player1Name.setTextColor(ColorRGBA.White);
        player1Name.setAlignment(TextAlign.LEFT);
        player1Name.leftTop(20, 10);

        player2Name = new Label(hudPanel, "Player 2", 16, 350, 40);
        player2Name.setTextColor(ColorRGBA.White);
        player2Name.setAlignment(TextAlign.RIGHT);
        player2Name.rightTop(20, 10);

        title = new Label(hudPanel, "Round 1", 58);
        title.centerAt(0, 10);
        title.setTextColor(ColorUtils.rgb(5, 5, 5));

        fightLabel = new Label(hudPanel, "Fight", 600, 50, new FontStyle(56, 6));
        fightLabel.centerAt(0, 10);
        fightLabel.setTextColor(ColorRGBA.Red);
        fightLabel.setOutlineColor(ColorUtils.rgb(5, 5, 5));

        keyboardControlInputListener = new AdvancedKeyboardControlInputListener();
        keyboardControlInputListener.addAdvancedKeyboardControlListener(this);

        joystickInputListener = new JoystickInputListener();
        joystickInputListener.addJoystickListener(this);
    }

    @Override
    protected void load() {

        player1Left = false;
        player1Right = false;

        level = mainApplication.getLevels().get(mainApplication.getCurrentLevel());

        game = new Game(mainApplication, rootNode, level.getScene());
        game.load();

        player = new Player(game, ColorUtils.rgb(0, 0, 0), ColorUtils.rgb(255, 184, 184), 1, "run");
//        player.setSpeedFactor(2);
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

        player2 = new Player2(game, level.getEnemyHairColor(), level.getEnemyBodyColor(), -1, "run2");
        player2.load();
        player2.warp(game.getPlayer2Start());
        player2.setAttackCallback(new AttackCallback() {
            @Override
            public void finishedAttack(int damage) {
                boolean inRange = player2.isInDamageRange(player, 1.2f);
//                log("Attack damage amount " + damage);

                if (inRange) {
//                    log("Player 1 take damage !");

                    player.hit(damage, player2.getMovementDirection());
                    showResults();

                }

            }
        });

        player2.getPlayerNode().addControl(new EnemyAIControl(game, player2, player, level.getDifficulty()));
        
        if (level.getDifficulty() == EnemyAIControl.DIFFICULTY_EASY) {
            player2.setSpeedFactor(0.8f);
            
        } else if (level.getDifficulty() == EnemyAIControl.DIFFICULTY_MEDIUM) {
            player2.setSpeedFactor(0.9f);
            
        } else if (level.getDifficulty() == EnemyAIControl.DIFFICULTY_DEFENSIVE) {
            player2.setSpeedFactor(1.1f);
            
        } else if (level.getDifficulty() == EnemyAIControl.DIFFICULTY_AGGRESIVE) {
            player2.setSpeedFactor(1.4f);
            
        }

        game.addGameListener(this);

        updateScore();

        camera.setLocation(new Vector3f(0, 6, 15));
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

        player2Name.setText(level.getName());

        title.setText("Round " + round);
        title.show();
        title.setScale(1);
        title.scaleFromTo(1, 1, 0.3f, 0.3f, 0.2f, 1f, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                title.hide();
                keyboardControlInputListener.registerWithInput(inputManager);
//                joystickInputListener.registerWithInput(inputManager);
                showMainMessage("FIGHT");
                game.getBaseApplication().getSoundManager().playSound("fight");
            }
        });
    }

    private void updateScore() {
        progressBarPlayer1.setProgress((float) player.getHealth() / (float) Player.MAX_HEALTH);
        progressBarPlayer2.setProgress((float) player2.getHealth() / (float) Player.MAX_HEALTH);

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
                fightLabel.scaleFromTo(1, 1, 0, 0, 0.5f, 2.5f, new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> bt) {
                        if (round < 3) {
                            //Continue to next round
                            showScreen(PlayScreen.NAME);

                        } else if (player1Wins > player2Wins) {
                            //Continue to new match
                            if (mainApplication.getCurrentLevel() == mainApplication.getLevels().size() - 1) {
                                //Game complete
                                round = 0;
                                player1Wins = 0;
                                player2Wins = 0;
                                showGameCompletMessage("Game Completed");

                            } else {
                                round = 0;
                                player1Wins = 0;
                                player2Wins = 0;
                                mainApplication.setCurrentLevel(mainApplication.getCurrentLevel() + 1);
                                showScreen(PlayScreen.NAME);
                            }

                        } else if (player1Wins == player2Wins) {
                            round = 0;
                            player1Wins = 0;
                            player2Wins = 0;
                            showScreen(PlayScreen.NAME);

                        } else {
                            round = 0;
                            player1Wins = 0;
                            player2Wins = 0;
                            showGameCompletMessage("Game over");
                        }

                    }
                });
            }
        });

    }

    protected void showGameCompletMessage(String message) {
        fightLabel.setText(message);
        fightLabel.setScale(0);
        fightLabel.show();
        fightLabel.scaleFromTo(0, 0, 1, 1, 1f, 0.5f, Bounce.OUT, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                fightLabel.scaleFromTo(1, 1, 0, 0, 0.5f, 2.5f, new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> bt) {
                        showScreen(MenuScreen.NAME);

                    }
                });
            }
        });

    }

    @Override
    protected void exit() {
        joystickInputListener.unregisterInput();
        keyboardControlInputListener.unregisterInput();
        game.close();
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

            if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !player.isDead() && !player2.isDead()) {
                if (player1Left) {

                    player.setWalkDirection(-1);
                    player.move(true);

                    if (player.isInRange(player2, 1, playerBlockRange)) {
                        player.setLookDirection(1);
                        player.moveBack(true);

                    } else {
                        player.setLookDirection(-1);
                        player.moveBack(false);
                    }

                } else if (player1Right) {

                    player.setWalkDirection(1);
                    player.move(true);

                    if (player.isInRange(player2, -1, playerBlockRange)) {
                        player.setLookDirection(-1);
                        player.moveBack(true);

                    } else {
                        player.setLookDirection(1);
                        player.moveBack(false);
                    }

                } else {
                    player.move(false);
                }

            }

        }
    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {
//        log("Game over");

        player1Left = false;
        player1Right = false;

        keyboardControlInputListener.unregisterInput();
        joystickInputListener.unregisterInput();

        showResults();

    }

    private void showResults() {
        if (player2.isDead()) {
            player1Wins++;
            player.win();
            showWinMessage("Player Wins");

        } else if (player.isDead()) {
            player2Wins++;
            player2.win();
            showWinMessage(level.getName() + " Wins");
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
//        log("Player and enemy collision");

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
    public void onKey(AdvancedKeyboardControlEvent keyboardControlEvent, float fps) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !player.isDead() && !player2.isDead()) {

            //PLAYER1
            if (keyboardControlEvent.isLeft() || keyboardControlEvent.isLeft2()) {
                player1Left = keyboardControlEvent.isKeyDown();
            }

            if (keyboardControlEvent.isRight() || keyboardControlEvent.isRight2()) {
                player1Right = keyboardControlEvent.isKeyDown();
            }

            if ((keyboardControlEvent.isUp() || keyboardControlEvent.isUp2()) && keyboardControlEvent.isKeyDown()) {
                player.jump();
            }

            if ((keyboardControlEvent.isBox() || keyboardControlEvent.isBox2()) && keyboardControlEvent.isKeyDown()) {
                player.box();
            }

            if ((keyboardControlEvent.isKick() || keyboardControlEvent.isKick2()) && keyboardControlEvent.isKeyDown()) {
                player.kick();
            }

        }

    }

    @Override
    public void stick(JoystickEvent joystickEvent, float fps) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused() && !player.isDead() && !player2.isDead()) {
            if (joystickEvent.isLeft()) {
                player1Left = joystickEvent.isAxisDown();
            }

            if (joystickEvent.isRight()) {
                player1Right = joystickEvent.isAxisDown();
            }

            if (joystickEvent.isButton5() && joystickEvent.isButtonDown()) {
                player.jump();
            }

            if (joystickEvent.isButton1() && joystickEvent.isButtonDown()) {
                player.box();
            }

            if (joystickEvent.isButton2() && joystickEvent.isButtonDown()) {
                player.kick();
            }

        }

    }

}
