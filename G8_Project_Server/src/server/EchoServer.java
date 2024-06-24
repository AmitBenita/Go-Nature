// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import common.Order;
import gui.ServerController;
import ocsf.server.*;
import report.Cancelereports;
import report.GenerateHourlyVisitors;
import report.GetReportsRequest;
import report.RetrieveHourlyVisitors;
import report.RetrieveOrdersReports;
import report.UsageReportRequest;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class EchoServer extends AbstractServer {
    private static Connection con;
    private static ServerController control;
    // Class variables *************************************************

    /**
     * The default port to listen on.
     */
    // final public static int DEFAULT_PORT = 5555;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     * 
     */

    public EchoServer(int port, Connection con, ServerController control) {
        super(port);
        EchoServer.con = con;
        EchoServer.control = control;
    }
    
    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try { 
            Statement stmt = con.createStatement(); 
            if (msg instanceof DisconnectUser) {
                try {
                    client.sendToClient(((DisconnectUser) msg).disconnectFromDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (msg instanceof RetrieveOrdersReports) {
                try {
                    client.sendToClient(((RetrieveOrdersReports) msg).ReportsfromDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (msg instanceof Cancelereports) {
                try {
                    client.sendToClient(((Cancelereports) msg).ordershandle(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof RetrieveHourlyVisitors) {
                try {
                    client.sendToClient(((RetrieveHourlyVisitors) msg).retrieveHourlyVisitorCountsAsString(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof GenerateHourlyVisitors) {
                try {
                    client.sendToClient(((GenerateHourlyVisitors) msg).generateHourlyVisitorCounts(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (msg instanceof ParkEntrance) {
                try {
                    client.sendToClient(((ParkEntrance) msg).ParkEntranceDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof FreeSpotsData) {
                try {
                    client.sendToClient(((FreeSpotsData) msg).ImportFreeSpotsFromDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof ChangeParkSetting) {
                try {
                    client.sendToClient(((ChangeParkSetting) msg).GetAndChangeSetting(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof CancleOrderInDB) {
                try {
                    client.sendToClient(((CancleOrderInDB) msg).DeleteOrder(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof RequestDataForEmployee) {
                try {
                    client.sendToClient(((RequestDataForEmployee) msg).SearchLoginDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (msg instanceof RequestDataForLogin) {
                try {
                    client.sendToClient(((RequestDataForLogin) msg).SearchLoginDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof InsertNewUser) {
                try {
                    client.sendToClient(((InsertNewUser) msg).registerUser(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof PlaceOrder) {
                try {
                    client.sendToClient(((PlaceOrder) msg).InsertOrderToDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (msg instanceof ImportOrderDetails) {
                try {
                    client.sendToClient(((ImportOrderDetails) msg).importOrderFromDB(con));
                    stmt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
      //////adding by snir
      			if (msg instanceof GetCurrentVisitors) {
      			    GetCurrentVisitors type = (GetCurrentVisitors)msg;
      			    // Handle the "Current" action to fetch the current number of visitors
      			    if (type.getWhatToDo().equals("Current")) {
      			        try {
      			            // Send the current visitors count back to the client
      			            client.sendToClient(type.getCurrentVisitors(con));
      			        } catch (IOException e) {
      			            // Log and handle any IOException that occurs when sending data back to the client
      			            System.err.println("Failed to send current visitors count to client: " + e.getMessage());
      			            e.printStackTrace();
      			        }
      			    } 
      			    // Handle the "Max" action to fetch the maximum allowed number of visitors
      			    else if (type.getWhatToDo().equals("Max")) {
      			        try {
      			            // Send the maximum visitors count back to the client
      			            client.sendToClient(type.getMaxVisitors(con));
      			        } catch (IOException e) {
      			            // Log and handle any IOException that occurs when sending data back to the client
      			            System.err.println("Failed to send max visitors count to client: " + e.getMessage());
      			            e.printStackTrace();
      			        }
      			    }
      			}
      			if (msg instanceof ParkManagementServer) {
      				//ParkManagementServer type = new ParkManagementServer();
      				ParkManagementServer reportType =  (ParkManagementServer)msg;
      				if (reportType.getReportType().equals("NumberOfVisitors")) {
      				try {
      		            // Send the maximum visitors count back to the client
      					client.sendToClient(reportType.handleReportNumberOfVisitorsRequest(con));
      		        } catch (IOException e) {
      		            // Log and handle any IOException that occurs when sending data back to the client
      		            System.err.println("Failed to send report to client: " + e.getMessage());
      		            e.printStackTrace();
      		        }
      				}
      			
      			else if(reportType.getReportType().equals("Usage")){
      				try {
      		            // Send the maximum visitors count back to the client
      					client.sendToClient(reportType.handleUsageReportRequest(con));
      		        } catch (IOException e) {
      		            // Log and handle any IOException that occurs when sending data back to the client
      		            System.err.println("Failed to send report to client: " + e.getMessage());
      		            e.printStackTrace();
      		        }
      	        }
      			}
      			if (msg instanceof GetReportsRequest) {
      		        GetReportsRequest request = (GetReportsRequest) msg;
      		        String parkName = request.getParkName();

      		        try {
      		            client.sendToClient(GetReportsRequestHandler.handleRequest(request, con));
      		            //con.close();
      		        }catch (IOException e) {
      		            try {
      						client.sendToClient("Error fetching reports for park: " + parkName);
      					} catch (IOException e1) {
      						// TODO Auto-generated catch block
      						e1.printStackTrace();
      					}
      		        } 
      		    }
            	if (msg instanceof ShowAllRequests) {
            		try {
            			client.sendToClient(((ShowAllRequests) msg).showRequestsFromParkManager(con));
            			stmt.close();
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
            	}
      	}catch (SQLException e) {
      				e.printStackTrace(); 
      				}			
      	}

      	
    
    /**
     * This method overrides the one in the superclass. Called when the server
     * starts listening for connections.
     */
    protected void serverStarted() {
        sendToGui("GoNature Server listening clients at port number: " + this.getPort());
    }

    /**
     * This method overrides the one in the superclass. Called when the server stops
     * listening for connections.
     */
    protected void serverStopped() {
        sendToGui("Server has stopped listening for connections.");
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        sendToGui("New Client conneted: "+ client.toString() +" "+ this.getPort());
    }
    
    public static void sendToGui(String str) {
        control.logIt(str);
    }
    
    public void setCon(Connection con) {
        EchoServer.con = con;
    }
}
