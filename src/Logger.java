import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	
	static PrintWriter out = null;
	public Logger()
	{
		try {
			//out  = new PrintWriter(new FileWriter("UserLog",true));
			out  = new PrintWriter(new FileOutputStream("UserLog",true),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error on defult logger start");
			System.exit(0);
		}
	}
	public Logger(String trace)
	{
		try {
			//out  = new PrintWriter(new FileWriter(trace),true);
			out  = new PrintWriter(new FileOutputStream(trace,true),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error on logger: "+trace+" start");
			System.exit(0);
		}
	}
	
	public  void logTofile(String s)
	{
		if(out==null)
		{
			System.out.println("not initailised logger");
			System.exit(0);
		}
		out.println(s);
	}
}
