package fr.NVT.TopOneReacher.modules.ressources;

import fr.NVT.TopOneReacher.kernel.utils.Position;

public class DirectionPosition {
	
	private final Position pos;
	private final byte dir;
	private final byte zone;
	
	public Position getPos() {
		return pos;
	}

	public byte getDir() {
		return dir;
	}

	public byte getZone() {
		return zone;
	}
	
	public DirectionPosition(Position pos ,byte dir, byte zone) {
		this.pos = pos;
		this.dir = dir;
		this.zone = zone;
	}
	
	public DirectionPosition(Position pos , byte dir) {
		this(pos, dir, (byte) -1);
	}

	public short getRgMin() {
		if (zone == 1)
			return -4;
		else
			return 0;
	}

	public short getRgMax() {
		if (zone == 1)
			return 0;
		else
			return 4;
	}
	
}
