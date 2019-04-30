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
import java.util.ArrayList;
import za.co.bruynhuis.ld44.game.EnemyAIControl;
import za.co.bruynhuis.ld44.game.Level;
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
    
    private ArrayList<Level> levels = new ArrayList<>();
    private int currentLevel = 0;

    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Paper Combat", 1280, 720, "ld44.save", "Interface/Fonts/RockSalt.ttf", null, true);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorUtils.rgb(250, 250, 250);
        setOrthographicProjection(5.2f);
        
        levels.add(new Level("Billy the kid", "Scenes/level1.j3o", EnemyAIControl.DIFFICULTY_EASY, ColorUtils.rgb(75, 75, 75), ColorUtils.rgb(24, 220, 255)));
        levels.add(new Level("Wild Dog Rick", "Scenes/level2.j3o", EnemyAIControl.DIFFICULTY_MEDIUM, ColorUtils.rgb(255, 159, 26), ColorUtils.rgb(103, 230, 220)));
        levels.add(new Level("Boabob Willy", "Scenes/level3.j3o", EnemyAIControl.DIFFICULTY_DEFENSIVE, ColorUtils.rgb(44, 44, 84), ColorUtils.rgb(132, 129, 122)));
        levels.add(new Level("Tiny Hulk", "Scenes/level4.j3o", EnemyAIControl.DIFFICULTY_AGGRESIVE, ColorUtils.rgb(197, 108, 240), ColorUtils.rgb(50, 255, 126)));
        
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
        soundManager.loadSoundFx("jump", "Sounds/male-jump.ogg");
        soundManager.loadSoundFx("block", "Sounds/male-block.ogg");
        soundManager.loadSoundFx("box", "Sounds/male-box.ogg");
        soundManager.loadSoundFx("kick", "Sounds/male-kick.ogg");
        soundManager.loadSoundFx("hit", "Sounds/male-hit.ogg");
        soundManager.loadSoundFx("hurt", "Sounds/male-hurt.ogg");
        soundManager.loadSoundFx("fight", "Sounds/fight.ogg");
        
        soundManager.loadMusic("run", "Sounds/male-run.ogg");
        soundManager.setMusicVolume("run", 0.6f);
        
        soundManager.loadMusic("run2", "Sounds/male-run.ogg");
        soundManager.setMusicPitch("run2", 0.9f);
        soundManager.setMusicVolume("run2", 0.6f);
        
        soundManager.loadMusic("music", "Sounds/music.ogg");
        soundManager.setMusicVolume("music", 0.5f);
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

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    
    
}