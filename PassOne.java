import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;

public class PassOne 
{
	public static void main(String args[]) throws IOException
	{
		HashMap<String, String> AD = new HashMap<String, String>();
		HashMap<String, String> IS = new HashMap<String, String>();
		HashMap<String, String> DS = new HashMap<String, String>();
		HashMap<String, String> CC = new HashMap<String, String>();
		HashMap<String, String> REG = new HashMap<String, String>();
		
		
		HashMap<String, Integer> symtab = new HashMap<String, Integer>();
		ArrayList <Table> littab = new ArrayList<>();
		TreeMap<String, Integer> sym_map = new TreeMap<String, Integer>();
		ArrayList<Integer> pooltab = new ArrayList<Integer>();
		
		
		
		int lc=0,sym[],pool[];
		sym = new int[1];
		pool = new int[1];
		sym[0] = 1;
		pool[0]=1;
		
		AD.put("START", "01");
		AD.put("END", "02");
		AD.put("ORIGIN", "03");
		AD.put("EQU", "04");
		AD.put("LTORG", "05");
		IS.put("STOP", "00");
		IS.put("ADD", "01");
		IS.put("SUB", "02");
		IS.put("MULT", "03");
		IS.put("MOVER", "04");
		IS.put("MOVEM", "05");
		IS.put("COMP", "06");
		IS.put("BC", "07");
		IS.put("DIV", "08");
		IS.put("READ", "09");
		IS.put("PRINT", "10");
		DS.put("DC", "01");
		DS.put("DS", "02");
		CC.put("LT", "1");
		CC.put("LE", "2");
		CC.put("EQ", "3");
		CC.put("GT", "4");
		CC.put("GE", "5");
		CC.put("ANY", "6");
		REG.put("AREG", "1");
		REG.put("BREG", "2");
		REG.put("CREG", "3");
		REG.put("DREG", "4");
		
		String Label,op1,op2,op,cl,opcode;
		
		try {
		
		File output = new File("output.txt");
		PrintWriter writer = new PrintWriter(output);
		writer.close();
		System.out.println("Output File Cleared!!");
		
		output = new File("symtab.txt");
		writer = new PrintWriter(output);
		writer.close();
		System.out.println("Symtab File Cleared!!");
		
		output = new File("littab.txt");
		writer = new PrintWriter(output);
		writer.close();
		System.out.println("Literal File Cleared!!");
		
		output = new File("pooltab.txt");
		writer = new PrintWriter(output);
		writer.close();
		System.out.println("Pooltab File Cleared!!");
		
		
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		
		
	
		Stack<Integer> lc_track = new Stack();
		
		File input = new File("input2.asm");
		
		Scanner inp_reader = new Scanner(input);
		
		
		while(inp_reader.hasNext())
		{
			Label = "";
			opcode="";
			cl="";
			
			String data = inp_reader.nextLine().trim();
			//System.out.println("----------------------------------------------------------------");
			String[] words = data.split("\t+| ");
			
		/*for(int i=0 ; i < words.length ; i++)
			{
				System.out.println(words[i]+" ");
				
			}*/
			
		//	System.out.println("---------------------------------------------------------");
			
			
			
			if(AD.containsKey(words[0]))
			{
				opcode = AD.get(words[0]);
				
				if(opcode.equals("01"))
				{
					start(words,lc_track);
					//System.out.println(data);
				}
				else if(opcode.equals("02"))
				{
					end(pool,littab,lc_track);
					
				}
				else if(opcode.equals("03"))
				{
					origin(words[1],symtab,lc_track);
				}
				else if(opcode.equals("05"))
				{
					ltorg(littab,lc_track,pool);
				}
				
			}			
			else if(IS.containsKey(words[0]))
			{
				lc = lc_track.peek();
				lc_track.add((lc+1));
				opcode = IS.get(words[0]);
				if(words.length==3 && !(opcode.equals("10")) )
				{
					File output = new File("output.txt");
					
					FileWriter writer = new FileWriter(output,true);
					BufferedWriter out = new BufferedWriter(writer);
					
					if(words[1].endsWith(","))
						words[1]=words[1].substring(0,(words[1].length())-1);
					
					
					if(!words[2].startsWith("=")) {
						if(!(symtab.containsKey(words[2])))
						{
							symtab.put(words[2], -1);
							sym_map.put(words[2],sym[0]);
							
							sym[0]++;
						}
						//cl = w[1]; opcode = w[2];
						//System.out.println("IS: "+"Label: "+words[0]+"  class: "+cl+"opcode:"+opcode);
						
						String str = lc_track.peek()+".\t"+"(IS,"+opcode+") ("+REG.get(words[1])+") (S,"+sym_map.get(words[2])+")\n";
					//	System.out.println("STR: "+str);
						out.write(str);
					}
					else if(words[2].startsWith("="))
					{
						
						
						if(search_literal(littab,words[2])==-1) 
						{
							Table T = new Table();
							T.Name = words[2];
							T.pool = pool[0];
							littab.add(T);
						}
						else
						{
							int ind = search_literal(littab,words[2]);
							if(!(pool[0]==littab.get(ind).pool))
							{
								Table T = new Table();
								T.Name = words[2];
								T.pool = pool[0];
								littab.add(T);
							}
						}
						
						/*System.out.println("INTEMIDIATE LITERAL TABLE");
						for(Table T:littab)
						{
							System.out.println(T.Name+"	"+T.addrs+"		"+T.pool);
							
						}*/
						
						//System.out.println("After Inserting Literal: "+search_literal(littab,words[2])+"POS:"+(search_literal(littab,words[2])+1));
						String str = lc_track.peek()+".\t"+"(IS,"+opcode+") ("+REG.get(words[1])+") (L,"+(search_literal(littab,words[2])+1)+")\n";
					//	System.out.println("STR: "+str);
						out.write(str);
						
					}
					else if(CC.containsKey(words[1]) && opcode.equals("07"))
					{
						if(!(REG.containsKey(words[2])))
							out.write(lc_track.peek()+".\t"+"(IS,"+opcode+") ("+CC.get(words[1]) +") (S,"+sym_map.get(words[2])+")\n");
							else
							out.write(lc_track.peek()+".\t"+"(IS,"+opcode+") ("+CC.get(words[1])+") ("+REG.get(words[2])+")\n");
						
					}
					
					
					
					out.newLine();
					out.close();
					
				}
				else if(opcode.equals("10")||opcode.equals("09"))
				{
					File output = new File("output.txt");
					FileWriter writer = new FileWriter(output,true);
					BufferedWriter out = new BufferedWriter(writer);
					if(!(REG.containsKey(words[1])))
					out.write(lc_track.peek()+".\t"+"(IS,"+opcode+") (S,"+sym_map.get(words[1])+")\n");
					else
						out.write(lc_track.peek()+".\t"+"(IS,"+opcode+") ("+REG.get(words[1])+")\n");
					
					out.newLine();
					out.close();
					
					
				}
				else if(opcode.equals("00"))
				{
					File output = new File("output.txt");
					
					FileWriter writer = new FileWriter(output,true);
					BufferedWriter out = new BufferedWriter(writer);
					
					out.write(lc_track.peek()+".\t"+"(IS,"+opcode+")\n");
					
				out.newLine();
					out.close();
					
				}
			}
			else if(IS.containsKey(words[1]))
			{
				lc = lc_track.peek();
				lc_track.add((lc+1));
				opcode = IS.get(words[1]);
				if(words.length==4)
				{
					File output = new File("output.txt");
					FileWriter writer = new FileWriter(output,true);
					BufferedWriter out = new BufferedWriter(writer);
					
					
					if(words[2].endsWith(","))
						words[2]=words[2].substring(0,(words[2].length())-1);
					
					if(!symtab.containsKey(words[0]))
					{
						symtab.put(words[0],lc_track.peek());
						sym_map.put(words[0],sym[0]);
						
						sym[0]++;
					}
					
					if(!symtab.containsKey(words[3]))
					{
						symtab.put(words[3],-1);
						sym_map.put(words[3],sym[0]);
						
						sym[0]++;
					}
					
					String str = lc_track.peek()+".\t"+"(IS,"+opcode+") ("+REG.get(words[2])+") (S,"+(sym_map.get(words[3])+1)+")\n";
					//System.out.println("STR: "+str);
					out.write(str);
					
					out.newLine();
					out.close();
					
					
					
				}
				
				else if(words.length==3 && ((opcode.equals("10"))||opcode.equals("09")) )
				{
					File output = new File("output.txt");
					FileWriter writer = new FileWriter(output,true);
					BufferedWriter out = new BufferedWriter(writer);
					
					if(!symtab.containsKey(words[2]))
					{
						symtab.put(words[2],-1);
						sym_map.put(words[2],sym[0]);
					
						sym[0]++;
					}
					if(!symtab.containsKey(words[0]))
					{
						symtab.put(words[0],lc_track.peek());
						sym_map.put(words[0],sym[0]);
					
						sym[0]++;
					}
					
					if(!(REG.containsKey(words[2])))
						out.write(lc_track.peek()+".\t"+"(IS,"+opcode+") (S,"+sym_map.get(words[2])+")\n");
						else
							out.write(lc_track.peek()+".\t"+"(IS,"+opcode+") ("+REG.get(words[2])+")\n");
					
					out.newLine();
					out.close();
				}
				
				
			}
			else if(DS.containsKey(words[1]))
			{
				DL(words,lc_track,DS.get(words[1]),symtab,sym_map,sym);
			}
			else if(AD.containsKey(words[1]))
			{
				opcode = AD.get(words[1]);
				if(opcode.equals("04"))
				{
					equ(words[0],words[2],symtab);
					
				}
				//System.out.println("EQU");
			}
			
			
			//System.out.println("LC : "+lc_track.peek());
			
			
		}

		
		File output = new File("symtab.txt");
		FileWriter writer = new FileWriter(output,true);
		BufferedWriter out = new BufferedWriter(writer);
		
		System.out.println("SYMBOL TABLE");
		for(String key : symtab.keySet())
		{
			System.out.println(key+"	"+symtab.get(key)+"		"+sym_map.get(key));
			out.write(key+" "+symtab.get(key)+" "+sym_map.get(key));
			out.newLine();
			
		}
		
	
		out.close();
		
		
	
		
		if(!littab.isEmpty()) {
			
		File output1 = new File("littab.txt");
		FileWriter writer1 = new FileWriter(output1,true);
		BufferedWriter out1 = new BufferedWriter(writer1);	
		
		File output2 = new File("pooltab.txt");
		FileWriter writer2 = new FileWriter(output2,true);
		BufferedWriter out2 = new BufferedWriter(writer2);	
		
		int  pool1 = littab.get((littab.size()-1)).pool;
			
		System.out.println("LITERAL TABLE		POOL");
		for(Table T:littab)
		{
			System.out.println(T.Name+"	"+T.addrs+"		"+T.pool);
			out1.write(T.Name+" "+T.addrs+" "+T.pool);
			out1.newLine();
			
		}
		
		out1.close();
		
		
		
		for(int i = 1 ; i <= pool1 ; i++)
		{
			out2.write("#"+i);out2.newLine();
		}
		
		out2.close();
		
		}
		
		
		
	}
	
public static void start(String[] words , Stack<Integer> lc_track) throws IOException
	{
		int lc = 0;
		File output = new File("output.txt");
		FileWriter writer = new FileWriter(output,true);
		BufferedWriter out = new BufferedWriter(writer);
		
		if(words.length==2)
		{
			 lc = Integer.parseInt(words[1]);
			 try {
				out.write(" 	"+"(AD,01)  (C,"+lc+")\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 out.newLine();
			 		
		}
		else
		{
			try {
				out.write(" 	"+"(AD,01)\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.newLine();
		}
		lc_track.add((lc-1));
	//	System.out.println("LC START: "+(lc_track.peek()+1));
		out.close();
	}	


public static void DL(String[] words , Stack<Integer> lc_track,String opcode,HashMap<String,Integer>symtab,TreeMap<String,Integer>sym_map,int sym[]) throws IOException
{
	int lc;
	lc = lc_track.peek();
	File output = new File("output.txt");
	FileWriter writer = new FileWriter(output,true);
	BufferedWriter out = new BufferedWriter(writer);
	//System.out.println("SYM : "+sym);
	
	if(opcode.equals("01"))
	{
		lc_track.add((lc+1));
		
		if(!symtab.containsKey(words[0]))
		{
			symtab.put(words[0],lc_track.peek());
			sym_map.put(words[0],sym[0]);
	
			sym[0]++;
		}
		else
		{
			symtab.put(words[0],lc_track.peek());
		}
		
		String inp = words[2].substring(1,(words[2].length()-1));
		
		out.write(lc_track.peek()+".	"+"(DL,01) (C,"+inp+")\n");
	}
	else
	{
		//System.out.println("Else : ");
	
		if(!symtab.containsKey(words[0]))
		{
			symtab.put(words[0],(lc+1));
			sym_map.put(words[0],sym[0]);
		
			sym[0]++;
		}
		else
		{
			symtab.put(words[0],(lc+1));
		}
		String inp = words[2];
		
		out.write((lc+1)+".	"+"(DL,02) (C,"+inp+")\n");
		//out.write((lc+1)+".	"+"(DL,02)\n");
		lc = lc+Integer.parseInt(words[2]);
		lc_track.add((lc));
		
		
	}
	
	out.newLine();
	out.close();
}

public static void end(int pool[],ArrayList<Table>litab,Stack<Integer>lc_track)throws IOException
{
	
	
	File output = new File("output.txt");
	FileWriter writer = new FileWriter(output,true);
	BufferedWriter out = new BufferedWriter(writer);
	
	out.write(" 	"+"(AD,02)\n");
	out.newLine();
	
	
	Table T;
	for(int i = 0 ; i < litab.size();i++)
	{
		T = litab.get(i);
		if(T.addrs==-1)
		{
			int lc = lc_track.peek();
			lc_track.add((lc+1));
			T.addrs = lc_track.peek();
			T.pool = pool[0];
			out.write(lc_track.peek()+".	"+"(DL,01) (C,"+T.Name.substring(2,(T.Name.length()-1))+")\n");
			out.newLine();
		}
	}
	
	
	pool[0] = pool[0]+1;
	
	
	
	
	out.newLine();
	
	out.close();
	
}


public static void equ(String a,String b ,HashMap<String,Integer>symtab) throws IOException
{

	File output = new File("output.txt");
	FileWriter writer = new FileWriter(output,true);
	BufferedWriter out = new BufferedWriter(writer);
	
	if(b.matches("(.)*+(.)*"))
	{
		String[] w = b.split("\\+");
		int val1 = symtab.get((w[0]));
		int val2 = Integer.parseInt((w[1]));
		int res = val1+val2;
		
		symtab.put(a, res);
		
		/*for(String c : w)
		{
			System.out.println(c);
		}*/
		
		
		
	}
	else
	{
		//System.out.println("symbol");
		symtab.put(a,symtab.get(b));
	}
	
	
	out.write(" 	"+"(AD,04)\n");
	out.newLine();
	out.close();
}

public static void origin(String b ,HashMap<String,Integer>symtab,Stack<Integer> lc_track) throws IOException
{
	File output = new File("output.txt");
	FileWriter writer = new FileWriter(output,true);
	BufferedWriter out = new BufferedWriter(writer);
	if(b.matches("(.)*+(.)*"))
	{
		String[] w = b.split("\\+");
		int val1 = symtab.get((w[0]));
		int val2 = Integer.parseInt(w[1]);
		int res = val1+val2;
		
		lc_track.add((res-1));
		
	/*for(String a : w)
		{
			System.out.println(a);
		}*/
		
		
		
		
	}
	else
	{
		//System.out.println("symbol");
		lc_track.add((Integer.parseInt(b)-1));
		
	}
	
	
	
	out.write("\t(AD,03)\n");
	
	out.newLine();
	
	out.close();
}

public static int search_literal(ArrayList<Table> litab,String lit)
{
	if(litab.isEmpty())
	{
		return -1;
	}
	else
	{
		for(int i = 0 ; i < litab.size();i++)
		{
			Table T = litab.get(i);
			
			if(T.Name.equals(lit))
			{
				//System.out.println("LITERAL TABLE :"+i);
				return i;
				
			}
		}
		return -1;
	}
}

public static void ltorg(ArrayList<Table> litab,Stack<Integer> lc_track,int[]pool) throws IOException
{
	File output = new File("output.txt");
	FileWriter writer = new FileWriter(output,true);
	BufferedWriter out = new BufferedWriter(writer);
	
	out.write("\t(AD,05)\n");
	out.newLine();
	
	Table T;
	for(int i = 0 ; i < litab.size();i++)
	{
		T = litab.get(i);
		if(T.addrs==-1)
		{
			int lc = lc_track.peek();
			lc_track.add((lc+1));
			T.addrs = lc_track.peek();
			T.pool = pool[0];
			out.write(lc_track.peek()+".	"+"(DL,01) (C,"+T.Name.substring(2,(T.Name.length()-1))+")\n");
			out.newLine();
		}
	}
	
	pool[0]=pool[0]+1;
	
		
	out.newLine();
	out.close();
}

}