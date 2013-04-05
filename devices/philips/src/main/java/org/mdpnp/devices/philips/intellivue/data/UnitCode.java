package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

public enum UnitCode implements EnumParseable<UnitCode>, Formatable {
    /**
     * (/)
     * Hex: 0x00
     * Dec: 0
     */
    NOM_DIM_NOS,

    /**
     * (nodimension)
     * Hex: 0x02
     * Dec: 2
     */
    NOM_DIM_DIV,

    /**
     * (percentage)
     * Hex: 0x200
     * Dec: 512
     */
    NOM_DIM_DIMLESS,

    /**
     * (partsperthousand)
     * Hex: 0x220
     * Dec: 544
     */
    NOM_DIM_PERCENT,

    /**
     * (partspermillion)
     * Hex: 0x240
     * Dec: 576
     */
    NOM_DIM_PARTS_PER_THOUSAND,

    /**
     * (molepermole)
     * Hex: 0x260
     * Dec: 608
     */
    NOM_DIM_PARTS_PER_MILLION,

    /**
     * (partspertrillion)
     * Hex: 0x2A0
     * Dec: 672
     */
    NOM_DIM_PARTS_PER_BILLION,

    /**
     * (pH)
     * Hex: 0x2C0
     * Dec: 704
     */
    NOM_DIM_PARTS_PER_TRILLION,

    /**
     * (partsperbillion)
     * Hex: 0x360
     * Dec: 864
     */
    NOM_DIM_X_MOLE_PER_MOLE,

    /**
     * (vitalsignscountdrop)
     * Hex: 0x3E0
     * Dec: 992
     */
    NOM_DIM_PH,

    /**
     * (vitalsignscountredbloodcells)
     * Hex: 0x400
     * Dec: 1024
     */
    NOM_DIM_DROP,

    /**
     * (vitalsignscountbeat)
     * Hex: 0x420
     * Dec: 1056
     */
    NOM_DIM_RBC,

    /**
     * (vitalsignscountbreath)
     * Hex: 0x440
     * Dec: 1088
     */
    NOM_DIM_BEAT,

    /**
     * (vitalsignscountcells)
     * Hex: 0x460
     * Dec: 1120
     */
    NOM_DIM_BREATH,

    /**
     * (vitalsignscountcough)
     * Hex: 0x480
     * Dec: 1152
     */
    NOM_DIM_CELL,

    /**
     * (vitalsignscountsigh)
     * Hex: 0x4A0
     * Dec: 1184
     */
    NOM_DIM_COUGH,

    /**
     * (percentofpackedcellvolume)
     * Hex: 0x4C0
     * Dec: 1216
     */
    NOM_DIM_SIGH,

    /**
     * (meter)
     * Hex: 0x4E0
     * Dec: 1248
     */
    NOM_DIM_PCT_PCV,

    /**
     * (centimeter)
     * Hex: 0x500
     * Dec: 1280
     */
    NOM_DIM_X_M,

    /**
     * (millimeter)
     * Hex: 0x511
     * Dec: 1297
     */
    NOM_DIM_CENTI_M,

    /**
     * (micro-meter)
     * Hex: 0x512
     * Dec: 1298
     */
    NOM_DIM_MILLI_M,

    /**
     * (inch)
     * Hex: 0x513
     * Dec: 1299
     */
    NOM_DIM_MICRO_M,

    /**
     * (usede.g.forSIandITBVI)
     * Hex: 0x560
     * Dec: 1376
     */
    NOM_DIM_X_INCH,

    /**
     * (permeter)
     * Hex: 0x592
     * Dec: 1426
     */
    NOM_DIM_MILLI_L_PER_M_SQ,

    /**
     * (permillimeter)
     * Hex: 0x5A0
     * Dec: 1440
     */
    NOM_DIM_PER_X_M,

    /**
     * (usede.g.forBSAcalculation)
     * Hex: 0x5B2
     * Dec: 1458
     */
    NOM_DIM_PER_MILLI_M,

    /**
     * (usede.g.forBSAcalculation)
     * Hex: 0x5C0
     * Dec: 1472
     */
    NOM_DIM_SQ_X_M,

    /**
     * (cubicmeter)
     * Hex: 0x5E0
     * Dec: 1504
     */
    NOM_DIM_SQ_X_INCH,

    /**
     * (cubiccentimeter)
     * Hex: 0x620
     * Dec: 1568
     */
    NOM_DIM_CUBIC_X_M,

    /**
     * (liter)
     * Hex: 0x631
     * Dec: 1585
     */
    NOM_DIM_CUBIC_CENTI_M,

    /**
     * (intl.unitsperliter)
     * Hex: 0x632
     * Dec: 1586
     */
    NOM_DIM_CUBIC_MILLI_M,

    /**
     * (milli-litersusede.g.forEVLWITBVSV)
     * Hex: 0x640
     * Dec: 1600
     */
    NOM_DIM_X_L,

    /**
     * (milli-literperbreath)
     * Hex: 0x652
     * Dec: 1618
     */
    NOM_DIM_MILLI_L,

    /**
     * (percubiccentimeter)
     * Hex: 0x672
     * Dec: 1650
     */
    NOM_DIM_MILLI_L_PER_BREATH,

    /**
     * (perliter)
     * Hex: 0x691
     * Dec: 1681
     */
    NOM_DIM_PER_CUBIC_CENTI_M,

    /**
     * (cubicmilli-meter)
     * Hex: 0x692
     * Dec: 1682
     */
    NOM_DIM_PER_CUBIC_MILLI_M,

    /**
     * (pernano-liter)
     * Hex: 0x6A0
     * Dec: 1696
     */
    NOM_DIM_PER_X_L,

    /**
     * (-)
     * Hex: 0x6B3
     * Dec: 1715
     */
    NOM_DIM_PER_MICRO_L,

    /**
     * (gram)
     * Hex: 0x6B4
     * Dec: 1716
     */
    NOM_DIM_PER_NANO_LITER,

    /**
     * (kilo-gram)
     * Hex: 0x6C0
     * Dec: 1728
     */
    NOM_DIM_X_G,

    /**
     * (milli-gram)
     * Hex: 0x6C3
     * Dec: 1731
     */
    NOM_DIM_KILO_G,

    /**
     * (micro-gram)
     * Hex: 0x6D2
     * Dec: 1746
     */
    NOM_DIM_MILLI_G,

    /**
     * (nono-gram)
     * Hex: 0x6D3
     * Dec: 1747
     */
    NOM_DIM_MICRO_G,

    /**
     * (pound)
     * Hex: 0x6D4
     * Dec: 1748
     */
    NOM_DIM_NANO_G,

    /**
     * (ounce)
     * Hex: 0x6E0
     * Dec: 1760
     */
    NOM_DIM_X_LB,

    /**
     * (pergram)
     * Hex: 0x700
     * Dec: 1792
     */
    NOM_DIM_X_OZ,

    /**
     * (usede.g.forLVSWRVSW)
     * Hex: 0x720
     * Dec: 1824
     */
    NOM_DIM_PER_X_G,

    /**
     * (usede.g.forRCWLCW)
     * Hex: 0x740
     * Dec: 1856
     */
    NOM_DIM_X_G_M,

    /**
     * (usede.g.forLVSWIandRVSWI)
     * Hex: 0x743
     * Dec: 1859
     */
    NOM_DIM_KILO_G_M,

    /**
     * (usede.g.forLCWIandRCWI)
     * Hex: 0x760
     * Dec: 1888
     */
    NOM_DIM_X_G_M_PER_M_SQ,

    /**
     * (grammetersquared)
     * Hex: 0x763
     * Dec: 1891
     */
    NOM_DIM_KILO_G_M_PER_M_SQ,

    /**
     * (kilo-grampersquaremeter)
     * Hex: 0x783
     * Dec: 1923
     */
    NOM_DIM_KILO_G_M_SQ,

    /**
     * (kilo-grampercubicmeter)
     * Hex: 0x7A3
     * Dec: 1955
     */
    NOM_DIM_KG_PER_M_SQ,

    /**
     * (grampercubicmeter)
     * Hex: 0x7C3
     * Dec: 1987
     */
    NOM_DIM_KILO_G_PER_M_CUBE,

    /**
     * (milli-grampercubiccentimeter)
     * Hex: 0x7E0
     * Dec: 2016
     */
    NOM_DIM_X_G_PER_CM_CUBE,

    /**
     * (micro-grampercubiccentimeter)
     * Hex: 0x7F2
     * Dec: 2034
     */
    NOM_DIM_MILLI_G_PER_CM_CUBE,

    /**
     * (nano-grampercubiccentimeter)
     * Hex: 0x7F3
     * Dec: 2035
     */
    NOM_DIM_MICRO_G_PER_CM_CUBE,

    /**
     * (gramperliter)
     * Hex: 0x7F4
     * Dec: 2036
     */
    NOM_DIM_NANO_G_PER_CM_CUBE,

    /**
     * (usede.g.forHb)
     * Hex: 0x800
     * Dec: 2048
     */
    NOM_DIM_X_G_PER_L,

    /**
     * (micro-liter)
     * Hex: 0x812
     * Dec: 2066
     */
    NOM_DIM_MILLI_G_PER_L,

    /**
     * (nano-gramperliter)
     * Hex: 0x813
     * Dec: 2067
     */
    NOM_DIM_MICRO_G_PER_L,

    /**
     * (percubicmillimeter)
     * Hex: 0x814
     * Dec: 2068
     */
    NOM_DIM_NANO_G_PER_L,

    /**
     * (milli-gramperdeciliter)
     * Hex: 0x840
     * Dec: 2112
     */
    NOM_DIM_X_G_PER_DL,

    /**
     * (grampermilli-liter)
     * Hex: 0x852
     * Dec: 2130
     */
    NOM_DIM_MILLI_G_PER_DL,

    /**
     * (milli-gramperliter)
     * Hex: 0x853
     * Dec: 2131
     */
    NOM_DIM_MICRO_G_PER_DL,

    /**
     * (milli-grampermilli-liter)
     * Hex: 0x860
     * Dec: 2144
     */
    NOM_DIM_X_G_PER_ML,

    /**
     * (micro-grampermilli-liter)
     * Hex: 0x872
     * Dec: 2162
     */
    NOM_DIM_MILLI_G_PER_ML,

    /**
     * (nano-grampermilli-liter)
     * Hex: 0x873
     * Dec: 2163
     */
    NOM_DIM_MICRO_G_PER_ML,

    /**
     * (seconds)
     * Hex: 0x874
     * Dec: 2164
     */
    NOM_DIM_NANO_G_PER_ML,

    /**
     * (micro-gramperliter)
     * Hex: 0x875
     * Dec: 2165
     */
    NOM_DIM_PICO_G_PER_ML,

    /**
     * (milli-seconds)
     * Hex: 0x880
     * Dec: 2176
     */
    NOM_DIM_SEC,

    /**
     * (micro-seconds)
     * Hex: 0x892
     * Dec: 2194
     */
    NOM_DIM_MILLI_SEC,

    /**
     * (minutes)
     * Hex: 0x893
     * Dec: 2195
     */
    NOM_DIM_MICRO_SEC,

    /**
     * (hours)
     * Hex: 0x8A0
     * Dec: 2208
     */
    NOM_DIM_MIN,

    /**
     * (days)
     * Hex: 0x8C0
     * Dec: 2240
     */
    NOM_DIM_HR,

    /**
     * (weeks)
     * Hex: 0x8E0
     * Dec: 2272
     */
    NOM_DIM_DAY,

    /**
     * (months)
     * Hex: 0x900
     * Dec: 2304
     */
    NOM_DIM_WEEKS,

    /**
     * (years)
     * Hex: 0x920
     * Dec: 2336
     */
    NOM_DIM_MON,

    /**
     * (timeofday)
     * Hex: 0x940
     * Dec: 2368
     */
    NOM_DIM_YR,

    /**
     * (date)
     * Hex: 0x960
     * Dec: 2400
     */
    NOM_DIM_TOD,

    /**
     * (persecond)
     * Hex: 0x980
     * Dec: 2432
     */
    NOM_DIM_DATE,

    /**
     * (hertz)
     * Hex: 0x9A0
     * Dec: 2464
     */
    NOM_DIM_PER_X_SEC,

    /**
     * (perminuteusede.g.forthePVCcountnumericalvalue)
     * Hex: 0x9C0
     * Dec: 2496
     */
    NOM_DIM_HZ,

    /**
     * (perhour)
     * Hex: 0x9E0
     * Dec: 2528
     */
    NOM_DIM_PER_MIN,

    /**
     * (perday)
     * Hex: 0xA00
     * Dec: 2560
     */
    NOM_DIM_PER_HR,

    /**
     * (perweek)
     * Hex: 0xA20
     * Dec: 2592
     */
    NOM_DIM_PER_DAY,

    /**
     * (permonth)
     * Hex: 0xA40
     * Dec: 2624
     */
    NOM_DIM_PER_WK,

    /**
     * (peryear)
     * Hex: 0xA60
     * Dec: 2656
     */
    NOM_DIM_PER_MO,

    /**
     * (beatsperminuteusede.g.forHR/PULSE)
     * Hex: 0xA80
     * Dec: 2688
     */
    NOM_DIM_PER_YR,

    /**
     * (pulsperminute)
     * Hex: 0xAA0
     * Dec: 2720
     */
    NOM_DIM_BEAT_PER_MIN,

    /**
     * (respirationbreathesperminute)
     * Hex: 0xAC0
     * Dec: 2752
     */
    NOM_DIM_PULS_PER_MIN,

    /**
     * (meterpersecond)
     * Hex: 0xAE0
     * Dec: 2784
     */
    NOM_DIM_RESP_PER_MIN,

    /**
     * (speedforrecordings)
     * Hex: 0xB00
     * Dec: 2816
     */
    NOM_DIM_X_M_PER_SEC,

    /**
     * (usedforCI)
     * Hex: 0xB12
     * Dec: 2834
     */
    NOM_DIM_MILLI_M_PER_SEC,

    /**
     * (usedforDO2IVO2IO2AVI)
     * Hex: 0xB20
     * Dec: 2848
     */
    NOM_DIM_X_L_PER_MIN_PER_M_SQ,

    /**
     * (squaremeterpersecond)
     * Hex: 0xB32
     * Dec: 2866
     */
    NOM_DIM_MILLI_L_PER_MIN_PER_M_SQ,

    /**
     * (squarecentimeterpersecond)
     * Hex: 0xB40
     * Dec: 2880
     */
    NOM_DIM_SQ_X_M_PER_SEC,

    /**
     * (cubicmeterpersecond)
     * Hex: 0xB51
     * Dec: 2897
     */
    NOM_DIM_SQ_CENTI_M_PER_SEC,

    /**
     * (cubiccentimeterpersecond)
     * Hex: 0xB60
     * Dec: 2912
     */
    NOM_DIM_CUBIC_X_M_PER_SEC,

    /**
     * (literpersecond)
     * Hex: 0xB71
     * Dec: 2929
     */
    NOM_DIM_CUBIC_CENTI_M_PER_SEC,

    /**
     * (literperminutes)
     * Hex: 0xBE0
     * Dec: 3040
     */
    NOM_DIM_X_L_PER_SEC,

    /**
     * (deciliterpersecond)
     * Hex: 0xC00
     * Dec: 3072
     */
    NOM_DIM_X_L_PER_MIN,

    /**
     * (usedforDO2VO2ALVENT)
     * Hex: 0xC10
     * Dec: 3088
     */
    NOM_DIM_DECI_L_PER_MIN,

    /**
     * (literperhour)
     * Hex: 0xC12
     * Dec: 3090
     */
    NOM_DIM_MILLI_L_PER_MIN,

    /**
     * (milli-literperhour)
     * Hex: 0xC20
     * Dec: 3104
     */
    NOM_DIM_X_L_PER_HR,

    /**
     * (literperday)
     * Hex: 0xC32
     * Dec: 3122
     */
    NOM_DIM_MILLI_L_PER_HR,

    /**
     * (milli-literperday)
     * Hex: 0xC40
     * Dec: 3136
     */
    NOM_DIM_X_L_PER_DAY,

    /**
     * (usede.g.forEVLWI)
     * Hex: 0xC52
     * Dec: 3154
     */
    NOM_DIM_MILLI_L_PER_DAY,

    /**
     * (kilo-grampersecond)
     * Hex: 0xC72
     * Dec: 3186
     */
    NOM_DIM_MILLI_L_PER_KG,

    /**
     * (gramperminute)
     * Hex: 0xCE3
     * Dec: 3299
     */
    NOM_DIM_KILO_G_PER_SEC,

    /**
     * (kilo-gramperminute)
     * Hex: 0xD00
     * Dec: 3328
     */
    NOM_DIM_X_G_PER_MIN,

    /**
     * (milli-gramperminute)
     * Hex: 0xD03
     * Dec: 3331
     */
    NOM_DIM_KILO_G_PER_MIN,

    /**
     * (micro-gramperminute)
     * Hex: 0xD12
     * Dec: 3346
     */
    NOM_DIM_MILLI_G_PER_MIN,

    /**
     * (nano-gramperminute)
     * Hex: 0xD13
     * Dec: 3347
     */
    NOM_DIM_MICRO_G_PER_MIN,

    /**
     * (gramperhour)
     * Hex: 0xD14
     * Dec: 3348
     */
    NOM_DIM_NANO_G_PER_MIN,

    /**
     * (kilo-gramperhour)
     * Hex: 0xD20
     * Dec: 3360
     */
    NOM_DIM_X_G_PER_HR,

    /**
     * (milli-gramperhour)
     * Hex: 0xD23
     * Dec: 3363
     */
    NOM_DIM_KILO_G_PER_HR,

    /**
     * (micro-gramperhour)
     * Hex: 0xD32
     * Dec: 3378
     */
    NOM_DIM_MILLI_G_PER_HR,

    /**
     * (nano-gramperhour)
     * Hex: 0xD33
     * Dec: 3379
     */
    NOM_DIM_MICRO_G_PER_HR,

    /**
     * (kilo-gramperday)
     * Hex: 0xD34
     * Dec: 3380
     */
    NOM_DIM_NANO_G_PER_HR,

    /**
     * (gramperkilo-gramperminute)
     * Hex: 0xD43
     * Dec: 3395
     */
    NOM_DIM_KILO_G_PER_DAY,

    /**
     * (milli-gramperkilo-gramperminute)
     * Hex: 0xD80
     * Dec: 3456
     */
    NOM_DIM_X_G_PER_KG_PER_MIN,

    /**
     * (micro-gramperkilo-gramperminute)
     * Hex: 0xD92
     * Dec: 3474
     */
    NOM_DIM_MILLI_G_PER_KG_PER_MIN,

    /**
     * (nano-gramperkilo-gramperminute)
     * Hex: 0xD93
     * Dec: 3475
     */
    NOM_DIM_MICRO_G_PER_KG_PER_MIN,

    /**
     * (gramperkilo-gramperhour)
     * Hex: 0xD94
     * Dec: 3476
     */
    NOM_DIM_NANO_G_PER_KG_PER_MIN,

    /**
     * (mili-gramperkilo-gramperhour)
     * Hex: 0xDA0
     * Dec: 3488
     */
    NOM_DIM_X_G_PER_KG_PER_HR,

    /**
     * (micro-gramperkilo-gramperhour)
     * Hex: 0xDB2
     * Dec: 3506
     */
    NOM_DIM_MILLI_G_PER_KG_PER_HR,

    /**
     * (nano-gramperkilo-gramperhour)
     * Hex: 0xDB3
     * Dec: 3507
     */
    NOM_DIM_MICRO_G_PER_KG_PER_HR,

    /**
     * (kilo-gramperliterpersecond)
     * Hex: 0xDB4
     * Dec: 3508
     */
    NOM_DIM_NANO_G_PER_KG_PER_HR,

    /**
     * (kilo-grampermeterpersecond)
     * Hex: 0xDE3
     * Dec: 3555
     */
    NOM_DIM_KILO_G_PER_L_SEC,

    /**
     * (kilo-grammeterpersecond)
     * Hex: 0xE63
     * Dec: 3683
     */
    NOM_DIM_KILO_G_PER_M_PER_SEC,

    /**
     * (newtonseconds)
     * Hex: 0xE83
     * Dec: 3715
     */
    NOM_DIM_KILO_G_M_PER_SEC,

    /**
     * (newton)
     * Hex: 0xEA0
     * Dec: 3744
     */
    NOM_DIM_X_NEWTON_SEC,

    /**
     * (pascal)
     * Hex: 0xEC0
     * Dec: 3776
     */
    NOM_DIM_X_NEWTON,

    /**
     * (hekto-pascal)
     * Hex: 0xF00
     * Dec: 3840
     */
    NOM_DIM_X_PASCAL,

    /**
     * (kilo-pascal)
     * Hex: 0xF02
     * Dec: 3842
     */
    NOM_DIM_HECTO_PASCAL,

    /**
     * (mmmercury)
     * Hex: 0xF03
     * Dec: 3843
     */
    NOM_DIM_KILO_PASCAL,

    /**
     * (centimeterH20)
     * Hex: 0xF20
     * Dec: 3872
     */
    NOM_DIM_MMHG,

    /**
     * (milli-bar)
     * Hex: 0xF40
     * Dec: 3904
     */
    NOM_DIM_CM_H2O,

    /**
     * (Joules)
     * Hex: 0xF72
     * Dec: 3954
     */
    NOM_DIM_MILLI_BAR,

    /**
     * (electronvolts)
     * Hex: 0xF80
     * Dec: 3968
     */
    NOM_DIM_X_JOULES,

    /**
     * (watt)
     * Hex: 0xFA0
     * Dec: 4000
     */
    NOM_DIM_EVOLT,

    /**
     * (milli-watt)
     * Hex: 0xFC0
     * Dec: 4032
     */
    NOM_DIM_X_WATT,

    /**
     * (nano-watt)
     * Hex: 0xFD2
     * Dec: 4050
     */
    NOM_DIM_MILLI_WATT,

    /**
     * (pico-watt)
     * Hex: 0xFD4
     * Dec: 4052
     */
    NOM_DIM_NANO_WATT,

    /**
     * Dyn-sec/cm^5(dynesecondpercm^5)
     * Hex: 0xFD5
     * Dec: 4053
     */
    NOM_DIM_PICO_WATT,

    /**
     * A
     * Hex: 0x1020
     * Dec: 4128
     */
    NOM_DIM_X_DYNE_PER_SEC_PER_CM5,

    /**
     * mA
     * Hex: 0x1040
     * Dec: 4160
     */
    NOM_DIM_X_AMPS,

    /**
     * C
     * Hex: 0x1052
     * Dec: 4178
     */
    NOM_DIM_MILLI_AMPS,

    /**
     * ??C
     * Hex: 0x1060
     * Dec: 4192
     */
    NOM_DIM_X_COULOMB,

    /**
     * V
     * Hex: 0x1073
     * Dec: 4211
     */
    NOM_DIM_MICRO_COULOMB,

    /**
     * mV
     * Hex: 0x10A0
     * Dec: 4256
     */
    NOM_DIM_X_VOLT,

    /**
     * ??V
     * Hex: 0x10B2
     * Dec: 4274
     */
    NOM_DIM_MILLI_VOLT,

    /**
     * Ohm
     * Hex: 0x10B3
     * Dec: 4275
     */
    NOM_DIM_MICRO_VOLT,

    /**
     * kOhm
     * Hex: 0x10C0
     * Dec: 4288
     */
    NOM_DIM_X_OHM,

    /**
     * F
     * Hex: 0x10C3
     * Dec: 4291
     */
    NOM_DIM_OHM_K,

    /**
     * ??K
     * Hex: 0x1100
     * Dec: 4352
     */
    NOM_DIM_X_FARAD,

    /**
     * ??F
     * Hex: 0x1120
     * Dec: 4384
     */
    NOM_DIM_KELVIN,

    /**
     * cd
     * Hex: 0x1140
     * Dec: 4416
     */
    NOM_DIM_FAHR,

    /**
     * mOsm
     * Hex: 0x1180
     * Dec: 4480
     */
    NOM_DIM_X_CANDELA,

    /**
     * mol
     * Hex: 0x11B2
     * Dec: 4530
     */
    NOM_DIM_MILLI_OSM,

    /**
     * mmol
     * Hex: 0x11C0
     * Dec: 4544
     */
    NOM_DIM_X_MOLE,

    /**
     * mEq
     * Hex: 0x11D2
     * Dec: 4562
     */
    NOM_DIM_MILLI_MOLE,

    /**
     * mOsm/l
     * Hex: 0x11F2
     * Dec: 4594
     */
    NOM_DIM_MILLI_EQUIV,

    /**
     * mmol/l
     * Hex: 0x1212
     * Dec: 4626
     */
    NOM_DIM_MILLI_OSM_PER_L,

    /**
     * ??mol/l
     * Hex: 0x1272
     * Dec: 4722
     */
    NOM_DIM_MILLI_MOLE_PER_L,

    /**
     * mEq/l
     * Hex: 0x1273
     * Dec: 4723
     */
    NOM_DIM_MICRO_MOLE_PER_L,

    /**
     * mEq/day
     * Hex: 0x12F2
     * Dec: 4850
     */
    NOM_DIM_MILLI_EQUIV_PER_L,

    /**
     * (micro-gramperdeci-liter)
     * Hex: 0x1352
     * Dec: 4946
     */
    NOM_DIM_MILLI_MOL_PER_KG,

    /**
     * i.u.
     * Hex: 0x1452
     * Dec: 5202
     */
    NOM_DIM_MILLI_EQUIV_PER_DAY,

    /**
     * mi.u.
     * Hex: 0x1560
     * Dec: 5472
     */
    NOM_DIM_X_INTL_UNIT,

    /**
     * i.u./cm3
     * Hex: 0x1572
     * Dec: 5490
     */
    NOM_DIM_MILLI_INTL_UNIT,

    /**
     * mi.u./cm3
     * Hex: 0x1580
     * Dec: 5504
     */
    NOM_DIM_X_INTL_UNIT_PER_CM_CUBE,

    /**
     * i.u./ml
     * Hex: 0x1592
     * Dec: 5522
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_CM_CUBE,

    /**
     * (10^6intl.unitsperliter)
     * Hex: 0x15C0
     * Dec: 5568
     */
    NOM_DIM_X_INTL_UNIT_PER_L,

    /**
     * (moleperkilo-gram)
     * Hex: 0x15C5
     * Dec: 5573
     */
    NOM_DIM_MEGA_INTL_UNIT_PER_L,

    /**
     * (internationalunitperminute)
     * Hex: 0x15E0
     * Dec: 5600
     */
    NOM_DIM_X_INTL_UNIT_PER_ML,

    /**
     * mi.u./min
     * Hex: 0x15F2
     * Dec: 5618
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_ML,

    /**
     * mi.u./ml
     * Hex: 0x1620
     * Dec: 5664
     */
    NOM_DIM_X_INTL_UNIT_PER_MIN,

    /**
     * i.u./hour
     * Hex: 0x1632
     * Dec: 5682
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_MIN,

    /**
     * mi.u./hour
     * Hex: 0x1640
     * Dec: 5696
     */
    NOM_DIM_X_INTL_UNIT_PER_HR,

    /**
     * i.u./kg/min
     * Hex: 0x1652
     * Dec: 5714
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_HR,

    /**
     * mi.u./kg/min(milli-internationalunitperkilo-gramperminute)
     * Hex: 0x16A0
     * Dec: 5792
     */
    NOM_DIM_X_INTL_UNIT_PER_KG_PER_MIN,

    /**
     * i.u./kg/hour(internationalunitperkilo-gramperhour)
     * Hex: 0x16B2
     * Dec: 5810
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_MIN,

    /**
     * mi.u./kg/hour(milli-internationalunitperkilo-gramperhour)
     * Hex: 0x16C0
     * Dec: 5824
     */
    NOM_DIM_X_INTL_UNIT_PER_KG_PER_HR,

    /**
     * ml/cmH2O(milli-literpercentimeterH2O)
     * Hex: 0x16D2
     * Dec: 5842
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_HR,

    /**
     * cmH2O/l/sec(centimeterH2Opersecond)
     * Hex: 0x1712
     * Dec: 5906
     */
    NOM_DIM_MILLI_L_PER_CM_H2O,

    /**
     * ml2/sec
     * Hex: 0x1720
     * Dec: 5920
     */
    NOM_DIM_CM_H2O_PER_L_PER_SEC,

    /**
     * cmH2O/%
     * Hex: 0x1752
     * Dec: 5970
     */
    NOM_DIM_MILLI_L_SQ_PER_SEC,

    /**
     * DS*m2/cm5(usedforSVRIandPVRI)
     * Hex: 0x1760
     * Dec: 5984
     */
    NOM_DIM_CM_H2O_PER_PERCENT,

    /**
     * ??C
     * Hex: 0x1780
     * Dec: 6016
     */
    NOM_DIM_DYNE_SEC_PER_M_SQ_PER_CM_5,

    /**
     * cmH2O/l
     * Hex: 0x17A0
     * Dec: 6048
     */
    NOM_DIM_DEGC,

    /**
     * ml/dl
     * Hex: 0x17D2
     * Dec: 6098
     */
    NOM_DIM_MILLI_AMP_HR,

    /**
     * mmHg/%
     * Hex: 0x1800
     * Dec: 6144
     */
    NOM_DIM_CM_H2O_PER_L,

    /**
     * kPa/%
     * Hex: 0x1820
     * Dec: 6176
     */
    NOM_DIM_MM_HG_PER_PERCENT,

    /**
     * l/mmHg
     * Hex: 0x1843
     * Dec: 6211
     */
    NOM_DIM_KILO_PA_PER_PERCENT,

    /**
     * ml/mmHg
     * Hex: 0x1880
     * Dec: 6272
     */
    NOM_DIM_X_L_PER_MM_HG,

    /**
     * mAh
     * Hex: 0x1892
     * Dec: 6290
     */
    NOM_DIM_MILLI_L_PER_MM_HG,

    /**
     * dB
     * Hex: 0x1912
     * Dec: 6418
     */
    NOM_DIM_MILLI_L_PER_DL,

    /**
     * g/mg
     * Hex: 0x1920
     * Dec: 6432
     */
    NOM_DIM_DECIBEL,

    /**
     * mg/mg
     * Hex: 0x1940
     * Dec: 6464
     */
    NOM_DIM_X_G_PER_MILLI_G,

    /**
     * bpm/l
     * Hex: 0x1952
     * Dec: 6482
     */
    NOM_DIM_MILLI_G_PER_MILLI_G,

    /**
     * bpm/ml
     * Hex: 0x1960
     * Dec: 6496
     */
    NOM_DIM_BEAT_PER_MIN_PER_X_L,

    /**
     * 1/(min*l)
     * Hex: 0x1972
     * Dec: 6514
     */
    NOM_DIM_BEAT_PER_MIN_PER_MILLI_L,

    /**
     * (meterperminute)
     * Hex: 0x1980
     * Dec: 6528
     */
    NOM_DIM_PER_X_L_PER_MIN,

    /**
     * (speedforrecordings)
     * Hex: 0x19A0
     * Dec: 6560
     */
    NOM_DIM_X_M_PER_MIN,

    /**
     * (pico-grampermilli-liter)
     * Hex: 0x19B1
     * Dec: 6577
     */
    NOM_DIM_CENTI_M_PER_MIN,

    /**
     * (countasadimension)
     * Hex: 0xF000
     * Dec: 61440
     */
    NOM_DIM_COMPLEX,

    /**
     * (part)
     * Hex: 0xF001
     * Dec: 61441
     */
    NOM_DIM_COUNT,

    /**
     * (puls)
     * Hex: 0xF002
     * Dec: 61442
     */
    NOM_DIM_PART,

    /**
     * (micro-voltpeaktopeak)
     * Hex: 0xF003
     * Dec: 61443
     */
    NOM_DIM_PULS,

    /**
     * (micor-voltsquare)
     * Hex: 0xF004
     * Dec: 61444
     */
    NOM_DIM_UV_PP,

    /**
     * (lumen)
     * Hex: 0xF005
     * Dec: 61445
     */
    NOM_DIM_UV_SQ,

    /**
     * (poundpersquareinch)
     * Hex: 0xF007
     * Dec: 61447
     */
    NOM_DIM_LUMEN,

    /**
     * (milli-metermercurypersecond)
     * Hex: 0xF008
     * Dec: 61448
     */
    NOM_DIM_LB_PER_INCH_SQ,

    /**
     * (milli-literpersecond)
     * Hex: 0xF009
     * Dec: 61449
     */
    NOM_DIM_MM_HG_PER_SEC,

    /**
     * (beatperminutepermilli-liter)
     * Hex: 0xF00A
     * Dec: 61450
     */
    NOM_DIM_ML_PER_SEC,

    /**
     * (jouleperday)
     * Hex: 0xF00B
     * Dec: 61451
     */
    NOM_DIM_BEAT_PER_MIN_PER_ML_C,

    /**
     * (kilojouleperday)
     * Hex: 0xF060
     * Dec: 61536
     */
    NOM_DIM_X_JOULE_PER_DAY,

    /**
     * (megajouleperday)
     * Hex: 0xF063
     * Dec: 61539
     */
    NOM_DIM_KILO_JOULE_PER_DAY,

    /**
     * (calories)
     * Hex: 0xF064
     * Dec: 61540
     */
    NOM_DIM_MEGA_JOULE_PER_DAY,

    /**
     * (kilocalories)
     * Hex: 0xF080
     * Dec: 61568
     */
    NOM_DIM_X_CALORIE,

    /**
     * (millioncalories)
     * Hex: 0xF083
     * Dec: 61571
     */
    NOM_DIM_KILO_CALORIE,

    /**
     * (caloriesperday)
     * Hex: 0xF084
     * Dec: 61572
     */
    NOM_DIM_MEGA_CALORIE,

    /**
     * (kilo-caloriesperday)
     * Hex: 0xF0A0
     * Dec: 61600
     */
    NOM_DIM_X_CALORIE_PER_DAY,

    /**
     * (megacaloriesperday)
     * Hex: 0xF0A3
     * Dec: 61603
     */
    NOM_DIM_KILO_CALORIE_PER_DAY,

    /**
     * (caloriespermilli-liter)
     * Hex: 0xF0A4
     * Dec: 61604
     */
    NOM_DIM_MEGA_CALORIE_PER_DAY,

    /**
     * (kilocaloriesperml)
     * Hex: 0xF0C0
     * Dec: 61632
     */
    NOM_DIM_X_CALORIE_PER_ML,

    /**
     * (Joulepermilli-liter)
     * Hex: 0xF0C3
     * Dec: 61635
     */
    NOM_DIM_KILO_CALORIE_PER_ML,

    /**
     * (kilo-joulespermilli-liter)
     * Hex: 0xF0E0
     * Dec: 61664
     */
    NOM_DIM_X_JOULE_PER_ML,

    /**
     * (revolutionsperminute)
     * Hex: 0xF0E3
     * Dec: 61667
     */
    NOM_DIM_KILO_JOULE_PER_ML,

    /**
     * (perminuteperliterperkilo)
     * Hex: 0xF100
     * Dec: 61696
     */
    NOM_DIM_X_REV_PER_MIN,

    /**
     * (literpermilli-bar)
     * Hex: 0xF120
     * Dec: 61728
     */
    NOM_DIM_PER_L_PER_MIN_PER_KG,

    /**
     * (milli-literpermilli-bar)
     * Hex: 0xF140
     * Dec: 61760
     */
    NOM_DIM_X_L_PER_MILLI_BAR,

    /**
     * (literperkilo-gramperhour)
     * Hex: 0xF152
     * Dec: 61778
     */
    NOM_DIM_MILLI_L_PER_MILLI_BAR,

    /**
     * (milli-literperkilogramperhour)
     * Hex: 0xF160
     * Dec: 61792
     */
    NOM_DIM_X_L_PER_KG_PER_HR,

    /**
     * (barperliterpersec)
     * Hex: 0xF172
     * Dec: 61810
     */
    NOM_DIM_MILLI_L_PER_KG_PER_HR,

    /**
     * (milli-barperliterpersec)
     * Hex: 0xF180
     * Dec: 61824
     */
    NOM_DIM_X_BAR_PER_LITER_PER_SEC,

    /**
     * (barperliter)
     * Hex: 0xF192
     * Dec: 61842
     */
    NOM_DIM_MILLI_BAR_PER_LITER_PER_SEC,

    /**
     * (barperliter)
     * Hex: 0xF1A0
     * Dec: 61856
     */
    NOM_DIM_X_BAR_PER_LITER,

    /**
     * (voltpermilli-volt)
     * Hex: 0xF1B2
     * Dec: 61874
     */
    NOM_DIM_MILLI_BAR_PER_LITER,

    /**
     * (cmH2Opermicro-volt)
     * Hex: 0xF1C0
     * Dec: 61888
     */
    NOM_DIM_VOLT_PER_MILLI_VOLT,

    /**
     * (jouleperliter)
     * Hex: 0xF1E0
     * Dec: 61920
     */
    NOM_DIM_CM_H2O_PER_MICRO_VOLT,

    /**
     * (literperbar)
     * Hex: 0xF200
     * Dec: 61952
     */
    NOM_DIM_X_JOULE_PER_LITER,

    /**
     * (meterpermilli-volt)
     * Hex: 0xF220
     * Dec: 61984
     */
    NOM_DIM_X_L_PER_BAR,

    /**
     * (milli-meterpermilli-volt)
     * Hex: 0xF240
     * Dec: 62016
     */
    NOM_DIM_X_M_PER_MILLI_VOLT,

    /**
     * (literperminuteperkilo-gram)
     * Hex: 0xF252
     * Dec: 62034
     */
    NOM_DIM_MILLI_M_PER_MILLI_VOLT,

    /**
     * (milli-literperminuteperkilo-gram)
     * Hex: 0xF260
     * Dec: 62048
     */
    NOM_DIM_X_L_PER_MIN_PER_KG,

    /**
     * (pascalperliterpersec)
     * Hex: 0xF272
     * Dec: 62066
     */
    NOM_DIM_MILLI_L_PER_MIN_PER_KG,

    /**
     * (hPaperliterpersec)
     * Hex: 0xF280
     * Dec: 62080
     */
    NOM_DIM_X_PASCAL_PER_L_PER_SEC,

    /**
     * (kPaperliterpersec)
     * Hex: 0xF282
     * Dec: 62082
     */
    NOM_DIM_HECTO_PASCAL_PER_L_PER_SEC,

    /**
     * (milli-literperpascal)
     * Hex: 0xF283
     * Dec: 62083
     */
    NOM_DIM_KILO_PASCAL_PER_L_PER_SEC,

    /**
     * (milli-literperhecto-pascal)
     * Hex: 0xF2A0
     * Dec: 62112
     */
    NOM_DIM_MILLI_L_PER_X_PASCAL,

    /**
     * (milli-literperkilo-pascal)
     * Hex: 0xF2A2
     * Dec: 62114
     */
    NOM_DIM_MILLI_L_PER_HECTO_PASCAL,

    /**
     * (mm)
     * Hex: 0xF2A3
     * Dec: 62115
     */
    NOM_DIM_MILLI_L_PER_KILO_PASCAL,

    /**
     * AlertCodes
     * Hex: 0xF2C0
     * Dec: 62144
     */
    NOM_DIM_MM_HG_PER_X_L_PER_SEC,

;
    
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	@Override
	public UnitCode parse(ByteBuffer bb) {
		return UnitCode.valueOf(Bits.getUnsignedShort(bb));
	}
    
    public OIDType asOID() {
    	return OIDType.lookup(asInt());
    }
    public int asInt() {
        switch(this) {
        case NOM_DIM_NOS:
            return 0;
        case NOM_DIM_DIV:
            return 2;
        case NOM_DIM_DIMLESS:
            return 512;
        case NOM_DIM_PERCENT:
            return 544;
        case NOM_DIM_PARTS_PER_THOUSAND:
            return 576;
        case NOM_DIM_PARTS_PER_MILLION:
            return 608;
        case NOM_DIM_PARTS_PER_BILLION:
            return 672;
        case NOM_DIM_PARTS_PER_TRILLION:
            return 704;
        case NOM_DIM_X_MOLE_PER_MOLE:
            return 864;
        case NOM_DIM_PH:
            return 992;
        case NOM_DIM_DROP:
            return 1024;
        case NOM_DIM_RBC:
            return 1056;
        case NOM_DIM_BEAT:
            return 1088;
        case NOM_DIM_BREATH:
            return 1120;
        case NOM_DIM_CELL:
            return 1152;
        case NOM_DIM_COUGH:
            return 1184;
        case NOM_DIM_SIGH:
            return 1216;
        case NOM_DIM_PCT_PCV:
            return 1248;
        case NOM_DIM_X_M:
            return 1280;
        case NOM_DIM_CENTI_M:
            return 1297;
        case NOM_DIM_MILLI_M:
            return 1298;
        case NOM_DIM_MICRO_M:
            return 1299;
        case NOM_DIM_X_INCH:
            return 1376;
        case NOM_DIM_MILLI_L_PER_M_SQ:
            return 1426;
        case NOM_DIM_PER_X_M:
            return 1440;
        case NOM_DIM_PER_MILLI_M:
            return 1458;
        case NOM_DIM_SQ_X_M:
            return 1472;
        case NOM_DIM_SQ_X_INCH:
            return 1504;
        case NOM_DIM_CUBIC_X_M:
            return 1568;
        case NOM_DIM_CUBIC_CENTI_M:
            return 1585;
        case NOM_DIM_CUBIC_MILLI_M:
            return 1586;
        case NOM_DIM_X_L:
            return 1600;
        case NOM_DIM_MILLI_L:
            return 1618;
        case NOM_DIM_MILLI_L_PER_BREATH:
            return 1650;
        case NOM_DIM_PER_CUBIC_CENTI_M:
            return 1681;
        case NOM_DIM_PER_CUBIC_MILLI_M:
            return 1682;
        case NOM_DIM_PER_X_L:
            return 1696;
        case NOM_DIM_PER_MICRO_L:
            return 1715;
        case NOM_DIM_PER_NANO_LITER:
            return 1716;
        case NOM_DIM_X_G:
            return 1728;
        case NOM_DIM_KILO_G:
            return 1731;
        case NOM_DIM_MILLI_G:
            return 1746;
        case NOM_DIM_MICRO_G:
            return 1747;
        case NOM_DIM_NANO_G:
            return 1748;
        case NOM_DIM_X_LB:
            return 1760;
        case NOM_DIM_X_OZ:
            return 1792;
        case NOM_DIM_PER_X_G:
            return 1824;
        case NOM_DIM_X_G_M:
            return 1856;
        case NOM_DIM_KILO_G_M:
            return 1859;
        case NOM_DIM_X_G_M_PER_M_SQ:
            return 1888;
        case NOM_DIM_KILO_G_M_PER_M_SQ:
            return 1891;
        case NOM_DIM_KILO_G_M_SQ:
            return 1923;
        case NOM_DIM_KG_PER_M_SQ:
            return 1955;
        case NOM_DIM_KILO_G_PER_M_CUBE:
            return 1987;
        case NOM_DIM_X_G_PER_CM_CUBE:
            return 2016;
        case NOM_DIM_MILLI_G_PER_CM_CUBE:
            return 2034;
        case NOM_DIM_MICRO_G_PER_CM_CUBE:
            return 2035;
        case NOM_DIM_NANO_G_PER_CM_CUBE:
            return 2036;
        case NOM_DIM_X_G_PER_L:
            return 2048;
        case NOM_DIM_MILLI_G_PER_L:
            return 2066;
        case NOM_DIM_MICRO_G_PER_L:
            return 2067;
        case NOM_DIM_NANO_G_PER_L:
            return 2068;
        case NOM_DIM_X_G_PER_DL:
            return 2112;
        case NOM_DIM_MILLI_G_PER_DL:
            return 2130;
        case NOM_DIM_MICRO_G_PER_DL:
            return 2131;
        case NOM_DIM_X_G_PER_ML:
            return 2144;
        case NOM_DIM_MILLI_G_PER_ML:
            return 2162;
        case NOM_DIM_MICRO_G_PER_ML:
            return 2163;
        case NOM_DIM_NANO_G_PER_ML:
            return 2164;
        case NOM_DIM_PICO_G_PER_ML:
            return 2165;
        case NOM_DIM_SEC:
            return 2176;
        case NOM_DIM_MILLI_SEC:
            return 2194;
        case NOM_DIM_MICRO_SEC:
            return 2195;
        case NOM_DIM_MIN:
            return 2208;
        case NOM_DIM_HR:
            return 2240;
        case NOM_DIM_DAY:
            return 2272;
        case NOM_DIM_WEEKS:
            return 2304;
        case NOM_DIM_MON:
            return 2336;
        case NOM_DIM_YR:
            return 2368;
        case NOM_DIM_TOD:
            return 2400;
        case NOM_DIM_DATE:
            return 2432;
        case NOM_DIM_PER_X_SEC:
            return 2464;
        case NOM_DIM_HZ:
            return 2496;
        case NOM_DIM_PER_MIN:
            return 2528;
        case NOM_DIM_PER_HR:
            return 2560;
        case NOM_DIM_PER_DAY:
            return 2592;
        case NOM_DIM_PER_WK:
            return 2624;
        case NOM_DIM_PER_MO:
            return 2656;
        case NOM_DIM_PER_YR:
            return 2688;
        case NOM_DIM_BEAT_PER_MIN:
            return 2720;
        case NOM_DIM_PULS_PER_MIN:
            return 2752;
        case NOM_DIM_RESP_PER_MIN:
            return 2784;
        case NOM_DIM_X_M_PER_SEC:
            return 2816;
        case NOM_DIM_MILLI_M_PER_SEC:
            return 2834;
        case NOM_DIM_X_L_PER_MIN_PER_M_SQ:
            return 2848;
        case NOM_DIM_MILLI_L_PER_MIN_PER_M_SQ:
            return 2866;
        case NOM_DIM_SQ_X_M_PER_SEC:
            return 2880;
        case NOM_DIM_SQ_CENTI_M_PER_SEC:
            return 2897;
        case NOM_DIM_CUBIC_X_M_PER_SEC:
            return 2912;
        case NOM_DIM_CUBIC_CENTI_M_PER_SEC:
            return 2929;
        case NOM_DIM_X_L_PER_SEC:
            return 3040;
        case NOM_DIM_X_L_PER_MIN:
            return 3072;
        case NOM_DIM_DECI_L_PER_MIN:
            return 3088;
        case NOM_DIM_MILLI_L_PER_MIN:
            return 3090;
        case NOM_DIM_X_L_PER_HR:
            return 3104;
        case NOM_DIM_MILLI_L_PER_HR:
            return 3122;
        case NOM_DIM_X_L_PER_DAY:
            return 3136;
        case NOM_DIM_MILLI_L_PER_DAY:
            return 3154;
        case NOM_DIM_MILLI_L_PER_KG:
            return 3186;
        case NOM_DIM_KILO_G_PER_SEC:
            return 3299;
        case NOM_DIM_X_G_PER_MIN:
            return 3328;
        case NOM_DIM_KILO_G_PER_MIN:
            return 3331;
        case NOM_DIM_MILLI_G_PER_MIN:
            return 3346;
        case NOM_DIM_MICRO_G_PER_MIN:
            return 3347;
        case NOM_DIM_NANO_G_PER_MIN:
            return 3348;
        case NOM_DIM_X_G_PER_HR:
            return 3360;
        case NOM_DIM_KILO_G_PER_HR:
            return 3363;
        case NOM_DIM_MILLI_G_PER_HR:
            return 3378;
        case NOM_DIM_MICRO_G_PER_HR:
            return 3379;
        case NOM_DIM_NANO_G_PER_HR:
            return 3380;
        case NOM_DIM_KILO_G_PER_DAY:
            return 3395;
        case NOM_DIM_X_G_PER_KG_PER_MIN:
            return 3456;
        case NOM_DIM_MILLI_G_PER_KG_PER_MIN:
            return 3474;
        case NOM_DIM_MICRO_G_PER_KG_PER_MIN:
            return 3475;
        case NOM_DIM_NANO_G_PER_KG_PER_MIN:
            return 3476;
        case NOM_DIM_X_G_PER_KG_PER_HR:
            return 3488;
        case NOM_DIM_MILLI_G_PER_KG_PER_HR:
            return 3506;
        case NOM_DIM_MICRO_G_PER_KG_PER_HR:
            return 3507;
        case NOM_DIM_NANO_G_PER_KG_PER_HR:
            return 3508;
        case NOM_DIM_KILO_G_PER_L_SEC:
            return 3555;
        case NOM_DIM_KILO_G_PER_M_PER_SEC:
            return 3683;
        case NOM_DIM_KILO_G_M_PER_SEC:
            return 3715;
        case NOM_DIM_X_NEWTON_SEC:
            return 3744;
        case NOM_DIM_X_NEWTON:
            return 3776;
        case NOM_DIM_X_PASCAL:
            return 3840;
        case NOM_DIM_HECTO_PASCAL:
            return 3842;
        case NOM_DIM_KILO_PASCAL:
            return 3843;
        case NOM_DIM_MMHG:
            return 3872;
        case NOM_DIM_CM_H2O:
            return 3904;
        case NOM_DIM_MILLI_BAR:
            return 3954;
        case NOM_DIM_X_JOULES:
            return 3968;
        case NOM_DIM_EVOLT:
            return 4000;
        case NOM_DIM_X_WATT:
            return 4032;
        case NOM_DIM_MILLI_WATT:
            return 4050;
        case NOM_DIM_NANO_WATT:
            return 4052;
        case NOM_DIM_PICO_WATT:
            return 4053;
        case NOM_DIM_X_DYNE_PER_SEC_PER_CM5:
            return 4128;
        case NOM_DIM_X_AMPS:
            return 4160;
        case NOM_DIM_MILLI_AMPS:
            return 4178;
        case NOM_DIM_X_COULOMB:
            return 4192;
        case NOM_DIM_MICRO_COULOMB:
            return 4211;
        case NOM_DIM_X_VOLT:
            return 4256;
        case NOM_DIM_MILLI_VOLT:
            return 4274;
        case NOM_DIM_MICRO_VOLT:
            return 4275;
        case NOM_DIM_X_OHM:
            return 4288;
        case NOM_DIM_OHM_K:
            return 4291;
        case NOM_DIM_X_FARAD:
            return 4352;
        case NOM_DIM_KELVIN:
            return 4384;
        case NOM_DIM_FAHR:
            return 4416;
        case NOM_DIM_X_CANDELA:
            return 4480;
        case NOM_DIM_MILLI_OSM:
            return 4530;
        case NOM_DIM_X_MOLE:
            return 4544;
        case NOM_DIM_MILLI_MOLE:
            return 4562;
        case NOM_DIM_MILLI_EQUIV:
            return 4594;
        case NOM_DIM_MILLI_OSM_PER_L:
            return 4626;
        case NOM_DIM_MILLI_MOLE_PER_L:
            return 4722;
        case NOM_DIM_MICRO_MOLE_PER_L:
            return 4723;
        case NOM_DIM_MILLI_EQUIV_PER_L:
            return 4850;
        case NOM_DIM_MILLI_MOL_PER_KG:
            return 4946;
        case NOM_DIM_MILLI_EQUIV_PER_DAY:
            return 5202;
        case NOM_DIM_X_INTL_UNIT:
            return 5472;
        case NOM_DIM_MILLI_INTL_UNIT:
            return 5490;
        case NOM_DIM_X_INTL_UNIT_PER_CM_CUBE:
            return 5504;
        case NOM_DIM_MILLI_INTL_UNIT_PER_CM_CUBE:
            return 5522;
        case NOM_DIM_X_INTL_UNIT_PER_L:
            return 5568;
        case NOM_DIM_MEGA_INTL_UNIT_PER_L:
            return 5573;
        case NOM_DIM_X_INTL_UNIT_PER_ML:
            return 5600;
        case NOM_DIM_MILLI_INTL_UNIT_PER_ML:
            return 5618;
        case NOM_DIM_X_INTL_UNIT_PER_MIN:
            return 5664;
        case NOM_DIM_MILLI_INTL_UNIT_PER_MIN:
            return 5682;
        case NOM_DIM_X_INTL_UNIT_PER_HR:
            return 5696;
        case NOM_DIM_MILLI_INTL_UNIT_PER_HR:
            return 5714;
        case NOM_DIM_X_INTL_UNIT_PER_KG_PER_MIN:
            return 5792;
        case NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_MIN:
            return 5810;
        case NOM_DIM_X_INTL_UNIT_PER_KG_PER_HR:
            return 5824;
        case NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_HR:
            return 5842;
        case NOM_DIM_MILLI_L_PER_CM_H2O:
            return 5906;
        case NOM_DIM_CM_H2O_PER_L_PER_SEC:
            return 5920;
        case NOM_DIM_MILLI_L_SQ_PER_SEC:
            return 5970;
        case NOM_DIM_CM_H2O_PER_PERCENT:
            return 5984;
        case NOM_DIM_DYNE_SEC_PER_M_SQ_PER_CM_5:
            return 6016;
        case NOM_DIM_DEGC:
            return 6048;
        case NOM_DIM_MILLI_AMP_HR:
            return 6098;
        case NOM_DIM_CM_H2O_PER_L:
            return 6144;
        case NOM_DIM_MM_HG_PER_PERCENT:
            return 6176;
        case NOM_DIM_KILO_PA_PER_PERCENT:
            return 6211;
        case NOM_DIM_X_L_PER_MM_HG:
            return 6272;
        case NOM_DIM_MILLI_L_PER_MM_HG:
            return 6290;
        case NOM_DIM_MILLI_L_PER_DL:
            return 6418;
        case NOM_DIM_DECIBEL:
            return 6432;
        case NOM_DIM_X_G_PER_MILLI_G:
            return 6464;
        case NOM_DIM_MILLI_G_PER_MILLI_G:
            return 6482;
        case NOM_DIM_BEAT_PER_MIN_PER_X_L:
            return 6496;
        case NOM_DIM_BEAT_PER_MIN_PER_MILLI_L:
            return 6514;
        case NOM_DIM_PER_X_L_PER_MIN:
            return 6528;
        case NOM_DIM_X_M_PER_MIN:
            return 6560;
        case NOM_DIM_CENTI_M_PER_MIN:
            return 6577;
        case NOM_DIM_COMPLEX:
            return 61440;
        case NOM_DIM_COUNT:
            return 61441;
        case NOM_DIM_PART:
            return 61442;
        case NOM_DIM_PULS:
            return 61443;
        case NOM_DIM_UV_PP:
            return 61444;
        case NOM_DIM_UV_SQ:
            return 61445;
        case NOM_DIM_LUMEN:
            return 61447;
        case NOM_DIM_LB_PER_INCH_SQ:
            return 61448;
        case NOM_DIM_MM_HG_PER_SEC:
            return 61449;
        case NOM_DIM_ML_PER_SEC:
            return 61450;
        case NOM_DIM_BEAT_PER_MIN_PER_ML_C:
            return 61451;
        case NOM_DIM_X_JOULE_PER_DAY:
            return 61536;
        case NOM_DIM_KILO_JOULE_PER_DAY:
            return 61539;
        case NOM_DIM_MEGA_JOULE_PER_DAY:
            return 61540;
        case NOM_DIM_X_CALORIE:
            return 61568;
        case NOM_DIM_KILO_CALORIE:
            return 61571;
        case NOM_DIM_MEGA_CALORIE:
            return 61572;
        case NOM_DIM_X_CALORIE_PER_DAY:
            return 61600;
        case NOM_DIM_KILO_CALORIE_PER_DAY:
            return 61603;
        case NOM_DIM_MEGA_CALORIE_PER_DAY:
            return 61604;
        case NOM_DIM_X_CALORIE_PER_ML:
            return 61632;
        case NOM_DIM_KILO_CALORIE_PER_ML:
            return 61635;
        case NOM_DIM_X_JOULE_PER_ML:
            return 61664;
        case NOM_DIM_KILO_JOULE_PER_ML:
            return 61667;
        case NOM_DIM_X_REV_PER_MIN:
            return 61696;
        case NOM_DIM_PER_L_PER_MIN_PER_KG:
            return 61728;
        case NOM_DIM_X_L_PER_MILLI_BAR:
            return 61760;
        case NOM_DIM_MILLI_L_PER_MILLI_BAR:
            return 61778;
        case NOM_DIM_X_L_PER_KG_PER_HR:
            return 61792;
        case NOM_DIM_MILLI_L_PER_KG_PER_HR:
            return 61810;
        case NOM_DIM_X_BAR_PER_LITER_PER_SEC:
            return 61824;
        case NOM_DIM_MILLI_BAR_PER_LITER_PER_SEC:
            return 61842;
        case NOM_DIM_X_BAR_PER_LITER:
            return 61856;
        case NOM_DIM_MILLI_BAR_PER_LITER:
            return 61874;
        case NOM_DIM_VOLT_PER_MILLI_VOLT:
            return 61888;
        case NOM_DIM_CM_H2O_PER_MICRO_VOLT:
            return 61920;
        case NOM_DIM_X_JOULE_PER_LITER:
            return 61952;
        case NOM_DIM_X_L_PER_BAR:
            return 61984;
        case NOM_DIM_X_M_PER_MILLI_VOLT:
            return 62016;
        case NOM_DIM_MILLI_M_PER_MILLI_VOLT:
            return 62034;
        case NOM_DIM_X_L_PER_MIN_PER_KG:
            return 62048;
        case NOM_DIM_MILLI_L_PER_MIN_PER_KG:
            return 62066;
        case NOM_DIM_X_PASCAL_PER_L_PER_SEC:
            return 62080;
        case NOM_DIM_HECTO_PASCAL_PER_L_PER_SEC:
            return 62082;
        case NOM_DIM_KILO_PASCAL_PER_L_PER_SEC:
            return 62083;
        case NOM_DIM_MILLI_L_PER_X_PASCAL:
            return 62112;
        case NOM_DIM_MILLI_L_PER_HECTO_PASCAL:
            return 62114;
        case NOM_DIM_MILLI_L_PER_KILO_PASCAL:
            return 62115;
        case NOM_DIM_MM_HG_PER_X_L_PER_SEC:
            return 62144;
        default:
            throw new IllegalArgumentException("Unknown Object Class:"+this);
        }
    }
    public final static UnitCode valueOf(int s) {
        switch(s) {
        case 0:
            return NOM_DIM_NOS;
        case 2:
            return NOM_DIM_DIV;
        case 512:
            return NOM_DIM_DIMLESS;
        case 544:
            return NOM_DIM_PERCENT;
        case 576:
            return NOM_DIM_PARTS_PER_THOUSAND;
        case 608:
            return NOM_DIM_PARTS_PER_MILLION;
        case 672:
            return NOM_DIM_PARTS_PER_BILLION;
        case 704:
            return NOM_DIM_PARTS_PER_TRILLION;
        case 864:
            return NOM_DIM_X_MOLE_PER_MOLE;
        case 992:
            return NOM_DIM_PH;
        case 1024:
            return NOM_DIM_DROP;
        case 1056:
            return NOM_DIM_RBC;
        case 1088:
            return NOM_DIM_BEAT;
        case 1120:
            return NOM_DIM_BREATH;
        case 1152:
            return NOM_DIM_CELL;
        case 1184:
            return NOM_DIM_COUGH;
        case 1216:
            return NOM_DIM_SIGH;
        case 1248:
            return NOM_DIM_PCT_PCV;
        case 1280:
            return NOM_DIM_X_M;
        case 1297:
            return NOM_DIM_CENTI_M;
        case 1298:
            return NOM_DIM_MILLI_M;
        case 1299:
            return NOM_DIM_MICRO_M;
        case 1376:
            return NOM_DIM_X_INCH;
        case 1426:
            return NOM_DIM_MILLI_L_PER_M_SQ;
        case 1440:
            return NOM_DIM_PER_X_M;
        case 1458:
            return NOM_DIM_PER_MILLI_M;
        case 1472:
            return NOM_DIM_SQ_X_M;
        case 1504:
            return NOM_DIM_SQ_X_INCH;
        case 1568:
            return NOM_DIM_CUBIC_X_M;
        case 1585:
            return NOM_DIM_CUBIC_CENTI_M;
        case 1586:
            return NOM_DIM_CUBIC_MILLI_M;
        case 1600:
            return NOM_DIM_X_L;
        case 1618:
            return NOM_DIM_MILLI_L;
        case 1650:
            return NOM_DIM_MILLI_L_PER_BREATH;
        case 1681:
            return NOM_DIM_PER_CUBIC_CENTI_M;
        case 1682:
            return NOM_DIM_PER_CUBIC_MILLI_M;
        case 1696:
            return NOM_DIM_PER_X_L;
        case 1715:
            return NOM_DIM_PER_MICRO_L;
        case 1716:
            return NOM_DIM_PER_NANO_LITER;
        case 1728:
            return NOM_DIM_X_G;
        case 1731:
            return NOM_DIM_KILO_G;
        case 1746:
            return NOM_DIM_MILLI_G;
        case 1747:
            return NOM_DIM_MICRO_G;
        case 1748:
            return NOM_DIM_NANO_G;
        case 1760:
            return NOM_DIM_X_LB;
        case 1792:
            return NOM_DIM_X_OZ;
        case 1824:
            return NOM_DIM_PER_X_G;
        case 1856:
            return NOM_DIM_X_G_M;
        case 1859:
            return NOM_DIM_KILO_G_M;
        case 1888:
            return NOM_DIM_X_G_M_PER_M_SQ;
        case 1891:
            return NOM_DIM_KILO_G_M_PER_M_SQ;
        case 1923:
            return NOM_DIM_KILO_G_M_SQ;
        case 1955:
            return NOM_DIM_KG_PER_M_SQ;
        case 1987:
            return NOM_DIM_KILO_G_PER_M_CUBE;
        case 2016:
            return NOM_DIM_X_G_PER_CM_CUBE;
        case 2034:
            return NOM_DIM_MILLI_G_PER_CM_CUBE;
        case 2035:
            return NOM_DIM_MICRO_G_PER_CM_CUBE;
        case 2036:
            return NOM_DIM_NANO_G_PER_CM_CUBE;
        case 2048:
            return NOM_DIM_X_G_PER_L;
        case 2066:
            return NOM_DIM_MILLI_G_PER_L;
        case 2067:
            return NOM_DIM_MICRO_G_PER_L;
        case 2068:
            return NOM_DIM_NANO_G_PER_L;
        case 2112:
            return NOM_DIM_X_G_PER_DL;
        case 2130:
            return NOM_DIM_MILLI_G_PER_DL;
        case 2131:
            return NOM_DIM_MICRO_G_PER_DL;
        case 2144:
            return NOM_DIM_X_G_PER_ML;
        case 2162:
            return NOM_DIM_MILLI_G_PER_ML;
        case 2163:
            return NOM_DIM_MICRO_G_PER_ML;
        case 2164:
            return NOM_DIM_NANO_G_PER_ML;
        case 2165:
            return NOM_DIM_PICO_G_PER_ML;
        case 2176:
            return NOM_DIM_SEC;
        case 2194:
            return NOM_DIM_MILLI_SEC;
        case 2195:
            return NOM_DIM_MICRO_SEC;
        case 2208:
            return NOM_DIM_MIN;
        case 2240:
            return NOM_DIM_HR;
        case 2272:
            return NOM_DIM_DAY;
        case 2304:
            return NOM_DIM_WEEKS;
        case 2336:
            return NOM_DIM_MON;
        case 2368:
            return NOM_DIM_YR;
        case 2400:
            return NOM_DIM_TOD;
        case 2432:
            return NOM_DIM_DATE;
        case 2464:
            return NOM_DIM_PER_X_SEC;
        case 2496:
            return NOM_DIM_HZ;
        case 2528:
            return NOM_DIM_PER_MIN;
        case 2560:
            return NOM_DIM_PER_HR;
        case 2592:
            return NOM_DIM_PER_DAY;
        case 2624:
            return NOM_DIM_PER_WK;
        case 2656:
            return NOM_DIM_PER_MO;
        case 2688:
            return NOM_DIM_PER_YR;
        case 2720:
            return NOM_DIM_BEAT_PER_MIN;
        case 2752:
            return NOM_DIM_PULS_PER_MIN;
        case 2784:
            return NOM_DIM_RESP_PER_MIN;
        case 2816:
            return NOM_DIM_X_M_PER_SEC;
        case 2834:
            return NOM_DIM_MILLI_M_PER_SEC;
        case 2848:
            return NOM_DIM_X_L_PER_MIN_PER_M_SQ;
        case 2866:
            return NOM_DIM_MILLI_L_PER_MIN_PER_M_SQ;
        case 2880:
            return NOM_DIM_SQ_X_M_PER_SEC;
        case 2897:
            return NOM_DIM_SQ_CENTI_M_PER_SEC;
        case 2912:
            return NOM_DIM_CUBIC_X_M_PER_SEC;
        case 2929:
            return NOM_DIM_CUBIC_CENTI_M_PER_SEC;
        case 3040:
            return NOM_DIM_X_L_PER_SEC;
        case 3072:
            return NOM_DIM_X_L_PER_MIN;
        case 3088:
            return NOM_DIM_DECI_L_PER_MIN;
        case 3090:
            return NOM_DIM_MILLI_L_PER_MIN;
        case 3104:
            return NOM_DIM_X_L_PER_HR;
        case 3122:
            return NOM_DIM_MILLI_L_PER_HR;
        case 3136:
            return NOM_DIM_X_L_PER_DAY;
        case 3154:
            return NOM_DIM_MILLI_L_PER_DAY;
        case 3186:
            return NOM_DIM_MILLI_L_PER_KG;
        case 3299:
            return NOM_DIM_KILO_G_PER_SEC;
        case 3328:
            return NOM_DIM_X_G_PER_MIN;
        case 3331:
            return NOM_DIM_KILO_G_PER_MIN;
        case 3346:
            return NOM_DIM_MILLI_G_PER_MIN;
        case 3347:
            return NOM_DIM_MICRO_G_PER_MIN;
        case 3348:
            return NOM_DIM_NANO_G_PER_MIN;
        case 3360:
            return NOM_DIM_X_G_PER_HR;
        case 3363:
            return NOM_DIM_KILO_G_PER_HR;
        case 3378:
            return NOM_DIM_MILLI_G_PER_HR;
        case 3379:
            return NOM_DIM_MICRO_G_PER_HR;
        case 3380:
            return NOM_DIM_NANO_G_PER_HR;
        case 3395:
            return NOM_DIM_KILO_G_PER_DAY;
        case 3456:
            return NOM_DIM_X_G_PER_KG_PER_MIN;
        case 3474:
            return NOM_DIM_MILLI_G_PER_KG_PER_MIN;
        case 3475:
            return NOM_DIM_MICRO_G_PER_KG_PER_MIN;
        case 3476:
            return NOM_DIM_NANO_G_PER_KG_PER_MIN;
        case 3488:
            return NOM_DIM_X_G_PER_KG_PER_HR;
        case 3506:
            return NOM_DIM_MILLI_G_PER_KG_PER_HR;
        case 3507:
            return NOM_DIM_MICRO_G_PER_KG_PER_HR;
        case 3508:
            return NOM_DIM_NANO_G_PER_KG_PER_HR;
        case 3555:
            return NOM_DIM_KILO_G_PER_L_SEC;
        case 3683:
            return NOM_DIM_KILO_G_PER_M_PER_SEC;
        case 3715:
            return NOM_DIM_KILO_G_M_PER_SEC;
        case 3744:
            return NOM_DIM_X_NEWTON_SEC;
        case 3776:
            return NOM_DIM_X_NEWTON;
        case 3840:
            return NOM_DIM_X_PASCAL;
        case 3842:
            return NOM_DIM_HECTO_PASCAL;
        case 3843:
            return NOM_DIM_KILO_PASCAL;
        case 3872:
            return NOM_DIM_MMHG;
        case 3904:
            return NOM_DIM_CM_H2O;
        case 3954:
            return NOM_DIM_MILLI_BAR;
        case 3968:
            return NOM_DIM_X_JOULES;
        case 4000:
            return NOM_DIM_EVOLT;
        case 4032:
            return NOM_DIM_X_WATT;
        case 4050:
            return NOM_DIM_MILLI_WATT;
        case 4052:
            return NOM_DIM_NANO_WATT;
        case 4053:
            return NOM_DIM_PICO_WATT;
        case 4128:
            return NOM_DIM_X_DYNE_PER_SEC_PER_CM5;
        case 4160:
            return NOM_DIM_X_AMPS;
        case 4178:
            return NOM_DIM_MILLI_AMPS;
        case 4192:
            return NOM_DIM_X_COULOMB;
        case 4211:
            return NOM_DIM_MICRO_COULOMB;
        case 4256:
            return NOM_DIM_X_VOLT;
        case 4274:
            return NOM_DIM_MILLI_VOLT;
        case 4275:
            return NOM_DIM_MICRO_VOLT;
        case 4288:
            return NOM_DIM_X_OHM;
        case 4291:
            return NOM_DIM_OHM_K;
        case 4352:
            return NOM_DIM_X_FARAD;
        case 4384:
            return NOM_DIM_KELVIN;
        case 4416:
            return NOM_DIM_FAHR;
        case 4480:
            return NOM_DIM_X_CANDELA;
        case 4530:
            return NOM_DIM_MILLI_OSM;
        case 4544:
            return NOM_DIM_X_MOLE;
        case 4562:
            return NOM_DIM_MILLI_MOLE;
        case 4594:
            return NOM_DIM_MILLI_EQUIV;
        case 4626:
            return NOM_DIM_MILLI_OSM_PER_L;
        case 4722:
            return NOM_DIM_MILLI_MOLE_PER_L;
        case 4723:
            return NOM_DIM_MICRO_MOLE_PER_L;
        case 4850:
            return NOM_DIM_MILLI_EQUIV_PER_L;
        case 4946:
            return NOM_DIM_MILLI_MOL_PER_KG;
        case 5202:
            return NOM_DIM_MILLI_EQUIV_PER_DAY;
        case 5472:
            return NOM_DIM_X_INTL_UNIT;
        case 5490:
            return NOM_DIM_MILLI_INTL_UNIT;
        case 5504:
            return NOM_DIM_X_INTL_UNIT_PER_CM_CUBE;
        case 5522:
            return NOM_DIM_MILLI_INTL_UNIT_PER_CM_CUBE;
        case 5568:
            return NOM_DIM_X_INTL_UNIT_PER_L;
        case 5573:
            return NOM_DIM_MEGA_INTL_UNIT_PER_L;
        case 5600:
            return NOM_DIM_X_INTL_UNIT_PER_ML;
        case 5618:
            return NOM_DIM_MILLI_INTL_UNIT_PER_ML;
        case 5664:
            return NOM_DIM_X_INTL_UNIT_PER_MIN;
        case 5682:
            return NOM_DIM_MILLI_INTL_UNIT_PER_MIN;
        case 5696:
            return NOM_DIM_X_INTL_UNIT_PER_HR;
        case 5714:
            return NOM_DIM_MILLI_INTL_UNIT_PER_HR;
        case 5792:
            return NOM_DIM_X_INTL_UNIT_PER_KG_PER_MIN;
        case 5810:
            return NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_MIN;
        case 5824:
            return NOM_DIM_X_INTL_UNIT_PER_KG_PER_HR;
        case 5842:
            return NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_HR;
        case 5906:
            return NOM_DIM_MILLI_L_PER_CM_H2O;
        case 5920:
            return NOM_DIM_CM_H2O_PER_L_PER_SEC;
        case 5970:
            return NOM_DIM_MILLI_L_SQ_PER_SEC;
        case 5984:
            return NOM_DIM_CM_H2O_PER_PERCENT;
        case 6016:
            return NOM_DIM_DYNE_SEC_PER_M_SQ_PER_CM_5;
        case 6048:
            return NOM_DIM_DEGC;
        case 6098:
            return NOM_DIM_MILLI_AMP_HR;
        case 6144:
            return NOM_DIM_CM_H2O_PER_L;
        case 6176:
            return NOM_DIM_MM_HG_PER_PERCENT;
        case 6211:
            return NOM_DIM_KILO_PA_PER_PERCENT;
        case 6272:
            return NOM_DIM_X_L_PER_MM_HG;
        case 6290:
            return NOM_DIM_MILLI_L_PER_MM_HG;
        case 6418:
            return NOM_DIM_MILLI_L_PER_DL;
        case 6432:
            return NOM_DIM_DECIBEL;
        case 6464:
            return NOM_DIM_X_G_PER_MILLI_G;
        case 6482:
            return NOM_DIM_MILLI_G_PER_MILLI_G;
        case 6496:
            return NOM_DIM_BEAT_PER_MIN_PER_X_L;
        case 6514:
            return NOM_DIM_BEAT_PER_MIN_PER_MILLI_L;
        case 6528:
            return NOM_DIM_PER_X_L_PER_MIN;
        case 6560:
            return NOM_DIM_X_M_PER_MIN;
        case 6577:
            return NOM_DIM_CENTI_M_PER_MIN;
        case 61440:
            return NOM_DIM_COMPLEX;
        case 61441:
            return NOM_DIM_COUNT;
        case 61442:
            return NOM_DIM_PART;
        case 61443:
            return NOM_DIM_PULS;
        case 61444:
            return NOM_DIM_UV_PP;
        case 61445:
            return NOM_DIM_UV_SQ;
        case 61447:
            return NOM_DIM_LUMEN;
        case 61448:
            return NOM_DIM_LB_PER_INCH_SQ;
        case 61449:
            return NOM_DIM_MM_HG_PER_SEC;
        case 61450:
            return NOM_DIM_ML_PER_SEC;
        case 61451:
            return NOM_DIM_BEAT_PER_MIN_PER_ML_C;
        case 61536:
            return NOM_DIM_X_JOULE_PER_DAY;
        case 61539:
            return NOM_DIM_KILO_JOULE_PER_DAY;
        case 61540:
            return NOM_DIM_MEGA_JOULE_PER_DAY;
        case 61568:
            return NOM_DIM_X_CALORIE;
        case 61571:
            return NOM_DIM_KILO_CALORIE;
        case 61572:
            return NOM_DIM_MEGA_CALORIE;
        case 61600:
            return NOM_DIM_X_CALORIE_PER_DAY;
        case 61603:
            return NOM_DIM_KILO_CALORIE_PER_DAY;
        case 61604:
            return NOM_DIM_MEGA_CALORIE_PER_DAY;
        case 61632:
            return NOM_DIM_X_CALORIE_PER_ML;
        case 61635:
            return NOM_DIM_KILO_CALORIE_PER_ML;
        case 61664:
            return NOM_DIM_X_JOULE_PER_ML;
        case 61667:
            return NOM_DIM_KILO_JOULE_PER_ML;
        case 61696:
            return NOM_DIM_X_REV_PER_MIN;
        case 61728:
            return NOM_DIM_PER_L_PER_MIN_PER_KG;
        case 61760:
            return NOM_DIM_X_L_PER_MILLI_BAR;
        case 61778:
            return NOM_DIM_MILLI_L_PER_MILLI_BAR;
        case 61792:
            return NOM_DIM_X_L_PER_KG_PER_HR;
        case 61810:
            return NOM_DIM_MILLI_L_PER_KG_PER_HR;
        case 61824:
            return NOM_DIM_X_BAR_PER_LITER_PER_SEC;
        case 61842:
            return NOM_DIM_MILLI_BAR_PER_LITER_PER_SEC;
        case 61856:
            return NOM_DIM_X_BAR_PER_LITER;
        case 61874:
            return NOM_DIM_MILLI_BAR_PER_LITER;
        case 61888:
            return NOM_DIM_VOLT_PER_MILLI_VOLT;
        case 61920:
            return NOM_DIM_CM_H2O_PER_MICRO_VOLT;
        case 61952:
            return NOM_DIM_X_JOULE_PER_LITER;
        case 61984:
            return NOM_DIM_X_L_PER_BAR;
        case 62016:
            return NOM_DIM_X_M_PER_MILLI_VOLT;
        case 62034:
            return NOM_DIM_MILLI_M_PER_MILLI_VOLT;
        case 62048:
            return NOM_DIM_X_L_PER_MIN_PER_KG;
        case 62066:
            return NOM_DIM_MILLI_L_PER_MIN_PER_KG;
        case 62080:
            return NOM_DIM_X_PASCAL_PER_L_PER_SEC;
        case 62082:
            return NOM_DIM_HECTO_PASCAL_PER_L_PER_SEC;
        case 62083:
            return NOM_DIM_KILO_PASCAL_PER_L_PER_SEC;
        case 62112:
            return NOM_DIM_MILLI_L_PER_X_PASCAL;
        case 62114:
            return NOM_DIM_MILLI_L_PER_HECTO_PASCAL;
        case 62115:
            return NOM_DIM_MILLI_L_PER_KILO_PASCAL;
        case 62144:
            return NOM_DIM_MM_HG_PER_X_L_PER_SEC;
        default:
        	return null;
        }
    }
}
