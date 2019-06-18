package org.galatea.starter.repository;

import static java.sql.DriverManager.getConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {

  private static final String databaseDriver = "org.h2.Driver";
  private static final String databaseConnection = "jdbc:h2:tcp://localhost/~/test";
  private static final String databaseUser = "sa";
  private static final String databasePassword = "";
  private static final String databaseURL = "jdbc:h2:mem:testtest";

  public static void CreateDB() {
    createDBTable();

  }

  private static void createDBTable() {

    String createSQLQuery = "CREATE TABLE STOCKPRICESDB"
        + "open"
        + "high"
        + "low"
        + "close"
        + "adjustedClose"
        + "volume"
        + "dividendAmount"
        + "splitCoefficient";

    //Create H2 Database Connection object
    try {
      Connection connection = getConnection((databaseURL), "sa", "");

      //Set auto commit to false
      connection.setAutoCommit(false);

      //Create a Statement Object
      Statement statement = connection.createStatement();

      //Execute the statement
      statement.execute(createSQLQuery);

      //Close the Statement Object
      statement.close();

      //Close the Connection Object
      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();

    }
  }

  private Connection connection() {

    Connection H2DBConnection = null;

    try {
      Class.forName(databaseDriver);
    } catch (ClassNotFoundException ex) {
      System.out.println(ex.toString());
    }
    try {
      H2DBConnection = DriverManager
          .getConnection(databaseConnection, databaseUser, databasePassword);

      return H2DBConnection;
    } catch (SQLException ex) {
      System.out.println(ex.toString());
    }

    return H2DBConnection;
  }

}

