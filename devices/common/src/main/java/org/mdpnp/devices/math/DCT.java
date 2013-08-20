/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.math;



public class DCT {
	
    public static double[] idct(double[] coeffs, double[] results) {
        return idct(coeffs, 0, coeffs.length, results, 0, results.length);
    }
    
	public static double[] idct(double[] coeffs, int coff, int clen, double[] results) {
		return idct(coeffs, coff, clen, results, 0, results.length);
	}
	
	public static double[] idct(double[] coeffs, int coff, int clen, double[] results, int roff, int rlen) {
		if(null == results) {
			results = new double[coeffs.length];
		} else {
			for(int i = 0; i < results.length; i++) {
				results[i] = 0.0;
			}
		}

		for(int i = 0; i < rlen; i++) {
			double cc = Math.sqrt(1.0 / 2.0);
			for(int j = 0; j < clen; j++) {
				results[roff+i] += Math.sqrt(2.0/rlen) * cc * coeffs[coff+j] * Math.cos(Math.PI * j * (i + 0.5) / rlen);
				cc = Math.sqrt(1.0/1.0);
			}
		}
		return results;
	}
	
	public static double[] dct(double[] d) {
		return dct(d, null);
	}
	
	public static double[] dct(double[] d, double[] r) {
		if(null == r) {
			r = new double[d.length];
		} else {
			for(int i = 0; i < r.length; i++) {
				r[i] = 0.0;
			}
		}
		double cc = Math.sqrt(1.0/d.length);
		for(int i = 0; i < d.length; i++) {
			for(int j = 0; j < d.length; j++) {
				r[i] += d[j] * Math.cos(Math.PI * (j + 0.5) * i / d.length);
			}
			r[i] *= cc;
			cc = Math.sqrt(2.0/d.length);
		}
		return r;
	}
	
	public static float[] dct(float[] d, int start, float[] r) {
	    return dct(d, start, r, d.length);
	}
	   public static float[] dct(float[] d, int start, float[] r, int count) {
	        if(null == r) {
	            r = new float[d.length];
	        } else {
	            for(int i = 0; i < r.length; i++) {
	                r[i] = 0f;
	            }
	        }
	        double cc = Math.sqrt(1.0/d.length);
	        for(int i = 0; i < count; i++) {
	            for(int j = 0; j < d.length; j++) {
	                r[i] += d[(start+j)%d.length] * Math.cos(Math.PI * (j + 0.5) * i / d.length);
	            }
	            r[i] *= cc;
	            cc = Math.sqrt(2.0/d.length);
	        }
	        return r;
	    }
}
