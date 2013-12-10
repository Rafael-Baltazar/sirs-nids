package pt.ist.sirs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;

public class ArffCreator {
	
	private int windowSize;
	private String inputFile;
	private int bitCount;
	
	private static String relation = "packets";
	private static String type = "type";
	private static String result = "passive";
	
	private static final String[] metrics = {"average", "sum"};
	
	public ArffCreator (String inputFile, int windowSize, int bitCount){
		this.windowSize= windowSize;
		this.inputFile=inputFile;
		this.bitCount=bitCount;
	}
	
	public void generateARFF() throws IOException {

		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		int totalPackets;
		float average[] = new float[bitCount];
		
		bufferedWriter = new BufferedWriter(new FileWriter("out.arff"));
		totalPackets = getNumLines(inputFile);
		
		
		bufferedWriter.write("@RELATION"+ relation + "\n\n");

		bufferedWriter.write("@ATTRIBUTE"+ type +"{\"without attack\",\"attack 1\",\"attack 2\",\"attack 3\"}\n\n");

		for(int i=0;i<bitCount;++i){
			for(int j=0;j<metrics.length;++j){
				bufferedWriter.write("@ATTRIBUTE "+metrics[j]+"_bit_"+i+" NUMERIC");
				bufferedWriter.newLine();
			}
			bufferedWriter.newLine();
		}
		bufferedWriter.write("@DATA\n");
		bufferedReader = new BufferedReader(new FileReader(inputFile));
		
		System.out.println("Window size:" + windowSize);
		System.out.println("Total:" + totalPackets);

		
		for(int wnds=(totalPackets/windowSize);wnds>=0;--wnds){ //window loop: this loot stops when there are no more windows to process
			for(int pkt=0;pkt<windowSize && totalPackets>0;++pkt, --totalPackets){ //packet loop: this loot stops if there are no more packets in the window OR no more packets at all
				for(int bit=0;bit<bitCount;++bit){//bit loop: this loop stops when it reads the last bit of a packet
					
					average[bit]+= bufferedReader.read() - '0';
					
				}//packet done
				bufferedReader.skip(1); //skip line feed
			}//window done
			bufferedWriter.write(average[bitCount-2]+","+result+"\n"); // write to file FIXME: Need to write all avg to file, this is just for debug
			average = new float[bitCount]; // clear buffer <----------------------FIXME: better way to clean buffer?
		}//file done
		
		
		
		
    	bufferedWriter.close();
    	bufferedReader.close();
	}
	
	public int getNumLines(String filename) throws IOException {
		LineNumberReader reader  = new LineNumberReader(new FileReader(filename));
		int cnt = 0;
		while ((reader.readLine()) != null);
		cnt = reader.getLineNumber(); 
		reader.close();
		return cnt;
	}
	  
	  
    	
    	
    	
	    	
	    	
	    
	    	
	       /* 
	    	
	        bufferedWriter.write("@RELATION relation\n"
	        					+"@ATTRIBUTE tipo {\"sem ataque\",\"ataque 1\",\"ataque 2\",\"ataque 3\"}\n"
	        					+"@ATTRIBUTE soma NUMERIC\n"
	        					+"@ATTRIBUTE media NUMERIC\n"
	        					+"@ATTRIBUTE desvio NUMERIC\n\n"
	        					+"@DATA\n");
	        
	    System.out.println("Janela de pacotes: "+WINDOW_SIZE);
	    System.out.println("Total de pacotes: "+totalPacotes);
	            
	        while ((totalPacotes-windowSize+1-linhaActual>=0)) {
	            
	            //coloca bookmark para poder voltar sem ter de carregar o ficheiro novamente
	            bufferedReadPre.mark(300000);
	            
	            //inicializacao de variaveis
	            somaBits=0;
	            average=0;
	            standardDeviation=0;
	            arffLine="";
	            
	            //calculo da soma de bits a 1
	            for (int linhaJanela=1;linhaJanela<=WINDOW_SIZE;linhaJanela++){
	                line = bufferedReadPre.readLine();
	                somaBitsLinha=count(line,"1");
	                somaBitsLinhas[linhaJanela-1]=somaBitsLinha;
	                somaBits=somaBits+somaBitsLinha;
	            }
	            //Aqui j� processou uma janela inteira
	   
	                
	                //calculo da m�dia 1's por pacote ---> somatotal/54
	            average=somaBits/((float)WINDOW_SIZE);
	           
	            //calculo do desvio padrao
	            //desvioPadrao=(float)StandardDeviation.StandardDeviationMean(somaBitsLinhas);
	            standardDeviation = 99;
	            
	            //gera entrada para o ficheiro
	            DecimalFormat form = new DecimalFormat("0.00");
	            arffLine=trafic+","+somaBits+","+((String)form.format(average)).replace(",", ".") +","+(String)form.format(standardDeviation).replace(",", ".")+"\n";
	            
	            //Escreve no ficheiro
	            bufferedWriter.write(arffLine);
	            bufferedReadPre.reset();
	            bufferedReadPre.readLine();
	            linhaActual++;
	            janelasProcessadas++;   
	            }
	        System.out.println("Janelas processadas: "+janelasProcessadas);
	        bufferedReadPre.close();
	        bufferedWriter.close();
	    }

	    
		public int count(String input, String countString){
			return input.split("\\Q"+countString+"\\E", -1).length - 1;
		}
	  
		public int countLines(String filename) throws IOException {
			LineNumberReader reader  = new LineNumberReader(new FileReader(filename));
			int cnt = 0;
			while ((reader.readLine()) != null) {}
			cnt = reader.getLineNumber(); 
			reader.close();
			return cnt;
		} */
	
	
	
	
	

}
