package pt.ist.sirs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class PreProcessor {
	private int byteCount;
	
	/**
	 * @param byteCount Number of bytes to save from each packet header
	 */
	public PreProcessor(int byteCount) {
		this.byteCount = byteCount;
	}
	
	/*
	 * Turns K14 file to byte-only easy-to-parse text file.
	 */
	public void parse(String k14FileName, String preProcessedFileName) throws IOException {
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(k14FileName));
		
		/* Convert every line to corresponding bit format */
		String line = null;
		String result = new String();
		while((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, "|", false);
			
			/* Ignore useless lines */
			if(st.countTokens() < 2) {
				continue;
			}
			
			st.nextToken();
			for(int i = 0; i < byteCount & st.hasMoreTokens(); i++) {
				String nt = st.nextToken();
				
				String hex = Integer.toBinaryString(Integer.parseInt(nt, 16));
				
				while(hex.length() < 8) {
					hex = "0" + hex;
				}
				
				result += hex;
			}
			result += "\n";
		}
		
		reader.close();
		
		/* Save the pre-processed file */
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(preProcessedFileName));
		
		writer.write(result);
		
		writer.close();
	}

}
