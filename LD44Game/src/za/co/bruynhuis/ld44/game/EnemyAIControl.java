/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game;

import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class EnemyAIControl extends AbstractControl implements DamageCallback {

    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_AGGRESIVE = 3;
    public static final int DIFFICULTY_DEFENSIVE = 4;

    private Game game;
    private Player player;
    private Player opponent;
    private int difficulty;
    private boolean takeDamage = false;

    private boolean playerLeft = false;
    private boolean playerRight = false;
    private boolean playerBox = false;
    private boolean playerKick = false;
    private boolean playerJump = false;
    private boolean playerWasHit = false;

    private Timer attackTimer = new Timer(100);
    private boolean canAttack = true;

    private Timer turnTimer = new Timer(100);
    private boolean canTurn = true;

    private Timer idleTimer = new Timer(200);
    private Timer moveTimer = new Timer(200);    
    private boolean canMove = false;

    public EnemyAIControl(Game game, Player player, Player opponent, int difficulty) {
        this.game = game;
        this.player = player;
        this.opponent = opponent;
        this.difficulty = difficulty;
        this.player.setDamageCallback(this);
        attackTimer.start();
        turnTimer.start();
        idleTimer.start();
        moveTimer.stop();
//        
//        if (difficulty == EnemyAIControl.DIFFICULTY_EASY) {
//
//            
//        } else if (difficulty == EnemyAIControl.DIFFICULTY_MEDIUM) {
//
//            
//        } else if (difficulty == EnemyAIControl.DIFFICULTY_DEFENSIVE) {
//            idleTimer.setMaxTime(300);
//            moveTimer.setMaxTime(100);
//            attackTimer.setMaxTime(300);
//            
//        } else if (difficulty == EnemyAIControl.DIFFICULTY_AGGRESIVE) {
//            idleTimer.setMaxTime(150);
//            moveTimer.setMaxTime(100);
//            attackTimer.setMaxTime(200);
//
//            
//        }
        

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {

            //RESET ACTIONS
            playerLeft = false;
            playerRight = false;
            playerBox = false;
            playerKick = false;
            playerJump = false;

            //Update all timers
            //##################################################
            attackTimer.update(tpf);
            if (attackTimer.finished()) {
                canAttack = true;
                attackTimer.stop();
            }

            turnTimer.update(tpf);
            if (turnTimer.finished()) {
                canTurn = true;
                turnTimer.stop();
            }

            idleTimer.update(tpf);
            if (idleTimer.finished()) {
                canMove = true;
                moveTimer.reset();
                idleTimer.stop();
            }
            
            moveTimer.update(tpf);
            if (moveTimer.finished()) {
                canMove = false;
                moveTimer.stop();
                idleTimer.reset();
            }
            
            //END TIMERS
            //##################################################

            //START CALCULATE ACTIONS
            //**************************************************
            if (!player.isDead() && !opponent.isDead()) {

                lookAtOpponent();

                if (canAttack) {

                    if (opponent.getPosition().x < player.getPosition().x) {
                        if (player.isInRange(opponent, -1, 1.5f)) {
                            pickRandomAttack();

                        }

                    } else if (player.isInRange(opponent, 1, 1.5f)) {
                        pickRandomAttack();

                    }

                }

                if (canMove) {
                    if (opponent.getPosition().x < player.getPosition().x) {
                        if (!player.isInRange(opponent, -1, 1.5f)) {
                            moveTowardsOpponent();

                        }

                    } else if (!player.isInRange(opponent, 1, 1.5f)) {
                        moveTowardsOpponent();
                    }

                }

            }

            //END CALCULATE ACTIONS
            //*************************************************
            //DO ACTIONS
            //-------------------------------------------------
            if (playerLeft) {
                player.setWalkDirection(-1);
                player.move(true);

                if (player.isInRange(opponent, 1, 4f)) {
                    player.setLookDirection(1);
                    player.moveBack(true);

                } else {
                    player.setLookDirection(-1);
                    player.moveBack(false);
                }

            } else if (playerRight) {
                player.setWalkDirection(1);
                player.move(true);

                if (player.isInRange(opponent, -1, 4f)) {
                    player.setLookDirection(-1);
                    player.moveBack(true);

                } else {
                    player.setLookDirection(1);
                    player.moveBack(false);
                }

            } else {
                player.move(false);
            }
            
            if (playerJump) {
                player.jump();                
            }

            if (playerBox) {
                player.box();
            }

            if (playerKick) {
                player.kick();
            }

            //END DO ACTIONS
            //-----------------------------------------------------
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void lookAtOpponent() {
        if (canTurn) {
            if (opponent.getPosition().x < player.getPosition().x) {
                player.setLookDirection(-1);
                player.setWalkDirection(-1);

            } else {
                player.setLookDirection(1);
                player.setWalkDirection(1);

            }
            canTurn = false;
            turnTimer.reset();
        }
    }

    private void pickRandomAttack() {
        if (canAttack) {
            int index = FastMath.nextRandomInt(0, 100);

            if (index > 70) {
                playerBox = true;

            } else if (index > 50) {
                playerKick = true;

            } else if (playerWasHit) {
                playerJump = true;
                playerWasHit = false;
            }
            
            canAttack = false;
            attackTimer.reset();
        }
    }

    private void moveTowardsOpponent() {
        if (opponent.getPosition().x < player.getPosition().x) {
            playerLeft = true;

        } else {
            playerRight = true;

        }

    }
    
    private void moveAwayFromOpponent() {
        if (opponent.getPosition().x < player.getPosition().x) {
            playerRight = true;

        } else {
            playerLeft = true;

        }

    }

    @Override
    public void takeDamage(int damage) {
        Debug.log("AI got hit");
        playerWasHit = true;

    }
}
