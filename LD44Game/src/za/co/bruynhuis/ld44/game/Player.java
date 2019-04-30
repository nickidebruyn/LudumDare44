/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game;

import com.bruynhuis.galago.control.AnimationControl;
import com.bruynhuis.galago.games.blender3d.Blender3DGame;
import com.bruynhuis.galago.games.blender3d.Blender3DPlayer;
import com.bruynhuis.galago.listener.AnimationListener;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.animation.AnimControl;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nicki
 */
public class Player extends Blender3DPlayer implements AnimationListener {

    protected static final String RUN_JUMP = "run-jump";
    protected static final String DIE = "die";
    protected static final String HIT = "hit";
    protected static final String FALL = "fall";
    protected static final String JUMPING = "jumping";
    protected static final String KICK = "kick";
    protected static final String IDLE = "idle";
    protected static final String BOXING = "boxing";
    protected static final String RUN = "run";
    protected static final String IDLE_ATTACK = "idle-attack";
    protected static final String LOOK = "look";
    protected static final String YELLING = "yelling";
    protected static final String DANCE = "dance";
    protected static final String VICTORY = "victory";
    protected static final String SPELL = "spell";
    protected static final String WALK_BACK = "walk-back";
    
    protected static final int ATTACK_BOX = 1;
    protected static final int ATTACK_KICK = 2;
    protected static final int ATTACK_SPELL = 3;

    public static final int MAX_HEALTH = 10;

    private Spatial model;
    private AnimationControl animationControl;
    private float moveSpeed = 220f;
    private float movementDirection = 0;
    private Quaternion rotator = new Quaternion();
    private Vector3f lookDirection = new Vector3f(1, 0, 0);
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private MyCharacterControl characterControl;
    private Timer jumpTimer = new Timer(30);
    private Timer attackTimer = new Timer(60);
    private Timer damageTimer = new Timer(30);
    private String idleAnim = IDLE_ATTACK;
    private String moveAnim = RUN;

    private Vector3f jumpForceRun = new Vector3f(0, 100, 0);
    private Vector3f jumpForceStand = new Vector3f(0, 120, 0);
    private boolean walking = false;
    private boolean jumping = false;
    private boolean attacking = false;
    private boolean damage = false;
    private boolean died = false;
    private Spatial hair;
    private Spatial body;
    private ColorRGBA hairColor;
    private ColorRGBA bodyColor;
    private AttackCallback attackCallback;
    private DamageCallback damageCallback;
    private boolean attackWasFired = false;
    private String runSound = "run";
    private int attackType = 1;
    private float speedFactor = 1f;

    public Player(Blender3DGame physicsGame, ColorRGBA hairColor, ColorRGBA bodyColor, float direction, String runSound) {
        super(physicsGame);
        this.hairColor = hairColor;
        this.bodyColor = bodyColor;
        this.lookDirection.setX(direction);
        this.runSound = runSound;
    }

    public void setAttackCallback(AttackCallback attackCallback) {
        this.attackCallback = attackCallback;
    }

    private void fireAttackFinishAction(int damage) {
        if (attackCallback != null) {
            attackCallback.finishedAttack(damage);
        }
    }

    public void setDamageCallback(DamageCallback damageCallback) {
        this.damageCallback = damageCallback;
    }

    private void fireDamageAction(int damage) {
        if (damageCallback != null) {
            damageCallback.takeDamage(damage);
        }
    }

    @Override
    protected void init() {
        lives = MAX_HEALTH;
        model = game.getBaseApplication().getAssetManager().loadModel("Models/characters/boy.j3o");
        model.scale(2.2f);
        playerNode.attachChild(model);

//        SpatialUtils.updateCartoonColor(model, null, ColorRGBA.White, ColorRGBA.Black, 2f, false, true);
        SpatialUtils.translate(playerNode, game.getStartPosition().x, game.getStartPosition().y, 0);
        playerNode.lookAt(lookDirection.mult(-1), Vector3f.UNIT_Y);

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
//                    Debug.log("Spatial: " + spatial.getName());
                if (spatial.getControl(AnimControl.class) != null && spatial.getUserData("animation") != null) {
//                    Debug.log("Found Anim Control on " + spatial.getName());
                    animationControl = new AnimationControl();
                    animationControl.addAnimationListener(Player.this);
                    spatial.addControl(animationControl);
                }

                if (spatial.getName().startsWith("body")) {
                    body = spatial;

                } else if (spatial.getName().startsWith("hair")) {
                    hair = spatial;

                }

                if (spatial.getName().startsWith("Armature.")) {
                    spatial.removeFromParent();

                } else if (spatial instanceof Geometry) {
                    Material mat = ((Geometry) spatial).getMaterial();
                    MatParam colorParam = mat.getParam("Color");

                    if (colorParam != null) {
                        ColorRGBA color = (ColorRGBA) colorParam.getValue();
//                        log("Color of geom = " + color);
                        SpatialUtils.addCartoonColor(spatial, null, color, ColorRGBA.Black, 1.8f, false, true);
                    }

                }

            }
        };
        model.depthFirstTraversal(sgv);

        //Load the physics part
        characterControl = new MyCharacterControl(0.3f, 1.0f, 10f); // construct character. (If your character bounces, try increasing height and weight.)
        playerNode.addControl(characterControl); // attach to wrapper

        // set basic physical properties:
        characterControl.setJumpForce(jumpForceRun);
        characterControl.warp(playerNode.getWorldTranslation().clone()); // warp character into landscape at particular location        
        characterControl.setViewDirection(lookDirection);
//        characterControl.setGravity(new Vector3f(0, 30, 0));

        // add to physics state
        game.getBaseApplication().getBulletAppState().getPhysicsSpace().add(characterControl);
        game.getBaseApplication().getBulletAppState().getPhysicsSpace().addAll(playerNode);

        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (!isDead()) {

                    if (animationControl != null) {

                        if (!jumping && !attacking && !damage && !died && characterControl.isOnGround()) {

                            if (walking) {
                                animationControl.play(moveAnim, true, false, 1.2f*speedFactor);
                                
                            } else {
                                animationControl.play(idleAnim, true, false, 1);
                            }

                        } else if (!died) {
                            jumpTimer.update(tpf);
                            if (jumpTimer.finished()) {
                                characterControl.jump();
                                game.getBaseApplication().getSoundManager().playSound("jump");
                                jumpTimer.stop();
                            }

                            attackTimer.update(tpf);
                            if (attackTimer.finished()) {
                                if (!attackWasFired) {
                                    attackWasFired = true;
                                    fireAttackFinishAction(attackType);
                                    attacking = false;
                                }
                            }

                            damageTimer.update(tpf);
                            if (damageTimer.finished()) {
                                damage = false;
                                damageTimer.stop();
                            }

                        }

//                        log("Damage = " + damage + "; attck = " + attacking);
                        //Calculation to move a character to a target position
                        rotator.lookAt(lookDirection, Vector3f.UNIT_Y);
                        spatial.getLocalRotation().slerp(rotator, 0.2f);
                        characterControl.setViewDirection(spatial.getLocalRotation().getRotationColumn(2));

                        if ((attacking || damage || died) && characterControl.isOnGround()) {
                            walkDirection.set(0, 0, 0);
                        }

                        characterControl.setWalkDirection(walkDirection.mult(tpf * moveSpeed * speedFactor));

                    }

                    if (!game.isGameOver() && getPosition().y < -5) {
                        lives = 0;
                        died = true;
                        game.doGameOver();
                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });

        if (hairColor != null) {
            SpatialUtils.updateSpatialColor(hair, hairColor);
        }

        if (bodyColor != null) {
            SpatialUtils.updateSpatialColor(body, bodyColor);
        }

    }

    @Override
    protected float getSize() {
        return 1f;

    }

    public int getHealth() {
        return lives;
    }

    @Override
    public Vector3f getPosition() {
        return characterControl.getPosition();

    }

    @Override
    public void doDie() {
        //TODO        
//        log("Player died " + playerNode.getName());

    }

    @Override
    public void doAnimationDone(String animationName) {

        if (!game.isGameOver()) {
            log("Anim done = " + animationName);

            if (animationName.equals(JUMPING)) {
                jumping = false;
                animationControl.play(FALL, true, false, 1);

            } else if (animationName.equals(RUN_JUMP)) {
                jumping = false;

            } else if (animationName.equals(HIT)) {
                damage = false;

            } else if (animationName.equals(DIE)) {
                died = false;

            } else if (animationName.equals(KICK) || animationName.equals(BOXING)) {
                attacking = false;
                jumping = false;

            } else if (animationName.equals(idleAnim)) {
                //Pick a new idle animtion
//                int index = FastMath.nextRandomInt(0, 2);
                int index = FastMath.nextRandomInt(2, 2); //FORCE TO IDLE ATTACK

                if (index == 0) {
                    idleAnim = IDLE;

                } else if (index == 1) {
                    idleAnim = LOOK;

                } else {
                    idleAnim = IDLE_ATTACK;
                }
            }

        }

    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public float getMovementDirection() {
        return movementDirection;
    }

    public void hit(int hitpoint, float direction) {
        doDamage(hitpoint);

        game.getBaseApplication().getEffectManager().doEffect("blooddust", getPosition().add(0, 0.8f, -0.5f));

        if (hitpoint > 1) {
            int i = FastMath.nextRandomInt(1, 2);
            game.getBaseApplication().getEffectManager().doEffect("bloodsplat" + i, getPosition().add(0, 0.8f, -1f), 3000);

        } else {
            int i = FastMath.nextRandomInt(1, 10);
            if (i > 3) {
                game.getBaseApplication().getEffectManager().doEffect("bloodsplat3", getPosition().add(0, 0.8f, -1f), 3000);
            }

        }
        
        if (isDead()) {
            died = true;
            animationControl.play(DIE, false, false, 1);

        } else {
            damageTimer.reset();
            damage = true;
            game.getBaseApplication().getSoundManager().playSound("hit");
            game.getBaseApplication().getSoundManager().playSound("hurt");
            
            if (!attacking) {
                animationControl.play(HIT, false, false, 1);
            }            

        }

        fireDamageAction(hitpoint);

        game.fireScoreChangedListener(score);
    }

    public void win() {
        int index = FastMath.nextRandomInt(0, 2);
        if (index == 0) {
            idleAnim = YELLING;
//            animationControl.play(YELLING, true, false, 1);

        } else if (index == 1) {
            idleAnim = VICTORY;
//            animationControl.play(VICTORY, true, false, 1);

        } else {
            idleAnim = DANCE;
//            animationControl.play(DANCE, true, false, 1);
        }

        lookDirection.set(0, 0, 1);        
        move(false);

    }

    public void setWalkDirection(float dir) {
        this.movementDirection = dir;
    }

    public void setLookDirection(float dir) {
        this.lookDirection.setX(dir);

    }

    public void move(boolean move) {
        this.walking = move;

        if (move) {                     
            this.walkDirection.setX(movementDirection);

        } else {            
            this.walkDirection.setX(0);

        }
        
        if (move && characterControl.isOnGround()) {
            game.getBaseApplication().getSoundManager().setMusicSpeed(runSound, speedFactor);
            game.getBaseApplication().getSoundManager().playMusic(runSound);
        } else {
            game.getBaseApplication().getSoundManager().stopMusic(runSound);
        }

    }

    public void moveBack(boolean move) {
        if (move) {
            moveAnim = WALK_BACK;
            moveSpeed = 160;

        } else {
            moveAnim = RUN;
            moveSpeed = 220;

        }
    }

    public void jump() {
        if (characterControl.isOnGround() && !jumping && !attacking && !damage && !died) {
            jumping = true;

            if (this.walking) {
                animationControl.play(RUN_JUMP, false, false, 1);
                characterControl.setJumpForce(jumpForceRun);
                characterControl.jump();
                game.getBaseApplication().getSoundManager().playSound("jump");

            } else {
                animationControl.play(JUMPING, false, false, 1.2f);
                characterControl.setJumpForce(jumpForceStand);
                jumpTimer.reset();
            }

        }

    }
    
    public void spell() {

        if ((!damage && !attacking && !died) || attackTimer.finished()) {
            attackType = ATTACK_SPELL;
//            game.getBaseApplication().getSoundManager().playSound("kick");
            animationControl.play(SPELL, false, false, 1.5f);
            attacking = true;
            attackWasFired = false;
            attackTimer.setMaxTime(80);
            attackTimer.reset();
        }

    }

    public void kick() {

        if ((!damage && !attacking && !died) || attackTimer.finished()) {
            attackType = ATTACK_KICK;
            game.getBaseApplication().getSoundManager().playSound("kick");
            animationControl.play(KICK, false, false, 1.5f);
            attacking = true;
            attackWasFired = false;
            attackTimer.setMaxTime(60);
            attackTimer.reset();
        }

    }

    public void box() {

        if ((!damage && !attacking && !died) || attackTimer.finished()) {
            attackType = ATTACK_BOX;
            game.getBaseApplication().getSoundManager().playSound("box");
            animationControl.play(BOXING, false, false, 2f);
            attacking = true;
            attackWasFired = false;
            attackTimer.setMaxTime(30);
            attackTimer.reset();
        }

    }

    public void warp(Vector3f pos) {
        characterControl.warp(pos.clone()); // warp character into landscape at particular location
    }

    @Override
    public void close() {
        game.getBaseApplication().getSoundManager().stopMusic(runSound);
        game.getBaseApplication().getBulletAppState().getPhysicsSpace().remove(characterControl);
        game.getBaseApplication().getBulletAppState().getPhysicsSpace().remove(playerNode);

        super.close(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Check if the player is in range of the other player.
     *
     * @param player
     * @return
     */
    public boolean isInDamageRange(Player player, float range) {
        boolean inrange = false;
        float yDis = FastMath.abs(getPosition().y - player.getPosition().y);
//        log("Y dis = " + yDis);
        if (yDis < 0.8f) {
            //First we check distance
            if (getPosition().distance(player.getPosition()) < 1.2f) {

                //We we chec facing direction
                //Facing right
                if (movementDirection > 0 && player.getPosition().x > getPosition().x) {
                    inrange = true;

                }

                if (movementDirection < 0 && player.getPosition().x < getPosition().x) {
                    inrange = true;
                }

            }
        }

        return inrange;
    }

    /**
     * Check if the player is in range
     *
     * @param player
     * @param range
     * @return
     */
    public boolean isInRange(Player player, int direction, float range) {
        boolean inrange = false;

        float yDis = FastMath.abs(getPosition().y - player.getPosition().y);
//        log("Y dis = " + yDis);
        if (yDis < 0.8f) {
            //First we check distance
            if (getPosition().distance(player.getPosition()) < 1.2f) {

                //We we chec facing direction
                //Facing right
                if (direction > 0 && player.getPosition().x > getPosition().x) {
                    inrange = true;

                }

                if (direction < 0 && player.getPosition().x < getPosition().x) {
                    inrange = true;
                }

            }

        }

        return inrange;
    }

    public void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }
    
    
}
