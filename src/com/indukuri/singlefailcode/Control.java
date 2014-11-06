package com.indukuri.singlefailcode;

import com.indukuri.predict.CSVFile;
import com.indukuri.predict.stochadient.GradientDescentHypothesis;

public class Control {

	public static void main(String[] args){
		GradientDescentHypothesis ch = new GradientDescentHypothesis(0.1,"1597");
		ch.runGradientDescent();
		
		CSVFile actual = new CSVFile("FutureFails.csv", false) ;
		String[] data;
		double predicted = 0;
		double correct = 0;
		double total =0;
		double correctlyPredicted =0;
		while((data = actual.getNextLineData()) != null){
			total++;
			if(Double.parseDouble("1597") == Double.parseDouble(data[6])){
				correct++;
			}
			if(	ch.evaluateSigmoid(data) > 0.5){
				predicted++;
				if(Double.parseDouble("1597") == Double.parseDouble(data[6])){
					correctlyPredicted++;
				}
			}
		}
		System.out.println("Total Fails: "+total);
		System.out.println("Predicted: "+predicted+".  Total Failcode: "+correct+".  Percent correct: "+(predicted/correct));
		System.out.println("Correctly Predicted: "+correctlyPredicted+".  Total Predicted: "+predicted+".  Percent correct: "+(correctlyPredicted/predicted));
	}
}
