import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Pass2 
{
	public static void main(String args[]) 
	{
		HashMap<String, Integer> symtab = new HashMap<String, Integer>();
		ArrayList <Table> littab = new ArrayList<>();
		HashMap<String, Integer> sym_map = new HashMap<String, Integer>();
		ArrayList<MachineCode> m_code = new ArrayList<>();
	
		
		try
		{
			File input = new File("symtab.txt");
			
			Scanner inp_reader = new Scanner(input);
			

			
			while(inp_reader.hasNext())
			{
				String data = inp_reader.nextLine().trim();
				
				if(data.length()>0)
				{
					String[] words = data.split(" ") ;
					symtab.put(words[0], Integer.parseInt(words[1]));
					sym_map.put(words[0],Integer.parseInt(words[2]));
				
				}
			}
		}
		catch(IOException e)
		{
			System.out.println(e);
		}

		try
		{
			File input = new File("littab.txt");
			
			Scanner inp_reader = new Scanner(input);
			Table T=new Table();

			
			while(inp_reader.hasNext())
			{
				String data = inp_reader.nextLine().trim();
				//System.out.println("DATA: "+data);
				T=new Table();
				if(data.length()>0)
				{
					String[] words = data.split(" ") ;
					T.Name=words[0];
					T.addrs=Integer.parseInt(words[1]);
					T.pool = Integer.parseInt(words[2]);
					littab.add(T);
				
				}
				
				
			}
		}
		catch(IOException e)
		{
			System.out.println(e);
		}



		System.out.println("SYMBOL TABLE");
		for(String key : symtab.keySet())
		{
			System.out.println(key+"	"+symtab.get(key)+"		"+sym_map.get(key));

			
		}

		System.out.println("Litral TABLE");
		for(Table T:littab)
		{
			System.out.println(T.Name+"\t"+T.addrs+"\t"+T.pool);
		}

		
		
		
		
		
		
		
		try {
		File input = new File("output.txt");
		Scanner inp_reader = new Scanner(input);
		
		
		while(inp_reader.hasNext())
		{
			String data = inp_reader.nextLine().trim();
			
			if(data.length()>0) 
			{
				//System.out.println("DATA: "+data);
			
				MachineCode code = new MachineCode();
			
				String[] words = data.split("\s+|\t");
				
				if(!words[0].startsWith("(AD"))
				{
					words[0]=words[0].substring(0,words[0].length()-1);
					code.lc = Integer.parseInt(words[0]);
					if(words[1].startsWith("(DL,01)"))
					{
						
						
						
						
							code.opcode="00";
							
							String op2 = words[2].substring(3,words[2].length()-1);
							
						//	System.out.println("OP2:"+op2+"\t"+op2.length());
							if(op2.length()==1)
							code.op2="00"+op2;
							else if(op2.length()==2)
							code.op2="0"+op2;
							else
								code.op2=op2;
						
						
					}
					else if(words[1].startsWith("(IS"))
					{
						
						//code.address = words[0];
						code.opcode = words[1].substring(4,words[1].length()-1);
						if(words.length>2)
						{
							if(words.length==3)
							{
								if(words[2].startsWith("(S")||words[2].startsWith("(L")) {
								
								if(words[2].startsWith("(S")) {
									String val = words[2].substring(3,words[2].length()-1);
									int value =Integer.parseInt(val);
								
									val = search_key(sym_map,value);
									code.op2 =Integer.toString(symtab.get(val));
								}
								else
								{
									String val = words[2].substring(3,words[2].length()-1);
									int value =Integer.parseInt(val);
								
									code.op2 = Integer.toString(littab.get(value-1).addrs);
								}
								}
								else
								{
									code.op2 = words[2].substring(1,words[2].length()-1);
								}
							}
							else if(words.length==4)
							{
								if(words[2].startsWith("(S")||words[2].startsWith("(L")) 
								{
									//code.op1 = "0"+words[2].substring(3,words[2].length()-1);
									if(words[2].startsWith("(S")) {
										String val = words[2].substring(3,words[2].length()-1);
										int value =Integer.parseInt(val);
										
										val = search_key(sym_map,value);
										code.op1 =Integer.toString(symtab.get(val));
									}
									else
									{
										String val = words[2].substring(3,words[2].length()-1);
										int value =Integer.parseInt(val);
										code.op1 = Integer.toString(littab.get(value-1).addrs);
									}
									
								
								}
								else
								{
									code.op1 = words[2].substring(1,words[2].length()-1);
								}
								
								if(words[3].startsWith("(S")||words[3].startsWith("(L")) {
									
									if(words[3].startsWith("(S")) {
									//code.op2 = "0"+words[3].substring(3,words[3].length()-1);
									String val = words[3].substring(3,words[3].length()-1);
									int value =Integer.parseInt(val);
									
									val = search_key(sym_map,value);
									
									code.op2 =Integer.toString(symtab.get(val));
								
									}
									else 
									{
										String val = words[3].substring(3,words[3].length()-1);
										int value =Integer.parseInt(val);
										
										code.op2 = Integer.toString(littab.get(value-1).addrs);
									}
									
								}
									else
									{
										code.op2 = words[3].substring(1,words[3].length()-1);
									}
								
							}
						}
						
					}
					m_code.add(code);
				}
				
				
			
				/*for(int i=0 ; i < words.length ; i++)
				{
					System.out.println(i+"."+words[i]+" ");
				
				}*/
			}
			//System.out.println();
			//System.out.println("-----------------------------------------------------");
			
		}
		
		
		
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
		}
		
		Collections.sort(m_code,new CodeComparator());
		
		try {
		File pt1 = new File("machine_code.txt");
		
		PrintWriter pt2 = new PrintWriter(pt1);
		pt2.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
		}
		
		try {
			
			File machine_code = new File("machine_code.txt");
			FileWriter writer = new FileWriter(machine_code);
			BufferedWriter out = new BufferedWriter(writer);
		
			int k=0,start=m_code.get(0).lc,end=m_code.get(m_code.size()-1).lc;
			//System.out.println("Start: "+start+"\tEnd: "+end);
			System.out.println("LC.\tSign\tOpcode\tOP1\tOP2");
			
			
			for(int i = start ; i <=end;i++)
			{
			
				MachineCode m = m_code.get(k);
				
				
				if(m.lc==i)
				{
					
					if(m.opcode!=null || m.op1!=null || m.op2!=null) {
						String str1=m.lc+"\t+\t";
						
						if(m.opcode!=null)
						{
							
						
							str1=str1+m.opcode+"\t";
							
						}
						else
						{
							
							str1=str1+"00\t";
							
						}
						
						
						if(m.op1!=null)
						{
							
							str1=str1+m.op1+"\t";
						}
						else
						{
							
							str1=str1+"0\t";
						}
						
						if(m.op2!=null)
						{
							
							str1=str1+m.op2+"\t";
						}
						else
						{
							
							str1=str1+"000\t";
						}
						
						
						out.write(str1);
						out.newLine();
						System.out.println(str1);
					
					}
					else
					{
						System.out.println(i+"\t");
						out.write(i+"\t");
						out.newLine();
					}
					
					k++;
				}
				else
				{
					System.out.println(i+"\t");
					out.write(i+"\t");
					out.newLine();
				}
			}
		
		/*for(MachineCode m : m_code)
		{
			
			if(m.opcode!=null || m.op1!=null || m.op2!=null) {
			String str1=m.lc+"\t+\t";
			
			if(m.opcode!=null)
			{
				
			
				str1=str1+m.opcode+"\t";
				
			}
			else
			{
				
				str1=str1+"00\t";
				
			}
			
			
			if(m.op1!=null)
			{
				
				str1=str1+m.op1+"\t";
			}
			else
			{
				
				str1=str1+"0\t";
			}
			
			if(m.op2!=null)
			{
				
				str1=str1+m.op2+"\t";
			}
			else
			{
				
				str1=str1+"000\t";
			}
			
			
			out.write(str1);
			out.newLine();
			System.out.println(str1);
			}
		}*/
		out.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public static String search_key(HashMap<String,Integer> map , int value)
	{
		for(String key : map.keySet())
		{
			if(map.get(key)==value)
			{
				
				return key;
			}
		}
		
		return null;
	}
}
