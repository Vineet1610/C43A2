import java.sql.*;

public class Assignment2 {
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

    // CONSTRUCTOR
    Assignment2() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Using the input parameters, establish a connection to be used for this
    // session. Returns true if connection is sucessful
    public boolean connectDB(String URL, String username, String password) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
  
    // Closes the connection. Returns true if closure was successful
    public boolean disconnectDB() {
        try {
            if (connection != null && !(connection.isClosed())) {
                connection.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        } catch (Exception e1) {
            return false;
        }
    }

    public boolean insertPlayer(int pid, String pname, int globalRank, int cid) {
      try {
        final String statement = "INSERT INTO A2.player VALUES (?, ?, ?, ?);";
        
        ps = connection.prepareStatement(statement);

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
        try {
            ps = connection.prepareStatement("SELECT COUNT(*) FROM A2.champion WHERE pid=?;");

            ps.setInt(1, pid);
            rs = ps.executeQuery();

            if (rs.next()) {
                int numberOfChampionships = rs.getInt(1);
                try {
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return 0;
                }
                return numberOfChampionships;
            } else {
                try {
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return 0;
                }
                return 0;
            }
        } catch (SQLException e) {
            try {
                ps.close();
                rs.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
                return 0;
            }
            e.printStackTrace();
            return 0;
        }
    }

    public String getCourtInfo(int courtid) {
      try {
        final String statment = "SELECT * FROM A2.court WHERE courtid=?;";
        
        ps = connection.prepareStatement(statment);

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
                return (courtid + ":" + courtName + ":" + capacity + ":" + tname);
            }
            
            return "";
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

    public boolean chgRecord(int pid, int year, int wins, int losses) {
        try {
            ps = connection.prepareStatement("UPDATE A2.record SET wins=?, losses=? WHERE pid=? AND year=?;");

            ps.setInt(1, wins);
            ps.setInt(2, losses);
            ps.setInt(3, pid);
            ps.setInt(4, year);

            int updateSuccess = ps.executeUpdate();
            if (updateSuccess > 0) {
                try {
                    ps.close();
                } catch (Exception e) {
                    return false;
                }
                
                return true;
            } else {
                try {
                    ps.close();
                } catch (Exception e) {
                    return false;
                }
                
                return false;
            }
        } catch (SQLException e) {
            try {
                ps.close();
            } catch (Exception e1) {
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMatcBetween(int p1id, int p2id){
      try {
        final String statment = "DELETE FROM A2.event WHERE (winid=? AND lossid=?)" + 
            " OR (winid=? AND lossid=?);";
        
        ps = connection.prepareStatement(statment);

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

    public String listPlayerRanking() {
        try {
            final String statement = "SELECT pname, globalrank FROM A2.player ORDER BY globalrank ASC;";
            ps = connection.prepareStatement(statement);

            rs = ps.executeQuery();

            String rankings = "";
            while (rs.next()) {
                int rank = rs.getInt("globalrank");
                String name = rs.getString("pname");

                rankings = rankings + String.format("%s:%d", name, rank) + "\n";
            }

            return rankings;
        } catch (SQLException e) {
            try {
                ps.close();
                rs.close();
            } catch (Exception e1) {
                e1.printStackTrace();
                return "";
            }
            e.printStackTrace();
            return "";
        }
    }
  
    public int findTriCircle(){
      try {
        final String statement = "SELECT COUNT(*) FROM (SELECT DISTINCT event1.winid, event2.winid," +
            " event3.winid FROM A2.event event1, A2.event event2, A2.event event3 WHERE event1.winid < event2.winid" + 
            " AND event2.winid < event3.winid AND event1.winid = event3.lossid AND event2.winid = event1.lossid" + 
            " AND event3.winid = event2.lossid) scope;";
        
        ps = connection.prepareStatement(statement);
        rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
      } catch (Exception e) {
          return 0;
      } finally {
          try {
              ps.close();
              rs.close();
          } catch (Exception e) {
              return 0;
          }
      }
    }

    public boolean updateDB() {
        try {
            ps = connection.prepareStatement("DROP TABLE IF EXISTS A2.championPlayers CASCADE;");
            ps.execute();
            ps.close();

            ps = connection.prepareStatement(
                    "CREATE TABLE championPlayers (pid INTEGER, pname VARCHAR NOT NULL, nchampions INTEGER);");
            ps.execute();
            ps.close();

            ps = connection.prepareStatement("INSERT INTO A2.championPlayers "
                    + "(SELECT play.pid, play.pname, COUNT(champ.tid) as nchampions "
                    + "FROM A2.player play JOIN A2.champion champ ON play.pid = champ.pid "
                    + "GROUP BY play.pid);");
            
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            try {
                ps.close();
            } catch (SQLException e1) {
                return false;
            }
            e.printStackTrace();
            return false;
        }    
    }  
}
