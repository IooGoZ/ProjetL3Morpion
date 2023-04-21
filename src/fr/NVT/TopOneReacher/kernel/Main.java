package fr.NVT.TopOneReacher.kernel;

import fr.NVT.TopOneReacher.modules.viewers.WebViewer;
public class Main {

	public static void main(String[] args) {
		new Main(args);
	}
	
	private Main(String[] args) {
		//new ConsoleViewer(this);
		new WebViewer(this);
	}
	
	public void exceptions(String msg) {
		System.err.println("Error : " + msg);
		System.exit(-1);
	}
}
