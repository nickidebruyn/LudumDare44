/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.ld44.game.weapons;

import com.jme3.animation.SkeletonControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nicki
 */
public class WeaponAttachmentControl extends AbstractControl {

    private final Node attachmentNode;

    public WeaponAttachmentControl(Node attachmentNode) {
        this.attachmentNode = attachmentNode;
    }

    public WeaponAttachmentControl(SkeletonControl skeletonControl, String bone) {
        if (skeletonControl != null && bone != null) {
            attachmentNode = skeletonControl.getAttachmentsNode(bone);
        } else {
            attachmentNode = null;
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial == null || attachmentNode == null) {
            return;
        }
        spatial.setLocalTranslation(attachmentNode.getWorldTranslation());
        spatial.setLocalRotation(attachmentNode.getWorldRotation());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
