package com.indukuri.predict.neuretwork;

import java.util.ArrayList;
import java.util.Date;

import com.indukuri.predict.CSVFile;

/** This motherfucking class takes the motherfucking data that some 
 * dumbass motherfucker put in two different files and stitches them 
 * together and assigns each motherfucking fail a unique id.  Then it 
 * splits the goddamn data up into two files again.  The training 
 *  set and the test set.  the past data and the future data.
 */
public class NNDataManipulator{
	
	protected CSVFile engines;
	protected CSVFile failClaims;
	protected CSVFile pastFormattedData;
	protected CSVFile futureFormattedData;
	
	protected String[] cfD ;
	protected String[] ceD;
	
	public NNDataManipulator(){
		String[] minitData = new String[31] ;
		
		initData = minitData ;
	}
	public void createFiles(String parent){
		engines = new CSVFile("Engines.csv", false);
		ceD = engines.getNextLineData();
		
		failClaims = new CSVFile("FailClaims.csv", false);
		pastFormattedData = new CSVFile(parent+"PastFails.csv", true);
		futureFormattedData = new CSVFile(parent+"FutureFails.csv", true);
	}
	protected void writeInitData(){
//		pastFormattedData.writeInitalData(initData);
//		futureFormattedData.writeInitalData(initData);
	}
	protected void writeToFile(){
		if(isFuture(cfD[NNI.FFAILDATE])){
			futureFormattedData.writeParameters(initData);
		}
		else {
			pastFormattedData.writeParameters(initData);
		}
	}
	protected boolean isFuture(String date){
		Date faildate = getDate(date) ;
		Date cutoffdate = getDate("02/30/2012");
		
		return faildate.after(cutoffdate) ;
	}
	protected String daysBetween(String servdate, String faildate){
		Date startDate = getDate(servdate);
		Date endDate = getDate(faildate) ;
		
		long time = (endDate.getTime() - startDate.getTime())/86400000 ;
		if(time <= 0) return null;
		return time+"";
	}
	
	
	// OUR DATA POINTS
	// ---------------------------------
	protected String[] initData ;                  
	
	/**
	 * Main method of this class orchestrates the
	 * parsing and stitching of the data from the 
	 * two sources
	 */
	@SuppressWarnings("deprecation")
	public void stitch(){
		createFiles("nn/");
		writeInitData();
		
		// Stitch together these two so we can have one coherent
		// complete dataset (in one file)
		while((cfD = failClaims.getNextLineData()) != null){
			if(cfD[0].equals("#N/A")) continue;
			while(!ceD[0].equals(cfD[0])){
				ceD = engines.getNextLineData();
			}
			Date bld = getDate(ceD[NNI.EBLDDATE]) ;
			initData[0] = bld.getMonth() + "" ;
			initData[1] = bld.getDate() + "";
			
			initData[2] = ceD[NNI.ENAME] ;
			if(initData[2] == null) continue ;
			
			initData[3] = processEDPhase(ceD[NNI.EDPHASE]);
			if(initData[3] == null) continue ;
			
			initData[4] = processEMHP(ceD[NNI.EMHP]);
			if(initData[4] == null) continue ;
			
			initData[5] = processEMRPM(ceD[NNI.EMRPM]);
			if(initData[5] == null) continue ;
			
			Date shp = getDate(ceD[NNI.ESHPDATE]);
			if(shp == null) continue ;
			initData[6] = shp.getMonth()+"";
			initData[7] = shp.getDate()+"";
			initData[8] = daysBetween(ceD[NNI.EBLDDATE], ceD[NNI.ESHPDATE]);
			if(initData[8] == null) continue ;
			initData[9] = processEOMCODE(ceD[NNI.EOEMCODE]) ;
			if(initData[9] == null) continue ;
			
			initData[10] = processEUAPP(ceD[NNI.EUAPP]);
			if(initData[10] == null) continue ;
			
			Date srv = getDate(ceD[NNI.ESERVDATE]);
			initData[11] = srv.getMonth()+"";
			initData[12] = srv.getDate()+"";
			
			initData[13] = daysBetween(ceD[NNI.ESHPDATE], ceD[NNI.ESERVDATE]);
			if(initData[13] == null) continue ;
			
			initData[14] = daysBetween(ceD[NNI.EBLDDATE], ceD[NNI.ESERVDATE]);
			if(initData[14] == null) continue ;
			
			initData[15] = ceD[NNI.EREGION];
			
			initData[16] = processECPL(ceD[NNI.ECPL]);
			if(initData[16] == null) continue ;
			
			initData[17] = processEConfig(ceD[NNI.EMCONFIG]);
			if(initData[17] == null) continue ;
			
			initData[18] = ceD[NNI.EMMODEL];
			if(initData[18] == null) continue ;
			
			initData[19] = cfD[NNI.FMCUSTOMER];
			if(initData[19] == null) continue ;
			
			initData[20] = cfD[NNI.FMIS];
			Date fail= getDate(cfD[NNI.FFAILDATE]);
			initData[21] = fail.getMonth()+"";
			initData[22] = fail.getDay()+"";
			initData[23] = cfD[NNI.FMFAIL];
			if(initData[23].equals("742") || initData[23].equals("2533")) continue;
			initData[24] = cfD[NNI.FMILES];
			
			initData[25] = cfD[NNI.FDISTR];
			if(initData[25] == null) continue ;
			
			initData[26] = cfD[NNI.FNETAMT];
			initData[27] = cfD[NNI.FNUMFAILCODES];
			
			initData[28] = daysBetween(ceD[NNI.EBLDDATE],cfD[NNI.FFAILDATE]);
			if(initData[28] == null) continue ;
			initData[29] = daysBetween(ceD[NNI.ESHPDATE],cfD[NNI.FFAILDATE]);
			if(initData[29] == null) continue ;
			initData[30] = daysBetween(ceD[NNI.ESERVDATE],cfD[NNI.FFAILDATE]);
			if(initData[30] == null) continue ;
//			
			writeToFile();
		}
	}
	
	// DATA PARSING METHODS 
	// -----------------------------------
	@SuppressWarnings("deprecation")
	public Date getDate(String date){
		if(date == null || date.equals("")) return null;
		String[] serv = date.split("/");
		if(serv[0].substring(0,1).equals(" ")){
			serv[0] = serv[0].substring(1);
		}
		Date startDate = new Date() ;
		startDate.setMonth(Integer.parseInt(serv[0])) ;
		startDate.setDate(Integer.parseInt(serv[1])) ;
		
		int year = Integer.parseInt(serv[2]);
		if(year < 2000) startDate.setYear(year + 2000) ;
		else startDate.setYear(year);
		
		return startDate;
	}
	public String processEUAPP(String app){
		if(app == null || app.equals("")) return null; 
		if(app.equals("AUTO")) return "1" ;
		if(app.equals("RV")) return "0" ;
		if(app.equals("FIRE TRUCK")) return "2";
		if(app.equals("BUS  NOT SCHOOL")) return "3" ;
		if(app.equals("NO WARRANTY")) return "4" ;
		
		return null ;
	}
	public String processEConfig(String config){
		if(config == null || config.equals("")) return null ;
		int scode = Integer.parseInt(config.substring(2)) ;
		int prefix = 0;
		char sl = config.charAt(1);
		if(sl == 'V') prefix = 1;
		else if(sl == 'F') prefix = 2 ;
		else if(sl == 'U') prefix = 3;
		else if(sl == 'G') prefix = 4;
		
		return (prefix*100 + scode)/500 +"" ;
	}
	public String processECPL(String ECPL){
		if(ECPL == null || ECPL.equals("")) return null ;
		switch(Integer.parseInt(ECPL)){
			case 3491: return "0" ;
			case 3490: return "1" ;
			case 3349: return "2" ;
			case 8073: return "3" ;
			case 8760: return "4" ;
			case 3458: return "5" ;
			case 3329: return "6" ;
			case 3087: return "7" ;
			case 3606: return "8" ;
			default: return null ;
		}
	}
	public String processEDPhase(String phase){
		switch(Integer.parseInt(phase)){
			case 52: return "1" ;
			case 51: return "2" ;
			case 53: return "3" ;
			default: return null ;
		}
	}
	public String processEMHP(String mhp){
		if(mhp == null || mhp.equals("")) return null;
		switch(Integer.parseInt(mhp)){
			case 450: return ".1" ;
			case 485: return ".2" ;
			case 425: return ".3" ;
			case 400: return ".4" ;
			case 500: return ".5" ;
			case 600: return ".6" ;
			case 435: return ".7" ;
			case 455: return ".8" ;
			case 550: return ".9" ;
			case 525: return ".10" ;
			default: return null ;
		}
	}
	public String processEMRPM(String mrpm){
		if(mrpm == null || mrpm.equals("")) return null;
		switch(Integer.parseInt(mrpm)){
			case 1800: return ".1";
			case 1700: return ".2";
			case 1900: return ".3";
			case 1600: return ".4";
			case 2000: return ".5";
			default: return null ;
		}
	}
	public String processEOMCODE(String code){
		if(code == null || code.equals("")) return null ;
		switch(Integer.parseInt(code)){
			case 1442: return "0" ;
			case 3987: return "1" ;
			case 1608: return "2" ;
			case 2822: return "3" ;
			case 75147:return "4" ;
			case 1306: return "5" ;
			case 2823: return "6" ;
			case 75414:return "7" ;
			case 1457: return "8" ;
			case 4275: return "9" ;
			case 54598:return "10";
			case 76872:return "11";
			case 1950: return "12";
			case 1485: return "13";
			case 1310: return "14";
			case 3520: return "15";
			case 76480:return "16";
			case 4972: return "17";
			case 2934: return "18";
			case 0:    return "19";
			case 5348: return "20";
			case 2876: return "21";
			case 2493: return "22";
			case 2254: return "23";
			case 2997: return "24";
			case 4959: return "25";
			case 2402: return "26";
			case 2881: return "27";
			case 73282:return "28";
			case 77007:return "29";
			case 5675: return "30";
			case 2331: return "31";
			default: return null ;
		}
	}
	
	// Preliminary analysis
	// -----------------------------------
	public void getUniqueEngineVals(int index){
		engines = new CSVFile("Engines.csv", false);
		ArrayList<String> uniquevals = new ArrayList<String>() ;
		while((ceD = engines.getNextLineData()) != null){
			if(uniquevals.contains(ceD[index])) continue ;
			else uniquevals.add(ceD[index]) ;
		}
		printArrayList(uniquevals);
	}
	public void printArrayList(ArrayList<String> array){
		for(String x : array){
			System.out.println(x);
		}
	}
	public void getUniqueFailVals(int index){
		failClaims = new CSVFile("FailClaims.csv", false);
		ArrayList<String> uniquevals = new ArrayList<String>() ;
		while((cfD = failClaims.getNextLineData()) != null){
			if(uniquevals.contains(cfD[index])) continue ;
			else uniquevals.add(cfD[index]) ;
		}
		printArrayList(uniquevals);
	}
}
