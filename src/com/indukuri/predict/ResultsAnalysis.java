package com.indukuri.predict;

// This class analyzes the quality of our predictions.
public class ResultsAnalysis {
	CSVFile predictions ;
	CSVFile actualFuture;
	
	public ResultsAnalysis(){
		// This file will contain only FAILCODES
		predictions = new CSVFile("FailPredictions.csv", false);
		
		// And this file contains the actual FAILCODES along
		// with all other data as well
		actualFuture = new CSVFile("FutureFails.csv", false);
	}
	
	public double percentCorrect(){
		String prediction;
		String[] actual;
		double total = 0;
		double correct =0;
		while((actual = actualFuture.getNextLineData()) != null){
			String[] predictionData = predictions.getNextLineData();
			if(predictionData == null) break ;
			prediction = predictionData[0];
			total++;
			if(Double.parseDouble(prediction) == Double.parseDouble(actual[Indexes.Failcode])) correct++ ;
			System.out.println("Prediction: "+predictionData[0]+".  Actual: "+actual[Indexes.Failcode]);
		}
		close();
		return correct/total ;
	}
	
	private void close(){
		actualFuture.end();
		predictions.end();
	}

}
