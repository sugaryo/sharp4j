package sharp4j.util.io;


import java.io.BufferedReader;
import java.io.IOException;

public class QuietBufferedReader extends QuietReader
{
	private final BufferedReader reader;
	public QuietBufferedReader(BufferedReader reader)
	{
		super( reader );
		this.reader = reader;
	}

	public String readLine()
	{
		try
		{
			return this.reader.readLine();
		}
		catch ( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
}