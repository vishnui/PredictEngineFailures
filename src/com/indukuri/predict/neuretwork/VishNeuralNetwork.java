package com.indukuri.predict.neuretwork;
/**
 * Copyright 2014.  All rights reserved to Vishnu Indukuri.
 */

import com.indukuri.predict.CSVFile;

// Not NNetwork by the book.  Let's throw in some
// some random in here and see where that goes
public class VishNeuralNetwork {
	
	// Numbers
	// --------------------------------------------
	int NOROW1NEURONS =1;
	int NOROW2NEURONS =10;
//	int NOROW3NEURONS =50;
//	int NOROW4NEURONS =50;
	int NOOUTPUTNEURONS = 1;
	double epochs = 1000;
	double failCode =1597;
	double learningRate = 0.000001;
	
	// Datamanipulation
	// ----------------------------------------------
    NNDataManipulator nnd = new NNDataManipulator();

	// Process weights
	// ----------------------------------------------
	double[] inputNeurons = new double[NOROW1NEURONS] ;
	double[] row2Neurons = new double[NOROW2NEURONS] ;
//	double[] row3Neurons = new double[NOROW3NEURONS] ;
//	double[] row4Neurons = new double[NOROW4NEURONS] ;
	double[] outputNeurons = new double[NOOUTPUTNEURONS];
	
	double[][] inputNeuronsWeights = new double[NOROW1NEURONS][NOROW2NEURONS] ;
	double[][] secondRowWeights = new double[NOROW2NEURONS][NOOUTPUTNEURONS] ;
//	double[][] thirdRowWeights = new double[NOROW3NEURONS][NOROW4NEURONS] ;
//	double[][] fourthRowWeights = new double[NOROW4NEURONS][NOOUTPUTNEURONS] ;
	double cost=0;
	double lastcost =0;
	
	// Random initialization
	public void setWeights(){
		for(int m=0; m < weights.length; m++){
			for(int i=0; i < weights[m].length; i++){
				for(int j=0; j < weights[m][i].length; j++){
					weights[m][i][j] = Math.random()*-2;
				}
			}
		}
	}
	
	public double getSigmoid(double input){
		return 1/(1+Math.exp(-1*input));
	}
	
	public double inverseSigmoid(double input){
		return Math.log(input/(1-input)) ;
	}
	
	public double getSigmoidDerivative(double input){
		double sigmoid = getSigmoid(input);
		// 0.1 for flat spot 
		return sigmoid*(1-sigmoid) + 0.1;
	}
	
	public void loadInput(String[] data){
//		inputNeurons[0] = Double.parseDouble(data[NNI.SMILES])/600;
		inputNeurons[0] = Double.parseDouble(data[NNI.SMIS])/25;
//		inputNeurons[1] = Double.parseDouble(data[NNI.SBLDTOFAILDAYS]);
//		inputNeurons[2] = Double.parseDouble(data[NNI.SSERVTOFAILDAYS]);
//		inputNeurons[3] = Double.parseDouble(data[NNI.SSHIPTOFAILDAYS]);
//		inputNeurons[1] = Double.parseDouble(data[NNI.SNETAMT])/5000;
	}

	// Make more concise using the weights data structure TODO
	public void evaluate(){
		for(int i=0; i < NOROW2NEURONS; i++){
			row2Neurons[i] =0;
			for(int j=0; j < NOROW1NEURONS; j++){
				row2Neurons[i] += (inputNeuronsWeights[j][i]*inputNeurons[j]) ;
			}
			row2Neurons[i] = getSigmoid(row2Neurons[i]);
		}
		for(int i=0; i < NOOUTPUTNEURONS; i++){
			outputNeurons[i] = 0;
			for(int j=0; j < NOROW2NEURONS; j++){
				outputNeurons[i] += (secondRowWeights[j][i]*row2Neurons[j]) ;
			}
			outputNeurons[i] = getSigmoid(outputNeurons[i]);
		}
//		for(int i=0; i < NOROW4NEURONS; i++){
//			row4Neurons[i] = 0;
//			for(int j=0; j < NOROW3NEURONS; j++){
//				row4Neurons[i] += thirdRowWeights[j][i]*row3Neurons[j] ;
//			}
//			row4Neurons[i] = getSigmoid(row4Neurons[i]);
//		}
//		for(int i=0; i < NOOUTPUTNEURONS; i++){
//			outputNeurons[i] = 0;
//			for(int j=0; j < NOROW4NEURONS; j++){
//				outputNeurons[i] += fourthRowWeights[j][i]*row4Neurons[j] ;
//			}
//			outputNeurons[i] = getSigmoid(outputNeurons[i]);
//		}
	}
	
	public double getFailcodeError(String[] data){
		double y ;
		double thisfailcode = Double.parseDouble(data[NNI.SMFAIL]);
		if(thisfailcode == failCode) y=1;
		else y =0;
		cost += Math.abs(y- outputNeurons[0]);
		return y - outputNeurons[0] ;
	}
	
//	public double getFailDateError(String[] data){
//		return Double.parseDouble(data[NNI.SSHIPTOFAILDAYS]) - outputNeurons[1] ; 
//	}
	
	public void loadOutputErrors(String[] data){
		error[1][0] = getSigmoidDerivative((outputNeurons[0]))*getFailcodeError(data);
//		error[3][1] = getFailDateError(data);
	}
	
	// Error back propagating
	// -----------------------------------------------
	double[][][] weights = { inputNeuronsWeights, secondRowWeights} ;
	double[][] neurons = {inputNeurons, row2Neurons, outputNeurons } ;
	// First row/input has no error associated with it
	double[][] error = {new double[NOROW2NEURONS], new double[NOOUTPUTNEURONS]} ;
	
	public void backPropagateError(){
		for(int row=error.length -2; row >= 0; row--){
			for(int neuron=0; neuron < error[row].length; neuron++){
				calculateError(row, neuron);} }
	}
	
	public void calculateError(int row, int neuron){
		double deltaSum =0;
		for(int i=0; i < weights[row+1][neuron].length; i++){
			deltaSum += error[row+1][i]*weights[row+1][neuron][i] ; 	}
		double f1 = getSigmoidDerivative((neurons[row+1][neuron])) ;
		error[row][neuron] = f1 * deltaSum  ;
	}
	
	public void updateNetworkWeights(){
		for(int row=weights.length-1; row >=0; row--){
			for(int neuron=0;neuron < weights[row].length;neuron++){
				for(int conn=0; conn < weights[row][neuron].length; conn++){
					weights[row][neuron][conn] += learningRate*neurons[row][neuron]*Math.tanh(error[row][conn]) ;} } }
	}
	
	public void train(){
		setWeights();
		for(int epoch=1; epoch < epochs; epoch++){
			String[] data ;
			CSVFile pastFails = new CSVFile("nn/PastFails.csv", false) ;
			while((data = pastFails.getNextLineData()) != null){
				loadInput(data) ;
				evaluate() ;
				loadOutputErrors(data) ;
				backPropagateError() ;
				updateNetworkWeights() ;
			}
			if(cost >= lastcost) learningRate = 0.05*learningRate ;
			lastcost = cost ;
			if(epoch % 5 ==0 ){
				System.out.println(cost) ;
			}
			cost = 0;
			pastFails.end() ;
		}
	}
	
	public void predict(){
		CSVFile failPs = new CSVFile("nn/FailPredictions.csv", true) ;
		failPs.writeInitalData(new String[] { "init line"}) ;
		CSVFile futureFails = new CSVFile("nn/FutureFails.csv", false);
		String[] input ;
		double predc=0;
		String[] predictions = new String[1];
		while((input = futureFails.getNextLineData()) != null){
			loadInput(input);
			evaluate();
			if(outputNeurons[0] > 0.5) predc =1;
			else predc =0;
			predictions[0] = predc+"" ;
//			predictions[1] = outputNeurons[1]+"" ;
			failPs.writeParameters(predictions);
		} 
	}
	
	public void correctPredictions(){
		CSVFile failPs = new CSVFile("nn/FailPredictions.csv", false) ;
		CSVFile futureFails = new CSVFile("nn/FutureFails.csv", false);
		String[] prediction ;
		String[] actual ;
		
		double truetrue =0;
		double falsefalse=0;
		double truefalse =0;
		double falsetrue =0;
		
		while((actual = futureFails.getNextLineData()) != null){
			prediction = failPs.getNextLineData() ;
			if(prediction == null) break ;
			double fail = Double.parseDouble(actual[NNI.SMFAIL]);
			double predc = Double.parseDouble(prediction[0]);
			if(fail == failCode && predc ==1){
				truetrue++;
			}
			if(fail != failCode && predc ==0){
				falsefalse++ ;
			}
			if(fail != failCode && predc == 1){
				truefalse++;
			}
			if(fail == failCode && predc == 0){
				falsetrue++;
			}
			
//			daysoff += Math.abs(Double.parseDouble(actual[NNI.SSHIPTOFAILDAYS]) - Double.parseDouble(prediction[1])) ;
		}
		System.out.println("True and Correct: "+truetrue+". False and correct: "+falsefalse);
		System.out.println("True and wrong: "+truefalse+". False and wrong: "+falsetrue);
//		System.out.println("Avg. days off: "+(daysoff/total));
	}
	
	public static double[] getRelvantData(String[] data){
		double[] newdata = new double[18];
		for(int i=0; i< 18; i++){
			newdata[i] = Double.parseDouble(data[i]);
		}
		
		return newdata ;
	}
	
	public static String[] convertToString(double[] data){
		String[] newdata = new String[data.length] ;
		for(int i=0; i< newdata.length; i++){
			newdata[i] = data[i]+"";
		}
		return newdata ;
	}
	public static void main(String[] args){
//		NNDataManipulator nnd = new NNDataManipulator() ;
//		nnd.stitch();
		VishNeuralNetwork nn = new VishNeuralNetwork() ;
		nn.train();
		nn.predict();
		nn.correctPredictions();
	}
}
