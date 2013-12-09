package pt.ist.sirs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class Parser {

	/*constants*/
	private final static String TESTSET_FILE_NAME = "_testSet.arff";
	private final static String TRAINERSET_FILE_NAME = "_trainerSet.arff";
	private final static String inputFileName = "file.txt";
	private static final int MAX_BYTES_PER_PACKET = 53;
	private static final int WINDOW_SIZE = 50;
	/*variables*/
    private static String inputFileDirectory;
    private static String outputFileDirectory;
    private static String preProcessedOutputFile;
    private static String relationName;
    
    private static int attack;
    private static boolean append=false;
    
    
    /*
     * 
     */
    public void preProcess(String filename) throws IOException  {
    	
    	BufferedReader bufferedReader = null;
        BufferedWriter bufferedPre = null;
        BufferedWriter bufferedWriter = null;
        
        int byteCount;
        String[] binaryBytes = new String[MAX_BYTES_PER_PACKET];
        String nextHexByte;
        
        String packetHexHeader=null;
           
    	if (attack==-1){ //testset
            outputFileDirectory=inputFileDirectory.replaceAll(".txt","")+ TESTSET_FILE_NAME;
        }else{ //trainer
            outputFileDirectory=inputFileDirectory.replaceAll(".txt","")+ TRAINERSET_FILE_NAME;
        }
        
        System.out.println("Data input file: " + inputFileDirectory);
        System.out.println("Pre-processed fata file: " + preProcessedOutputFile);
        
        if (append){
            System.out.println("Data output file (appending): " + outputFileDirectory);
        }else{
            System.out.println("Data output file: " + outputFileDirectory);
        }
            
        bufferedReader = new BufferedReader(new FileReader(filename));
        bufferedPre = new BufferedWriter(new FileWriter(preProcessedOutputFile));
        bufferedWriter = new BufferedWriter(new FileWriter(outputFileDirectory,append));
            
            
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Pre-processing input file...");
            

        while ((bufferedReader.readLine()) != null) { // skip first line
        	byteCount=0;
            
            bufferedReader.readLine(); // skip time stamp line
            
            packetHexHeader=bufferedReader.readLine(); // packet header data, in Hexadecial
            
            bufferedReader.readLine(); // skip blank line
            
            StringTokenizer st = new StringTokenizer(packetHexHeader, "|");
            st.nextToken();
            
            while(st.hasMoreTokens() && byteCount< MAX_BYTES_PER_PACKET){
                
                nextHexByte=st.nextToken();
                
                binaryBytes[byteCount]=Integer.toBinaryString(Integer.parseInt(nextHexByte, 16));
      
                byteCount++;
            }
            
            if (byteCount==MAX_BYTES_PER_PACKET){ 
            	for(int byteNumber = 0; byteNumber<MAX_BYTES_PER_PACKET; byteNumber++){
            		bufferedPre.write(binaryBytes[byteNumber]);
            	}
            	bufferedPre.newLine();
            }
            
        }
        bufferedReader.close();
        bufferedPre.close();
        bufferedWriter.close();
    }
    
    
    
    
    

    public void generateARFF() throws IOException {
    	BufferedReader bufferedReadPre;
    	BufferedWriter bufferedWriter;
        String line = null;
        String trafic="";
    	
		float average;             
		float standardDeviation;
		
		int somaBits=0;
		int somaBitsLinha=0;
		
		double somaBitsLinhas[]=new double[WINDOW_SIZE];
		
		int linhaActual=1;
		
		int totalPacotes=countLines(preProcessedOutputFile);
		
		int janelasProcessadas=0;
	
		String arffLine="";
		
		   
        if (attack>=0){
        	trafic=String.valueOf(attack);
        if (attack==0){
        	System.out.println("Traffic without attack.");
        }
        }else{
        	System.out.println("Testing Set.");
        }
		
		
		
    	bufferedReadPre = new BufferedReader(new FileReader(preProcessedOutputFile));
    	bufferedWriter = new BufferedWriter(new FileWriter(outputFileDirectory,append));
    	
        
        if (!append){
        bufferedWriter.write("@RELATION relation\n"
        					+"@ATTRIBUTE tipo {\"sem ataque\",\"ataque 1\",\"ataque 2\",\"ataque 3\"}\n"
        					+"@ATTRIBUTE soma NUMERIC\n"
        					+"@ATTRIBUTE media NUMERIC\n"
        					+"@ATTRIBUTE desvio NUMERIC\n\n"
        					+"@DATA\n");
        }
    
        System.out.println("Janela de pacotes: "+WINDOW_SIZE);
        System.out.println("Total de pacotes: "+totalPacotes);
            
        while ((totalPacotes-WINDOW_SIZE+1-linhaActual>=0)) {
            
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
            
            switch (attack) {
                case -1:  trafic = "?";
                    break;
                case 0:  trafic = "\"sem ataque\"";
                    break;    
                case 1:  trafic = "\"ataque 1\"";
                    break;
                case 2:  trafic = "\"ataque 2\"";
                    break;
                case 3:  trafic = "\"ataque 3\"";
                    break;
                                       
            }
            
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
	}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
    	
        inputFileDirectory = "" + inputFileName;
        
        preProcessedOutputFile="PreProcessing.txt";
        
        relationName = "pacotes";
        
        if (args.length > 0) {
            inputFileDirectory = args[0];
        }
        if (args.length > 1) {
            if (args[1].charAt(0) =='t'){ //gerar testing set
                attack=-1;
            }else{
                attack=Integer.parseInt(args[1]);
            }
        }else{
            attack=0;
        }
        if (args.length > 2) {
            System.out.println("Acrescenta a: "+args[2]);
            outputFileDirectory=args[2];
            append=true;
        } else {
        	outputFileDirectory="";
        }
   
        System.out.println("Parsing...");
        Parser p = new Parser();
        try {
        	p.preProcess(inputFileDirectory);
        	p.generateARFF();
        } catch (Exception e){
        	System.out.println("Exception caught!" + e.getMessage());
        }
            
        System.out.println("Concluido.");
    }
}
