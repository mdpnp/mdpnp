package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum Language implements OrdinalEnum.ShortType {
	Unspecified(0),
	English(1),
	German(2),
	French(3),
	Italian(4),
	Spanish(5),
	Dutch(6),
	Swedish(7),
	Finnish(8),
	Norwegian(9),
	Danish(10),
	Japanese(11),
	RepublicOfChina(12),
	PeoplesRepublicOfChina(13),
	Portuguese(14),
	Russian(15),
	Byelorussian(16),
	Ukrainian(17),
	Croatian(18),
	Serbian(19),
	Macedonian(20),
	Bulgarian(21),
	Greek(22),
	Polish(23),
	Czech(24),
	Slovak(25),
	Slovenian(26),
	Hungarian(27),
	Romanian(28),
	Turkish(29),
	Latvian(30),
	Lithuanian(31),
	Estonian(32),
	Korean(33);
	
	private final short x;
	
	private Language(int x) {
	    this((short)x);
	}
	
	private Language(short x) {
	    this.x = x;
    }
	
	private static final Map<Short, Language> map = OrdinalEnum.buildShort(Language.class);
	
	public short asShort() {
	    return x;
	}


	public static Language valueOf(int x) {
	    return valueOf((short)x);
	}
	
	public static Language valueOf(short x) {
	    return map.get(x);
	}

}
