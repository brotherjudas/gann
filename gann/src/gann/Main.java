package gann;

/// code by Tim Roberts for www.ai-junkie.com

import java.util.*;

public class Main {
	
	static char[] valTable={'0','1','2','3','4','5','6','7','8','9','+','-','*','/'};
	static int chLen = 5;
	static double crossRate = 0.7d;
	static double mutRate = 0.001d;
	static Random rand = new Random();
	static int poolSize = 40;
	

	public static void main(String[] args) {
		
		new Main().compute(23);

	}
	
	private void compute(int target){
	
		int gen=0;
		ArrayList pool = new ArrayList(poolSize);
		ArrayList newPool = new ArrayList(pool.size());
		
		for(int i=0;i<poolSize;i++)
			pool.add(new Chromosome(target));
		
		while(true){
			newPool.clear();
			
			gen++;
			
			for(int x=pool.size()-1;x>=0;x-=2){
				Chromosome n1=selectMember(pool);
				Chromosome n2=selectMember(pool);
				
				n1.crossOver(n2);
				n1.mutate();
				n2.mutate();
				
				n1.scoreChromo(target);
				n2.scoreChromo(target);
				
				if(n1.total == target && n1.isValid()){
					System.out.println("Generations: "+gen+" Solution: "+n1.decodeChromo());
					return;
				}
				if(n2.total == target && n2.isValid()){
					System.out.println("Generations: "+gen+" Solution: "+n2.decodeChromo());
					return;
				}
				
				newPool.add(n1);
				newPool.add(n2);
			}
			pool.addAll(newPool);
		}
	}
	
	private Chromosome selectMember(ArrayList l){
		double tot=0.0;
		for(int i=l.size()-1;i>=0;i--){
			double score = ((Chromosome)l.get(i)).score;
			tot+=score;
		}
		double slice = tot*rand.nextDouble();
		
		double ttot=0.0d;
		for(int i=l.size()-1;i>=0;i--){
			Chromosome node=(Chromosome)l.get(i);
			ttot+=node.score;
			if(ttot>=slice){l.remove(i); return node;}
		}
		return (Chromosome)l.remove(l.size()-1);
	}
	
	private static class Chromosome {
		StringBuffer chromo=new StringBuffer(chLen*4);
		public StringBuffer decodeChromo = new StringBuffer(chLen*4);
		public double score;
		public int total;
		
		public Chromosome(int target){
			for(int j=0;j<chLen;j++){
				int pos=chromo.length();
				
				String binString=Integer.toBinaryString(rand.nextInt(valTable.length));
				int fillLen=4 - binString.length();
				for(int i=0;i<fillLen;i++)
					chromo.append('0');
				chromo.append(binString);
			}
		}
		
		public Chromosome(StringBuffer chromo){
			this.chromo=chromo;
		}
		
		public final String decodeChromo(){
			decodeChromo.setLength(0);
			
			for(int i=0;i<chromo.length();i+=4){
				int idx=Integer.parseInt(chromo.substring(i,i+4),2);
				if(idx<valTable.length) decodeChromo.append(valTable[idx]);
			}
			return decodeChromo.toString();
		}
		
		public final void scoreChromo(int target){
			total=addUp();
			if(total==target) score=0;
			score=(double)1/(target-total);
		}
		
		public final void crossOver(Chromosome other){
			if(rand.nextDouble()>crossRate) return;
			
			int pos=rand.nextInt(chromo.length());
			
			for(int i=pos;i<chromo.length();i++){
				char aux=chromo.charAt(i);
				
				chromo.setCharAt(i, other.chromo.charAt(i));
				other.chromo.setCharAt(i, aux);
			}
		}
		
		public final void mutate(){
			for(int i=0; i<chromo.length();i++){
				if(rand.nextDouble()<=mutRate)
					chromo.setCharAt(i, (chromo.charAt(i)=='0'?'1':'0'));
			}
		}
		
		public final int addUp(){
			String decodedString = decodeChromo();
			
			int tot=0;
						int ptr=0;
			
			while(ptr<decodedString.length()){
				char ch=decodedString.charAt(ptr);
				if(Character.isDigit(ch)){
					tot=ch-'0';
					ptr++;
					break;
				}else{
					ptr++;
				}
			}
			
			if(ptr==decodedString.length()) return 0;
			
			boolean num=false;
			char oper=' ';
			while(ptr<decodedString.length()){
				char ch=decodedString.charAt(ptr);
				
				if(num&&!Character.isDigit(ch)){ptr++;continue;}
				if(!num&&Character.isDigit(ch)){ptr++;continue;}
				try{
				if(num){
					switch(oper){
					case '+' : { tot+=(ch-'0');break;}
					case '-' : { tot-=(ch-'0');break;}
					case '*' : { tot*=(ch-'0');break;}
					case '/' : { if(ch!=0) tot/=(ch-'0');break;}
					}
				}else{ oper=ch;}
				}catch(ArithmeticException e){
					//System.out.println(e.getMessage());
				}
				ptr++;
				num=!num;
			}
			return tot;
		}
		
		public final boolean isValid(){
			String decodedString=decodeChromo();
			
			boolean num=true;
			for(int i=0;i<decodedString.length();i++){
				char ch=decodedString.charAt(i);
				
				if(num == !Character.isDigit(ch)) return false;
				
				if(i>0 && ch=='0' && decodedString.charAt(i-1)=='/') return false;
				
				num=!num;
			}
			
			if(!Character.isDigit(decodedString.charAt(decodedString.length()-1))) return false;
			return true;
		}
	}

}