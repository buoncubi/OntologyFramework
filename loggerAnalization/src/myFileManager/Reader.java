package myFileManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fileManagerAPI.FileReader;

public class Reader extends FileReader< Boolean>{
					 
	private List< LogAnalizer> logs = new ArrayList< LogAnalizer>();
	private Integer idx = -1;
	
	// usa solo il costruttore della super-classe
	public Reader(String path, Boolean isRelative) {
		super(path, isRelative);
	}

	// leggi tutte le righe del file
	@Override
	public Boolean manipulateFile() {
		try {
			this.openFile(); // inizializza manipolatore
			// ottiemi il manipolatore
			BufferedReader reader = this.getFileMatipolator();
			// se non c'è stato nessun errore di input output
			if( reader != null){
				// leggi la prossima linea
				String line = reader.readLine();
				// fino a che ci sono nuove linee
				while( line != null){
					checkLine( line);	
					// ottieni la prossima linea
					line = reader.readLine();
				}
			}
			// la superclasse si preoccupa degli errori
		} catch (FileNotFoundException e) {
			this.showError( e);
			return( false);
		} catch (IOException e) {
			this.showError( e);
			return( false);
		} finally {
			try {
				// se hai finito con successo 
				// chidi la comunicazione con il file 
				this.closeFile();
			} catch (IOException e) {
				this.showError( e);
				return( false);
			}
		}
		
		/*for( LogAnalizer l:logs)
			System.out.println(  l.toString());*/
		return true;
	}

	private void checkLine(  String line) {
		
		if( line.contains( LogAnalizer.NEW_DATA_SET)){
			idx = idx + 1;
			String tmp = line.replace(  LogAnalizer.NEW_DATA_SET, "");
			logs.add( new LogAnalizer( tmp));
		} else if( line.contains( LogAnalizer.ACTIVITY_RECOGNISED) && idx > -1){
			Long time = LogAnalizer.getTimeFromLog( line);
			Integer interestingIdx = line.indexOf( LogAnalizer.RECOGNISED_NUMBER) + LogAnalizer.RECOGNISED_NUMBER.length();
			String taskNumber = line.substring( interestingIdx, interestingIdx + 1);
			logs.get( idx).addRecognised( time, taskNumber);
		} else if( line.contains( LogAnalizer.REASONING_TIME) && idx > -1){
			Long time = LogAnalizer.getTimeFromLog( line);
			Integer interestingIdx = line.indexOf( LogAnalizer.RECOGNISED_NUMBER) + LogAnalizer.RECOGNISED_NUMBER.length();
			String taskNumber = line.substring( interestingIdx, interestingIdx + 1).trim();
			interestingIdx = line.lastIndexOf( LogAnalizer.TIMING_SYMB);
			Integer interestingIdx2 = line.lastIndexOf( LogAnalizer.TIMING_SYMB2);
			String taskTiming = line.substring( interestingIdx + 1, interestingIdx2).trim();
			String info = taskNumber + LogAnalizer.SEPARATOR_SYMB + taskTiming;
			logs.get( idx).addReasoningTime( time, info);
		} else if( line.contains( LogAnalizer.DATA_IMPORTING) && idx > -1){
			Long time = LogAnalizer.getTimeFromLog( line);
			Integer interestingIdx = line.indexOf( LogAnalizer.IMPORTING_DATA_SYMB);
			String info = line.substring( interestingIdx - 1, interestingIdx).trim();
			logs.get( idx).addLabel( time, info);
		} else if( line.contains( LogAnalizer.ERROR_dethacer)){
			Long time = LogAnalizer.getTimeFromLog( line);
			String taskNumber = line.substring( line.indexOf( LogAnalizer.NUMBER_errorDethacer ) + LogAnalizer.NUMBER_errorDethacer.length(),
					line.indexOf( LogAnalizer.NUMBER_errorDethacer ) + LogAnalizer.NUMBER_errorDethacer.length() + 1).trim();
			String errorRate = line.substring( line.indexOf( LogAnalizer.ERROR_dethacer) + LogAnalizer.ERROR_dethacer.length(),
					line.indexOf( LogAnalizer.SEPARATOR_errorDethacer)).trim();
			String threshould = line.substring( line.indexOf( LogAnalizer.SEPARATOR_errorDethacer) + LogAnalizer.SEPARATOR_errorDethacer.length());
			String info =  taskNumber + LogAnalizer.SEPARATOR_SYMB + errorRate + LogAnalizer.SEPARATOR_SYMB + threshould;
			logs.get( idx).addError( time, info);
		} else if( line.contains( LogAnalizer.AXIOMdethacer) && idx > -1){
			String ontoName = line.substring( line.indexOf( LogAnalizer.AXIOMontologyName) + LogAnalizer.AXIOMontologyName.length(), line.indexOf( LogAnalizer.AxiomCount)).trim();
			Integer ontoNumber = null, axiomNumber = null;
			if( ontoName.equals( "PredefinedOntology")){
				ontoNumber = -1; // prdefined
				axiomNumber = 0;
			}else if( ontoName.equals( "PlacingOntology")){
				ontoNumber = 0;
				axiomNumber = addingPlacing;
				addingPlacing = 0;
			}else if( ontoName.equals( "Task1Description") || ontoName.equals( "task1Ontology")){
				ontoNumber = 1;
				axiomNumber = addingTask1;
				addingTask1 = 0;
			}else if( ontoName.equals( "Task2Description") || ontoName.equals( "task2Ontology")){
				ontoNumber = 2;
				axiomNumber = addingTask2;
				addingTask2 = 0;
			}else if( ontoName.equals( "Task3Description") || ontoName.equals( "task3Ontology")){
				ontoNumber = 3;
				axiomNumber = addingTask3;
				addingTask3 = 0;
			}else if( ontoName.equals( "Task4Description") || ontoName.equals( "task4Ontology")){
				ontoNumber = 4;
				axiomNumber = addingTask4;
				addingTask4 = 0;
			}else if( ontoName.equals( "Task5Description") || ontoName.equals( "task5Ontology")){
				ontoNumber = 5;
				axiomNumber = addingTask5;
				addingTask5 = 0;
			}else if( ontoName.equals( "Task6Description") || ontoName.equals( "task6Ontology")){
				ontoNumber = 6;
				axiomNumber = addingTask6;
				addingTask6 = 0;
			}else if( ontoName.equals( "Task7Description") || ontoName.equals( "task7Ontology")){
				ontoNumber = 7;
				axiomNumber = addingTask7;
				addingTask7 = 0;
			}else if( ontoName.equals( "Task8Description") || ontoName.equals( "task8Ontology")){
				ontoNumber = 8;
				axiomNumber = addingTask8;
				addingTask8 = 0;
			}
			
			String mouvingRate = line.substring( line.indexOf( LogAnalizer.AxiomCount) + LogAnalizer.AxiomCount.length(), 
					line.indexOf( LogAnalizer.LOGICAxiomCount)).trim();
			String axiomTime = line.substring( line.indexOf( LogAnalizer.ReasonigTimeNano) + LogAnalizer.ReasonigTimeNano.length(), 
					line.indexOf( LogAnalizer.AXIOMTaskName)).trim();			
			String logicAxiomTime = line.substring( line.indexOf( LogAnalizer.LOGICAxiomCount) + LogAnalizer.LOGICAxiomCount.length(), 
					line.indexOf( LogAnalizer.Axiomreasoned)).trim();
			String reasonedAxiomTime = line.substring( line.indexOf( LogAnalizer.Axiomreasoned) + LogAnalizer.Axiomreasoned.length()).trim();
			
			String info = ontoNumber + LogAnalizer.SEPARATOR_SYMB + 
					axiomNumber + LogAnalizer.SEPARATOR_SYMB + 
					mouvingRate + LogAnalizer.SEPARATOR_SYMB + 
					axiomTime +	LogAnalizer.SEPARATOR_SYMB + 
					logicAxiomTime + LogAnalizer.SEPARATOR_SYMB + 
					reasonedAxiomTime;
			logs.get( idx).addTimeAxiom( LogAnalizer.getTimeFromLog( line), info);
		} else if( line.contains( LogAnalizer.NUMBERofActivation)){
			Long time = LogAnalizer.getTimeFromLog( line);
			String ontoName = line.substring( line.indexOf( LogAnalizer.VIRTUALdethacer) + LogAnalizer.VIRTUALdethacer.length(), 
					line.indexOf( LogAnalizer.NUMBERofActivation)).trim();
			int count = 0;
			String tmp = line;
			while( tmp.contains( LogAnalizer.NumberofSensor)){
				count++;
				tmp = tmp.replaceFirst( LogAnalizer.NumberofSensor, "");
			}
			Integer ontoNumber = 0;
			if( ontoName.equals( "P_Task1Updater")){
				ontoNumber = 1;
			}else if( ontoName.equals( "P_Task2Updater")){
				ontoNumber = 2;
			}else if( ontoName.equals( "P_Task3Updater")){
				ontoNumber = 3;
			}else if( ontoName.equals( "P_Task4Updater")){
				ontoNumber = 4;
			}else if( ontoName.equals( "P_Task5Updater")){
				ontoNumber = 5;
			}else if( ontoName.equals( "P_Task6Updater")){
				ontoNumber = 6;
			}else if( ontoName.equals( "P_Task7Updater")){
				ontoNumber = 7;
			}else if( ontoName.equals( "P_Task8Updater")){
				ontoNumber = 8;
			}
			String info =  ontoNumber + LogAnalizer.SEPARATOR_SYMB +
					count + LogAnalizer.SEPARATOR_SYMB;
			logs.get( idx).addVirtualSensor( LogAnalizer.getTimeFromLog( line), info);
		} if( line.contains( "P_Task1Dethacer adding data") || line.contains( "change syste clock in task1Ontology")){
			addingTask1++;
		} if( line.contains( "P_Task2Dethacer adding data") || line.contains( "change syste clock in task2Ontology")){
			addingTask2++;
		} if( line.contains( "P_Task3Dethacer adding data") || line.contains( "change syste clock in task3Ontology")){
			addingTask3++;
		} if( line.contains( "P_Task4Dethacer adding data") || line.contains( "change syste clock in task4Ontology")){
			addingTask4++;
		} if( line.contains( "P_Task5Dethacer adding data") || line.contains( "change syste clock in task5Ontology")){
			addingTask5++;
		} if( line.contains( "P_Task6Dethacer adding data") || line.contains( "change syste clock in task6Ontology")){
			addingTask6++;
		} if( line.contains( "P_Task7Dethacer adding data") || line.contains( "change syste clock in task7Ontology")){
			addingTask7++;
		} if( line.contains( "P_Task8Dethacer adding data") || line.contains( "change syste clock in task8Ontology")){
			addingTask8++;
		} if( line.contains( "Importing data:")){
			addingPlacing++;
		}
	}

	private static Integer addingPlacing = 0;
	private static Integer addingTask1 = 0;
	private static Integer addingTask2 = 0;
	private static Integer addingTask3 = 0;
	private static Integer addingTask4 = 0;
	private static Integer addingTask5 = 0;
	private static Integer addingTask6 = 0;
	private static Integer addingTask7 = 0;
	private static Integer addingTask8 = 0;
	
	
	public List< LogAnalizer> getLogs() {
		return logs;
	}
}
