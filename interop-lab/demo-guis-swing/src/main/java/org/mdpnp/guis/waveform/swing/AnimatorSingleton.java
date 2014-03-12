/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
            if(animator.start()) {
                log.debug("Created an GLAnimatorControl singleton");
            } else {
                log.warn("Unable to start GLAnimatorControl singleton");
            }
            
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
            if(animator.stop()) {
                
                log.debug("Destroyed a GLAnimatorControl singleton");
            } else {
                log.warn("Unable to stop an animator");
            }
            AnimatorSingleton.animator = null;
        }
        
    }
}
