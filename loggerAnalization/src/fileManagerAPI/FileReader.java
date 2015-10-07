package fileManagerAPI;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * This class implements the operations used to be able to read the lines from a file.
 * Moreover, it propagates the implementation of the method {@link #manipulateFile()}
 * through the modifier {@code abstract}. To Note that the generic type of data {@code T},
 * defined in {@link FileManager} and propagate in {@link CommonFileOperations} has been
 * fixed in this class to be an {@link BufferedReader} object.
 * <br><br>
 * 
 * A call to the function {@link #openFile()} will generate the proper initialization of the
 * data returned by {@link #getFileMatipolator()} with respect to the parameters given
 * in inputs to the constructor. Since file manipulator is of rime BufferedReader it is
 * possible to loop along all the lines of a file using:
 * <br><code>
 * 	&nbsp&nbspString line = getFileMatipolator().readLine();<br>
 * 	&nbsp&nbspWhile( line != null){<br>
 * 	&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp// do something <br>
 * 	&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp	....<br>
 * 	&nbsp&nbsp&nbsp&nbsp&nbsp&nbspline = getFileMatipolator().readLine();<br>
 *  &nbsp&nbsp}<br>
 * To make permanent the changes over the file {@link #closeFile()} should be called. This
 * will also effect the value of the reader: {@link #getFileMatipolator()}. It must 
 * be reinitialized to be used again. 
 * </code>
 * 
 * @author Buoncompagni Luca
 * 
 * @param <E> generic type of data returned by the method {@link #manipulateFile()}.
 * 
 * @see CommonFileOperations
 * @see FileManager
 */
public abstract class FileReader<E> extends CommonFileOperations<E, BufferedReader>{
	
	// attributes
	private BufferedReader reader = null;
	private FileInputStream fis = null;
	
	List< String> lines = new ArrayList<String>(); 
	
	/**
	 * Constructor which just call the construction of {@link CommonFileOperations}
	 * and does not process any further the data.
	 * 
	 * @param path is the directory path to the file in relative or absolute notation.
	 * @param isRelative if it is true, it identifies that the parameter {@code path} 
	 * defines a relative path. Otherwise, if it is false, it denotes that the parameter
	 * {@code path} is an absolute address.
	 */
	public FileReader(String path, Boolean isRelative) {
		super(path, isRelative);
	}
					
	@Override
	public abstract E manipulateFile();
	
	@Override
	public BufferedReader getFileMatipolator(){
		return( reader);
	}

	@Override
	public void closeFile() throws IOException {
		reader.close();
		fis.close();
	}

	@Override
	public void openFile() throws FileNotFoundException {
		// ottieni un puntatore al file
		File f = new File( this.getAbsolutePath());
		fis = new FileInputStream( f);
		// inizializa l'oggetto reader
		InputStreamReader  isr = new InputStreamReader( fis);
		reader = new BufferedReader( isr);
	}

	public void setLines( List<String> lines){
		this.lines = lines;
	}
	
	public List<String> getLines(){
		return( lines);
	}
}
