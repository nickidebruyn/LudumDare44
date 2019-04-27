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
import com.bruynhuis.galago.util.Debug;
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

    private Spatial model;
    private AnimationControl animationControl;
    private float moveSpeed = 200f;
    private Quaternion rotator = new Quaternion();
    private Vector3f lookDirection = new Vector3f(1, 0, 0);
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private MyCharacterControl characterControl;
    private Timer jumpTimer = new Timer(30);
    private Timer attackTimer = new Timer(50);
    private String idleAnim = IDLE_ATTACK;

    private Vector3f jumpForceRun = new Vector3f(0, 80, 0);
    private Vector3f jumpForceStand = new Vector3f(0, 110, 0);
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
    private boolean attackWasFired = false;

    public Player(Blender3DGame physicsGame, ColorRGBA hairColor, ColorRGBA bodyColor, float direction) {
        super(physicsGame);
        this.hairColor = hairColor;
        this.bodyColor = bodyColor;
        this.lookDirection.setX(direction);
    }

    public void setAttackCallback(AttackCallback attackCallback) {
        this.attackCallback = attackCallback;
    }

    private void fireAttackFinishAction(int damage) {
        if (attackCallback != null) {
            attackCallback.finishedAttack(damage);
        }
    }

    @Override
    protected void init() {
        lives = 5;
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
                    Debug.log("Found Anim Control on " + spatial.getName());
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
                        SpatialUtils.addCartoonColor(spatial, null, color, ColorRGBA.Black, 2f, false, true);
                    }

                }

            }
        };
        model.depthFirstTraversal(sgv);

        //Load the physics part
        characterControl = new MyCharacterControl(0.4f, 1.0f, 10f); // construct character. (If your character bounces, try increasing height and weight.)
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
                                animationControl.play(RUN, true, false, 1);
                            } else {
                                animationControl.play(idleAnim, true, false, 1);
                            }

                        } else {
                            jumpTimer.update(tpf);
                            if (jumpTimer.finished()) {
                                characterControl.jump();
                                jumpTimer.stop();
                            }

                            attackTimer.update(tpf);
                            if (attackTimer.finished()) {
                                if (!attackWasFired) {
                                    attackWasFired = true;
                                    fireAttackFinishAction(1);
                                }
                            }

                        }

                        //Calculation to move a character to a target position
                        rotator.lookAt(lookDirection, Vector3f.UNIT_Y);
                        spatial.getLocalRotation().slerp(rotator, 0.2f);
                        characterControl.setViewDirection(spatial.getLocalRotation().getRotationColumn(2));

                        if ((attacking || damage || died) && characterControl.isOnGround()) {
                            walkDirection.set(0, 0, 0);
                        }

                        characterControl.setWalkDirection(walkDirection.mult(tpf * moveSpeed));

                    }

                    if (getPosition().y < -5) {
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
        log("Player died " + playerNode.getName());

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
                int index = FastMath.nextRandomInt(0, 2);
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

    public void setDirection(float dir) {
        this.lookDirection.setX(dir);

    }
    
    public boolean isDead() {
        return getHealth() <= 0;
    }

    public void hit(int hitpoint) {
        doDamage(hitpoint);
        
        game.getBaseApplication().getEffectManager().doEffect("blooddust", getPosition().add(0, 0.8f, -0.5f));
        
        int i = FastMath.nextRandomInt(1, 2);
        game.getBaseApplication().getEffectManager().doEffect("bloodsplat" + i, getPosition().add(0, 0.8f, -1f), 3000);

        if (isDead()) {
            died = true;
            animationControl.play(DIE, false, false, 1);

        } else {
            damage = true;
            animationControl.play(HIT, false, false, 1);
        }

        game.fireScoreChangedListener(score);
    }
    
    public void win() {
        int index = FastMath.nextRandomInt(0, 2);
        if (index == 0) {
            animationControl.play(YELLING, true, false, 1);
            
        } else if (index == 1) {
            animationControl.play(VICTORY, true, false, 1);
            
        } else {
            animationControl.play(DANCE, true, false, 1);
        }
        
        lookDirection.set(0, 0, 1);
        
    }

    public void walk(boolean move) {
        this.walking = move;

        if (move) {
            this.walkDirection.setX(lookDirection.x);

        } else {
            this.walkDirection.setX(0);

        }

    }

    public void jump() {
        if (characterControl.isOnGround() && !jumping && !attacking && !damage && !died) {
            jumping = true;
            if (this.walking) {
                animationControl.play(RUN_JUMP, false, false, 1);
                characterControl.setJumpForce(jumpForceRun);
                characterControl.jump();

            } else {
                animationControl.play(JUMPING, false, false, 1.2f);
                characterControl.setJumpForce(jumpForceStand);
                jumpTimer.reset();
            }

        }

    }

    public void kick() {

        if ((!damage && !attacking && !died) || attackTimer.finished()) {
            animationControl.play(KICK, false, false, 1.5f);
            attacking = true;
            attackWasFired = false;
            attackTimer.reset();
        }

    }

    public void box() {

        if ((!damage && !attacking && !died) || attackTimer.finished()) {
            animationControl.play(BOXING, false, false, 1.5f);
            attacking = true;
            attackWasFired = false;
            attackTimer.reset();
        }

    }

    public void warp(Vector3f pos) {
        characterControl.warp(pos.clone()); // warp character into landscape at particular location
    }

    @Override
    public void close() {
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
    public int getDamageRange(Player player) {
        int damage = 0;
        if (getPosition().distance(player.getPosition()) < 1f) {
            damage = 1;
        }
        return damage;
    }
}
