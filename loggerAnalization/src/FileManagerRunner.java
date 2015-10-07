import myFileManager.LogAnalizer;
import myFileManager.Reader;
import myFileManager.Writer;


public class FileManagerRunner {
	
	public static void main(String[] args) {
		String path = "/files/task1logs.txt";
		Reader r = new Reader( path, true);
		r.manipulateFile();
		
		for( LogAnalizer i : r.getLogs()){
			Writer wr = new Writer( "/files/" + i.getDataSetName() + ".label", true, false);
			wr.setToAppend( i.getLabelString());
			wr.manipulateFile();
			wr = new Writer( "/files/" +i.getDataSetName() + ".recognised", true, false);
			wr.setToAppend( i.getRecognisedString());
			wr.manipulateFile();
			wr = new Writer( "/files/" + i.getDataSetName() + ".reasoningTime", true, false);
			wr.setToAppend( i.getReasoningTimeString());
			wr.manipulateFile();
			wr = new Writer( "/files/" + i.getDataSetName() + ".reasoningAxiom", true, false);
			wr.setToAppend( i.getAxiomTimeString());
			wr.manipulateFile();
			//wr = new Writer( "/files/" + i.getDataSetName() + ".virtualSensor", true, false);
			//wr.setToAppend( i.getVirtualSensorString());
			//wr.manipulateFile();
			wr = new Writer( "/files/" + i.getDataSetName() + ".error", true, false);
			wr.setToAppend( i.getErrorString());
			wr.manipulateFile();
		}
		
	}
	
}
