package com.indukuri.predict;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.indukuri.predict.neuretwork.NNI;


/** This motherfucking class takes the motherfucking data that some 
 * dumbass motherfucker put in two different files and stitches them 
 * together and assigns each motherfucking fail a unique id.  Then it 
 * splits the cocksucking data up into two files again.  The training 
 *  set and the test set.  the past data and the future data.
 */
public class DataManipulator {
	
	protected String[] cfD ;
	protected String[] ceD;
	protected String[] initData = {"UUID", "MSEN", "NAME","RUNTIME", "MILES" , "MIS","MFAIL","MCUSTOMER","OEM_CODE", "NUMFAILCODES"};
	
	protected CSVFile engines;
	protected CSVFile failClaims;
	protected CSVFile pastFormattedData;
	protected CSVFile futureFormattedData;
	
	protected int counterUUID = 0;
	
	public void createFiles(String parent){
		engines = new CSVFile("Engines.csv", false);
		ceD = engines.getNextLineData();
		
		failClaims = new CSVFile("FailClaims.csv", false);
		pastFormattedData = new CSVFile(parent+"PastFails.csv", true);
		futureFormattedData = new CSVFile(parent+"FutureFails.csv", true);
	}
	
	protected void writeInitData(){
		pastFormattedData.writeInitalData(initData);
		futureFormattedData.writeInitalData(initData);
	}
	
	protected String daysBetween(String servdate, String faildate){
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = new SimpleDateFormat("MM/dd/yyyy").parse(servdate);
			endDate = new SimpleDateFormat("MM/dd/yyyy").parse(faildate);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Problem parsing dates");
			System.exit(003);
		}
		long time = (endDate.getTime() - startDate.getTime())/86400000 ;
		if(time <= 0) return null;
		return time+"";
	}
	
	protected boolean isFuture(String date){
		Date faildate = null ;
		Date cutoffdate = null;
		try {
			faildate = new SimpleDateFormat("MM/dd/yyyy").parse(date) ;
			cutoffdate = new SimpleDateFormat("MM/dd/yyyy").parse("02/30/2012");
		} catch (ParseException e) { 	}
		
		return faildate.after(cutoffdate) ;
	}
	
	public void splitPastFutureData(){
		createFiles("");
		writeInitData();
		
		// Stitch together these two so we can have one coherent
		// complete dataset (in one file)
		while((cfD = failClaims.getNextLineData()) != null){
			if(cfD[0].equals("#N/A")) continue;
			while(!ceD[0].equals(cfD[0])){
				ceD = engines.getNextLineData();
			}
			
			initData[0] = counterUUID++ + "";
			initData[1] = cfD[0];
			initData[2] = ceD[2];
			initData[3] = daysBetween(ceD[9], cfD[3]);
			if(initData[3] == null) continue;
			initData[4] = cfD[5];
			initData[5] = cfD[2];
			initData[6] = cfD[4];
			if(initData[6].equals("742") || initData[6].equals("2533")) continue;
			initData[7] = cfD[1];
			initData[8] = ceD[7];
			initData[9] = cfD[10];
			
			writeToFile();
		}
		close() ;
	}
	
	protected void writeToFile(){
		if(isFuture(cfD[NNI.FFAILDATE])){
			futureFormattedData.writeParameters(initData);
		}
		else {
			pastFormattedData.writeParameters(initData);
		}
	}
	
	public static void largestVolumeFailcodes(){
		double total =0;
		double counted =0;
		String[] data ;
		CSVFile failfreq = new CSVFile("FailFreq.csv", true);
		for(int i =1; i < 2700; i++){
			CSVFile pastFails  = new CSVFile("PastFails.csv", false);
			double occurrences =0;
			while((data = pastFails.getNextLineData()) != null){
				if(data[6].equals(i+"")){
					occurrences++ ;
				}
			}
			total += occurrences ;
			if(occurrences < 100) continue ;
			counted += occurrences;
			
			pastFails.end();
			String[] params = {i+"",occurrences+""};
			failfreq.writeParameters(params);
		}
		failfreq.end();
		
		System.out.println("Total Fails: "+total);
		System.out.println("Counted above 100: "+counted);
		System.out.println("Percentage above 100: "+(counted/total));
	}
	
	public static void countFailcode(String failcode, String file){
		CSVFile pastFails  = new CSVFile(file, false);
		double occurrences =0;
		String[] data ;
		while((data = pastFails.getNextLineData()) != null){
			if(Double.parseDouble(failcode) == Double.parseDouble(data[6])){
				occurrences++ ;
			}
		}
		System.out.println("Failcode "+failcode+".  Occurrences: "+occurrences+".  In "+file);
	}
	
	public static void writeSingleFailCodeFile(String failcode, String file){
		CSVFile pastFails  = new CSVFile(file+".csv", false);
		CSVFile futureFails = new CSVFile("/SingleFailcode/"+file+failcode+".csv", true);
		String[] data ;
		while((data = pastFails.getNextLineData()) != null){
			if(Double.parseDouble(failcode) == Double.parseDouble(data[6])){
				futureFails.writeParameters(data);
			}
		}
	}
	
	private void close(){
		engines.end();
		failClaims.end();
		futureFormattedData.end();
		pastFormattedData.end();
	}
}
