/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.ui;

import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.ColorUtils;

/**
 *
 * @author nicki
 */
public class LargeButton extends TouchButton {
    
    private static final float scale = 0.68f;
    
    public LargeButton(Panel panel, String id, String text) {
        super(panel, id, "Interface/button-large.png", 340*scale, 100*scale, new FontStyle(24), true);
        setText(text);
        addEffect(new TouchEffect(this));
        setTextColor(ColorUtils.rgb(5, 5, 5));
        
    }
    
}
