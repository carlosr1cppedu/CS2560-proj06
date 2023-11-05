import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {
	public static  int count=0;
	public static int mem = 16;
	public static String memTemp,destTemp,jumpTemp; //temporary
	public static void main(String[] args) {
	
	
		
		String name = args[0].substring(0, args[0].indexOf('.'));	//copies name of existing file without the file type
		
		String outFileName = name+".hack";  //out file name
		
		SymbolTable st = new SymbolTable(); //initialize symbol table
		
		Code ct = new Code();  //initialize code tables
		
		Parser parser = new Parser(args[0]);  //new parser object
	
		File out = new File(outFileName);  //output, .hack file
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(out.getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw); // Ready to write on file
	
		
		
		//first pass
		while(parser.hasMoreCommand()) {  
		if(parser.commandType()== Parser.commandType.L_COMMAND) { 
			
			st.addEntry(parser.symbol(),Integer.toString(count)) ; //adds new symbol to symbol table
		}
		else count++; //next line
		
		parser.advance();  // next command
		
		}
		parser.lineCount =0;   // resets count for starts from first line
		
		//second pass
		while(parser.hasMoreCommand())
		{
			if(parser.commandType()== Parser.commandType.A_COMMAND) //@xxx
			{
				if(parser.strFileArr[parser.lineCount].startsWith("@"))
				{
				String temp  = parser.symbol(); //returns xxx
					if(parser.isNum(temp))  //checks if xxx is number
					{
						int xxx = Integer.parseInt(temp);
						temp = Parser.dexToBin(xxx);	// return bin value of xxx
						temp = parser.addZero(temp);
						try {
							bw.write(temp + '\n');//write to hack
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					else  //if not number
					{
						if(!st.containKey(temp))  // not exists in Symbol Table
						{
							st.addEntry(temp,Integer.toString(mem));  //Adds to Symbol Table
							mem++;
						}
						 if(st.containKey(temp)) // already exists in Symbol Table
							{
							String temp2 = st.getValue(temp);
							int xxx = Integer.parseInt(temp2);
							temp2 = Parser.dexToBin(xxx);
							temp2 = parser.addZero(temp2);
							try {
								bw.write(temp2+'\n');  //write to hack
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}//if command type A_COMMAND 
			if(parser.commandType()== Parser.commandType.C_COMMAND)
			{
				if(parser.strFileArr[parser.lineCount].contains("="))//dest=comp
				{
					
					
					destTemp = ct.getDest(parser.dest());
					memTemp = ct.getComp(parser.comp());
					jumpTemp = ct.getJump("NULL");  //no need jump
					try {
						bw.write("111" + memTemp + destTemp + jumpTemp +'\n');
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				else if(parser.strFileArr[parser.lineCount].contains(";")) //jump
				{
					destTemp = ct.getDest("NULL"); // no need dest
					memTemp = ct.getComp(parser.comp());
					jumpTemp = ct.getJump(parser.jump());
					
					try {
						bw.write("111" + memTemp + destTemp + jumpTemp +'\n');
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}//if command type C_COMMAND 
			parser.advance();		
		}//end while
		
try {
	bw.close();
} catch (IOException e) {
	e.printStackTrace();
}
	}

}
