package org.mdpnp.devices.philips.intellivue.data;

public enum Language {
	Unspecified,
	English,
	German,
	French,
	Italian,
	Spanish,
	Dutch,
	Swedish,
	Finnish,
	Norwegian,
	Danish,
	Japanese,
	RepublicOfChina,
	PeoplesRepublicOfChina,
	Portuguese,
	Russian,
	Byelorussian,
	Ukrainian,
	Croatian,
	Serbian,
	Macedonian,
	Bulgarian,
	Greek,
	Polish,
	Czech,
	Slovak,
	Slovenian,
	Hungarian,
	Romanian,
	Turkish,
	Latvian,
	Lithuanian,
	Estonian,
	Korean;
	
	public short asShort() {
		switch(this) {
		case Unspecified:
			return 0;
		case English:
			return 1;
		case German:
			return 2;
		case French:
			return 3;
		case Italian:
			return 4;
		case Spanish:
			return 5;
		case Dutch:
			return 6;
		case Swedish:
			return 7;
		case Finnish:
			return 8;
		case Norwegian:
			return 9;
		case Danish:
			return 10;
		case Japanese:
			return 11;
		case RepublicOfChina:
			return 12;
		case PeoplesRepublicOfChina:
			return 13;
		case Portuguese:
			return 14;
		case Russian:
			return 15;
		case Byelorussian:
			return 16;
		case Ukrainian:
			return 17;
		case Croatian:
			return 18;
		case Serbian:
			return 19;
		case Macedonian:
			return 20;
		case Bulgarian:
			return 21;
		case Greek:
			return 22;
		case Polish:
			return 23;
		case Czech:
			return 24;
		case Slovak:
			return 25;
		case Slovenian:
			return 26;
		case Hungarian:
			return 27;
		case Romanian:
			return 28;
		case Turkish:
			return 29;
		case Latvian:
			return 30;
		case Lithuanian:
			return 31;
		case Estonian:
			return 32;
		case Korean:
			return 33;
		default:
			throw new IllegalArgumentException("Unknown language:"+this);
		}
	}


	public static Language valueOf(int x) {
		switch(x) {
		case 0:
			return Unspecified;
		case 1:
			return English;
		case 2:
			return German;
		case 3:
			return French;
		case 4:
			return Italian;
		case 5:
			return Spanish;
		case 6:
			return Dutch;
		case 7:
			return Swedish;
		case 8:
			return Finnish;
		case 9:
			return Norwegian;
		case 10:
			return Danish;
		case 11:
			return Japanese;
		case 12:
			return RepublicOfChina;
		case 13:
			return PeoplesRepublicOfChina;
		case 14:
			return Portuguese;
		case 15:
			return Russian;
		case 16:
			return Byelorussian;
		case 17:
			return Ukrainian;
		case 18:
			return Croatian;
		case 19:
			return Serbian;
		case 20:
			return Macedonian;
		case 21:
			return Bulgarian;
		case 22:
			return Greek;
		case 23:
			return Polish;
		case 24:
			return Czech;
		case 25:
			return Slovak;
		case 26:
			return Slovenian;
		case 27:
			return Hungarian;
		case 28:
			return Romanian;
		case 29:
			return Turkish;
		case 30:
			return Latvian;
		case 31:
			return Lithuanian;
		case 32:
			return Estonian;
		case 33:
			return Korean;
		default:
			throw new IllegalArgumentException("Unknown Language:"+x);
		}
	}

}
