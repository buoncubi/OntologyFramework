package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.List;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Task8UpdaterAcc extends OFJobAbstract{


	private static final double epsilon = 90;
	public static final boolean infNorm = false; //  false --> 2norm
	
	public static final double spatioalSupportX = 6.0;
	public static final double spatioalSupportY = 76.0;
	public static final double spatioalSupportZ = 3.0;
	public static final List< Double> thX = new ArrayList< Double>();
	public static final List< Double> thY = new ArrayList< Double>();
	public static final List< Double> thZ = new ArrayList< Double>();	
	static{
		thX.add( 0.98552); thX.add( 1.2318); thX.add( 1.0809); thX.add( 1.0864);
		thY.add( 1.77760); thY.add( 1.9550); thY.add( 1.9673); thY.add( 1.8299);
		thZ.add( 1.49550); thZ.add( 1.3831); thZ.add( 1.4797); thZ.add( 1.4949);
			
	}
	
	public static final String thisOntoName = "task8Ontology";
	private static OFDebugLogger logger = new OFDebugLogger( Task8UpdaterAcc.class, true);
	
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
