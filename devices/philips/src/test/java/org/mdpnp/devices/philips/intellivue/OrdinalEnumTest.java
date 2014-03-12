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
package org.mdpnp.devices.philips.intellivue;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.mdpnp.devices.philips.intellivue.association.AssociationMessageType;
import org.mdpnp.devices.philips.intellivue.data.ApplicationArea;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.ComponentId;
import org.mdpnp.devices.philips.intellivue.data.Dimension;
import org.mdpnp.devices.philips.intellivue.data.Label;
import org.mdpnp.devices.philips.intellivue.data.Language;
import org.mdpnp.devices.philips.intellivue.data.LineFrequency;
import org.mdpnp.devices.philips.intellivue.data.MDSStatus;
import org.mdpnp.devices.philips.intellivue.data.MetricCategory;
import org.mdpnp.devices.philips.intellivue.data.MetricModality;
import org.mdpnp.devices.philips.intellivue.data.NomPartition;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.data.ObservedValue;
import org.mdpnp.devices.philips.intellivue.data.PatientBSAFormula;
import org.mdpnp.devices.philips.intellivue.data.PatientDemographicState;
import org.mdpnp.devices.philips.intellivue.data.PatientPacedMode;
import org.mdpnp.devices.philips.intellivue.data.PatientSex;
import org.mdpnp.devices.philips.intellivue.data.PatientType;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecificationType;
import org.mdpnp.devices.philips.intellivue.data.SampleArrayFixedValId;
import org.mdpnp.devices.philips.intellivue.data.SimpleColor;
import org.mdpnp.devices.philips.intellivue.data.UnitCode;
import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.ModifyOperator;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperation;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperationLinkedState;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorStatus;
import org.mdpnp.devices.philips.intellivue.dataexport.error.RemoteError;

/**
 * @author Jeff Plourde
 *
 */
public class OrdinalEnumTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testEnumValues() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        for (Class<?> cls : shortTypes) {
            testOrdinalEnumShort((Class<? extends OrdinalEnum.ShortType>) cls);
        }
        for (Class<?> cls : intTypes) {
            testOrdinalEnumInt((Class<? extends OrdinalEnum.IntType>) cls);
        }
        for (Class<?> cls : longTypes) {
            testOrdinalEnumLong((Class<? extends OrdinalEnum.LongType>) cls);
        }
    }

    @SuppressWarnings("rawtypes")
    private static final Class[] intTypes = new Class[] { MetricCategory.class, ApplicationArea.class, AttributeId.class, ComponentId.class,
            Dimension.class, LineFrequency.class, MDSStatus.class, MetricModality.class, ObjectClass.class, PatientBSAFormula.class,
            PatientDemographicState.class, PatientPacedMode.class, PatientSex.class, PatientType.class, ProductionSpecificationType.class,
            SampleArrayFixedValId.class, SimpleColor.class, ErrorStatus.class, RemoteError.class, CommandType.class, ModifyOperator.class,
            RemoteOperation.class, UnitCode.class, ObservedValue.class,

    };
    @SuppressWarnings("rawtypes")
    private static final Class[] shortTypes = new Class[] { AssociationMessageType.class, Language.class, NomPartition.class,
            RemoteOperationLinkedState.class,

    };
    @SuppressWarnings("rawtypes")
    private static final Class[] longTypes = new Class[] { Label.class };

    private <T extends OrdinalEnum.IntType> void testOrdinalEnumInt(Class<T> cls) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method valueOf = cls.getMethod("valueOf", int.class);
        for (T t : cls.getEnumConstants()) {
            assertEquals(t, valueOf.invoke(null, t.asInt()));
        }
    }

    private <T extends OrdinalEnum.ShortType> void testOrdinalEnumShort(Class<T> cls) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method valueOf = cls.getMethod("valueOf", short.class);
        for (T t : cls.getEnumConstants()) {
            assertEquals(t, valueOf.invoke(null, t.asShort()));
        }
    }

    private <T extends OrdinalEnum.LongType> void testOrdinalEnumLong(Class<T> cls) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method valueOf = cls.getMethod("valueOf", long.class);
        for (T t : cls.getEnumConstants()) {
            assertEquals(t, valueOf.invoke(null, t.asLong()));
        }
    }
}
