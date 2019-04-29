/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.bruynhuis.ld44.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.effect.TextWriteEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.font.LineWrapMode;
import za.co.bruynhuis.ld44.ui.LargeButton;

/**
 *
 * @author nicki
 */
public class CreditsScreen extends AbstractScreen {
	
    public static final String NAME = "CreditsScreen";
    private Label title;
    private Label info;
    private LargeButton backButton;
    private boolean firstLoad = true;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Credits", 58);
        title.setTextColor(ColorUtils.rgb(5, 5, 5));
        title.centerTop(0, 50);
        
        info = new Label(hudPanel, "This was my entry for the 44th Ludum Dare gaming challenge.\n"
                + "This game was writen in 72 hours and all art, sounds and code was done by myself.\n"
                + "\n"
                + "This was my very first one on one combat fighting game.\n"
                + "You as player need to battle/fight your opponent and win 3 rounds.", 20, 800, 500);
        info.setTextColor(ColorUtils.rgb(5, 5, 5));
        info.setWrapMode(LineWrapMode.Word);
        info.setAlignment(TextAlign.LEFT);
        info.setVerticalAlignment(TextAlign.TOP);
        info.centerAt(0, 0);
        info.setAnimated(true);
        info.addEffect(new TextWriteEffect(info, 5));
        
        
        backButton = new LargeButton(hudPanel, "credits-back", "Back");
        backButton.centerBottom(0, 20);
        backButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showPreviousScreen();
                    
                }
            }
            
        });
    }

    @Override
    protected void load() {
        
    }

    @Override
    protected void show() {
        setPreviousScreen(MenuScreen.NAME);
        
        info.setVisible(false);
        info.show();
    }

    @Override
    protected void exit() {
    }

    @Override
    protected void pause() {
    }
    
}