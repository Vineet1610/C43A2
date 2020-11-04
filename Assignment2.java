import java.sql.*;

public class Assignment2 {

    // A connection to the database
    Connection connection;

    // Statement to run queries
    Statement sql;

    // Prepared Statement
    PreparedStatement ps;
    PreparedStatement ps2;

    // Resultset for the query
    ResultSet rs;
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
            ps = connection.prepareStatement("INSERT INTO A2.player VALUES (?, ?, ?, ?);");

            ps.setInt(1, pid);
            ps.setString(2, pname);
            ps.setInt(3, globalRank);
            ps.setInt(4, cid);

            int count = ps.executeUpdate();
            return count == 1;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                return false;
            }
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
                return "";
            }
        } catch (Exception e) {
            return "";
        } finally {
            try {
                ps.close();
                rs.close();
                ps2.close();
                rs2.close();
            } catch (Exception e) {
                return "";
            }
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
            }
            else {
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

    public boolean deleteMatcBetween(int p1id, int p2id) {
        try {
            ps = connection
                    .prepareStatement("DELETE FROM A2.event WHERE (winid=? AND lossid=?) OR (winid=? AND lossid=?);");

            ps.setInt(1, p1id);
            ps.setInt(2, p2id);
            ps.setInt(3, p2id);
            ps.setInt(4, p1id);

            int count = ps.executeUpdate();
            return count > 0;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                return false;
            }
        }
    }

    public String listPlayerRanking() {
        try {
            ps = connection.prepareStatement("SELECT pname, globalrank FROM A2.player ORDER BY globalrank [ASC];");

            rs = ps.executeQuery();

            if(rs.next()) {
                int rank = rs.getInt(2);
                String name = rs.getString(1);

                String rankings = String.format("%s:%d\n", name, rank);
                try {
                    ps.close();
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
                return rankings;
            }
            else {
                try {
                    ps.close();
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
                return "";
            }
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
        return 0;
    }
    
    public boolean updateDB(){
	      return false;    
    }  
}
