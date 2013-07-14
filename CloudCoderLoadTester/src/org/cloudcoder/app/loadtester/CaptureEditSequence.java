package org.cloudcoder.app.loadtester;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.cloudcoder.app.server.persist.Database;
import org.cloudcoder.app.server.persist.IDatabase;
import org.cloudcoder.app.server.persist.JDBCDatabaseConfig;
import org.cloudcoder.app.server.persist.util.DBUtil;
import org.cloudcoder.app.shared.model.Change;

/**
 * Capture a series of edits ({@link Change}s) that were saved to the database.
 * For a given user and problem (exercise), all change events in a specified
 * range of event ids will be captured.
 * 
 * @author David Hovemeyer
 */
public class CaptureEditSequence {
	private int userId;
	private int problemId;
	private int minEventId;
	private int maxEventId;
	private EditSequence editSequence;
	
	/**
	 * Constructor.
	 * Setters must be called before the object is used.
	 */
	public CaptureEditSequence() {
		editSequence = new EditSequence();
	}

	/**
	 * @return the edit sequence
	 */
	public EditSequence getEditSequence() {
		return editSequence;
	}
	
	/**
	 * Set the user id.
	 * 
	 * @param userId the user id
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	/**
	 * Set the problem id.
	 * 
	 * @param problemId the problem id
	 */
	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}
	
	/**
	 * Set the minimum event id.
	 * 
	 * @param minEventId the minimum event id
	 */
	public void setMinEventId(int minEventId) {
		this.minEventId = minEventId;
	}
	
	/**
	 * Set the maximum event id.
	 * 
	 * @param maxEventId the maximum event id
	 */
	public void setMaxEventId(int maxEventId) {
		this.maxEventId = maxEventId;
	}
	
	/**
	 * Capture the sequence of edits from the database.
	 */
	public void captureFromDB() {
		IDatabase db = Database.getInstance();
		List<Change> captured = db.loadChanges(userId, problemId, minEventId, maxEventId);
		editSequence.setChangeList(captured);
	}
	
	public static void main(String[] args) throws IOException {
		final Properties config = DBUtil.getConfigProperties();
		JDBCDatabaseConfig.ConfigProperties configProps = new JDBCDatabaseConfig.ConfigProperties() {
			@Override
			public String getUser() {
				return config.getProperty("cloudcoder.db.user", "root");
			}
			
			@Override
			public String getPortStr() {
				return config.getProperty("cloudcoder.db.portStr", "");
			}
			
			@Override
			public String getPasswd() {
				return config.getProperty("cloudcoder.db.passwd", "root");
			}
			
			@Override
			public String getHost() {
				return config.getProperty("cloudcoder.db.host", "localhost");
			}
			
			@Override
			public String getDatabaseName() {
				return config.getProperty("cloudcoder.db.databaseName", "cloudcoderdb");
			}
		};
		
		JDBCDatabaseConfig.create(configProps);
		
		// FIXME: don't hardcode ids
		CaptureEditSequence ces = new CaptureEditSequence();
		ces.setUserId(1);
		ces.setProblemId(3);
		ces.setMinEventId(182);
		ces.setMaxEventId(309);
		
		ces.captureFromDB();
		System.out.println("Edits captured successfully");
		
		EditSequence editSequence = ces.getEditSequence();
		
		editSequence.saveToFile("edits.dat");
		System.out.println("Edits saved successfully");
	}
}