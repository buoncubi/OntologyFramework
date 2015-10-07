package fileManagerAPI;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * This class implements some common methods defined in {@link FileManager}.
 * In particular it has been designed to care about the initialization and storage
 * of the directory path of the file, both in terms of relative and absolute path.
 * It also implements a basic error notification through the command {@code e.printStackTrace();}.
 * Finally it delegates the implementation of the other methods of the type FileManager
 * through the modifier {@code abstract}
 *
 * @author Buoncompagni Luca
 *
 * @param <E> generic returning type of the method {@link #manipulateFile()}
 * @param <T> generic returning type of the method {@link #getFileMatipolator()}
 * 
 * @see FileManager
 */
public abstract class CommonFileOperations<E, T> implements FileManager<E, T> {
	// constants
	/**
	 * describe the directory path with respect to the folder in which the software is running.
	 * It is based on the command: <br>{@code RELATIVE_PATH = System.getProperty("user.dir");}
	 */
	public static String RELATIVE_PATH = System.getProperty("user.dir");
	
	private Boolean pathRelative = null; // is path relative?
	private String absolutePath = null; // contains the absolute path
		
	/**
	 * Constructor to initialize the class with a path that can be either relative or absolute.
	 * Where a relative path is the directory address starting for the folder in which
	 * the program is actually running. While absolute path is the directory address starting
	 * for the system root folder.  
	 * 
	 * @param path is the directory path to the file in relative or absolute notation.
	 * @param isRelative if it is true, it identifies that the parameter {@code path} 
	 * defines a relative path. Otherwise, if it is false, it denotes that the parameter
	 * {@code path} is an absolute address.
	 */
	public CommonFileOperations( String path, Boolean isRelative){
		this.pathRelative = isRelative;
		if( pathRelative){ // is true
			this.absolutePath = RELATIVE_PATH + path;
		} else { // is false
			this.absolutePath = path;
		}
	}
	
	@Override
	public String getRelativePath() {
		// elimino la sottostringa uguale alla path relativa sostituendula con niente
		String relativePath = absolutePath.replace(RELATIVE_PATH, "");
		return( relativePath);
	}

	@Override
	public String getAbsolutePath() { 
		return absolutePath;
	}

	@Override
	public void showError(Exception e) {
		e.printStackTrace();
	}
	
	@Override
	public abstract E manipulateFile();

	@Override
	public abstract T getFileMatipolator();

	@Override
	public abstract void closeFile() throws IOException;

	@Override
	public abstract void openFile() throws FileNotFoundException, IOException;
	
}
