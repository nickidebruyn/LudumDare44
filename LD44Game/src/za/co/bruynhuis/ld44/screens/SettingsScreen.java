/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.bruynhuis.ld44.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.ColorUtils;

/**
 *
 * @author nicki
 */
public class SettingsScreen extends AbstractScreen {
	
    public static final String NAME = "SettingsScreen";
    private Label title;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Settings", 58);
        title.setTextColor(ColorUtils.rgb(5, 5, 5));
        title.centerTop(0, 50);
        
    }

    @Override
    protected void load() {
    }

    @Override
    protected void show() {
        setPreviousScreen(MenuScreen.NAME);
    }

    @Override
    protected void exit() {
    }

    @Override
    protected void pause() {
    }
    
}