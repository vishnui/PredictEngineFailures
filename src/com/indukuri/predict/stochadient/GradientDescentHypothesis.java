package com.indukuri.predict.stochadient;

import com.indukuri.predict.CSVFile;
import com.indukuri.predict.Indexes;

// This class modularizes the cost function and all 
// related mathematics.  This will the home of our
// logistic regression
public class GradientDescentHypothesis {
	private double alpha ;
	
	private double runtimeTHETA=0;
//	private double nameTHETA =0;
	private double runtimeTHETASQUARED=0;
//	private double nameTHETASQUARED =0;
	private double runtimeTHETACUBED=0;
//	private double nameTHETACUBED =0;
	private double runtimeTHETAFOURTH=0;
//	private double nameTHETAFOURTH =0;
	
	private boolean converged = false;
	protected String pastfails = "PastFails.csv";
	private String failcodeY;
	
	public GradientDescentHypothesis(double alpha, String failcode){
		this.alpha = alpha ;
		failcodeY = failcode;
	}
	
	public GradientDescentHypothesis(double alpha, double runtimeT, double nameT, String failcode){
		this.alpha = alpha ;
		runtimeTHETA = runtimeT;
//		nameTHETA = nameT;
		failcodeY = failcode ;
	}
	
	protected double evaluateRaw(String[] parameters){
		double[] numparams = convertToDoubles(parameters);
		
//		double nameThetaX = (numparams[Indexes.Name]/3) * nameTHETA;
		double runtimeThetaX = (numparams[Indexes.Runtime]) * runtimeTHETA ;
//		double nameThetaXSquared = Math.pow((numparams[Indexes.Name]/3),2) * nameTHETASQUARED;
		double runtimeThetaXSquared = (Math.pow((numparams[Indexes.Runtime]),2)/700) * runtimeTHETASQUARED ;
//		double nameThetaXCubed = Math.pow((numparams[Indexes.Name]/3), 3)* nameTHETACUBED;
		double runtimeThetaXCubed = (Math.pow((numparams[Indexes.Runtime]),3)/490000) * runtimeTHETACUBED ;
//		double nameThetaXFourth = Math.pow((numparams[Indexes.Name]/3), 4)* nameTHETAFOURTH;
		double runtimeThetaXFourth = (Math.pow((numparams[Indexes.Runtime])/700,4)/343000000) * runtimeTHETAFOURTH ;
		
		return runtimeThetaX + runtimeThetaXSquared + runtimeThetaXFourth + runtimeThetaXCubed  ;
	}
	
	public double evaluateSigmoid(String[] parameters){
		double raw = evaluateRaw(parameters);
		double inverseSigmoid = 1 + Math.pow(Math.E, -1*raw);
		return 1/inverseSigmoid ;
	}
	
	protected double evaluateCostFunction(){
		String[] data ;
		double cost = 0;
		double m =0;
		double y =0;
		CSVFile pastFails = new CSVFile(pastfails, false);
		while((data = pastFails.getNextLineData()) != null){
			m++;
			if(data[Indexes.Failcode].equals(failcodeY)) y =1;
			else y =0;
			double hofX = evaluateSigmoid(data);
			cost += y*Math.log(hofX) + (1-y)*Math.log(1-hofX);
		}
		pastFails.end();
		return (-1*cost)/m ;
	}
	
	protected double evaluateDerivative(int index){
		double derivative =0;
		double count =0;
		String[] data ;
		double y =0;
		CSVFile pastFails = new CSVFile(pastfails, false);
		while((data = pastFails.getNextLineData()) != null){
			count++ ;
			if(Double.parseDouble(data[Indexes.Failcode]) == Double.parseDouble(failcodeY)){ y =1; }
			else { y =0; }
			derivative += (y - evaluateSigmoid(data)) ;
		}
		pastFails.end();
		return (-2*derivative/count) ;
	}
	
	protected double evaluate2OrderDerivative(int index){
		double derivative =0;
		double count =0;
		String[] data ;
		double y =0;
		CSVFile pastFails = new CSVFile(pastfails, false);
		while((data = pastFails.getNextLineData()) != null){
			count++ ;
			if(Double.parseDouble(data[Indexes.Failcode]) == Double.parseDouble(failcodeY)){ y =1; }
			else { y =0; }
			derivative += (y - evaluateSigmoid(data))*Double.parseDouble(data[index]) ;
		}
		pastFails.end();
		return (-4*derivative/count) ;
	}
	
	protected double evaluate3OrderDerivative(int index){
		double derivative =0;
		double count =0;
		String[] data ;
		double y =0;
		CSVFile pastFails = new CSVFile(pastfails, false);
		while((data = pastFails.getNextLineData()) != null){
			count++ ;
			if(Double.parseDouble(data[Indexes.Failcode]) == Double.parseDouble(failcodeY)){ y =1; }
			else { y =0; }
			derivative += (y - evaluateSigmoid(data))*Math.pow(Double.parseDouble(data[index]),2) ;
		}
		pastFails.end();
		return (-6*derivative/count) ;
	}
	
	protected double evaluate4OrderDerivative(int index){
		double derivative =0;
		double count =0;
		String[] data ;
		double y =0;
		CSVFile pastFails = new CSVFile(pastfails, false);
		while((data = pastFails.getNextLineData()) != null){
			count++ ;
			if(Double.parseDouble(data[Indexes.Failcode]) == Double.parseDouble(failcodeY)){ y =1; }
			else { y =0; }
			derivative += (y - evaluateSigmoid(data))*Math.pow(Double.parseDouble(data[index]),3) ;
		}
		pastFails.end();
		return (-8*derivative/count) ;
	}
	
	private void updateThetas(){
		double runtimeTemp = runtimeTHETA - alpha*evaluateDerivative(Indexes.Runtime) ;
//		double nameTemp = nameTHETA - alpha*evaluateDerivative(Indexes.Name) ; 
		double runtimeTempSQUARED = runtimeTHETASQUARED - alpha*evaluate2OrderDerivative(Indexes.Runtime) ;
//		double nameTempSQUARED = nameTHETASQUARED - alpha*evaluate2OrderDerivative(Indexes.Name) ;
		double runtimeTempCUBED = runtimeTHETACUBED - alpha*evaluate3OrderDerivative(Indexes.Runtime) ;
//		double nameTempCUBED = nameTHETACUBED - alpha*evaluate3OrderDerivative(Indexes.Name) ;
		double runtimeTempFOURTH = runtimeTHETAFOURTH - alpha*evaluate4OrderDerivative(Indexes.Runtime) ;
//		double nameTempFOURTH = nameTHETAFOURTH - alpha*evaluate4OrderDerivative(Indexes.Name) ;
		
		if(Math.abs(runtimeTemp - runtimeTHETA) < 0.00001){ converged = true; }
		runtimeTHETA = runtimeTemp;
//		nameTHETA = nameTemp;
		runtimeTHETASQUARED = runtimeTempSQUARED;
//		nameTHETASQUARED = nameTempSQUARED;
		runtimeTHETACUBED = runtimeTempCUBED;
//		nameTHETACUBED = nameTempCUBED;
		runtimeTHETAFOURTH = runtimeTempFOURTH;
//		nameTHETAFOURTH = nameTempFOURTH;
	}
	
	public double[] runGradientDescent(){
		for(int i=0; i<900;i++){
			updateThetas();
			if(converged) break;
		}
		converged = false;
		
		double[] convhypo = new double[10] ;
		convhypo[Indexes.Runtime] = runtimeTHETA;
//		convhypo[Indexes.Name] = nameTHETA;
		return convhypo;
	}
	
	public static double[] convertToDoubles(String[] parameters){
		if(parameters == null) return null;
		double[] newparams = new double[parameters.length];
		
		for(int i=0; i < parameters.length; i++){
			newparams[i] = Double.parseDouble(parameters[i]);
		}
		
		return newparams ;
	}
	
	public static String[] convertToString(double[] params){
		if(params == null) return null;
		String[] newparams = new String[params.length];
		for(int i=0; i < params.length; i++){
			newparams[i] = params[i] +"";
		}
		
		return newparams;
	}

}
