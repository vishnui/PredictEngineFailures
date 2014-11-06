package com.indukuri.predict;

import com.indukuri.predict.stochadient.GradientDescentHypothesis;

public class Control {
	private static double alpha = 0.1 ;
	
	/**
	 * The buck starts here.
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException{
//		DataManipulator converter = new DataManipulator();
//		converter.splitPastFutureData();
//		System.out.println("Data Split");
		
		// learn
//		CSVFile hypotheses = new CSVFile("Hypotheses.csv", true);
		CSVFile failfreq = new CSVFile("FailFreq.csv", false);
		String[] data ;
		while((data = failfreq.getNextLineData()) != null){
//			if(Double.parseDouble(data[0]) <= 2411){ continue; }
//			System.out.println("Calculating fail code number "+data[0]+"...");
			GradientDescentHypothesis ch = new GradientDescentHypothesis(alpha, data[0]);
//			double[] thetas = ch.runGradientDescent();
//			thetas[0] = Double.parseDouble(data[0]);
//			hypotheses.writeParameters(GradientDescentHypothesis.convertToString(thetas));
		}
//		hypotheses.end();
//		System.out.println("Data Learnt");
		
		// predict
		String[] actualData;
		double[] hypoData;
		CSVFile actual = new CSVFile("FutureFails.csv", false);
		CSVFile predictions = new CSVFile("FailPredictions.csv", true);
		String highestFailcode = "";
		double highestSigmoid= -2;
		while((actualData = actual.getNextLineData()) != null){
			CSVFile hypotheses = new CSVFile("Hypotheses.csv", false);
			while((hypoData = GradientDescentHypothesis.convertToDoubles(hypotheses.getNextLineData())) != null){
				GradientDescentHypothesis thisHypo = new GradientDescentHypothesis(alpha, hypoData[Indexes.Runtime], hypoData[Indexes.Name],hypoData[0]+"");
				
				double newhigh = thisHypo.evaluateSigmoid(actualData);
				if(newhigh > highestSigmoid){
					highestSigmoid = newhigh ;
					highestFailcode = hypoData[0] +"" ;
				}
			}
			hypotheses.end();
			String[] failcode = { highestFailcode } ;
			predictions.writeParameters(failcode);
		}
		
		ResultsAnalysis ra = new ResultsAnalysis();
		System.out.println(ra.percentCorrect());
//		
//		DataManipulator.writeSingleFailCodeFile("1597", "PastFails");
//		DataManipulator.writeSingleFailCodeFile("1597", "FutureFails");
		
	} 
}
