package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class AlgorithmMainJob extends OFJobAbstract{

	public AlgorithmMainJob(){
		super();
	}
	
	OFDebugLogger logger = new OFDebugLogger( this, true);
	
	@Override
	void runJob( JobExecutionContext context) throws JobExecutionException{
		
		logger.addDebugStrign( " !!!!  MainJob is running .....");
		/*try {
			Thread.sleep( 40000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		logger.addDebugStrign( " ....  MainJob is done !!!!");
	}

}
