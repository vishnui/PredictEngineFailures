package com.indukuri.predict;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Predict {
	
	private String[] currentFailData ;
	private String[] currentEngineData;
	private String[] initData = {"MSEN", "NAME","RUNTIME", "MILES" ,"MFAIL","MCUSTOMER","OEM_CODE", "NUMFAILCODES"};
	
	private CSVFile engines;
	private CSVFile failClaims;
	private CSVFile allRelevantFailData;
	
	/**
	 * The buck starts here.
	 * @param args
	 */
	public static void main(String[] args) {
		Predict predicter = new Predict();
		System.out.println("Running...");
		predicter.convertData();
		System.out.println("Finished!");
	}
	
	public void createFiles(){
		engines = new CSVFile("C:\\Users\\vishnu\\Desktop\\Engines\\Engines.csv", false);
		failClaims = new CSVFile("C:\\Users\\vishnu\\Desktop\\Engines\\FailClaims.csv", false);
		allRelevantFailData = new CSVFile("C:\\Users\\vishnu\\Desktop\\Engines\\TotalFails.csv", true);
	}
	
	private void writeInitData(){
		allRelevantFailData.writeInitalData(initData);
		currentEngineData = engines.getNextLineData();
	}
	
	private String daysBetween(String servdate, String faildate){
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
		
		return ((endDate.getTime() - startDate.getTime())/86400000)+"";
	}
	
	public void convertData(){
		createFiles();
		writeInitData();
		// Stitch together these two so we can have one coherent
		// complete dataset (in one file)
		while((currentFailData = failClaims.getNextLineData()) != null){
			while(!currentEngineData[0].equals(currentFailData[0])){
				currentEngineData = engines.getNextLineData();
				if(currentEngineData == null) return;
				break;
			}
			
			initData[0] = currentFailData[0];
			initData[1] = currentEngineData[2];
			initData[2] = daysBetween(currentEngineData[9], currentFailData[3]);
			initData[3] = currentFailData[5];
			initData[4] = currentFailData[4];
			initData[5] = currentFailData[1];
			initData[6] = currentEngineData[7];
			initData[7] = currentFailData[10];
			
			allRelevantFailData.writeParameters(initData);
		}
	}
}