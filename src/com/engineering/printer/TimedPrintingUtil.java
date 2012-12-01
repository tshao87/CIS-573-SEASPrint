package com.engineering.printer;

import java.io.IOException;

import com.trilead.ssh2.Connection;

public class TimedPrintingUtil {
	private static Connection connection;
	private static TimedPrintingUtil instance;
	private CommandConnection mConn;
	private ErrorCallback mCb;
	private static final String TO_PRINT = "to_print";
	private static final String SETUP_SH = "curl -L https://raw.github.com/emish/cets_autoprint/master/setup.sh | sh";

	public synchronized static TimedPrintingUtil getInstance(Connection conn,ErrorCallback cb) throws IOException{
		if (instance == null || conn != connection){
			instance = new TimedPrintingUtil(conn);
		}
		instance.mCb = cb;
		return instance;
	}

	private TimedPrintingUtil(Connection conn) throws IOException{
		mConn = new CommandConnection(conn);
		setup();
	}

	private void setup() throws IOException{
		mConn.execWithReturn("screen");
		mConn.execWithReturn(SETUP_SH);
	}
	
	public boolean addToPrintList(String filename){
		try {
			String home = mConn.execWithReturn("echo ~");
			String target = home + "/" + TO_PRINT;
			String pdfFilename = filename + ".pdf";
			mConn.execWithReturn("unoconv -o " + pdfFilename + " " + filename);
			mConn.execWithReturn("mv " + pdfFilename + " " + target);
		} catch (IOException e) {
			mCb.error();
		}
		return true;
	}

	public CommandConnection getmConn() {
		return mConn;
	}

}