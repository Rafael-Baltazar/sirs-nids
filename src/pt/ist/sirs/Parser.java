package pt.ist.sirs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class Parser {

    public static String caminhoDadosIn;
    public static String caminhoDadosOut;
    public static String caminhoDadosOutAppend;
    public static String caminhoDadosPre;
    public static String nomeDaRelacao;
    public static int ataque;
    public static boolean append=false;
    public void readFromFile(String filename) {

        BufferedReader bufferedReader = null;
        BufferedWriter bufferedPre = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReadPre = null;

        try {
            
            if (ataque==-1){ //testset
                caminhoDadosOut=caminhoDadosIn.replaceAll(".txt","")+"_testSet.arff";
            }else{ //trainer
                caminhoDadosOut=caminhoDadosIn.replaceAll(".txt","")+"_trainerSet.arff";
            }
            
            if (append){
                caminhoDadosOut=caminhoDadosOutAppend;
            }
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
            System.out.println("FileIn: " + caminhoDadosIn);
            System.out.println("PreProcessing: " + caminhoDadosPre);
            if (append){
                System.out.println("Fileout (appending): " + caminhoDadosOutAppend);
            }else{
                System.out.println("Fileout: " + caminhoDadosOut);
            }
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
            System.out.println("Pre-processamento...");

            //declara�oes dos buffers read e write
            FileWriter fstreamPre = new FileWriter(caminhoDadosPre);
            bufferedReader = new BufferedReader(new FileReader(filename));
            bufferedReadPre = new BufferedReader(new FileReader(caminhoDadosPre));
       
            FileWriter fstream = new FileWriter(caminhoDadosOut,append);          
            
            
            bufferedPre = new BufferedWriter(fstreamPre);
            bufferedWriter = new BufferedWriter(fstream);

            

            String line = null;
            //String parameters = bufferedReader.readLine();
            String tipo = null;
            String timeStamp=null;
            String headerHEX=null;
            
            String _tipoTrafego="";
            
            
            
            if (ataque>=0){
            _tipoTrafego=String.valueOf(ataque);
            if (ataque==0){
                    System.out.println("Trafego sem ataque.");
            }
            }else{
                    System.out.println("Testing Set.");
            }
            //parameters = StringUtils.remove(parameters, '"');
            // System.out.println("Linha dos parametros: " + parameters);
           //defini�ao dos campos dos dados (colunas)

            
            
            int i = 1;
            int byteCount;
            
            
            String[] BINbits = new String[54];

            String lineBIN = "";
            
            String nextHEX;
            //Collection HEXbytes=new Collection();
            
            
            
            while ((line = bufferedReader.readLine()) != null) {
                byteCount=0;
                //line contem "+---------+---------------+----------+");
                timeStamp=bufferedReader.readLine();
                headerHEX=bufferedReader.readLine();
                bufferedReader.readLine();
                      
                
                //System.out.println("Pack "+i+" "+headerHEX);
                
                StringTokenizer st = new StringTokenizer(headerHEX, "|");
                st.nextToken();
                
                while(st.hasMoreTokens() && byteCount<54){
                    
                    nextHEX=st.nextToken();
                    
                    //System.out.print(Integer.toBinaryString(Integer.parseInt(nextHEX, 16)));
                    
                    BINbits[byteCount]=Integer.toBinaryString(Integer.parseInt(nextHEX, 16));
                    //System.out.print(" "+nextHEX);
                //System.out.println("byte2 "+st.nextToken());
               
                byteCount++;
                }
                //System.out.println("\nContei "+byteCount);
                if (byteCount==54){ //apanhou um pacote com o tamanho desejado grava no out
                  
                    for (int number = 0; number <= 53; number++) {
                        lineBIN=lineBIN.concat(BINbits[number]);
                    }
                    
                    
                bufferedPre.write(lineBIN);
                bufferedPre.newLine();
                //System.out.println(lineBIN);
                    
                }

                
                //System.out.println("Outra linha");
                lineBIN="";
                i++;
            }
            /*************************************DONE READING*****************************************/
            
            System.out.println("...pre-processamento concluido.");
            
            
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedPre != null) {
                    bufferedPre.close();
                }                

            } catch (IOException ex) {
                ex.printStackTrace();
            }
 
            System.out.println("A gerar ARFF...");

            bufferedReadPre = new BufferedReader(new FileReader(caminhoDadosPre));
            
            int janelaPacotes=50;                                           //numero de linhas a tratar
            float media;                                                    //media 1 por pacote              
            float desvioPadrao;                                             //desvio padrao
            int somaBits=0;                                                 //soma bits a 1 por janela
            int somaBitsLinha=0;                                            //soma bits a 1 por pacote
            double somaBitsLinhas[]=new double[janelaPacotes];
            int linhaActual=1;
            int totalPacotes=countLines(caminhoDadosPre);
            int janelasProcessadas=0;
            String arffLine="";
            
            if (!append){
            bufferedWriter.write("@RELATION relation\n"
+"@ATTRIBUTE tipo {\"sem ataque\",\"ataque 1\",\"ataque 2\",\"ataque 3\"}\n"
+"@ATTRIBUTE soma NUMERIC\n"
+"@ATTRIBUTE media NUMERIC\n"
+"@ATTRIBUTE desvio NUMERIC\n\n"
+"@DATA\n");
            }
            
            System.out.println("Janela de pacotes: "+janelaPacotes);
            System.out.println("Total de pacotes: "+totalPacotes);
            
            while ((totalPacotes-janelaPacotes+1-linhaActual>=0)) {
                
                //coloca bookmark para poder voltar sem ter de carregar o ficheiro novamente
                bufferedReadPre.mark(300000);
                
                //inicializacao de variaveis
                somaBits=0;
                media=0;
                desvioPadrao=0;
                arffLine="";
                
                //calculo da soma de bits a 1
                for (int linhaJanela=1;linhaJanela<=janelaPacotes;linhaJanela++){
                line = bufferedReadPre.readLine();
                somaBitsLinha=count(line,"1");
                somaBitsLinhas[linhaJanela-1]=somaBitsLinha;
                somaBits=somaBits+somaBitsLinha;
                }
                //Aqui j� processou uma janela inteira
   
                
                //calculo da m�dia 1's por pacote ---> somatotal/54
                media=somaBits/((float)janelaPacotes);
               
                //calculo do desvio padrao
                //desvioPadrao=(float)StandardDeviation.StandardDeviationMean(somaBitsLinhas);
                desvioPadrao = 99;
                switch (ataque) {
                    case -1:  _tipoTrafego = "?";
                        break;
                    case 0:  _tipoTrafego = "\"sem ataque\"";
                        break;    
                    case 1:  _tipoTrafego = "\"ataque 1\"";
                        break;
                    case 2:  _tipoTrafego = "\"ataque 2\"";
                        break;
                    case 3:  _tipoTrafego = "\"ataque 3\"";
                        break;
                                           
                }
                
                //gera entrada para o ficheiro
                DecimalFormat form = new DecimalFormat("0.00");
                arffLine=_tipoTrafego+","+somaBits+","+((String)form.format(media)).replace(",", ".") +","+(String)form.format(desvioPadrao).replace(",", ".")+"\n";
                
                //Escreve no ficheiro
                bufferedWriter.write(arffLine);
                bufferedReadPre.reset();
                bufferedReadPre.readLine();
                linhaActual++;
                janelasProcessadas++;
                

                
            }
            System.out.println("Janelas processadas: "+janelasProcessadas);
            
           
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //fechar os buffers
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (bufferedReadPre != null) {
                    bufferedReadPre.close();
                }                

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    
  public int count(String input, String countString){
             return input.split("\\Q"+countString+"\\E", -1).length - 1;
 }
  
  public int countLines(String filename) throws IOException {
    LineNumberReader reader  = new LineNumberReader(new FileReader(filename));
    int cnt = 0;
    String lineRead = "";
    while ((lineRead = reader.readLine()) != null) {}

    cnt = reader.getLineNumber(); 
    reader.close();
    return cnt;
}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //ficheiros por omissao
        caminhoDadosIn = "C:\\Users\\Un\\Desktop\\LLS_DDOS_1.0-inside.txt";
        caminhoDadosPre="PreProcessing.txt";
        
        nomeDaRelacao = "pacotes";
        if (args.length > 0) {
            caminhoDadosIn = args[0];
        }
        if (args.length > 1) {
                        //System.out.println("---"ataque+"----");
            if (args[1].charAt(0) =='t'){ //gerar testing set
                ataque=-1;
            }else{
                ataque=Integer.parseInt(args[1]);
            }
        }else{
            ataque=0;
        }
        if (args.length > 2) {
            System.out.println("Acrescenta a: "+args[2]);
            caminhoDadosOutAppend=args[2];
            append=true;
        }
   
            System.out.println("Parsing...");
        new Parser().readFromFile(caminhoDadosIn);
            
            System.out.println("Concluido.");
    }
}
