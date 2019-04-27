/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game;

import com.bruynhuis.galago.games.blender3d.Blender3DGame;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nicki
 */
public class Player2 extends Player {
    
    public Player2(Blender3DGame physicsGame, ColorRGBA hairColor, ColorRGBA bodyColor, float direction) {
        super(physicsGame, hairColor, bodyColor, direction);
    }

    @Override
    protected void init() {
        super.init(); 
        
        playerNode.setName(Game.TYPE_ENEMY);
        
    }
    
}
