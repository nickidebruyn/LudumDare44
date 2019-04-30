/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game;

import com.jme3.math.ColorRGBA;

/**
 *
 * @author nicki
 */
public class Level {
    
    private String name;
    private String scene;
    private int difficulty;
    private ColorRGBA enemyHairColor;
    private ColorRGBA enemyBodyColor;

    public Level(String name, String scene, int difficulty, ColorRGBA enemyHairColor, ColorRGBA enemyBodyColor) {
        this.name = name;
        this.scene = scene;
        this.difficulty = difficulty;
        this.enemyHairColor = enemyHairColor;
        this.enemyBodyColor = enemyBodyColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public ColorRGBA getEnemyHairColor() {
        return enemyHairColor;
    }

    public void setEnemyHairColor(ColorRGBA enemyHairColor) {
        this.enemyHairColor = enemyHairColor;
    }

    public ColorRGBA getEnemyBodyColor() {
        return enemyBodyColor;
    }

    public void setEnemyBodyColor(ColorRGBA enemyBodyColor) {
        this.enemyBodyColor = enemyBodyColor;
    }
    
    
    
}
