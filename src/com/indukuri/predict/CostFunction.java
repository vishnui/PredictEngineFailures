package com.indukuri.predict;

public class CostFunction {
	double theta_i[], learningFactorAlpha = 0;
	double x_i[] ;
	
	public double evaluateFunction(){
		double result = 0;
		
		for(int i=0; i < theta_i.length; i++){
			result += theta_i[i]*x_i[i];
		}
		return result ;	
	}

}
