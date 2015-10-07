package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.List;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Task5UpdaterAcc extends OFJobAbstract {

	
	private static final double epsilon = 90;
	public static final boolean infNorm = false; //  false --> 2norm
	
	public static final double spatioalSupportX = 100.0;
	public static final double spatioalSupportY = 76.0;
	public static final double spatioalSupportZ = 51.0;
	public static final List< Double> thX = new ArrayList< Double>();
	public static final List< Double> thY = new ArrayList< Double>();
	public static final List< Double> thZ = new ArrayList< Double>();	
	static{
		thX.add( 1.9791); thX.add( 1.9483); thX.add( 2.3171);
		thY.add( 3.3332); thY.add( 3.1492); thY.add( 2.9063);
		thZ.add( 2.1041); thZ.add( 1.6481); thZ.add( 3.1893); 
			
	}
	
	public static final String thisOntoName = "task5Ontology";
	private static OFDebugLogger logger = new OFDebugLogger( Task5UpdaterAcc.class, true);
	
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
