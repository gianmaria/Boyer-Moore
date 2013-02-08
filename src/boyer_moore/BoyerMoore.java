package boyer_moore;

import java.util.Vector;

public class BoyerMoore {
	
	private char P[];
	private int n;
	private char T[];
	private int m;
	
	private int bc[];
	private int N[]; //Nj(P)
	private int L[]; //L'(i)
	private int l[]; //l'(i)
	
	public BoyerMoore(){}
		
	/**
	 * Cerca tutte le occorrenze di P in T e restituisce un vettore
	 * di interi con le posizioni dei match.
	 * 
	 * @param text
	 * 		  il testo in cui cercare
	 * @param pattern
	 * 		  la parola da cercare
	 * 
	 * @return un array d'interi con le posizoini di match
	 */
	public Vector<Integer> search(String text, String pattern){
		return search(text, pattern, false);
	}
	
	public Vector<Integer> search(String text, String pattern, boolean debug){
		init(pattern, text);
		
		badCharacterRulePreprocess();
		goodSuffixRulePreprocess();
		
		Vector<Integer> ris = new Vector<Integer>();
		
		int k=n; // k: indice esterno del Testo
				 // n: indice estrtno del Pattern
		while(k<=m){
			int i=n; // i: indice interno del Pattern
			int h=k; // h: indice interno del Testo
			if(debug) printAlignment(h);
			while(i>0 && P[i]==T[h]){
				--i;
				--h;
			}
			if(i==0){ // ho trovato un match
				ris.add(new Integer(k));
				k += n - l[2]; // allineo il più lungo prefisso di P che sia anche suffisso di P stesso abcxabc
				System.out.println("gs=" + (n - l[2]) + " bc=" + 1);
				
			}else{ // ho un mismatch
				char mismatch = T[h];
				
				/*			 		Extended Bad Charcter Rule
				 * 
				 * se durante la ricerca ho un mismatch in posizione P[i] controllo
				 * se il carattere in T che ha provocato il mismatch, x, è presente a
				 * sinistra di i in P[1..i-1] se si, prendo la sua occorrenza più a
				 * destra e lo allineo sotto x in T 
				 */
				int ebc_shift = bcShift(mismatch, i);
				
				
				/*					Strong Good Suffix Rule
				 * 
				 * se durante la ricerca ho un mismatch in posizione i-1 controllo
				 * se L'(i)>0 se si shifto P di n-L'(i)
				 * altrimenti shifto P di n-l'(i)
				 * 
				 * se i=n, ho un mismatch tra T[i] e P[n] quindi shito P di 1 verso destra
				 */
				int sgs_shift = (i == n) ? 1 : ( L[i+1] > 0 ? (n - L[i+1]) : (n - l[i+1]) ); 
				
				k += Math.max(ebc_shift, sgs_shift);
				System.out.println("gs=" + sgs_shift + " bc=" + ebc_shift);				
			}
		}
		
		if(debug) printAlignment(k);
		
		return ris;
	}
	
	private void init(String pattern, String text){
		this.P = ("#" + pattern).toCharArray(); //P: #abc
		this.n = (P.length-1); //n=3
		this.T = ("#" + text).toCharArray();
		this.m = (T.length-1);
		
		this.bc = new int[P.length];
		this.N = new int[P.length];
		this.L= new int[P.length];
		this.l= new int[P.length];
	}
	
	/**
	 * Supponiamo che per un dato allineamento di P e T gli n-i carattere di P matchano la loro
	 * controlarte in T, ma il carattere successivo a sinistra, P(i), provoca un mismatch con la
	 * sua controparte in T diciamo x.
	 * Cerco se esiste l'occorrenza più a destra di x in P[1..i], x', se esiste allineo x'
	 * sotto x di T. Altrimenti sposto P oltre il punto di mismatch.
	 * 
	 * In caso di match sposto P di 1 posizione a destra.
	 * 
	 */
	private void badCharacterRulePreprocess() {
		for(int i=1; i<=n; ++i)
			bc[i]=i;
	}
	
	private int bcShift(char mismatch, int posP){
		for(int i=(posP-1); i>=1; --i){
			if(mismatch==P[i]){
				return posP-i;
			}
		}
		return posP;
	}
	
	/**
	 * Supponiamo che per un dato allineamento di P e T, una sottostringa t di T matcha un suffisso P, 
	 * ma un mismatch si presenta nella successiva comparazione verso sinistra.
	 * posso:
	 *  
	 * 1. trovare, se esiste, la copia più a destra di t, t', in P in modo che t' non sia un suffisso di P e che 
	 *    il carattere a sinistra di t' in P differisca dal carattere a sinistra di t in P (essendo quello che ha
	 *    provocato il mismatch.
	 *                      x
	 *         T: bbbbdccggbcBAAdcdc....
	 *         P: dBAAcgBAAbgbaa
	 *         P:           dBAAcgBAAbgbaa
	 *         
	 *     spostare P verso destra in modo che la sottostringa t' in T sia allineata con la sottostringa t in T.
	 *         
	 * 2. se t' non esiste spostare la parte sinistra di P sotto t in modo che un prefisso di P matcha un suffisso di t'.
	 *                  x
	 *         T: bbcggbcBAA....
	 *         P: AAgaabgbaa
	 *         P:         AAgaabgbaa
	 *          
	 * 3. se non è possibile neanche questo shift, spostare P di n posizioni verso destra
	 *               x
	 *      T: bbcggbcbaa....
	 *      P: abgaabgbaa
	 *      P:           abgaabgbaa
	 *       
	 * In caso di mismatch al primo confronto P[n] contro T[k] shifto P di 1 posizione
	 * 
	 * 4. se si raggiunge un match di P, spostare P verso destra in modo che un prefisso proprio di P, 
	 *    P[1..n-1] matchi un suffisso dell'occorrenza di P in T, se non è possibile shiftare P di n posizioni.
	 *       
	 *      T: cbaagbcbaa....
	 *      P: cbaagbcbaa
	 *      P:       cbaagbcbaa
	 *       
	 */
	private void goodSuffixRulePreprocess() {
		process_N();
		process_BigL(); // regola 1.
		process_minusl(); // regola 2, regola 4
	}
		
	/**
	 * Data una stringa P, Nj(P) è la lunghezza del più lungo suffisso della sotostringa P[1..j]
	 *  che è anche un suffisso dell'intera stringa P.
	 *  
	 * Nj(P) è Z(reverse_P)
	 */
	private void process_N(){
		String tmp = new String(P).substring(1, P.length); // rimuovo #
		tmp = new StringBuilder(tmp).reverse().toString(); // rovescio la stringa tmp
		char rP[] = ("#"+tmp).toCharArray(); // aggiungo # e converto tmp in un array di char
		//char rP[] = P;
		
		N[0]=-1; // Nj[0] non deve essere mai utilizzato
		
		int k=2;
		int match=0;
		while(k <= n){
			if(rP[k]==rP[1]){
				for(int i=1, j=k; j<=n; ++i,++j){
					if(rP[i]==rP[j])
						++ match;
					else
						break;
				}
				N[n-k+1]=match;
				match=0;
			}
			else{
				N[n-k+1]=0;
			}	
		++k;
		}
		
		if(N[0]!=-1) System.exit(-1); // controllo di consistenza
	}
	
	/**
	 * Per ogni i, L'(i) è la più grande posizione minore di n tale che la stringa P[i..n]
	 *  matcha un suffisso di P[1..L'(i)] e che il carattere che precede questo suffisso è diverso
	 *  da P(i-1).
	 *  L'(i) è definita zero altrimenti.
	 */
	private void process_BigL(){
		L[0]=-1;
		
		for(int i=1; i<=n; ++i)
			L[i]=0;
		
		for(int j=1; j<=n-1; ++j){
			int i= n - N[j] + 1;
			if(i<=n) // quando Nj[i]=0 ho un problema di indici dato che n-0+1 > n
				L[i]=j;
		}
		
		if(L[0]!=-1) System.exit(-1); // controllo di consistenza
	}
	
	/**
	 * l'(i) denota la lunghezza della più largo suffisso di P[i..n] che è anche
	 *  un prefisso di P. Se non esiste l'(i) è 0.
	 *  
	 * l'(i) è uguale al più largo j <=|P[i..n]|, che è n-i+1, tale che Nj(P)=j
	 */
	private void process_minusl() {
		l[0]=-1;
		
		for(int i=1; i<=n; ++i){
			l[i]=0;
			for(int j=(n-i+1); j>=1; --j){
				if(N[j]==j){
					l[i]=j;
					break; // vogliamo solo il più grande Nj che sia uguale a j
				}
			}
		}
		
		if(l[0]!=-1) System.exit(-1);
	}
	
	private void printAlignment(int k){
		System.out.print("\n");
		System.out.print("   ");
		for(int i=1; i<=m; ++i)
			System.out.print(i%10);
		System.out.println("");
		System.out.println("T: " + this.getText());
		System.out.print("P: ");
		for(int i=1; i<=(k-n);++i){
			System.out.print(" ");
		}
		System.out.println(this.getPattern());
		System.out.print("\n");
	}
	
	public int getLengthPattern(){
		return this.n;
	}
	
	public int getLengthText(){
		return this.m;
	}
	
	public int[] getN() {
		return N;
	}

	public int[] getL() {
		return L;
	}
	
	public int[] getl(){
		return this.l;
	}
	
	public int[] getBc(){
		return this.bc;
	}
	
	public String getPattern(){
		return new String(P).substring(1, P.length); // substring(a,b) a compreso b escluso
	}
	
	public String getText(){
		return new String(T).substring(1, T.length); // substring(a,b) a compreso b escluso
	}

	public String getDebugInfo(){
		StringBuilder ret = new StringBuilder();
		
		ret.append("i:  ");
		for(int i=1; i<=n; ++i){
			ret.append( (i % 10) + " ");
		}
		ret.append("\n");
		
		ret.append("P:  " + print(getPattern()) + "\n");
		ret.append("N:  " + print(getN())  + "\n");
		ret.append("L': " + print(getL())  + "\n");
		ret.append("l': " + print(getl())  + "\n");
		
		return ret.toString();
	}
	
	private String print(String s){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<s.length(); ++i)
			sb.append(s.charAt(i) + " ");
		return sb.toString();
	}
	
	private String print(int v[]){
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<v.length; ++i)
			sb.append( (v[i] == 0) ? "- " : v[i] + " ");
		return sb.toString();
	}

}







