/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.bruynhuis.ld44.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;

/**
 *
 * @author nicki
 */
public class SettingsScreen extends AbstractScreen {
	
    public static final String NAME = "SettingsScreen";
    private Label title;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Screen Title");
        title.centerTop(0, 0);
        
        
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