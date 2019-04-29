/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.co.bruynhuis.ld44;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.math.Vector3f;
import za.co.bruynhuis.ld44.screens.CombatScreen;
import za.co.bruynhuis.ld44.screens.CreditsScreen;
import za.co.bruynhuis.ld44.screens.MenuScreen;
import za.co.bruynhuis.ld44.screens.PlayScreen;
import za.co.bruynhuis.ld44.screens.SettingsScreen;

/**
 *
 * @author nicki
 */
public class MainApplication extends Base3DApplication {

    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Paper Combat", 1280, 720, "ld44.save", "Interface/Fonts/RockSalt.ttf", null, false);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorUtils.rgb(250, 250, 250);
        setOrthographicProjection(5.6f);
    }

    @Override
    protected void postInitApp() {
        showScreen(MenuScreen.NAME);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -20, 0));
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen(MenuScreen.NAME, new MenuScreen());
        screenManager.loadScreen(PlayScreen.NAME, new PlayScreen());
        screenManager.loadScreen(CombatScreen.NAME, new CombatScreen());
        screenManager.loadScreen(SettingsScreen.NAME, new SettingsScreen());
        screenManager.loadScreen(CreditsScreen.NAME, new CreditsScreen());

    }

    @Override
    public void initModelManager(ModelManager modelManager) {
    }

    @Override
    protected void initSound(SoundManager soundManager) {
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        effectManager.loadEffect("blooddust", "Models/effects/blooddust.j3o");
        effectManager.loadEffect("bloodsplat1", "Models/effects/bloodsplat.j3o");
        effectManager.loadEffect("bloodsplat2", "Models/effects/bloodsplat2.j3o");
        effectManager.loadEffect("bloodsplat3", "Models/effects/bloodsplat3.j3o");
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
    }

    @Override
    protected void initFonts(FontManager fontManager) {
        fontManager.loadFont(new FontStyle(24));
        fontManager.loadFont(new FontStyle(58));
        fontManager.loadFont(new FontStyle(56, 6));
    }

    
    
}