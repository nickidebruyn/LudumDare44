/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.input;

/**
 *
 * @author nicki
 */
public class AdvancedKeyboardControlEvent {

    private boolean keyDown = false;
    
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    
    private boolean left2 = false;
    private boolean right2 = false;
    private boolean up2 = false;
    private boolean down2 = false;
    
    private boolean kick = false;
    private boolean box = false;
    
    private boolean kick2 = false;
    private boolean box2 = false;

    public boolean isKeyDown() {
        return keyDown;
    }

    public void setKeyDown(boolean keyDown) {
        this.keyDown = keyDown;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft2() {
        return left2;
    }

    public void setLeft2(boolean left2) {
        this.left2 = left2;
    }

    public boolean isRight2() {
        return right2;
    }

    public void setRight2(boolean right2) {
        this.right2 = right2;
    }

    public boolean isUp2() {
        return up2;
    }

    public void setUp2(boolean up2) {
        this.up2 = up2;
    }

    public boolean isDown2() {
        return down2;
    }

    public void setDown2(boolean down2) {
        this.down2 = down2;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }

    public boolean isKick2() {
        return kick2;
    }

    public void setKick2(boolean kick2) {
        this.kick2 = kick2;
    }

    public boolean isBox2() {
        return box2;
    }

    public void setBox2(boolean box2) {
        this.box2 = box2;
    }

    
}
