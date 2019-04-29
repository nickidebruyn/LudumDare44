/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.games.blender3d.Blender3DGame;
import com.bruynhuis.galago.games.blender3d.Blender3DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;

/**
 *
 * @author nicki
 */
public class Game extends Blender3DGame {
    
    private CartoonEdgeProcessor cep;
    private FilterPostProcessor fpp;

    private Sprite background;
    private float scale = 0.05f;
    private float outlineSize = 0.05f;
    private ColorRGBA outlineColor = ColorRGBA.Black;
    private Vector3f player2Start = new Vector3f(0, 0, 0);

    public Game(Base3DApplication baseApplication, Node rootNode, String sceneFile) {
        super(baseApplication, rootNode, sceneFile);
    }

    @Override
    public void init() {

        background = SpatialUtils.addSprite(rootNode, 1280 * scale, 800 * scale);
        background.setImage("Textures/background.png");
        SpatialUtils.translate(background, 0, 0, -10);
        background.addControl(new BillboardControl());
        background.scaleTextureCoords(new Vector2f(2, 2));
        
        cep = new CartoonEdgeProcessor();
        baseApplication.getViewPort().addProcessor(cep);
                
        
//        fpp = new FilterPostProcessor(baseApplication.getAssetManager());
//        baseApplication.getViewPort().addProcessor(fpp);
//        
//        CartoonEdgeFilter cartoonEdgeFilter = new CartoonEdgeFilter();
////        cartoonEdgeFilter.setEdgeIntensity(0.8f);
//        cartoonEdgeFilter.setEdgeWidth(1.2f);
//        fpp.addFilter(cartoonEdgeFilter);
        

    }

    @Override
    public void parse(Spatial spatial) {
//        log("Found: " + spatial.getName());

        if (spatial instanceof Geometry) {
            Material mat = ((Geometry) spatial).getMaterial();
            MatParam colorParam = mat.getParam("Color");

            if (colorParam != null) {
                ColorRGBA color = (ColorRGBA) colorParam.getValue();
//                log("Color of geom = " + color);
                SpatialUtils.addCartoonColor(spatial, null, color, outlineColor, outlineSize, false, true);
//                SpatialUtils.addCartoonColor(spatial, null, ColorRGBA.LightGray, outlineColor, outlineSize, false, true);
            }

        }

        if (spatial.getName().equalsIgnoreCase("start2")) {
            player2Start = spatial.getWorldTranslation().clone();

        }

        if (spatial.getUserData("coin") != null) {
            spatial.addControl(new RotationControl(150));

        }

    }

    @Override
    public void close() {
        baseApplication.getViewPort().removeProcessor(cep);
        super.close(); //To change body of generated methods, choose Tools | Templates.
    }

    public Vector3f getPlayer2Start() {
        return player2Start;
    }

    @Override
    public void start(Blender3DPlayer physicsPlayer) {
        baseApplication.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0, -20, 0));
        super.start(physicsPlayer); //To change body of generated methods, choose Tools | Templates.
    }

}
