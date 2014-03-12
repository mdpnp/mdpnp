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
package org.mdpnp.devices.math;

/**
 * @author Jeff Plourde
 *
 */
public class DCT {

    public static double[] idct(double[] coeffs, double[] results) {
        return idct(coeffs, 0, coeffs.length, results, 0, results.length);
    }

    public static double[] idct(double[] coeffs, int coff, int clen, double[] results) {
        return idct(coeffs, coff, clen, results, 0, results.length);
    }

    public static double[] idct(double[] coeffs, int coff, int clen, double[] results, int roff, int rlen) {
        if (null == results) {
            results = new double[coeffs.length];
        } else {
            for (int i = 0; i < results.length; i++) {
                results[i] = 0.0;
            }
        }

        for (int i = 0; i < rlen; i++) {
            double cc = Math.sqrt(1.0 / 2.0);
            for (int j = 0; j < clen; j++) {
                results[roff + i] += Math.sqrt(2.0 / rlen) * cc * coeffs[coff + j] * Math.cos(Math.PI * j * (i + 0.5) / rlen);
                cc = Math.sqrt(1.0 / 1.0);
            }
        }
        return results;
    }

    public static double[] dct(double[] d) {
        return dct(d, null);
    }

    public static double[] dct(double[] d, double[] r) {
        if (null == r) {
            r = new double[d.length];
        } else {
            for (int i = 0; i < r.length; i++) {
                r[i] = 0.0;
            }
        }
        double cc = Math.sqrt(1.0 / d.length);
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                r[i] += d[j] * Math.cos(Math.PI * (j + 0.5) * i / d.length);
            }
            r[i] *= cc;
            cc = Math.sqrt(2.0 / d.length);
        }
        return r;
    }

    public static float[] dct(float[] d, int start, float[] r) {
        return dct(d, start, r, d.length);
    }

    public static float[] dct(float[] d, int start, float[] r, int count) {
        if (null == r) {
            r = new float[d.length];
        } else {
            for (int i = 0; i < r.length; i++) {
                r[i] = 0f;
            }
        }
        double cc = Math.sqrt(1.0 / d.length);
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < d.length; j++) {
                r[i] += d[(start + j) % d.length] * Math.cos(Math.PI * (j + 0.5) * i / d.length);
            }
            r[i] *= cc;
            cc = Math.sqrt(2.0 / d.length);
        }
        return r;
    }
}
