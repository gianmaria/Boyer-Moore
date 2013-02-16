package test;

import java.util.Vector;

import boyer_moore.BoyerMoore;

public class BMTest {

	public static void main(String[] args) {
		String text = "GCATCGCAGAGAGTATACAGTACG";		
		String pattern="GCAGAGAG";
		
		if(args.length==2){
			text = args[0];
			if(text.length()<2){
				System.out.println("Il Testo deve essere lungo almeno 2 caratteri");
				System.exit(-1);
			}
			pattern = args[1];
			if(pattern.length()<2){
				System.out.println("Il Pattern deve essere lungo alemno 2 caratteri");
				System.exit(-1);
				
			}
		}
		
		BoyerMoore bm = new BoyerMoore();
		try{
			Vector<Integer> ris = bm.search(text, pattern); // stampo gli allineamenti per debug
			//System.out.println(bm.getDebugInfo());
			
			printResultSearch(ris, text, pattern);
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			System.out.println("\n\nERROR!!!");
			System.out.println(e.toString());			
			StackTraceElement ste[] = e.getStackTrace();
			for(StackTraceElement item : ste){
				System.out.println("file: " + item.getFileName() + ", linea: " + item.getLineNumber() + ", metodo: " + item.getMethodName());
			}
		}
	}
	
	
	public static void printResultSearch(Vector<Integer> ris, String text, String pattern){
		if(ris.size() > 0){
			System.out.println("*******************************");
			System.out.println("Trovat" + (ris.size() > 1 ? "e " : "a ") + ris.size() + " corrispondenz" + (ris.size() > 1 ? "e:" : "a:") + "\n");
			System.out.println("T: " + text);
			for(Integer item : ris){
				System.out.print("P: ");
				for(int j=1; j<=(item.intValue()-pattern.length()); ++j){
					System.out.print(" ");
				}
				System.out.println(pattern);
				//System.out.println("");
			}
		}else{
			System.out.println("Nessun match trovato!");
		}
		
	}

}
