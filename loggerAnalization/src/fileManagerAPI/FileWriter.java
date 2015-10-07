package fileManagerAPI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import myFileManager.LogAnalizer;

/**
 * This class implements the operations used to be able to write lines into a file.
 * Moreover, it propagates the implementation of the method {@link #manipulateFile()}
 * through the modifier {@code abstract}. To Note that the generic type of data {@code T},
 * defined in {@link FileManager} and propagate in {@link CommonFileOperations} has been
 * fixed in this class to be an {@link BufferedWriter} object.
 *
 * A call to the function {@link #openFile()} will generate the proper initialization of the
 * data returned by {@link #getFileMatipolator()} with respect to the parameters given
 * in inputs to the constructor. Since file manipulator is of rime BufferedWriter it is
 * possible to just use:
 * <br><code>
 * 	&nbsp&nbspString line = "something to write"<br>
 *  &nbsp&nbspgetFileMatipolator().write( line);<br>
 *  <br></code>
 * To make permanent the changes over the file {@link #closeFile()} should be called. This 
 * will also effect the value of the writer: {@link #getFileMatipolator()}. It must 
 * be reinitialized to be used again. 
 * </code>
 * @author Buoncomapagni Luca
 *
 * @param <E> generic type of data returned by the method {@link #manipulateFile()}.
 * 
 * @see CommonFileOperations
 * @see FileManager
 *
 */
public abstract class FileWriter< E> extends CommonFileOperations< E, BufferedWriter>{

	private BufferedWriter writer = null;
	private java.io.FileWriter fw = null;
	private Boolean appendFileFlag = false;
	private List< String> toAppend = new ArrayList< String>();
	
	/**
	 * Constructor which just call the construction of {@link CommonFileOperations} 
	 * and uses the third parameter to set the method {@link #setAppendingFileType(Boolean)}.
	 * 
	 * @param path is the directory path to the file in relative or absolute notation.
	 * @param isRelative if it is true, it identifies that the parameter {@code path} 
	 * defines a relative path. Otherwise, if it is false, it denotes that the parameter
	 * {@code path} is an absolute address.
	 * @param appendFile if it is true that the new lines will be written at the end of
	 * the file. Otherwise, if it is false, the file will be replaced with an empty one, and 
	 * than the data will be written on it.
	 */
	public FileWriter(String path, Boolean isRelative, Boolean appendFile) {
		super(path, isRelative);
		this.setAppendingFileType( appendFile);
	}

	@Override
	public abstract E manipulateFile();

	@Override
	public BufferedWriter getFileMatipolator() {
		return writer;
	}

	@Override
	public void closeFile() throws IOException {
		writer.close();
		fw.close();
	}

	@Override
	public void openFile() throws IOException {
		File f = new File( this.getAbsolutePath());
		if( ! f.exists()){
			createFile( f);
		}
		
		fw = new java.io.FileWriter( this.getAbsolutePath(), appendFileFlag);
		writer = new BufferedWriter( fw);
	}
	
	/**
	 * Create a new file given an initialized object of type {@link File}. 
	 * It is also automatically called by the method {@link #openFile()}
	 * when the given directory does not contains any file with such name. 
	 * Namely, if it does not exist it will be created 
	 * 
	 * @param f description of the file to create.
	 * @throws IOException
	 */
	public static void createFile( File f) throws IOException{
		f.createNewFile();
	}
	
	private void setAppendingFileType( Boolean append){
		this.appendFileFlag = append;
	}

	public List< String> getToAppend(){
		return( toAppend); 
	}
	
	public void setToAppend( List< String> list){
		toAppend = list;
	}
}