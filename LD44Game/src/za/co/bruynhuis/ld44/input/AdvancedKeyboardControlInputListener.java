/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author nicki
 */
public class AdvancedKeyboardControlInputListener implements ActionListener {

    private static final String KEYBOARD_BOX = "KEY-BOX";
    private static final String KEYBOARD_KICK = "KEY-KICK";
    private static final String KEYBOARD_LEFT = "KEY-LEFT";
    private static final String KEYBOARD_RIGHT = "KEY-RIGHT";
    private static final String KEYBOARD_UP = "KEY-UP";
    private static final String KEYBOARD_DOWN = "KEY-DOWN";
    
    private static final String KEYBOARD_BOX2 = "KEY-BOX2";
    private static final String KEYBOARD_KICK2 = "KEY-KICK2";
    private static final String KEYBOARD_LEFT2 = "KEY-LEFT2";
    private static final String KEYBOARD_RIGHT2 = "KEY-RIGHT2";
    private static final String KEYBOARD_UP2 = "KEY-UP2";
    private static final String KEYBOARD_DOWN2 = "KEY-DOWN2";

    private ArrayList<AdvancedKeyboardControlListener> keyboardControlListeners = new ArrayList<AdvancedKeyboardControlListener>();
    private InputManager inputManager;
    private boolean enabled = true;
    private AdvancedKeyboardControlEvent keyboardControlEvent;

    public AdvancedKeyboardControlInputListener() {
        keyboardControlEvent = new AdvancedKeyboardControlEvent();

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;
        if (!inputManager.hasMapping(KEYBOARD_BOX)) {

            inputManager.addMapping(KEYBOARD_BOX, new KeyTrigger(KeyInput.KEY_LCONTROL));
            inputManager.addMapping(KEYBOARD_KICK, new KeyTrigger(KeyInput.KEY_LSHIFT));
            inputManager.addMapping(KEYBOARD_LEFT, new KeyTrigger(KeyInput.KEY_H), new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping(KEYBOARD_RIGHT, new KeyTrigger(KeyInput.KEY_K), new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping(KEYBOARD_UP, new KeyTrigger(KeyInput.KEY_U), new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping(KEYBOARD_DOWN, new KeyTrigger(KeyInput.KEY_J), new KeyTrigger(KeyInput.KEY_S));
            
            inputManager.addMapping(KEYBOARD_BOX2, new KeyTrigger(KeyInput.KEY_RCONTROL));
            inputManager.addMapping(KEYBOARD_KICK2, new KeyTrigger(KeyInput.KEY_RSHIFT));
            inputManager.addMapping(KEYBOARD_LEFT2, new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_NUMPAD4));
            inputManager.addMapping(KEYBOARD_RIGHT2, new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_NUMPAD6));
            inputManager.addMapping(KEYBOARD_UP2, new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_NUMPAD8));
            inputManager.addMapping(KEYBOARD_DOWN2, new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_NUMPAD5));

            inputManager.addListener(this, new String[]{KEYBOARD_BOX, KEYBOARD_KICK,
                KEYBOARD_LEFT, KEYBOARD_RIGHT, KEYBOARD_UP, KEYBOARD_DOWN,
                KEYBOARD_BOX2, KEYBOARD_KICK2,
                KEYBOARD_LEFT2, KEYBOARD_RIGHT2, KEYBOARD_UP2, KEYBOARD_DOWN2});
        }
    }

    public void unregisterInput() {

        if (inputManager == null) {
            return;
        }

        inputManager.deleteMapping(KEYBOARD_BOX);
        inputManager.deleteMapping(KEYBOARD_KICK);
        inputManager.deleteMapping(KEYBOARD_LEFT);
        inputManager.deleteMapping(KEYBOARD_RIGHT);
        inputManager.deleteMapping(KEYBOARD_UP);
        inputManager.deleteMapping(KEYBOARD_DOWN);
        
        inputManager.deleteMapping(KEYBOARD_BOX2);
        inputManager.deleteMapping(KEYBOARD_KICK2);
        inputManager.deleteMapping(KEYBOARD_LEFT2);
        inputManager.deleteMapping(KEYBOARD_RIGHT2);
        inputManager.deleteMapping(KEYBOARD_UP2);
        inputManager.deleteMapping(KEYBOARD_DOWN2);

        inputManager.removeListener(this);

    }

    /**
     * Log some text to the console
     *
     * @param text
     */
    protected void log(String text) {
        System.out.println(text);
    }

    public void addAdvancedKeyboardControlListener(AdvancedKeyboardControlListener keyboardControlListener) {
        this.keyboardControlListeners.add(keyboardControlListener);
    }

    public void removeAdvancedKeyboardControlListener(AdvancedKeyboardControlListener keyboardControlListener) {
        this.keyboardControlListeners.remove(keyboardControlListener);
    }

    private void fireKeyboardControlEvent(AdvancedKeyboardControlEvent event, float tpf) {
        if (keyboardControlListeners != null) {
            for (Iterator<AdvancedKeyboardControlListener> it = keyboardControlListeners.iterator(); it.hasNext();) {
                AdvancedKeyboardControlListener keyboardControlListener = it.next();
                if (keyboardControlListener != null) {
                    keyboardControlListener.onKey(event, tpf);
                }

            }

        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name != null) {

            keyboardControlEvent.setKeyDown(isPressed);
            keyboardControlEvent.setUp(KEYBOARD_UP.equals(name));
            keyboardControlEvent.setDown(KEYBOARD_DOWN.equals(name));
            keyboardControlEvent.setLeft(KEYBOARD_LEFT.equals(name));
            keyboardControlEvent.setRight(KEYBOARD_RIGHT.equals(name));
            keyboardControlEvent.setBox(KEYBOARD_BOX.equals(name));
            keyboardControlEvent.setKick(KEYBOARD_KICK.equals(name));
            
            keyboardControlEvent.setUp2(KEYBOARD_UP2.equals(name));
            keyboardControlEvent.setDown2(KEYBOARD_DOWN2.equals(name));
            keyboardControlEvent.setLeft2(KEYBOARD_LEFT2.equals(name));
            keyboardControlEvent.setRight2(KEYBOARD_RIGHT2.equals(name));
            keyboardControlEvent.setBox2(KEYBOARD_BOX2.equals(name));
            keyboardControlEvent.setKick2(KEYBOARD_KICK2.equals(name));

            fireKeyboardControlEvent(keyboardControlEvent, tpf);
        }
    }
}
