package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.List;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Task6UpdaterAcc extends OFJobAbstract {

	private static final double epsilon = 90;
	public static final boolean infNorm = true; //  false --> 2norm
	
	public static final double spatioalSupportX = 51.0;
	public static final double spatioalSupportY = 3.0;
	public static final double spatioalSupportZ = 1.0;
	public static final List< Double> thX = new ArrayList< Double>();
	public static final List< Double> thY = new ArrayList< Double>();
	public static final List< Double> thZ = new ArrayList< Double>();	
	static{
		thX.add( 0.46768); thX.add( 0.37996); thX.add( 0.39042);
		thY.add( 0.41408); thY.add( 0.39494); thY.add( 0.32954);
		thZ.add( 0.40091); thZ.add( 0.37245); thZ.add( 0.41992); 
			
	}
	
	public static final String thisOntoName = "task6Ontology";
	private static OFDebugLogger logger = new OFDebugLogger( Task6UpdaterAcc.class, true);
	
	private static boolean initialised = false;
	private static TaskUpdaterAccellerometer task;
	
	private static Integer timeOfRecognition = 0;
	
	@Override
	void runJob(JobExecutionContext context) throws JobExecutionException {
		if( ! initialised){
			List< Double> spatialSupport = new ArrayList< Double>( 3);
			spatialSupport.add( spatioalSupportX);
			spatialSupport.add( spatioalSupportY);
			spatialSupport.add( spatioalSupportZ);
			List< List< Double>> treshoulds = new ArrayList< List< Double>>(3);
			treshoulds.add( thX);
			treshoulds.add( thY);
			treshoulds.add( thZ);
		
			task = new TaskUpdaterAccellerometer( spatialSupport, 
					treshoulds, epsilon, infNorm, thisOntoName, this.getProcedureIndividualName(),
					this.getOWLOntologyRefeferences(), logger);
		} else initialised = true;
		
		timeOfRecognition = task.updateState( timeOfRecognition);
	}
}
