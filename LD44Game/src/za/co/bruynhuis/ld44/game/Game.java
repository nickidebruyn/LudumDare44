/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.filters.ArtFilter;
import com.bruynhuis.galago.filters.BleachFilter;
import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.games.blender3d.Blender3DGame;
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

    private Sprite background;
    private float scale = 0.05f;
    private float outlineSize = 0.06f;
    private ColorRGBA outlineColor = ColorRGBA.Black;
    private CartoonEdgeProcessor cep;
    private FilterPostProcessor fpp;
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

        fpp = new FilterPostProcessor(baseApplication.getAssetManager());
        baseApplication.getViewPort().addProcessor(fpp);

        FXAAFilter fXAAFilter = new FXAAFilter();
        fpp.addFilter(fXAAFilter);
        
//        ArtFilter bleachFilter = new ArtFilter();
//        fpp.addFilter(bleachFilter);

//        SSAOFilter ssaof = new SSAOFilter();        
//        fpp.addFilter(ssaof);
    }

    @Override
    public void parse(Spatial spatial) {
        log("Found: " + spatial.getName());

        if (spatial instanceof Geometry) {
            Material mat = ((Geometry) spatial).getMaterial();
            MatParam colorParam = mat.getParam("Color");

            if (colorParam != null) {
                ColorRGBA color = (ColorRGBA) colorParam.getValue();
                log("Color of geom = " + color);
                SpatialUtils.addCartoonColor(spatial, null, color, outlineColor, outlineSize, false, true);
            }

        }
        
        if (spatial.getName().equalsIgnoreCase("enemy-start")) {
            player2Start = spatial.getWorldTranslation().clone();
            
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

}
