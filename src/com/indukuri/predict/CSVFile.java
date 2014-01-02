package com.indukuri.predict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CSVFile{
	private BufferedReader br ;
	private BufferedWriter bw ;
	private File inputDataFile;
	private String[] parameters;
	private String currentLine ;
	
	public CSVFile(String path, boolean outputFile) {
		try {
			inputDataFile = new File(path) ;
			br = new BufferedReader(new FileReader(inputDataFile));
			bw = new BufferedWriter(new FileWriter(inputDataFile, true));
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening input stream");
			e.printStackTrace(); System.exit(001);
		} catch (IOException e) {
			System.out.println("Problem opening output stream");
			e.printStackTrace(); System.exit(001);
		}
		
		if(outputFile) return;
		
		try {
			currentLine = br.readLine();
			parameters = currentLine.split(",");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem reading initial stream");
			System.exit(002);
		} 
	}
	
	// Close this instance
	public void end(){
		try {
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problem ending stream");
		}
	}
	
	// Writes to file 
	public boolean writeParameters(String[] parameters){
		try {
			for(int i=0; i< parameters.length; i++){
				if(i == parameters.length-1){
					bw.write(parameters[i]); break;
				}
				bw.write(parameters[i]+",");
			}
			bw.newLine();
			bw.flush() ;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problem writing to stream");
			return false;
		}
		return true ;
	}
	
	// Writes initial paramters data
	public boolean writeInitalData(String[] initData){
		writeParameters(initData);
		return true ;
	}
	
	// Gets the next data for the next line.  
	public String[] getNextLineData(){
		try {
			return br.readLine().split(",");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem reading stream");
			return null;
		}
	}
	
	// gets the type of data in each column
	public String[] getParameters(){
		return parameters;
	}
}
