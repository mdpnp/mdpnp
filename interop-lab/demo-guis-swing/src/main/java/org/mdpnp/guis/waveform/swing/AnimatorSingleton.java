package org.mdpnp.guis.waveform.swing;

import javax.media.opengl.GLAnimatorControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * This might be temporary but the GUIs are not mature enough to need anything but a
 * rudimentary animator ... and it's cheaper to share.
 * @author jplourde
 *
 */
public class AnimatorSingleton {
    private static GLAnimatorControl animator = null;
    private static int referenceCount = 0;
    
    private static final Logger log = LoggerFactory.getLogger(AnimatorSingleton.class);
    
    // at this point anything more would be excessive
    private static final int FRAMES_PER_INTERVAL = 15;
    
    public static synchronized GLAnimatorControl getInstance() {
        if(0 == referenceCount) {
            // Will be paused with no drawables
            animator = new FPSAnimator(FRAMES_PER_INTERVAL);
            animator.start();
            log.debug("Created an GLAnimatorControl singleton");
        }
        ++referenceCount;
        return animator;
    }
    public static synchronized void releaseInstance(final GLAnimatorControl animator) {
        if(referenceCount < 1) {
            throw new IllegalStateException("No references to release");
        }
        if(animator != AnimatorSingleton.animator) {
            throw new IllegalArgumentException(animator + " is not the current singleton");
        }
        if(0 == --referenceCount) {
            animator.stop();
            AnimatorSingleton.animator = null;
            log.debug("Destroyed a GLAnimatorControl singleton");
        }
        
    }
}
