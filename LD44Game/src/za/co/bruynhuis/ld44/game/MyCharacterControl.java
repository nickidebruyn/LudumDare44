/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;

/**
 *
 * @author nicki
 */
public class MyCharacterControl extends BetterCharacterControl {

    public MyCharacterControl(float radius, float height, float mass) {
        super(radius, height, mass);
        
        rigidBody.setFriction(0);
        
    }        

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        super.prePhysicsTick(space, tpf); //To change body of generated methods, choose Tools | Templates.
        
        rigidBody.setPhysicsLocation(rigidBody.getPhysicsLocation().multLocal(1, 1, 0));
        
    }

    
}
