/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.bruynhuis.ld44.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.ColorUtils;

/**
 *
 * @author nicki
 */
public class SettingsScreen extends AbstractScreen {
	
    public static final String NAME = "SettingsScreen";
    private Label title;
    private Label player1Label;
    private Label player2Label;
    private Image middle;
    private Image key1;
    private Image key2;
    private Image key3;
    private Image key4;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Settings", 58);
        title.setTextColor(ColorUtils.rgb(5, 5, 5));
        title.centerTop(0, 50);
        
        player1Label = new Label(hudPanel, "Player1 Controls", 24, 300, 30);
        player1Label.setTextColor(ColorUtils.rgb(5, 5, 5));
        player1Label.centerAt(-300, 200);
        
        key1 = new Image(hudPanel, "Interface/keyboard1.png", 300, 200, true);
        key1.centerAt(-300, 50);
        
        key2 = new Image(hudPanel, "Interface/keyboard2.png", 300, 200, true);
        key2.centerAt(-300, -200);
        
        middle = new Image(hudPanel, "Interface/line.png", 2, 500, true);
        middle.centerAt(0, -30);
        
        player2Label = new Label(hudPanel, "Player2 Controls", 24, 300, 30);
        player2Label.setTextColor(ColorUtils.rgb(5, 5, 5));
        player2Label.centerAt(300, 200);
        
        
        key3 = new Image(hudPanel, "Interface/keyboard3.png", 300, 200, true);
        key3.centerAt(300, 50);
        
        key4 = new Image(hudPanel, "Interface/keyboard4.png", 300, 200, true);
        key4.centerAt(300, -200);
        
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