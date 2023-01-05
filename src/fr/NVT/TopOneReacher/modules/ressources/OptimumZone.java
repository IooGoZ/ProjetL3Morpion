package fr.NVT.TopOneReacher.modules.ressources;


//Utils for TopTwoReacher
public enum OptimumZone {

	
	//Presets values
	ONE(10000, 5000, (byte) 5, (byte) 5, (byte) 15),
	TWO(10050, 5050, (byte) 4, (byte) 4,  (byte) 23),
	THREE(1000, 500, (byte) 5, (byte) 4,  (byte) 7),
	FOUR(1005, 505, (byte) 5, (byte) 3,  (byte) 11),
	FIVE(1010, 510, (byte) 4, (byte) 3,  (byte) 19),
	SIX(100, 50, (byte) 5, (byte) 3,  (byte) 3),
	SEVEN(990, 490, (byte) 5, (byte) 2,  (byte) 13),
	EIGHT(120, 70, (byte) 5, (byte) 2,  (byte) 5),
	NINE(110, 60, (byte) 5, (byte) 2,  (byte) 9),
	TEN(25, 9, (byte) 5, (byte) 2,  (byte) 1),
	ELEVEN(95, 45, (byte) 5, (byte) 4,  (byte) 6),
	TWELVE(15, 7, (byte) 5, (byte) 3,  (byte) 2),
	THIRTEEN(100000, 50000, (byte) -1, (byte) -1,  (byte) -1),
	FOURTEEN(10100, 5100, (byte) 3, (byte) 3,  (byte) -1),
	FIFTEEN(995, 495, (byte) 4, (byte) 4,  (byte) -1),
	SIXTEEN(115, 65, (byte) 3, (byte) 3,  (byte) -1),
	SEVENTEEN(980, 480, (byte) 5, (byte) 5,  (byte) -1),
	EIGHTEEN(105, 55, (byte) 5, (byte) 3,  (byte) -1),
	NINETEEN(10, 6, (byte) 5, (byte) 2,  (byte) -1),
	TWENTY(2, 1, (byte) 1, (byte) 1,  (byte) -1);
	
	private final int current_prioritie;
	private final int ennemie_prioritie;
	private final byte opti_attack;
	private final byte opti_defend;
	private final byte reversable_value;
	
	
	private static final double RATIO = 0.0d;
	

	private OptimumZone(int current_prioritie, int ennemie_prioritie, byte opti_attack, byte opti_defend, byte reversable_value) {
		this.current_prioritie = (int) (current_prioritie*(1-RATIO));
		this.ennemie_prioritie = (int) (ennemie_prioritie*(1+RATIO));
		this.opti_attack = opti_attack;
		this.opti_defend = opti_defend;
		this.reversable_value = reversable_value;
	}

	public int getCurrentPrioritie() {
		return current_prioritie;
	}

	public int getEnnemiePrioritie() {
		return ennemie_prioritie;
	}

	public byte getOptimumAttack() {
		return opti_attack;
	}
	
	public byte getOptimumDefense() {
		return opti_defend;
	}
	
	public byte getReversableValue() {
		return reversable_value;
	}
	
	public static OptimumZone getOptimumZone(byte zn_index) {
		switch (zn_index) {
		case 0:
			return TWENTY;
		case 1:
			return TEN;
		case 2:
			return TWELVE;
		case 3:
			return SIX;
		case 4:
			return FIFTEEN;
		case 5:
			return EIGHT;
		case 6:
			return ELEVEN;
		case 7:
			return THREE;
		case 8:
			return TWELVE;
		case 9:
			return NINE;
		case 10:
			return EIGHTEEN;
		case 11:
			return FOUR;
		case 12:
			return ELEVEN;
		case 13:
			return SEVEN;
		case 14:
			return SEVENTEEN;
		case 15:
			return ONE;
		case 16:
			return TEN;
		case 17:
			return SIXTEEN;
		case 18:
			return NINE;
		case 19:
			return FIVE;
		case 20:
			return EIGHT;
		case 21:
			return FIFTEEN;
		case 22:
			return SEVEN;
		case 23:
			return TWO;
		case 24:
			return SIX;
		case 25:
			return FIVE;
		case 26:
			return FOUR;
		case 27:
			return FOURTEEN;
		case 28:
			return THREE;
		case 29:
			return TWO;
		case 30:
			return ONE;
		case 31:
			return THIRTEEN;
		default:
			System.err.println("Optimum Zone, problème de zone");
			return null;
		}
	}
	
}
