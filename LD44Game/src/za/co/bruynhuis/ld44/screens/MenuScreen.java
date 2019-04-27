/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.bruynhuis.ld44.screens;

import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author nicki
 */
public class MenuScreen extends AbstractScreen {
	
    public static final String NAME = "MenuScreen";
    private float scale = 0.02f;
    private Sprite background;    
    private Label title;
    private CartoonEdgeProcessor cep;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Paper Boy");
        title.centerTop(0, 0);
        
    }

    @Override
    protected void load() {
        
    }

    @Override
    protected void show() {
        setPreviousScreen(null);
    }

    @Override
    protected void exit() {
    }

    @Override
    protected void pause() {
    }
    
}