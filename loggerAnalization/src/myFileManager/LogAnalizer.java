package myFileManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogAnalizer {

	// constant to catch interesting lines of the file
	public static final String NEW_DATA_SET = " NEW DATA SET, on ";
	public static final String REASONING_TIME = "Updater ends in:";//" ends in:";//
	public static final String ACTIVITY_RECOGNISED = " recognised !!!!!!!! ";
	public static final String DATA_IMPORTING = "Importing data:";	
	// constant to catch interesting data in such line
	public static final String TIME_SEPARATOR = " -> ";
	public static final String RECOGNISED_NUMBER = "P_Task"; 
	public static final String TIMING_SYMB = ":";
	public static final String TIMING_SYMB2 = "[ms]";
	public static final String IMPORTING_DATA_SYMB = "]"; 
	
	public static final String VIRTUALdethacer = "Updater : ";
	public static final String NUMBERofActivation = " activated virtual sensors: [";
	public static final String NumberofSensor = "OntologyFramework/SmartHome/Task1Description#";
	
	
	public static final  String AXIOMdethacer = "synchronising... reasoner.flush() for ontology named:";
	public static final  String AXIOMontologyName = "OntologyFramework/accelerometer/";//"OntologyFramework/SmartHome/";//
	public static final  String ReasonigTimeNano = ". Reasoning Time:";
	public static final  String AxiomCount = ">))) [Axioms: ";
	public static final  String LOGICAxiomCount = "Logical Axioms:";
	public static final  String AXIOMTaskName = "[ns] over ontology:";
	public static final  String Axiomreasoned= "] reasoned axioms";
	
	public static final String ERROR_dethacer = "computed error: ";
	public static final String NUMBER_errorDethacer = "-> Class Task";
	public static final String SEPARATOR_errorDethacer = "threshoulding error (>)";
	
	// constant to represent multi data
	public static final String SEPARATOR_SYMB = ",\t";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private String dataSetPath;
	private List< LoggerInfo> reasoningTime = new ArrayList< LoggerInfo>();
	private List< LoggerInfo> recognised = new ArrayList< LoggerInfo>();
	private List< LoggerInfo> label = new ArrayList< LoggerInfo>();
	private List< LoggerInfo> reasoningTimeAxiom = new ArrayList< LoggerInfo>();
	private List< LoggerInfo> virtualSensor = new ArrayList< LoggerInfo>();
	private List< LoggerInfo> error = new ArrayList< LoggerInfo>();

	public LogAnalizer( String pathToAnalizedFile){
		this.dataSetPath = pathToAnalizedFile;
	}
	
	public void addReasoningTime( Long time, String info){
		reasoningTime.add( new LoggerInfo( time, info));
	}
	
	public void addRecognised( Long time, String info){
		recognised.add(new LoggerInfo( time, info));
	}
	
	public void addLabel( Long time, String info){
		label.add( new LoggerInfo( time, info));
	}
	
	public void addTimeAxiom( Long time, String info){
		reasoningTimeAxiom.add( new LoggerInfo( time, info));
	}
	
	public void addVirtualSensor( Long time, String info){
		virtualSensor.add( new LoggerInfo( time, info));
	}
	
	public void addError( Long time, String info){
		error.add( new LoggerInfo( time, info));
	}

	public String getDataSetPath() {
		return dataSetPath;
	}
	public String getDataSetName() {
		return dataSetPath.substring( dataSetPath.lastIndexOf("/"));
	}

	public List<LoggerInfo> getReasoningTime() {
		return reasoningTime;
	}
	public List<String> getReasoningTimeString(){
		List<String> out = new ArrayList<String>();
		for( LoggerInfo i : getReasoningTime())
			out.add( i.toString());
		return( out);
	}

	public List<LoggerInfo> getRecognised() {
		return recognised;
	}
	public List<String> getRecognisedString(){
		List<String> out = new ArrayList<String>();
		for( LoggerInfo i : getRecognised())
			out.add( i.toString());
		return( out);
	}

	public List<LoggerInfo> getLabel() {
		return label;
	}
	public List<String> getLabelString(){
		List<String> out = new ArrayList<String>();
		for( LoggerInfo i : getLabel())
			out.add( i.toString());
		return( out);
	}

	public List<LoggerInfo> getAxiomTime() {
		return reasoningTimeAxiom;
	}
	public List<String> getAxiomTimeString(){
		List<String> out = new ArrayList<String>();
		for( LoggerInfo i : getAxiomTime())
			out.add( i.toString());
		return( out);
	}
	

	public List<LoggerInfo> getVirtualSensor() {
		return virtualSensor;
	}
	public List<String> getVirtualSensorString(){
		List<String> out = new ArrayList<String>();
		for( LoggerInfo i : getVirtualSensor())
			out.add( i.toString());
		return( out);
	}
	
	public List<LoggerInfo> getError() {
		return error;
	}
	public List<String> getErrorString(){
		List<String> out = new ArrayList<String>();
		for( LoggerInfo i : getError())
			out.add( i.toString());
		return( out);
	}
	
	@Override
	public String toString(){
		String out = this.getDataSetPath() + LINE_SEPARATOR;
		out += "labels : "+ LINE_SEPARATOR;
		for( LoggerInfo i : this.getLabel()){
			out += i.toString() + LINE_SEPARATOR;
		}
		out += LINE_SEPARATOR;
		out += "reasoningTime : "+ LINE_SEPARATOR;
		for( LoggerInfo i : this.getReasoningTime()){
			out += i.toString() + LINE_SEPARATOR;
		}
		out += LINE_SEPARATOR;
		out += "recognised : "+ LINE_SEPARATOR;
		for( LoggerInfo i : this.getRecognised()){
			out += i.toString() + LINE_SEPARATOR;
		}
		return( out);
	}
	
	
	public static Long getTimeFromLog( String line){
		String time = line.substring( 0, line.indexOf( LogAnalizer.TIME_SEPARATOR)).trim();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy_HH:mm:ss,SSS");
		Long milliseconds = null;
		try {
			Date d = f.parse( time);
			milliseconds = d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return( milliseconds);
	}

	public class LoggerInfo{
		
		private Long time;
		private String info;
		
		public LoggerInfo( Long time, String info){
			this.time = time;
			this.info = info;
		}

		public synchronized Long getTime() {
			return time;
		}

		public synchronized String getInfo() {
			return info;
		}
		
		@Override
		public String toString(){
			String out = this.getTime().toString() + SEPARATOR_SYMB + this.getInfo().toString();
			return( out);
		}
	}
	
}
