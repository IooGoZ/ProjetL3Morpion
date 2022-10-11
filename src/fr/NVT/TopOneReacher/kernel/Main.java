package fr.NVT.TopOneReacher.kernel;

import fr.NVT.TopOneReacher.modules.viewers.ConsoleViewer;

public class Main {

	public static void main(String[] args) {
		new Main(args);
	}
	
	private Main(String[] args) {
		new ConsoleViewer(this, 20, 20, 1);
	}
}
