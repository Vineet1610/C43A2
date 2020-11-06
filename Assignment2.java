import java.sql.*;

public class Assignment2 {
  
  public static void main(String[] args) throws Exception {
    Assignment2 a2 = new Assignment2();

    System.out.printf("%b\t%s\n", a2.connectDB(args[0], args[1], args[2]), "Connecting ...");
    System.out.printf("%b\t%s\n", !a2.connection.isClosed(), "Connection open!");
    
    System.out.printf("%b\t%s\n", a2.insertPlayer(8, "Raj Patel", 8, 3), "Insert new player");
    System.out.printf("%b\t%s\n", !a2.insertPlayer(8, "Raj Patel", 8, 3), "Insert same player");
    System.out.printf("%b\t%s\n", !a2.insertPlayer(8, "Vineet Desai", 9, 3), "Insert with duplicate playerID");
    System.out.printf("%b\t%s\n", !a2.insertPlayer(9, "Vineet Desai", 9, 10), "Insert with invalid invalid countryID");
    
    System.out.printf("%b\t%s\n", a2.disconnectDB(), "Closing ...");
    System.out.printf("%b\t%s\n", a2.connection, "Connection closed!");
  }
    
    // A connection to the database  
    Connection connection;
  
    // Statement to run queries
    Statement sql;
  
    // Prepared Statement
    PreparedStatement ps;
    // Second Prepared Statment
    PreparedStatement ps2;
  
    // Resultset for the query
    ResultSet rs;
    // Second Resultset
    ResultSet rs2;
  
    //CONSTRUCTOR
    Assignment2(){
      try {
        Class.forName("org.postgresql.Driver");
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      }
    }
  
    //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
    public boolean connectDB(String URL, String username, String password){
      try {
        if (connection != null && !connection.isClosed()) {
            if (!disconnectDB()) {
                return false;
            }
        }

        connection = DriverManager.getConnection(URL, username, password);
        return true;
      } catch (Exception e) {
          return false;
      }
    }
  
    // Closes the connection. Returns true if closure was successful
    public boolean disconnectDB(){
      try {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            return true;
        } else {
            return false;
        }
    } catch (Exception e) {
        return false;
    }   
    }
    
    public boolean insertPlayer(int pid, String pname, int globalRank, int cid) {
      try {
        ps = connection.prepareStatement("INSERT INTO A2.player VALUES (?, ?, ?, ?);");

        ps.setInt(1, pid);
        ps.setString(2, pname);
        ps.setInt(3, globalRank);
        ps.setInt(4, cid);

        int inserted = ps.executeUpdate();
        try {
            ps.close();
        } catch (Exception e) {
            return false;
        }
        return inserted == 1;
      } catch (Exception e) {
          try {
              ps.close();
          } catch (Exception e2) {
              return false;
          }
          return false;
      }
    }
  
    public int getChampions(int pid) {
	      return 0;  
    }
   
    public String getCourtInfo(int courtid){
      try {
        ps = connection.prepareStatement("SELECT * FROM A2.court WHERE courtid=?;");

        ps.setInt(1, courtid);
        rs = ps.executeQuery();

        if (rs.next()) {
            int tid = rs.getInt("tid");
            int capacity = rs.getInt("capacity");
            String courtName = rs.getString("courtname");

            ps2 = connection.prepareStatement("SELECT * FROM A2.tournament WHERE tid=?;");
            ps2.setInt(1, tid);
            rs2 = ps2.executeQuery();

            if (rs2.next()) {
                String tname = rs2.getString("tname");
                return String.format("%d:%s:%d:%s", courtid, courtName, capacity, tname);
            } else {
                return "";
            }
        } else {
            try {
                ps.close();
                ps2.close();
                rs.close();
                rs2.close();
            } catch (Exception e) {
                return "";
            }
            return "";
        }
      } catch (Exception e) {
          try {
              ps.close();
              ps2.close();
              rs.close();
              rs2.close();
          } catch (Exception e2) {
              return "";
          }
          return "";
      }
    }

    public boolean chgRecord(int pid, int year, int wins, int losses){
        return false;
    }

    public boolean deleteMatcBetween(int p1id, int p2id){
      try {
        ps = connection.prepareStatement("DELETE FROM A2.event WHERE (winid=? AND lossid=?) OR (winid=? AND lossid=?);");

        ps.setInt(1, p1id);
        ps.setInt(2, p2id);
        ps.setInt(3, p2id);
        ps.setInt(4, p1id);

        int count = ps.executeUpdate();
        try {
            ps.close();
        } catch (Exception e) {
            return false;
        }

        return count > 0;
      } catch (Exception e) {
          try {
              ps.close();
          } catch (Exception e2) {
              return false;
          }
          return false;
      }
    }
  
    public String listPlayerRanking(){
	      return "";
    }
  
    public int findTriCircle(){
        try {
            ps = connection.prepareStatement("SELECT COUNT(*) FROM (SELECT DISTINCT e1.winid, e2.winid, e3.winid "
                    + "FROM A2.event e1, A2.event e2, A2.event e3 WHERE e1.winid < e2.winid AND e2.winid < e3.winid "
                    + "AND e1.winid = e3.lossid AND e3.winid = e2.lossid AND e2.winid = e1.lossid) scope;");
            rs = ps.executeQuery();

            try {
                ps.close();
                rs.close();
            } catch (Exception e) {
                return 0;
            }
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            try {
                ps.close();
                rs.close();
            } catch (Exception e2) {
                return 0;
            }
            return 0;
        }
    }
    
    public boolean updateDB(){
	      return false;    
    }  
}
