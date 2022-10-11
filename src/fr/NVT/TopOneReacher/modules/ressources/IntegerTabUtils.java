package fr.NVT.TopOneReacher.modules.ressources;

public abstract class IntegerTabUtils {
	
	public static int maxValue(int[] tab) {
	    int max = tab[0];
	    for (int i = 1; i < tab.length; i++) {
	        if (tab[i] > max)
	            max = tab[i];
	    }
	    return max;
	}
	
	public static int maxValueId(int[] tab) {
	    int max = tab[0];
	    int id = 0;
	    for (int i = 1; i < tab.length; i++) {
	        if (tab[i] > max)
	            max = tab[i];
	        	id = i;
	    }
	    return id;
	}
	
}
