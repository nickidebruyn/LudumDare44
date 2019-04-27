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
import com.jme3.bullet.control.BetterCharacterControl;
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

    private Spatial model;
    private AnimationControl animationControl;
    private float moveSpeed = 200f;
    private Quaternion rotator = new Quaternion();
    private Vector3f lookDirection = new Vector3f(1, 0, 0);
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private BetterCharacterControl characterControl;
    private Timer jumpTimer = new Timer(30);
    private Timer attackTimer = new Timer(50);
    private String idleAnim = IDLE;

    private Vector3f jumpForceRun = new Vector3f(0, 80, 0);
    private Vector3f jumpForceStand = new Vector3f(0, 110, 0);
    private boolean walking = false;
    private boolean jumping = false;
    private boolean attacking = false;
    private Spatial hair;
    private Spatial body;
    private ColorRGBA hairColor;
    private ColorRGBA bodyColor;

    public Player(Blender3DGame physicsGame, ColorRGBA hairColor, ColorRGBA bodyColor, float direction) {
        super(physicsGame);
        this.hairColor = hairColor;
        this.bodyColor = bodyColor;
        this.lookDirection.setX(direction);
    }

    @Override
    protected void init() {
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
        characterControl = new MyCharacterControl(0.25f, 1.0f, 10f); // construct character. (If your character bounces, try increasing height and weight.)
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

                if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

                    if (animationControl != null) {

                        if (!jumping && !attacking && characterControl.isOnGround()) {

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

                        }

                        //Calculation to move a character to a target position
                        rotator.lookAt(lookDirection, Vector3f.UNIT_Y);
                        spatial.getLocalRotation().slerp(rotator, 0.2f);
                        characterControl.setViewDirection(spatial.getLocalRotation().getRotationColumn(2));
                        
                        if (attacking && characterControl.isOnGround()) {
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

    @Override
    public Vector3f getPosition() {
        //TODO: Get the character control physics position
        return playerNode.getWorldTranslation();

    }

    @Override
    public void doDie() {
        //TODO

    }

    @Override
    public void doAnimationDone(String animationName) {

        if (animationName.equals(JUMPING)) {
            jumping = false;
            animationControl.play(FALL, true, false, 1);

        } else if (animationName.equals(RUN_JUMP)) {
            jumping = false;

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

    public void setDirection(float dir) {
        this.lookDirection.setX(dir);

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
        if (characterControl.isOnGround() && !jumping && !attacking) {
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

        if (!attacking || attackTimer.finished()) {
            animationControl.play(KICK, false, false, 1.5f);
            attacking = true;
            attackTimer.reset();
        }

    }

    public void box() {

        if (!attacking || attackTimer.finished()) {
            animationControl.play(BOXING, false, false, 1.5f);
            attacking = true;
            attackTimer.reset();
        }

    }
    
    public void warp(Vector3f pos) {
        characterControl.warp(pos.clone()); // warp character into landscape at particular location
    }

}
