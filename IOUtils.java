import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;


/*
 * Klasa agregujaca procedury do zapisu i odczytu testowanych sekwencji
 * 
 */

public class IOUtils extends PushbackReader 
{
	final static int  EOF_VALUE = -1;
	final static char ZERO_CHAR = '\0';
	private boolean formatProblem = false;
	
	public IOUtils(String file_name)
	{
		super(makeFileReader(file_name));
	}
	
	public static int fileExist(String file_name)
	{
		int doesExist = 0;
		File checkedFilename = new File(file_name);
	    if (checkedFilename.exists() ) {
	    	if (checkedFilename.canRead()) 
	    		doesExist = 1;
	    	else
	    		doesExist = -1;
	    }
		return doesExist;
	}
	
	public static PrintWriter makeFileWriter(String file_name) //(String file_name, int dimension_amount, int all_pieces_amount, int xAxissAmount, int yAxissAmount, boolean all_pieces_unique, boolean all_pieces_used)
	{
		PrintWriter seqFile = null;
	    try
	    {
	      seqFile = new PrintWriter( new FileWriter(file_name, false));
	    }
	    catch (IOException e) {
	    	handleException(e);
	    }
	    return seqFile;
	}
	
	private static FileReader makeFileReader(String file_name)
	{
		try
		{
			return new FileReader(file_name);
		}
		catch (FileNotFoundException e)
		{
			handleException(e);
			return null;
		}
	}
	
	  
	private static void handleException(Exception e)
	{
		System.err.println("Exception:" + e);
		System.err.println("IOUtils zatrzymal program, wystapil niespodziewany blad");
		System.exit(0);
	}

	  
	public boolean isEOF( )  //EndOfFile
	{
		return (readAhead( ) == EOF_VALUE);
	}
	
	  
	public boolean isEOL( )//EndOfLine
	{
		int next = readAhead( );
		return (next == '\n') || (next == '\r') || (next == EOF_VALUE);
	}
	  
	public boolean isFormatProblem( )
	{
		return formatProblem;
	}
	
	
	public static void pause(long milliseconds)
	{
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e)
		{}
	}
	
	public char peek( )
	{
		int next = readAhead( );
		if (next == EOF_VALUE)
			return ZERO_CHAR;
		else
			return (char) next;
	}
	  
	
	private int readAhead( )
	{
		int next = EOF_VALUE;
	
		try
		{
			next = read( );
			if (next == EOF_VALUE)
			{
				pause(1000);
			}
			else
				unread(next);
		} 
		catch (IOException e)
		{
			handleException(e); 
		}
		return next;
	}
		
	private char readChar( )
	{
		int next = EOF_VALUE;
	
		try
		{
			next = read( );
			if (next == EOF_VALUE)
			{
				next = ZERO_CHAR;
				pause(1000);
			}
		} 
		catch (IOException e)
		{
			handleException(e); 
		}
		return (char) next;
	}

	public void skipLine( )
	{
		while (!isEOL( ))
		readChar( );
		if (peek( ) == '\r')
		  readChar( );
		  if (peek( ) == '\n')
			  readChar( );
	}

	public String stringInputLine( )
	{
		StringBuffer buffer = new StringBuffer( );
		
		while (!isEOL( ) && !isEOF( ))
			buffer.append(readChar( ));
		skipLine( );
		return buffer.toString( );
	}  
 
}