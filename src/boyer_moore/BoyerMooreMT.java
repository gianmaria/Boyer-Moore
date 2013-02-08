package boyer_moore;
import java.util.Vector;


public class BoyerMooreMT {
	
	private Vector<Integer> ris;
	
	public BoyerMooreMT(){
		ris = new Vector<Integer>();
	}
	
	public Vector<Integer> search(String text, String pattern){
		int m = text.length();
		int half= m/2;
		
		ris.clear();
		
		//BoyerMoore bm1 = new BoyerMoore(text, pattern.substring(0, half), ris);
		//BoyerMoore bm2 = new BoyerMoore(text,pattern.substring(half), ris);
		//bm1.start();
		//bm2.start();
		
		
		return ris;
	}

}
