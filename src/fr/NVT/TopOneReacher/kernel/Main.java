package fr.NVT.TopOneReacher.kernel;

import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.modules.viewers.ConsoleViewer;

public class Main {

	public static void main(String[] args) {
		new Main(args);
	}
	
	private VViewer viewer;
	
	private Main(String[] args) {
		viewer = new ConsoleViewer(this, 20, 20, 1);
	}
}
