package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Task1UpdaterAcc extends OFJobAbstract{

	
	public static final String CLEANER_indName = "tw-cleaner";
	public static final String TIMEREPRESENTATION_clssName = "TimeRepresentation";
	public static final String CLEANINGPOLL_className = "Tw-cleaner";

	private static final double epsilon = 90;
	public static final boolean infNorm = false; //  false --> 2norm
	
	public static final double spatioalSupportX = 51.0;
	public static final double spatioalSupportY = 6.0;
	public static final double spatioalSupportZ = 16.0;
	public static final List< Double> thX = new ArrayList< Double>();
	public static final List< Double> thY = new ArrayList< Double>();
	public static final List< Double> thZ = new ArrayList< Double>();	
	static{
		thX.add( 3.4132); 
		thY.add( 3.4011); 
		thZ.add( 3.1153); 
			
	}
	
	private static boolean initialised = false;
	private static TaskUpdaterAccellerometer task;
	
	public static final String thisOntoName = "task1Ontology";
	private static OFDebugLogger logger = new OFDebugLogger( Task1UpdaterAcc.class, true);
	
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
	
	
/*	@Override
	void runJob(JobExecutionContext context) throws JobExecutionException {
		// remove data for synchronisation with respect to P_Importer
		this.getOWLOntologyRefeferences().removeDataPropertyB2Individual( this.getProcedureIndividualName(), DataImporterAcc.NEWDATA_dataProp, 
				Boolean.valueOf( true), false);
		
		// get Ontology by name from list invoker 
		OWLReferences taskOntoRef = OWLReferences.getOWLReferences( thisOntoName);
		
		taskOntoRef.synchroniseReasoner();
		
		// cleaning individual inside the cleaning time window
		Set< OWLNamedIndividual> toClean = taskOntoRef.getIndividualB2Class( CLEANINGPOLL_className);
		if( toClean != null){
			toClean.remove( taskOntoRef.getOWLIndividual( CLEANER_indName));
			for( OWLNamedIndividual i : toClean){
					taskOntoRef.removeIndividual(i, false);
			}
			logger.addDebugStrign( "individual cleaned " + toClean);
		} else logger.addDebugStrign( "no individual to clean");
		
		// computing norm
		// get all time windows class less the the cleaning pool
		System.err.println("INIZIO CALCOLO NORMA");
		Set< OWLClass> timeWindows = taskOntoRef.getSubClassOf( taskOntoRef.getOWLClass( TIMEREPRESENTATION_clssName));
		timeWindows.remove( taskOntoRef.getOWLClass( CLEANINGPOLL_className));
		List< AccelerometricData> accelratioNorm = new ArrayList< AccelerometricData>();
		for( OWLClass tw : timeWindows){
			// get all the sub time windows (for computing norm) for all windows (for thresholding)
			Set<OWLClass> subTimeWindows = taskOntoRef.getSubClassOf( tw);
			List< AccelerometricData> acceleration = new ArrayList< AccelerometricData>();
			for( OWLClass stw : subTimeWindows){
				AccelerometricData ac = new AccelerometricData();
				// get all the individual inside a window for norm computation
				Set<OWLNamedIndividual> individuals = taskOntoRef.getIndividualB2Class( stw);
				for( OWLNamedIndividual i : individuals){
					if( ( OWLLibrary.getOWLObjectName( i).contains( "Bf_baseValue"))){
						ac.addBaseFromIndividual( i, taskOntoRef);
					}else if( ! OWLLibrary.getOWLObjectName( i).contains( "tw") &&
							( ! OWLLibrary.getOWLObjectName( i).contains( "C_SystClock"))){
						ac.addDataFromIndividual( i, taskOntoRef);
					}
				}
				acceleration.add( ac);
			}
			// compute the mean
			AccelerometricData norm;
			if( ! infNorm)
				norm = compute2Norm( acceleration);
			else norm = computeInfNorm( acceleration);
			accelratioNorm.add( norm);
		}
		
		// compute recognition
		double totalCorrect = 0;
		int idx = 0;
		for( AccelerometricData norm : accelratioNorm){
			if( norm.getX() != Double.NaN){
				if( norm.getX() < thX.get( idx))
					totalCorrect += spatioalSupportX;
				if( norm.getY() < thY.get( idx))
					totalCorrect += spatioalSupportY;
				if( norm.getZ() < thZ.get( idx))
					totalCorrect += spatioalSupportZ;
				idx = idx + 1;
			}
			logger.addDebugStrign( " computing new norm " + norm);
		}
		double minError = ((spatioalSupportX + spatioalSupportY + spatioalSupportZ ) * 
				accelratioNorm.size() * epsilon ) / 100;
		if( totalCorrect > minError)
			logger.addDebugStrign( this.getProcedureIndividualName() +  " activity recognised k!!!!! task1Ontology cleaned, copy available in: ");
		logger.addDebugStrign( " computed error: " + totalCorrect + " threshoulding error (>)" + minError);
	}
	
	private AccelerometricData computeInfNorm(List<AccelerometricData> acceleration) {
		Double xNorm = Double.NEGATIVE_INFINITY;
		Double yNorm = Double.NEGATIVE_INFINITY;
		Double zNorm = Double.NEGATIVE_INFINITY;
		for( AccelerometricData ac : acceleration){
			double xComponent = ac.getxBase() - ac.getX();
			double yComponent = ac.getyBase() - ac.getY();
			double zComponent = ac.getzBase() - ac.getZ();
			if( xComponent > xNorm)
				xNorm = xComponent;
			if( yComponent > yNorm)
				yNorm = yComponent;
			if( zComponent > zNorm)
				zNorm = zComponent;
		}
		System.err.println("FINE CALCOLO NORMA " + xNorm + yNorm + zNorm);
		return( new AccelerometricData( xNorm.floatValue(), yNorm.floatValue(), zNorm.floatValue()));
	}

	private AccelerometricData compute2Norm(List<AccelerometricData> acceleration) {
		double totalSumX = 0;
		double totalSumY = 0;
		double totalSumZ = 0;
		for( AccelerometricData ac : acceleration){
			float xMed = ac.getX();
			float yMed = ac.getY();
			float zMed = ac.getZ();
			float xBase = ac.getxBase();
			float yBase = ac.getyBase();
			float zBase = ac.getzBase();
			totalSumX += Math.pow( xBase - xMed, 2);
			totalSumY += Math.pow( yBase - yMed, 2);
			totalSumZ += Math.pow( zBase - zMed, 2);
		}
		Double xNorm = Math.sqrt( totalSumX);
		Double yNorm = Math.sqrt( totalSumY);
		Double zNorm = Math.sqrt( totalSumZ);
		
		System.err.println("FINE CALCOLO NORMA " + yNorm + yNorm + zNorm);
		return( new AccelerometricData( xNorm.floatValue(), yNorm.floatValue(), zNorm.floatValue()));
	}

	public class AccelerometricData{
		float x, y, z;
		
		Set< Float> dataX, dataY, dataZ; 
		
		float xBase, yBase, zBase;
		
		public AccelerometricData( float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public AccelerometricData(){
			this.dataX = new HashSet< Float>();
			this.dataY = new HashSet< Float>();
			this.dataZ = new HashSet< Float>();
		}
		
		public void addData( float x, float y, float z){
			this.dataX.add( x);
			this.dataY.add( y);
			this.dataZ.add( z);
		}

		public void addBase( float x, float y, float z){
			this.xBase = x;
			this.yBase = y;
			this.zBase = z;
		}
		
		public void addBaseFromIndividual( OWLNamedIndividual ind, OWLReferences ontoRef){
			addFromOntology( ind, ontoRef, true);
		}
		public void addDataFromIndividual( OWLNamedIndividual ind, OWLReferences ontoRef){
			addFromOntology( ind, ontoRef, false);
		}
		
		private void addFromOntology( OWLNamedIndividual ind, OWLReferences ontoRef, boolean base){
			OWLDataProperty prop = ontoRef.getOWLDataProperty( DataImporterAcc.dataPropertyName.get( 0));
			OWLLiteral lit = ontoRef.getOnlyDataPropertyB2Individual( ind, prop);
			float x = lit.parseFloat();
			
			prop = ontoRef.getOWLDataProperty( DataImporterAcc.dataPropertyName.get( 1));
			lit = ontoRef.getOnlyDataPropertyB2Individual( ind, prop);
			float y = lit.parseFloat();
			
			prop = ontoRef.getOWLDataProperty( DataImporterAcc.dataPropertyName.get( 2));
			lit = ontoRef.getOnlyDataPropertyB2Individual( ind, prop);
			float z = lit.parseFloat();
			
			if( base)
				this.addBase(x, y, z);
			else this.addData(x, y, z);
		}
		
		private float computeMedian( Set< Float> array) {
			float count = 0f;
			for( Float f : array){
				count += f;
			}
			return( count / array.size());
		}		

		public float getX() {
			if( dataX == null && dataY == null && dataZ == null)
				return x;
			else
				return( computeMedian( dataX));
		}
		public float getY() {
			if( dataX == null && dataY == null && dataZ == null)
				return y;
			else
				return( computeMedian( dataY));
		}
		public float getZ() {
			if( dataX == null && dataY == null && dataZ == null)
				return z;
			else
				return( computeMedian( dataZ));
		}
		
		public synchronized float getxBase() {
			return xBase;
		}
		public synchronized float getyBase() {
			return yBase;
		}
		public synchronized float getzBase() {
			return zBase;
		}
		
		@Override
		public String toString() {
			return "AccelerometricData: [" + 
					this.getX() + "|" +
					this.getY() + "|" +
					this.getZ() + "]";
		}
		
		
	}*/
}
