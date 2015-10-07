package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class TaskUpdaterAccellerometer {

	public static final String CLEANER_indName = "tw-cleaner";
	public static final String TIMEREPRESENTATION_clssName = "TimeRepresentation";
	public static final String CLEANINGPOLL_className = "Tw-cleaner";
		
	public static final Integer timeRecognisedLimit = 30;//1;
	
	public double spatioalSupportX;
	public double spatioalSupportY;
	public double spatioalSupportZ;
	public List< Double> thX = new ArrayList< Double>();
	public List< Double> thY = new ArrayList< Double>();
	public List< Double> thZ = new ArrayList< Double>();
	
	private double epsilon;
	public boolean infNorm; //  true --> Infnorm
	private OFDebugLogger logger;
	
	public OWLReferences predefinedRef;
	public String procedureName;
	
	public String thisOntoName;
	
	// spatialSupport [x y z]
	// threshoulds [[thX(1) thX(2) ..thX(tw)],[[thY(1) thY(2) ..thY(tw)],[[thZ(1) thZ(2) ..thZ(tw)]]
	public TaskUpdaterAccellerometer( List<Double> spatialSupport, List< List< Double>> threshoulds, double epsilon,
			boolean infNorm, String thisOntology, String procedureName, OWLReferences predefinedRef, OFDebugLogger logger){
		this.spatioalSupportX = spatialSupport.get(0);
		this.spatioalSupportY = spatialSupport.get(1);
		this.spatioalSupportZ = spatialSupport.get(2);
		
		this.epsilon = 50;//epsilon;
		this.infNorm = infNorm;
		
		this.thX = threshoulds.get(0);
		this.thY = threshoulds.get(1);
		this.thZ = threshoulds.get(2);
		
		this.thisOntoName = thisOntology;
		this.procedureName = procedureName;
		this.predefinedRef = predefinedRef;
		this.logger = logger;
	}
	
	
	
	public Integer updateState(Integer timeOfRecognition){
		// remove data for synchronisation with respect to P_Importer
		predefinedRef.removeDataPropertyB2Individual( procedureName, DataImporterAcc.NEWDATA_dataProp, 
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
				// add median individual
				
			}
			// compute the mean
			AccelerometricData norm;
			if( infNorm)
				norm = computeInfNorm( acceleration);
			else norm = compute2Norm( acceleration);
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
		if( totalCorrect > minError){
			if( timeOfRecognition >= timeRecognisedLimit){
				logger.addDebugStrign( procedureName +  " activity recognised !!!!!!!! task1Ontology cleaned, copy available in: ");
				DataImporterAcc.getMutexCleaningOntology().lock();
				try{
					OWLReferences newOntoRef = taskOntoRef.reloadOnrology();
					DataImporterAcc.updateListenersOntology( predefinedRef.getOWLIndividual(procedureName), newOntoRef);
				} finally{
					DataImporterAcc.getMutexCleaningOntology().unlock();
				}
			} else timeOfRecognition++;
		} else timeOfRecognition = 0;
		logger.addDebugStrign( " computed error: " + totalCorrect + " threshoulding error (>)" + minError + " time of recognition " + timeOfRecognition);
		
		return( timeOfRecognition);
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
		if( xNorm.equals( Double.NEGATIVE_INFINITY) && yNorm.equals( Double.NEGATIVE_INFINITY) && zNorm.equals( Double.NEGATIVE_INFINITY))
			return( new AccelerometricData( Float.NaN, Float.NaN, Float.NaN));
		else
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
		
		public float getxBase() {
			return xBase;
		}
		public float getyBase() {
			return yBase;
		}
		public float getzBase() {
			return zBase;
		}
		
		@Override
		public String toString() {
			return "AccelerometricData: [" + 
					this.getX() + "|" +
					this.getY() + "|" +
					this.getZ() + "]";
		}
		
		
	}
	
	
}
