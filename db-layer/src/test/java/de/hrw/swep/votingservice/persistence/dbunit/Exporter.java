package de.hrw.swep.votingservice.persistence.dbunit;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * 
 * @author andriesc
 *
 */
public class Exporter {
	private static final String CONNECTION_STRING = "jdbc:hsqldb:file:../db-layer/database/votesdb";
	private static final String DB_USER = "sa";
	private static final String DB_PASSWORD = "";
	
	/**
	 * Exports current database into XML file for DBUnit.
	 * 
	 * @param args none expected
	 * @throws Exception 
	 */
    public static void main(String[] args) throws Exception
    {
        // database connection
        Connection jdbcConnection = DriverManager.getConnection(
        		CONNECTION_STRING, DB_USER, DB_PASSWORD);
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // full database export
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));
        connection.close();
        jdbcConnection.close();
    }
}
