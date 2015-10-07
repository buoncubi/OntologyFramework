package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.List;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Task7UpdaterAcc extends OFJobAbstract{

	private static final double epsilon = 90;
	public static final boolean infNorm = false; //  false --> 2norm
	
	public static final double spatioalSupportX = 1.0;
	public static final double spatioalSupportY = 6.0;
	public static final double spatioalSupportZ = 76.0;
	public static final List< Double> thX = new ArrayList< Double>();
	public static final List< Double> thY = new ArrayList< Double>();
	public static final List< Double> thZ = new ArrayList< Double>();	
	static{
		thX.add( 2.5344); thX.add( 2.6241); thX.add( 2.5196);
		thY.add( 2.2161); thY.add( 1.9820); thY.add( 2.2098);
		thZ.add( 2.5260); thZ.add( 1.9001); thZ.add( 2.0697); 
			
	}
	
	public static final String thisOntoName = "task7Ontology";
	private static OFDebugLogger logger = new OFDebugLogger( Task7UpdaterAcc.class, true);
	
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
