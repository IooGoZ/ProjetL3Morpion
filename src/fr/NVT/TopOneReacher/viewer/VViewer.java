package fr.NVT.TopOneReacher.viewer;

import fr.NVT.TopOneReacher.Main;

public interface VViewer {
	public boolean init(Main main);
	
	public void reset();
	
	public void update();
}
