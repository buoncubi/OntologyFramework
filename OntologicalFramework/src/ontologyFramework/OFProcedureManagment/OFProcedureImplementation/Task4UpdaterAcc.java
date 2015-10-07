package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.List;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Task4UpdaterAcc extends OFJobAbstract{

	
	private static final double epsilon = 90;
	public static final boolean infNorm = false; //  false --> 2norm
	
	public static final double spatioalSupportX = 16.0;
	public static final double spatioalSupportY = 1.0;
	public static final double spatioalSupportZ = 76.0;
	public static final List< Double> thX = new ArrayList< Double>();
	public static final List< Double> thY = new ArrayList< Double>();
	public static final List< Double> thZ = new ArrayList< Double>();	
	static{
		thX.add( 1.390); thX.add( 1.7746); thX.add( 1.364); thX.add( 2.0591); thX.add( 2.8061);
		thY.add( 1.945); thY.add( 2.0507); thY.add( 1.6336); thY.add( 2.2812); thY.add( 2.6522);
		thZ.add( 1.5215); thZ.add( 1.5458); thZ.add( 1.5912); thZ.add( 1.274); thZ.add( 1.7184); 
			
	}
	
	public static final String thisOntoName = "task4Ontology";
	private static OFDebugLogger logger = new OFDebugLogger( Task4UpdaterAcc.class, true);
	
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
