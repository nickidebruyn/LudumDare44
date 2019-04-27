/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author nicki
 */
public class LargeButton extends TouchButton {
    
    public LargeButton(Panel panel, String id, String pictureFile) {
        super(panel, id, pictureFile, 260, 50, true);
        
    }
    
}
