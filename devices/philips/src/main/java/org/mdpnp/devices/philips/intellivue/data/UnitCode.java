package org.mdpnp.devices.philips.intellivue.data;


import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum UnitCode implements EnumMessage<UnitCode>, OrdinalEnum.IntType {
    /**
     * (/)
     * Hex: 0x00
     * Dec: 0
     */
    NOM_DIM_NOS(0),

    /**
     * (nodimension)
     * Hex: 0x02
     * Dec: 2
     */
    NOM_DIM_DIV(2),

    /**
     * (percentage)
     * Hex: 0x200
     * Dec: 512
     */
    NOM_DIM_DIMLESS(512),

    /**
     * (partsperthousand)
     * Hex: 0x220
     * Dec: 544
     */
    NOM_DIM_PERCENT(544),

    /**
     * (partspermillion)
     * Hex: 0x240
     * Dec: 576
     */
    NOM_DIM_PARTS_PER_THOUSAND(576),

    /**
     * (molepermole)
     * Hex: 0x260
     * Dec: 608
     */
    NOM_DIM_PARTS_PER_MILLION(608),

    /**
     * (partspertrillion)
     * Hex: 0x2A0
     * Dec: 672
     */
    NOM_DIM_PARTS_PER_BILLION(672),

    /**
     * (pH)
     * Hex: 0x2C0
     * Dec: 704
     */
    NOM_DIM_PARTS_PER_TRILLION(704),

    /**
     * (partsperbillion)
     * Hex: 0x360
     * Dec: 864
     */
    NOM_DIM_X_MOLE_PER_MOLE(864),

    /**
     * (vitalsignscountdrop)
     * Hex: 0x3E0
     * Dec: 992
     */
    NOM_DIM_PH(992),

    /**
     * (vitalsignscountredbloodcells)
     * Hex: 0x400
     * Dec: 1024
     */
    NOM_DIM_DROP(1024),

    /**
     * (vitalsignscountbeat)
     * Hex: 0x420
     * Dec: 1056
     */
    NOM_DIM_RBC(1056),

    /**
     * (vitalsignscountbreath)
     * Hex: 0x440
     * Dec: 1088
     */
    NOM_DIM_BEAT(1088),

    /**
     * (vitalsignscountcells)
     * Hex: 0x460
     * Dec: 1120
     */
    NOM_DIM_BREATH(1120),

    /**
     * (vitalsignscountcough)
     * Hex: 0x480
     * Dec: 1152
     */
    NOM_DIM_CELL(1152),

    /**
     * (vitalsignscountsigh)
     * Hex: 0x4A0
     * Dec: 1184
     */
    NOM_DIM_COUGH(1184),

    /**
     * (percentofpackedcellvolume)
     * Hex: 0x4C0
     * Dec: 1216
     */
    NOM_DIM_SIGH(1216),

    /**
     * (meter)
     * Hex: 0x4E0
     * Dec: 1248
     */
    NOM_DIM_PCT_PCV(1248),

    /**
     * (centimeter)
     * Hex: 0x500
     * Dec: 1280
     */
    NOM_DIM_X_M(1280),

    /**
     * (millimeter)
     * Hex: 0x511
     * Dec: 1297
     */
    NOM_DIM_CENTI_M(1297),

    /**
     * (micro-meter)
     * Hex: 0x512
     * Dec: 1298
     */
    NOM_DIM_MILLI_M(1298),

    /**
     * (inch)
     * Hex: 0x513
     * Dec: 1299
     */
    NOM_DIM_MICRO_M(1299),

    /**
     * (usede.g.forSIandITBVI)
     * Hex: 0x560
     * Dec: 1376
     */
    NOM_DIM_X_INCH(1376),

    /**
     * (permeter)
     * Hex: 0x592
     * Dec: 1426
     */
    NOM_DIM_MILLI_L_PER_M_SQ(1426),

    /**
     * (permillimeter)
     * Hex: 0x5A0
     * Dec: 1440
     */
    NOM_DIM_PER_X_M(1440),

    /**
     * (usede.g.forBSAcalculation)
     * Hex: 0x5B2
     * Dec: 1458
     */
    NOM_DIM_PER_MILLI_M(1458),

    /**
     * (usede.g.forBSAcalculation)
     * Hex: 0x5C0
     * Dec: 1472
     */
    NOM_DIM_SQ_X_M(1472),

    /**
     * (cubicmeter)
     * Hex: 0x5E0
     * Dec: 1504
     */
    NOM_DIM_SQ_X_INCH(1504),

    /**
     * (cubiccentimeter)
     * Hex: 0x620
     * Dec: 1568
     */
    NOM_DIM_CUBIC_X_M(1568),

    /**
     * (liter)
     * Hex: 0x631
     * Dec: 1585
     */
    NOM_DIM_CUBIC_CENTI_M(1585),

    /**
     * (intl.unitsperliter)
     * Hex: 0x632
     * Dec: 1586
     */
    NOM_DIM_CUBIC_MILLI_M(1586),

    /**
     * (milli-litersusede.g.forEVLWITBVSV)
     * Hex: 0x640
     * Dec: 1600
     */
    NOM_DIM_X_L(1600),

    /**
     * (milli-literperbreath)
     * Hex: 0x652
     * Dec: 1618
     */
    NOM_DIM_MILLI_L(1618),

    /**
     * (percubiccentimeter)
     * Hex: 0x672
     * Dec: 1650
     */
    NOM_DIM_MILLI_L_PER_BREATH(1650),

    /**
     * (perliter)
     * Hex: 0x691
     * Dec: 1681
     */
    NOM_DIM_PER_CUBIC_CENTI_M(1681),

    /**
     * (cubicmilli-meter)
     * Hex: 0x692
     * Dec: 1682
     */
    NOM_DIM_PER_CUBIC_MILLI_M(1682),

    /**
     * (pernano-liter)
     * Hex: 0x6A0
     * Dec: 1696
     */
    NOM_DIM_PER_X_L(1696),

    /**
     * (-)
     * Hex: 0x6B3
     * Dec: 1715
     */
    NOM_DIM_PER_MICRO_L(1715),

    /**
     * (gram)
     * Hex: 0x6B4
     * Dec: 1716
     */
    NOM_DIM_PER_NANO_LITER(1716),

    /**
     * (kilo-gram)
     * Hex: 0x6C0
     * Dec: 1728
     */
    NOM_DIM_X_G(1728),

    /**
     * (milli-gram)
     * Hex: 0x6C3
     * Dec: 1731
     */
    NOM_DIM_KILO_G(1731),

    /**
     * (micro-gram)
     * Hex: 0x6D2
     * Dec: 1746
     */
    NOM_DIM_MILLI_G(1746),

    /**
     * (nono-gram)
     * Hex: 0x6D3
     * Dec: 1747
     */
    NOM_DIM_MICRO_G(1747),

    /**
     * (pound)
     * Hex: 0x6D4
     * Dec: 1748
     */
    NOM_DIM_NANO_G(1748),
    

    /**
     * (ounce)
     * Hex: 0x6E0
     * Dec: 1760
     */
    NOM_DIM_X_LB(1760),

    /**
     * (pergram)
     * Hex: 0x700
     * Dec: 1792
     */
    NOM_DIM_X_OZ(1792),

    /**
     * (usede.g.forLVSWRVSW)
     * Hex: 0x720
     * Dec: 1824
     */
    NOM_DIM_PER_X_G(1824),

    /**
     * (usede.g.forRCWLCW)
     * Hex: 0x740
     * Dec: 1856
     */
    NOM_DIM_X_G_M(1856),

    /**
     * (usede.g.forLVSWIandRVSWI)
     * Hex: 0x743
     * Dec: 1859
     */
    NOM_DIM_KILO_G_M(1859),

    /**
     * (usede.g.forLCWIandRCWI)
     * Hex: 0x760
     * Dec: 1888
     */
    NOM_DIM_X_G_M_PER_M_SQ(1888),

    /**
     * (grammetersquared)
     * Hex: 0x763
     * Dec: 1891
     */
    NOM_DIM_KILO_G_M_PER_M_SQ(1891),

    /**
     * (kilo-grampersquaremeter)
     * Hex: 0x783
     * Dec: 1923
     */
    NOM_DIM_KILO_G_M_SQ(1923),

    /**
     * (kilo-grampercubicmeter)
     * Hex: 0x7A3
     * Dec: 1955
     */
    NOM_DIM_KG_PER_M_SQ(1955),

    /**
     * (grampercubicmeter)
     * Hex: 0x7C3
     * Dec: 1987
     */
    NOM_DIM_KILO_G_PER_M_CUBE(1987),

    /**
     * (milli-grampercubiccentimeter)
     * Hex: 0x7E0
     * Dec: 2016
     */
    NOM_DIM_X_G_PER_CM_CUBE(2016),

    /**
     * (micro-grampercubiccentimeter)
     * Hex: 0x7F2
     * Dec: 2034
     */
    NOM_DIM_MILLI_G_PER_CM_CUBE(2034),

    /**
     * (nano-grampercubiccentimeter)
     * Hex: 0x7F3
     * Dec: 2035
     */
    NOM_DIM_MICRO_G_PER_CM_CUBE(2035),

    /**
     * (gramperliter)
     * Hex: 0x7F4
     * Dec: 2036
     */
    NOM_DIM_NANO_G_PER_CM_CUBE(2036),

    /**
     * (usede.g.forHb)
     * Hex: 0x800
     * Dec: 2048
     */
    NOM_DIM_X_G_PER_L(2048),

    /**
     * (micro-liter)
     * Hex: 0x812
     * Dec: 2066
     */
    NOM_DIM_MILLI_G_PER_L(2066),

    /**
     * (nano-gramperliter)
     * Hex: 0x813
     * Dec: 2067
     */
    NOM_DIM_MICRO_G_PER_L(2067),

    /**
     * (percubicmillimeter)
     * Hex: 0x814
     * Dec: 2068
     */
    NOM_DIM_NANO_G_PER_L(2068),

    /**
     * (milli-gramperdeciliter)
     * Hex: 0x840
     * Dec: 2112
     */
    NOM_DIM_X_G_PER_DL(2112),

    /**
     * (grampermilli-liter)
     * Hex: 0x852
     * Dec: 2130
     */
    NOM_DIM_MILLI_G_PER_DL(2130),

    /**
     * (milli-gramperliter)
     * Hex: 0x853
     * Dec: 2131
     */
    NOM_DIM_MICRO_G_PER_DL(2131),

    /**
     * (milli-grampermilli-liter)
     * Hex: 0x860
     * Dec: 2144
     */
    NOM_DIM_X_G_PER_ML(2144),

    /**
     * (micro-grampermilli-liter)
     * Hex: 0x872
     * Dec: 2162
     */
    NOM_DIM_MILLI_G_PER_ML(2162),

    /**
     * (nano-grampermilli-liter)
     * Hex: 0x873
     * Dec: 2163
     */
    NOM_DIM_MICRO_G_PER_ML(2163),

    /**
     * (seconds)
     * Hex: 0x874
     * Dec: 2164
     */
    NOM_DIM_NANO_G_PER_ML(2164),

    /**
     * (micro-gramperliter)
     * Hex: 0x875
     * Dec: 2165
     */
    NOM_DIM_PICO_G_PER_ML(2165),

    /**
     * (milli-seconds)
     * Hex: 0x880
     * Dec: 2176
     */
    NOM_DIM_SEC(2176),

    /**
     * (micro-seconds)
     * Hex: 0x892
     * Dec: 2194
     */
    NOM_DIM_MILLI_SEC(2194),

    /**
     * (minutes)
     * Hex: 0x893
     * Dec: 2195
     */
    NOM_DIM_MICRO_SEC(2195),

    /**
     * (hours)
     * Hex: 0x8A0
     * Dec: 2208
     */
    NOM_DIM_MIN(2208),

    /**
     * (days)
     * Hex: 0x8C0
     * Dec: 2240
     */
    NOM_DIM_HR(2240),

    /**
     * (weeks)
     * Hex: 0x8E0
     * Dec: 2272
     */
    NOM_DIM_DAY(2272),

    /**
     * (months)
     * Hex: 0x900
     * Dec: 2304
     */
    NOM_DIM_WEEKS(2304),

    /**
     * (years)
     * Hex: 0x920
     * Dec: 2336
     */
    NOM_DIM_MON(2336),

    /**
     * (timeofday)
     * Hex: 0x940
     * Dec: 2368
     */
    NOM_DIM_YR(2368),

    /**
     * (date)
     * Hex: 0x960
     * Dec: 2400
     */
    NOM_DIM_TOD(2400),

    /**
     * (persecond)
     * Hex: 0x980
     * Dec: 2432
     */
    NOM_DIM_DATE(2432),

    /**
     * (hertz)
     * Hex: 0x9A0
     * Dec: 2464
     */
    NOM_DIM_PER_X_SEC(2464),

    /**
     * (perminuteusede.g.forthePVCcountnumericalvalue)
     * Hex: 0x9C0
     * Dec: 2496
     */
    NOM_DIM_HZ(2496),

    /**
     * (perhour)
     * Hex: 0x9E0
     * Dec: 2528
     */
    NOM_DIM_PER_MIN(2528),

    /**
     * (perday)
     * Hex: 0xA00
     * Dec: 2560
     */
    NOM_DIM_PER_HR(2560),

    /**
     * (perweek)
     * Hex: 0xA20
     * Dec: 2592
     */
    NOM_DIM_PER_DAY(2592),

    /**
     * (permonth)
     * Hex: 0xA40
     * Dec: 2624
     */
    NOM_DIM_PER_WK(2624),

    /**
     * (peryear)
     * Hex: 0xA60
     * Dec: 2656
     */
    NOM_DIM_PER_MO(2656),

    /**
     * (beatsperminuteusede.g.forHR/PULSE)
     * Hex: 0xA80
     * Dec: 2688
     */
    NOM_DIM_PER_YR(2688),

    /**
     * (pulsperminute)
     * Hex: 0xAA0
     * Dec: 2720
     */
    NOM_DIM_BEAT_PER_MIN(2720),

    /**
     * (respirationbreathesperminute)
     * Hex: 0xAC0
     * Dec: 2752
     */
    NOM_DIM_PULS_PER_MIN(2752),

    /**
     * (meterpersecond)
     * Hex: 0xAE0
     * Dec: 2784
     */
    NOM_DIM_RESP_PER_MIN(2784),

    /**
     * (speedforrecordings)
     * Hex: 0xB00
     * Dec: 2816
     */
    NOM_DIM_X_M_PER_SEC(2816),

    /**
     * (usedforCI)
     * Hex: 0xB12
     * Dec: 2834
     */
    NOM_DIM_MILLI_M_PER_SEC(2834),

    /**
     * (usedforDO2IVO2IO2AVI)
     * Hex: 0xB20
     * Dec: 2848
     */
    NOM_DIM_X_L_PER_MIN_PER_M_SQ(2848),

    /**
     * (squaremeterpersecond)
     * Hex: 0xB32
     * Dec: 2866
     */
    NOM_DIM_MILLI_L_PER_MIN_PER_M_SQ(2866),

    /**
     * (squarecentimeterpersecond)
     * Hex: 0xB40
     * Dec: 2880
     */
    NOM_DIM_SQ_X_M_PER_SEC(2880),

    /**
     * (cubicmeterpersecond)
     * Hex: 0xB51
     * Dec: 2897
     */
    NOM_DIM_SQ_CENTI_M_PER_SEC(2897),

    /**
     * (cubiccentimeterpersecond)
     * Hex: 0xB60
     * Dec: 2912
     */
    NOM_DIM_CUBIC_X_M_PER_SEC(2912),

    /**
     * (literpersecond)
     * Hex: 0xB71
     * Dec: 2929
     */
    NOM_DIM_CUBIC_CENTI_M_PER_SEC(2929),

    /**
     * (literperminutes)
     * Hex: 0xBE0
     * Dec: 3040
     */
    NOM_DIM_X_L_PER_SEC(3040),

    /**
     * (deciliterpersecond)
     * Hex: 0xC00
     * Dec: 3072
     */
    NOM_DIM_X_L_PER_MIN(3072),

    /**
     * (usedforDO2VO2ALVENT)
     * Hex: 0xC10
     * Dec: 3088
     */
    NOM_DIM_DECI_L_PER_MIN(3088),

    /**
     * (literperhour)
     * Hex: 0xC12
     * Dec: 3090
     */
    NOM_DIM_MILLI_L_PER_MIN(3090),

    /**
     * (milli-literperhour)
     * Hex: 0xC20
     * Dec: 3104
     */
    NOM_DIM_X_L_PER_HR(3104),

    /**
     * (literperday)
     * Hex: 0xC32
     * Dec: 3122
     */
    NOM_DIM_MILLI_L_PER_HR(3122),

    /**
     * (milli-literperday)
     * Hex: 0xC40
     * Dec: 3136
     */
    NOM_DIM_X_L_PER_DAY(3136),

    /**
     * (usede.g.forEVLWI)
     * Hex: 0xC52
     * Dec: 3154
     */
    NOM_DIM_MILLI_L_PER_DAY(3154),

    /**
     * (kilo-grampersecond)
     * Hex: 0xC72
     * Dec: 3186
     */
    NOM_DIM_MILLI_L_PER_KG(3186),

    /**
     * (gramperminute)
     * Hex: 0xCE3
     * Dec: 3299
     */
    NOM_DIM_KILO_G_PER_SEC(3299),

    /**
     * (kilo-gramperminute)
     * Hex: 0xD00
     * Dec: 3328
     */
    NOM_DIM_X_G_PER_MIN(3328),

    /**
     * (milli-gramperminute)
     * Hex: 0xD03
     * Dec: 3331
     */
    NOM_DIM_KILO_G_PER_MIN(3331),

    /**
     * (micro-gramperminute)
     * Hex: 0xD12
     * Dec: 3346
     */
    NOM_DIM_MILLI_G_PER_MIN(3346),

    /**
     * (nano-gramperminute)
     * Hex: 0xD13
     * Dec: 3347
     */
    NOM_DIM_MICRO_G_PER_MIN(3347),

    /**
     * (gramperhour)
     * Hex: 0xD14
     * Dec: 3348
     */
    NOM_DIM_NANO_G_PER_MIN(3348),

    /**
     * (kilo-gramperhour)
     * Hex: 0xD20
     * Dec: 3360
     */
    NOM_DIM_X_G_PER_HR(3360),

    /**
     * (milli-gramperhour)
     * Hex: 0xD23
     * Dec: 3363
     */
    NOM_DIM_KILO_G_PER_HR(3363),

    /**
     * (micro-gramperhour)
     * Hex: 0xD32
     * Dec: 3378
     */
    NOM_DIM_MILLI_G_PER_HR(3378),

    /**
     * (nano-gramperhour)
     * Hex: 0xD33
     * Dec: 3379
     */
    NOM_DIM_MICRO_G_PER_HR(3379),

    /**
     * (kilo-gramperday)
     * Hex: 0xD34
     * Dec: 3380
     */
    NOM_DIM_NANO_G_PER_HR(3380),

    /**
     * (gramperkilo-gramperminute)
     * Hex: 0xD43
     * Dec: 3395
     */
    NOM_DIM_KILO_G_PER_DAY(3395),

    /**
     * (milli-gramperkilo-gramperminute)
     * Hex: 0xD80
     * Dec: 3456
     */
    NOM_DIM_X_G_PER_KG_PER_MIN(3456),

    /**
     * (micro-gramperkilo-gramperminute)
     * Hex: 0xD92
     * Dec: 3474
     */
    NOM_DIM_MILLI_G_PER_KG_PER_MIN(3474),

    /**
     * (nano-gramperkilo-gramperminute)
     * Hex: 0xD93
     * Dec: 3475
     */
    NOM_DIM_MICRO_G_PER_KG_PER_MIN(3475),

    /**
     * (gramperkilo-gramperhour)
     * Hex: 0xD94
     * Dec: 3476
     */
    NOM_DIM_NANO_G_PER_KG_PER_MIN(3476),

    /**
     * (mili-gramperkilo-gramperhour)
     * Hex: 0xDA0
     * Dec: 3488
     */
    NOM_DIM_X_G_PER_KG_PER_HR(3488),

    /**
     * (micro-gramperkilo-gramperhour)
     * Hex: 0xDB2
     * Dec: 3506
     */
    NOM_DIM_MILLI_G_PER_KG_PER_HR(3506),

    /**
     * (nano-gramperkilo-gramperhour)
     * Hex: 0xDB3
     * Dec: 3507
     */
    NOM_DIM_MICRO_G_PER_KG_PER_HR(3507),

    /**
     * (kilo-gramperliterpersecond)
     * Hex: 0xDB4
     * Dec: 3508
     */
    NOM_DIM_NANO_G_PER_KG_PER_HR(3508),

    /**
     * (kilo-grampermeterpersecond)
     * Hex: 0xDE3
     * Dec: 3555
     */
    NOM_DIM_KILO_G_PER_L_SEC(3555),

    /**
     * (kilo-grammeterpersecond)
     * Hex: 0xE63
     * Dec: 3683
     */
    NOM_DIM_KILO_G_PER_M_PER_SEC(3683),

    /**
     * (newtonseconds)
     * Hex: 0xE83
     * Dec: 3715
     */
    NOM_DIM_KILO_G_M_PER_SEC(3715),

    /**
     * (newton)
     * Hex: 0xEA0
     * Dec: 3744
     */
    NOM_DIM_X_NEWTON_SEC(3744),

    /**
     * (pascal)
     * Hex: 0xEC0
     * Dec: 3776
     */
    NOM_DIM_X_NEWTON(3776),

    /**
     * (hekto-pascal)
     * Hex: 0xF00
     * Dec: 3840
     */
    NOM_DIM_X_PASCAL(3840),

    /**
     * (kilo-pascal)
     * Hex: 0xF02
     * Dec: 3842
     */
    NOM_DIM_HECTO_PASCAL(3842),

    /**
     * (mmmercury)
     * Hex: 0xF03
     * Dec: 3843
     */
    NOM_DIM_KILO_PASCAL(3843),

    /**
     * (centimeterH20)
     * Hex: 0xF20
     * Dec: 3872
     */
    NOM_DIM_MMHG(3872),

    /**
     * (milli-bar)
     * Hex: 0xF40
     * Dec: 3904
     */
    NOM_DIM_CM_H2O(3904),

    /**
     * (Joules)
     * Hex: 0xF72
     * Dec: 3954
     */
    NOM_DIM_MILLI_BAR(3954),

    /**
     * (electronvolts)
     * Hex: 0xF80
     * Dec: 3968
     */
    NOM_DIM_X_JOULES(3968),

    /**
     * (watt)
     * Hex: 0xFA0
     * Dec: 4000
     */
    NOM_DIM_EVOLT(4000),

    /**
     * (milli-watt)
     * Hex: 0xFC0
     * Dec: 4032
     */
    NOM_DIM_X_WATT(4032),

    /**
     * (nano-watt)
     * Hex: 0xFD2
     * Dec: 4050
     */
    NOM_DIM_MILLI_WATT(4050),

    /**
     * (pico-watt)
     * Hex: 0xFD4
     * Dec: 4052
     */
    NOM_DIM_NANO_WATT(4052),

    /**
     * Dyn-sec/cm^5(dynesecondpercm^5)
     * Hex: 0xFD5
     * Dec: 4053
     */
    NOM_DIM_PICO_WATT(4053),

    /**
     * A
     * Hex: 0x1020
     * Dec: 4128
     */
    NOM_DIM_X_DYNE_PER_SEC_PER_CM5(4128),

    /**
     * mA
     * Hex: 0x1040
     * Dec: 4160
     */
    NOM_DIM_X_AMPS(4160),

    /**
     * C
     * Hex: 0x1052
     * Dec: 4178
     */
    NOM_DIM_MILLI_AMPS(4178),

    /**
     * ??C
     * Hex: 0x1060
     * Dec: 4192
     */
    NOM_DIM_X_COULOMB(4192),

    /**
     * V
     * Hex: 0x1073
     * Dec: 4211
     */
    NOM_DIM_MICRO_COULOMB(4211),

    /**
     * mV
     * Hex: 0x10A0
     * Dec: 4256
     */
    NOM_DIM_X_VOLT(4256),

    /**
     * ??V
     * Hex: 0x10B2
     * Dec: 4274
     */
    NOM_DIM_MILLI_VOLT(4274),

    /**
     * Ohm
     * Hex: 0x10B3
     * Dec: 4275
     */
    NOM_DIM_MICRO_VOLT(4275),

    /**
     * kOhm
     * Hex: 0x10C0
     * Dec: 4288
     */
    NOM_DIM_X_OHM(4288),

    /**
     * F
     * Hex: 0x10C3
     * Dec: 4291
     */
    NOM_DIM_OHM_K(4291),

    /**
     * ??K
     * Hex: 0x1100
     * Dec: 4352
     */
    NOM_DIM_X_FARAD(4352),

    /**
     * ??F
     * Hex: 0x1120
     * Dec: 4384
     */
    NOM_DIM_KELVIN(4384),

    /**
     * cd
     * Hex: 0x1140
     * Dec: 4416
     */
    NOM_DIM_FAHR(4416),

    /**
     * mOsm
     * Hex: 0x1180
     * Dec: 4480
     */
    NOM_DIM_X_CANDELA(4480),

    /**
     * mol
     * Hex: 0x11B2
     * Dec: 4530
     */
    NOM_DIM_MILLI_OSM(4530),

    /**
     * mmol
     * Hex: 0x11C0
     * Dec: 4544
     */
    NOM_DIM_X_MOLE(4544),

    /**
     * mEq
     * Hex: 0x11D2
     * Dec: 4562
     */
    NOM_DIM_MILLI_MOLE(4562),

    /**
     * mOsm/l
     * Hex: 0x11F2
     * Dec: 4594
     */
    NOM_DIM_MILLI_EQUIV(4594),

    /**
     * mmol/l
     * Hex: 0x1212
     * Dec: 4626
     */
    NOM_DIM_MILLI_OSM_PER_L(4626),

    /**
     * ??mol/l
     * Hex: 0x1272
     * Dec: 4722
     */
    NOM_DIM_MILLI_MOLE_PER_L(4722),

    /**
     * mEq/l
     * Hex: 0x1273
     * Dec: 4723
     */
    NOM_DIM_MICRO_MOLE_PER_L(4723),

    /**
     * mEq/day
     * Hex: 0x12F2
     * Dec: 4850
     */
    NOM_DIM_MILLI_EQUIV_PER_L(4850),

    /**
     * (micro-gramperdeci-liter)
     * Hex: 0x1352
     * Dec: 4946
     */
    NOM_DIM_MILLI_MOL_PER_KG(4946),

    /**
     * i.u.
     * Hex: 0x1452
     * Dec: 5202
     */
    NOM_DIM_MILLI_EQUIV_PER_DAY(5202),

    /**
     * mi.u.
     * Hex: 0x1560
     * Dec: 5472
     */
    NOM_DIM_X_INTL_UNIT(5472),

    /**
     * i.u./cm3
     * Hex: 0x1572
     * Dec: 5490
     */
    NOM_DIM_MILLI_INTL_UNIT(5490),

    /**
     * mi.u./cm3
     * Hex: 0x1580
     * Dec: 5504
     */
    NOM_DIM_X_INTL_UNIT_PER_CM_CUBE(5504),

    /**
     * i.u./ml
     * Hex: 0x1592
     * Dec: 5522
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_CM_CUBE(5522),

    /**
     * (10^6intl.unitsperliter)
     * Hex: 0x15C0
     * Dec: 5568
     */
    NOM_DIM_X_INTL_UNIT_PER_L(5568),

    /**
     * (moleperkilo-gram)
     * Hex: 0x15C5
     * Dec: 5573
     */
    NOM_DIM_MEGA_INTL_UNIT_PER_L(5573),

    /**
     * (internationalunitperminute)
     * Hex: 0x15E0
     * Dec: 5600
     */
    NOM_DIM_X_INTL_UNIT_PER_ML(5600),

    /**
     * mi.u./min
     * Hex: 0x15F2
     * Dec: 5618
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_ML(5618),

    /**
     * mi.u./ml
     * Hex: 0x1620
     * Dec: 5664
     */
    NOM_DIM_X_INTL_UNIT_PER_MIN(5664),

    /**
     * i.u./hour
     * Hex: 0x1632
     * Dec: 5682
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_MIN(5682),

    /**
     * mi.u./hour
     * Hex: 0x1640
     * Dec: 5696
     */
    NOM_DIM_X_INTL_UNIT_PER_HR(5696),

    /**
     * i.u./kg/min
     * Hex: 0x1652
     * Dec: 5714
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_HR(5714),

    /**
     * mi.u./kg/min(milli-internationalunitperkilo-gramperminute)
     * Hex: 0x16A0
     * Dec: 5792
     */
    NOM_DIM_X_INTL_UNIT_PER_KG_PER_MIN(5792),

    /**
     * i.u./kg/hour(internationalunitperkilo-gramperhour)
     * Hex: 0x16B2
     * Dec: 5810
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_MIN(5810),

    /**
     * mi.u./kg/hour(milli-internationalunitperkilo-gramperhour)
     * Hex: 0x16C0
     * Dec: 5824
     */
    NOM_DIM_X_INTL_UNIT_PER_KG_PER_HR(5824),

    /**
     * ml/cmH2O(milli-literpercentimeterH2O)
     * Hex: 0x16D2
     * Dec: 5842
     */
    NOM_DIM_MILLI_INTL_UNIT_PER_KG_PER_HR(5842),

    /**
     * cmH2O/l/sec(centimeterH2Opersecond)
     * Hex: 0x1712
     * Dec: 5906
     */
    NOM_DIM_MILLI_L_PER_CM_H2O(5906),

    /**
     * ml2/sec
     * Hex: 0x1720
     * Dec: 5920
     */
    NOM_DIM_CM_H2O_PER_L_PER_SEC(5920),

    /**
     * cmH2O/%
     * Hex: 0x1752
     * Dec: 5970
     */
    NOM_DIM_MILLI_L_SQ_PER_SEC(5970),

    /**
     * DS*m2/cm5(usedforSVRIandPVRI)
     * Hex: 0x1760
     * Dec: 5984
     */
    NOM_DIM_CM_H2O_PER_PERCENT(5984),

    /**
     * ??C
     * Hex: 0x1780
     * Dec: 6016
     */
    NOM_DIM_DYNE_SEC_PER_M_SQ_PER_CM_5(6016),

    /**
     * cmH2O/l
     * Hex: 0x17A0
     * Dec: 6048
     */
    NOM_DIM_DEGC(6048),

    /**
     * ml/dl
     * Hex: 0x17D2
     * Dec: 6098
     */
    NOM_DIM_MILLI_AMP_HR(6098),

    /**
     * mmHg/%
     * Hex: 0x1800
     * Dec: 6144
     */
    NOM_DIM_CM_H2O_PER_L(6144),

    /**
     * kPa/%
     * Hex: 0x1820
     * Dec: 6176
     */
    NOM_DIM_MM_HG_PER_PERCENT(6176),

    /**
     * l/mmHg
     * Hex: 0x1843
     * Dec: 6211
     */
    NOM_DIM_KILO_PA_PER_PERCENT(6211),

    /**
     * ml/mmHg
     * Hex: 0x1880
     * Dec: 6272
     */
    NOM_DIM_X_L_PER_MM_HG(6272),

    /**
     * mAh
     * Hex: 0x1892
     * Dec: 6290
     */
    NOM_DIM_MILLI_L_PER_MM_HG(6290),

    /**
     * dB
     * Hex: 0x1912
     * Dec: 6418
     */
    NOM_DIM_MILLI_L_PER_DL(6418),

    /**
     * g/mg
     * Hex: 0x1920
     * Dec: 6432
     */
    NOM_DIM_DECIBEL(6432),

    /**
     * mg/mg
     * Hex: 0x1940
     * Dec: 6464
     */
    NOM_DIM_X_G_PER_MILLI_G(6464),

    /**
     * bpm/l
     * Hex: 0x1952
     * Dec: 6482
     */
    NOM_DIM_MILLI_G_PER_MILLI_G(6482),

    /**
     * bpm/ml
     * Hex: 0x1960
     * Dec: 6496
     */
    NOM_DIM_BEAT_PER_MIN_PER_X_L(6496),

    /**
     * 1/(min*l)
     * Hex: 0x1972
     * Dec: 6514
     */
    NOM_DIM_BEAT_PER_MIN_PER_MILLI_L(6514),

    /**
     * (meterperminute)
     * Hex: 0x1980
     * Dec: 6528
     */
    NOM_DIM_PER_X_L_PER_MIN(6528),

    /**
     * (speedforrecordings)
     * Hex: 0x19A0
     * Dec: 6560
     */
    NOM_DIM_X_M_PER_MIN(6560),

    /**
     * (pico-grampermilli-liter)
     * Hex: 0x19B1
     * Dec: 6577
     */
    NOM_DIM_CENTI_M_PER_MIN(6577),

    /**
     * (countasadimension)
     * Hex: 0xF000
     * Dec: 61440
     */
    NOM_DIM_COMPLEX(61440),

    /**
     * (part)
     * Hex: 0xF001
     * Dec: 61441
     */
    NOM_DIM_COUNT(61441),

    /**
     * (puls)
     * Hex: 0xF002
     * Dec: 61442
     */
    NOM_DIM_PART(61442),

    /**
     * (micro-voltpeaktopeak)
     * Hex: 0xF003
     * Dec: 61443
     */
    NOM_DIM_PULS(61443),

    /**
     * (micor-voltsquare)
     * Hex: 0xF004
     * Dec: 61444
     */
    NOM_DIM_UV_PP(61444),

    /**
     * (lumen)
     * Hex: 0xF005
     * Dec: 61445
     */
    NOM_DIM_UV_SQ(61445),

    /**
     * (poundpersquareinch)
     * Hex: 0xF007
     * Dec: 61447
     */
    NOM_DIM_LUMEN(61447),

    /**
     * (milli-metermercurypersecond)
     * Hex: 0xF008
     * Dec: 61448
     */
    NOM_DIM_LB_PER_INCH_SQ(61448),

    /**
     * (milli-literpersecond)
     * Hex: 0xF009
     * Dec: 61449
     */
    NOM_DIM_MM_HG_PER_SEC(61449),

    /**
     * (beatperminutepermilli-liter)
     * Hex: 0xF00A
     * Dec: 61450
     */
    NOM_DIM_ML_PER_SEC(61450),

    /**
     * (jouleperday)
     * Hex: 0xF00B
     * Dec: 61451
     */
    NOM_DIM_BEAT_PER_MIN_PER_ML_C(61451),

    /**
     * (kilojouleperday)
     * Hex: 0xF060
     * Dec: 61536
     */
    NOM_DIM_X_JOULE_PER_DAY(61536),

    /**
     * (megajouleperday)
     * Hex: 0xF063
     * Dec: 61539
     */
    NOM_DIM_KILO_JOULE_PER_DAY(61539),

    /**
     * (calories)
     * Hex: 0xF064
     * Dec: 61540
     */
    NOM_DIM_MEGA_JOULE_PER_DAY(61540),

    /**
     * (kilocalories)
     * Hex: 0xF080
     * Dec: 61568
     */
    NOM_DIM_X_CALORIE(61568),

    /**
     * (millioncalories)
     * Hex: 0xF083
     * Dec: 61571
     */
    NOM_DIM_KILO_CALORIE(61571),

    /**
     * (caloriesperday)
     * Hex: 0xF084
     * Dec: 61572
     */
    NOM_DIM_MEGA_CALORIE(61572),

    /**
     * (kilo-caloriesperday)
     * Hex: 0xF0A0
     * Dec: 61600
     */
    NOM_DIM_X_CALORIE_PER_DAY(61600),

    /**
     * (megacaloriesperday)
     * Hex: 0xF0A3
     * Dec: 61603
     */
    NOM_DIM_KILO_CALORIE_PER_DAY(61603),

    /**
     * (caloriespermilli-liter)
     * Hex: 0xF0A4
     * Dec: 61604
     */
    NOM_DIM_MEGA_CALORIE_PER_DAY(61604),

    /**
     * (kilocaloriesperml)
     * Hex: 0xF0C0
     * Dec: 61632
     */
    NOM_DIM_X_CALORIE_PER_ML(61632),

    /**
     * (Joulepermilli-liter)
     * Hex: 0xF0C3
     * Dec: 61635
     */
    NOM_DIM_KILO_CALORIE_PER_ML(61635),

    /**
     * (kilo-joulespermilli-liter)
     * Hex: 0xF0E0
     * Dec: 61664
     */
    NOM_DIM_X_JOULE_PER_ML(61664),

    /**
     * (revolutionsperminute)
     * Hex: 0xF0E3
     * Dec: 61667
     */
    NOM_DIM_KILO_JOULE_PER_ML(61667),

    /**
     * (perminuteperliterperkilo)
     * Hex: 0xF100
     * Dec: 61696
     */
    NOM_DIM_X_REV_PER_MIN(61696),

    /**
     * (literpermilli-bar)
     * Hex: 0xF120
     * Dec: 61728
     */
    NOM_DIM_PER_L_PER_MIN_PER_KG(61728),

    /**
     * (milli-literpermilli-bar)
     * Hex: 0xF140
     * Dec: 61760
     */
    NOM_DIM_X_L_PER_MILLI_BAR(61760),

    /**
     * (literperkilo-gramperhour)
     * Hex: 0xF152
     * Dec: 61778
     */
    NOM_DIM_MILLI_L_PER_MILLI_BAR(61778),

    /**
     * (milli-literperkilogramperhour)
     * Hex: 0xF160
     * Dec: 61792
     */
    NOM_DIM_X_L_PER_KG_PER_HR(61792),

    /**
     * (barperliterpersec)
     * Hex: 0xF172
     * Dec: 61810
     */
    NOM_DIM_MILLI_L_PER_KG_PER_HR(61810),

    /**
     * (milli-barperliterpersec)
     * Hex: 0xF180
     * Dec: 61824
     */
    NOM_DIM_X_BAR_PER_LITER_PER_SEC(61824),

    /**
     * (barperliter)
     * Hex: 0xF192
     * Dec: 61842
     */
    NOM_DIM_MILLI_BAR_PER_LITER_PER_SEC(61842),

    /**
     * (barperliter)
     * Hex: 0xF1A0
     * Dec: 61856
     */
    NOM_DIM_X_BAR_PER_LITER(61856),

    /**
     * (voltpermilli-volt)
     * Hex: 0xF1B2
     * Dec: 61874
     */
    NOM_DIM_MILLI_BAR_PER_LITER(61874),

    /**
     * (cmH2Opermicro-volt)
     * Hex: 0xF1C0
     * Dec: 61888
     */
    NOM_DIM_VOLT_PER_MILLI_VOLT(61888),

    /**
     * (jouleperliter)
     * Hex: 0xF1E0
     * Dec: 61920
     */
    NOM_DIM_CM_H2O_PER_MICRO_VOLT(61920),

    /**
     * (literperbar)
     * Hex: 0xF200
     * Dec: 61952
     */
    NOM_DIM_X_JOULE_PER_LITER(61952),

    /**
     * (meterpermilli-volt)
     * Hex: 0xF220
     * Dec: 61984
     */
    NOM_DIM_X_L_PER_BAR(61984),

    /**
     * (milli-meterpermilli-volt)
     * Hex: 0xF240
     * Dec: 62016
     */
    NOM_DIM_X_M_PER_MILLI_VOLT(62016),

    /**
     * (literperminuteperkilo-gram)
     * Hex: 0xF252
     * Dec: 62034
     */
    NOM_DIM_MILLI_M_PER_MILLI_VOLT(62034),

    /**
     * (milli-literperminuteperkilo-gram)
     * Hex: 0xF260
     * Dec: 62048
     */
    NOM_DIM_X_L_PER_MIN_PER_KG(62048),

    /**
     * (pascalperliterpersec)
     * Hex: 0xF272
     * Dec: 62066
     */
    NOM_DIM_MILLI_L_PER_MIN_PER_KG(62066),

    /**
     * (hPaperliterpersec)
     * Hex: 0xF280
     * Dec: 62080
     */
    NOM_DIM_X_PASCAL_PER_L_PER_SEC(62080),

    /**
     * (kPaperliterpersec)
     * Hex: 0xF282
     * Dec: 62082
     */
    NOM_DIM_HECTO_PASCAL_PER_L_PER_SEC(62082),

    /**
     * (milli-literperpascal)
     * Hex: 0xF283
     * Dec: 62083
     */
    NOM_DIM_KILO_PASCAL_PER_L_PER_SEC(62083),

    /**
     * (milli-literperhecto-pascal)
     * Hex: 0xF2A0
     * Dec: 62112
     */
    NOM_DIM_MILLI_L_PER_X_PASCAL(62112),

    /**
     * (milli-literperkilo-pascal)
     * Hex: 0xF2A2
     * Dec: 62114
     */
    NOM_DIM_MILLI_L_PER_HECTO_PASCAL(62114),

    /**
     * (mm)
     * Hex: 0xF2A3
     * Dec: 62115
     */
    NOM_DIM_MILLI_L_PER_KILO_PASCAL(62115),

    /**
     * AlertCodes
     * Hex: 0xF2C0
     * Dec: 62144
     */
    NOM_DIM_MM_HG_PER_X_L_PER_SEC(62144),

;
    
    private final int x;
    
    private UnitCode(final int x) {
        this.x = x;
    }
    
    private static final Map<Integer, UnitCode> map = OrdinalEnum.buildInt(UnitCode.class);
    
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
        return x;

    }
    public final static UnitCode valueOf(int s) {
        return map.get(s);
    }
}
