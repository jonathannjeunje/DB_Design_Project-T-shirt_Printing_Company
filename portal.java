/*
*
* DB Project: T-Shirt Printing Company 
* 
*
* Author: Alex Jonathan Mvami Njeunje
* Author: Sakala Lakshmi Venkata Maurya
* 
* Date: 04/21/2020
*/

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.*;

public class portal {
	//Declaring Global variables
	//For IO
	static BufferedReader keyboard;
	//For connection to DB
	static Connection conn;
	//For running sql statements
	static Statement stmt;
	static boolean DEBUG = false; 

	public static void main (String[] args) throws IOException {
		
		String username="ajmn100", password = "di93bVZc";
		
		keyboard = new BufferedReader(new InputStreamReader (System.in));
		
		try { 
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("[INFO] Registered the driver...");

			
			//Establishing the connection to the DB
			conn = DriverManager.getConnection ("jdbc:oracle:thin:@oracle1.wiu.edu:1521/toolman.wiu.edu", username, password);
			//Disabling automated commit
			conn.setAutoCommit(false);
			
			System.out.println("[INFO] Logged into oracle as: " + username);
			
			stmt = conn.createStatement();
			
			//Select user interface
			selectUserType();
		} catch(SQLException e) { printException("SQL", e.toString()); }	
	}
	
	private static void selectUserType() {
		String userType = "";
		//Options loop
		while(!userType.equals("0")) {
			System.out.print("\n"
					+ "[OPTIONS] Select the user interface:\n"
					+ "1: Customer.\n"
					+ "2: Employee.\n"
					+ "0: To Exit!\n"
					+ "\n[INPUT] Your selection [ex. 1]> ");
			
			try {
				userType = keyboard.readLine();
			} catch (IOException e) {userType = "IOException";}
			System.out.println();
			
			switch(userType) {
			case "1":
			case "2":
				loadInterface(userType);
				break;
			case "0":
				System.out.println("[EXIT] Program terminated! Thanks for using our program!"); 
				break;
			default:
				System.out.println("[ERROR] Wrong selection! Please, make a valid selection.");
				break;
			}
		}
	}
	
	private static void loadInterface(String userType) {
		
		String command = "";
		
		switch (userType) {
		case "1": //Customer
			while(!command.equals("0")) {
				System.out.print("\n[INFO] Welcome to the Customer interface!\n"
						+ "[OPTIONS] Select the command you wish to run:\n"
						+ "1: Show all the pending, done, and cancelled orders from a customer.\n"
						+ "2: Show all the available T-shirts.\n"
						+ "3: Show all the printing profiles.\n"
						+ "4: Show all the customer's product usage history.\n"
						+ "5: Create a new order\n"
						+ "6: Create a new art work\n"
						+ "0: To Exit!\n"
						+ "\n[INPUT] Your selection [ex. 1]> ");
				
				try {
					command = keyboard.readLine();
				} catch (IOException e) {command = "IOException";}
				System.out.println();
				
				switch(command) {
				case "1":
					getCustomerOrders();
					break;
				case "2":
					getAvailableTShirts();
					break;
				case "3":
					printProfilesList();
					break;
				case "4":
					printCustomerProductHistory();
					break;
				case "5":
					createNewOrder(userType);
					break;
				case "6":
					createArtWork();
					break;
				case "0":
					System.out.println("[EXIT] Interface terminated!"); 
					break;
				default:
					System.out.println("[ERROR] Wrong selection! Please, make a valid selection.");
					break;
				}
			}
			break;
		case "2": //Employee
			while(!command.equals("0")) {
				System.out.print("\n[INFO] Welcome to the Employee interface!\n"
						+ "[OPTIONS] Select the command you wish to run:\n"
						+ "1: Show all the pending, done, and cancelled orders from a customer.\n"
						+ "2: Show all the pending orders with related printing jobs, ordered by their estimated delivery date .\n"
						+ "3: Show all the overdue orders.\n"
						+ "4: Show all the available T-shirts.\n"
						+ "5: Show all the printing profiles.\n"
						+ "6: Create a new order\n"
						+ "7: Create a new printing profile\n"
						+ "8: Update a printing profile price\n"
						+ "9: Delete all canceled orders\n"
						+ "10: Delete all incomplete orders\n"
						+ "0: To Exit!\n"
						+ "\n[INPUT] Your selection [ex. 1]> ");
				
				try {
					command = keyboard.readLine();
				} catch (IOException e) {command = "IOException";}
				System.out.println();
				
				switch(command) {
				case "1":
					getCustomerOrders();
					break;
				case "2":
					printPendingOrders();
					break;
				case "3":
					getOverDueOrders();
					break;
				case "4":
					getAvailableTShirts();
					break;
				case "5":
					printProfilesList();
					break;
				case "6":
					createNewOrder(userType);
					break;
				case "7":
					createNewPrintingProfile();
					break;
				case "8":
					updatePrintingProfilePrice();
					break;
				case "9":
					deleteCancelledOrders();
					break;
				case "10":
					deleteIncompleteOrders();
					break;
				case "0":
					System.out.println("[EXIT] Program terminated! Thanks for using our program!"); 
					break;
				default:
					System.out.println("[ERROR] Wrong selection! Please, make a valid selection.");
					break;
				}
			}
			break;
		default:
			break;
		}		
		
	}

	private static void printSQL(String sql) {
		System.out.print("[SQL]---------------------------------------------------");
		System.out.println(sql);
		System.out.println("--------------------------------------------------------");
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * creates new order
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static void createNewOrder(String userType) {
		System.out.println("[-->] Create an order.\n");
		
		boolean instanceExists = false;
		
		String empEmail = "";
		String cusEmail = "";
		
		float ordTotalCost = 0;
		
		try {
			
			if(userType.equals("1")) { //Customer self service
				empEmail = "selfservice@company.com";
				System.out.print("[INFO] Welcom to self service! \n");
			} else { //Employee
				instanceExists = false;
				while(!instanceExists){
					//Get information from user					
					System.out.print("[INPUT] Enter the employee's email [ex. filiffec@nifty.com]> ");
					empEmail = keyboard.readLine();
					
					//Verify if the instance already exists
					if(empExists(empEmail)) instanceExists = true;
					else System.out.println("[ERROR] Couldn't find a matching instance in the database. Please, try again!");
				}
			}
			
			instanceExists = false;
			while(!instanceExists){
				//Get information from user					
				System.out.print("[INPUT] Enter the customer's email [ex. owhate5@yahoo.com]> ");
				cusEmail = keyboard.readLine();
				
				//Verify if the instance already exists
				if(cusExists(cusEmail)) instanceExists = true;
				else System.out.println("[ERROR] Couldn't find a matching instance in the database. Please, try again!");
			}
			
			//Create Order in database and get id
			int ordID = insertOrder(empEmail, cusEmail);
			
			//insert another printing job option
			String option = "yes";
			
			//loop for more printing jobs
			while(!option.equals("no")) {
				switch(option) {
				case "yes":
					
					String proName = "";
					String tshBrand = "";
					String tshName = "";
					String tshColor = "";
					String tshSize = "";
					String artName = "";
					
					instanceExists = false;
					while(!instanceExists){
						//get printing profile info
						System.out.print("[INPUT] Enter the printing profile name [ex. Profile-71247]> ");
						proName = keyboard.readLine();
						
						//Verify if the instance already exists
						if(proExists(proName)) instanceExists = true;
						else System.out.println("[ERROR] Couldn't find a matching instance in the database. Please, try again!");
					}
					
					instanceExists = false;
					while(!instanceExists){
						//get t-shirt info
						System.out.print("[INPUT] Enter the tshirt's brand [ex. Harriton]> ");
						tshBrand = keyboard.readLine();
						System.out.print("[INPUT] Enter the tshirt's name [ex. Fruit of the Loom Cotton]> ");
						tshName = keyboard.readLine();
						System.out.print("[INPUT] Enter the tshirt's color [ex. Maroon]> ");
						tshColor = keyboard.readLine();
						System.out.print("[INPUT] Enter the tshirt's size [ex. x-small]> ");
						tshSize = keyboard.readLine();
						
						//Verify if the instance already exists
						if(tshExists(tshBrand, tshName, tshColor, tshSize)) instanceExists = true;
						else System.out.println("[ERROR] Couldn't find a matching instance in the database. Please, try again!");
					}
					
					instanceExists = false;
					while(!instanceExists){
						//get art work info
						System.out.print("[INPUT] Enter the art's work name [ex. Art-33280]> ");
						artName = (keyboard.readLine());
						
						//Verify if the instance already exists
						if(artExists(artName)) instanceExists = true;
						else System.out.println("[ERROR] Couldn't find a matching instance in the database. Please, try again!");
					}
					
					System.out.print("[INPUT] Enter the quantity needed [ex. 5]> ");
					int jobQuantity = Integer.parseInt(keyboard.readLine());
					
					//Calculate job unit price
					float jobUnitPrice = getTshirtPrice(tshBrand, tshName, tshColor, tshSize) + getProPrice(proName);
					//Calculate job total cost
					float jobTotalCost = jobUnitPrice * jobQuantity;
					//Calculate job estimated time
					float jobEstTime = getProTime(proName) * jobQuantity;
					//Calculate the order total cost
					ordTotalCost += jobTotalCost;
					
					//Get additional instructions if any
					String jobInstructions = "";
					String option2 = ""; 
					while(!option2.equals("yes") && !option2.equals("no")) {
						System.out.print("\n[OPTIONS] Do you have any additional instructions [yes/no]: ");
						option2 = keyboard.readLine();
						switch(option2) {
						case "yes":
							System.out.print("[INPUT] Enter the instructions [ex. Add a smiley on the bottom left of one shirt]> ");
							jobInstructions = keyboard.readLine();
							break;
						default:
							break;
						}
					}
					
					//Create new printing job
					insertPrintingJob(proName, jobQuantity, jobUnitPrice, jobTotalCost, jobInstructions, jobEstTime, tshBrand, tshName, tshColor, tshSize, ordID, artName);
					
					break;
				default:
					//Keep asking if yes or no not inputed.
					break;
				}
				
				System.out.print("\n[OPTIONS] Do you have an additional printing job [yes/no]: ");
				option = keyboard.readLine();
				
				if(option.equals("no")) updateOrderToPending(ordID);
			}
		
			//Update order total cost
			updateOrderTotalCost(ordID, ordTotalCost);
			
			//Print full order
			printOrder(ordID);
		
		} catch (IOException e) { printException("IO", e.toString()); }
					
		System.out.println();
		System.out.println("[<--] Insert record into NeededToInstall table.");
	}

	/*
	 * updates order status to pending
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static void updateOrderToPending(int ordID) {
		try {
			String sql = ""
					+ "\nUPDATE  Orders "
					+ "\nSET ordStatus = 'pending'"
					+ "\nWHERE ordID = " + ordID + ""
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New Update into the table
			stmt.executeUpdate(sql);				
			
			//Print result
			System.out.println("\n[RESULT] Total cost updated!");
			//Commit changes to the database
			conn.commit();
		} catch (SQLException e) { printException("SQL", e.toString()); }
	}

	/*
	 * checks existence 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static boolean artExists(String artName) {
		boolean instanceExists = false;
		try {
			String sql = ""
					+ "\nSELECT *"
					+ "\nFROM artWorks"
					+ "\nWHERE artName LIKE '" + artName + "'"
					+ "";
			if(DEBUG) if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set is assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
			
			instanceExists = rs.next();
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		return instanceExists;
	}

	/*
	 * checks existence 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static boolean tshExists(String tshBrand, String tshName, String tshColor, String tshSize) {
		boolean instanceExists = false;
		try {
			String sql = ""
					+ "\nSELECT *"
					+ "\nFROM tshirts"
					+ "\nWHERE tshBrand LIKE '" + tshBrand + "'"
					+ "\nAND tshName LIKE '" + tshName + "'"
					+ "\nAND tshColor LIKE '" + tshColor + "'"
					+ "\nAND tshSize LIKE '" + tshSize + "'"
					+ "";
			//if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set is assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
			
			instanceExists = rs.next();
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		return instanceExists;
	}

	/*
	 * checks existence 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static boolean proExists(String proName) {
		boolean instanceExists = false;
		try {
			String sql = ""
					+ "\nSELECT *"
					+ "\nFROM printingProfiles"
					+ "\nWHERE proName LIKE '" + proName + "'"
					+ "";
			//if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set is assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
			
			instanceExists = rs.next();
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		return instanceExists;
	}

	/*
	 * checks existence 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static boolean cusExists(String cusEmail) {
		boolean instanceExists = false;
		try {
			String sql = ""
					+ "\nSELECT *"
					+ "\nFROM customers"
					+ "\nWHERE cusEmail LIKE '" + cusEmail + "'"
					+ "";
			//if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set is assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
			
			instanceExists = rs.next();
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		return instanceExists;
	}

	/*
	 * checks existence 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static boolean empExists(String empEmail) {
		boolean instanceExists = false;
		try {
			String sql = ""
					+ "\nSELECT *"
					+ "\nFROM employees"
					+ "\nWHERE empEmail LIKE '" + empEmail + "'"
					+ "";
			//if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set is assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
						
			instanceExists = rs.next();
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		return instanceExists;
	}

	/*
	 * insert new incomplete order
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static int insertOrder(String empEmail, String cusEmail) {
		int ordID = -1;
		try {
			String sql = ""
					+ "\nINSERT INTO orders(empEmail, cusEmail, ordStatus) "
					+ "\nVALUES('" + empEmail + "', '" + cusEmail + "', 'incomplete')";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New insertion into the table
			stmt.executeUpdate(sql);				
			
			//Print result
			System.out.println("\n[RESULT] New incomplete order in progress!");
			//Commit changes to the database
			conn.commit();
			
			//get recently inserted order
			ordID = getMaxOrdID();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return ordID;
		 
	}

	/*
	 * insert printing job
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static void insertPrintingJob(String proName, int jobQuantity, float jobUnitPrice, float jobTotalCost,
			String jobInstructions, float jobEstTime, String tshBrand, String tshName, String tshColor, String tshSize, int ordID, String artName) {
		try {
			String sql = ""
					+ "\nINSERT INTO printingJobs(proName, jobQuantity, jobUnitPrice, jobTotalCost, jobInstructions, jobEstTime, tshBrand, tshName, tshColor, tshSize, ordID, artName)"
					+ "\nVALUES ('" + proName + "', " + jobQuantity + ", " + jobUnitPrice + ", " + jobTotalCost + ", '" + jobInstructions + "', " + jobEstTime + ", '" + tshBrand + "', '" + tshName + "', '" + tshColor + "', '" + tshSize + "', " + ordID + ", '" + artName + "')";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New insertion into the table
			stmt.executeUpdate(sql);
			
			//Print result
			System.out.println("\n[RESULT] Printing job registered successfully!");
			//Commit changes to the database
			conn.commit();
		} catch (SQLException e) { printException("SQL", e.toString()); }
	}

	/*
	 * update order total cost
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static void updateOrderTotalCost(int ordID, float ordTotalCost) {
		try {
			String sql = ""
					+ "\nUPDATE  Orders "
					+ "\nSET ordTotalCost = " + ordTotalCost + ""
					+ "\nWHERE ordID = " + ordID + ""
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New Update into the table
			stmt.executeUpdate(sql);				
			
			//Print result
			System.out.println("\n[RESULT] Total cost updated!");
			//Commit changes to the database
			conn.commit();
		} catch (SQLException e) { printException("SQL", e.toString()); }
	}
	
	/*
	 * create new printing profile
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static void createNewPrintingProfile() {
		System.out.println("[-->] Create a printing profile.\n");
		try {
			boolean instanceExists = false;
			String empEmail = "";

			while(!instanceExists){
				//Get information from user					
				System.out.print("[INPUT] Enter the employee's email [ex. filiffec@nifty.com]> ");
				empEmail = keyboard.readLine();
				
				//Verify if the instance already exists
				if(empExists(empEmail)) instanceExists = true;
				else System.out.println("[ERROR] Couldn't find a matching instance in the database. Please, try again!");
			}
		
			System.out.print("[INPUT] Enter the profile name [ex. BigBWSleeves]> ");
			String proName = keyboard.readLine();
			System.out.print("[INPUT] Enter the profile mode [ex. blackandwhite]> ");
			String proMode = keyboard.readLine();
			System.out.print("[INPUT] Enter the profile size [ex. large]> ");
			String proSize = keyboard.readLine();
			System.out.print("[INPUT] Enter the profile position [ex. Sleeves]> ");
			String proPosition = keyboard.readLine();
			System.out.print("[INPUT] Enter the profile description [ex. sleeves only.]> ");
			String proDescription = keyboard.readLine();
			System.out.print("[INPUT] Enter the profile estimated time [ex. 2]> ");
			float proEstTime = Float.parseFloat(keyboard.readLine());
			System.out.print("[INPUT] Enter the profile price [ex. 100]> ");
			float proPrice = Float.parseFloat(keyboard.readLine());
			
			insertPrintingProfile(proName, proMode, proSize, proPosition, proDescription, proEstTime, proPrice, empEmail);
			
		} catch (IOException e) {printException("IO", e.toString());}
		
		
		System.out.println("[<--] Create a printing profile.\n");
	}
	/*
	 * insert printing profile in db
	 * Author: Alex Jonathan Mvami Njeunje
	 * */	
	private static void insertPrintingProfile(String proName, String proMode, String proSize, String proPosition,
			String proDescription, float proEstTime, float proPrice, String empEmail) {
		try {
			String sql = ""
					+ "\nINSERT INTO printingProfiles(proName, proMode, proSize, proPosition, proDescription, proEstTime, proPrice, empEmail)"
					+ "\nVALUES ('" + proName + "', '" + proMode + "', '" + proSize + "', '" + proPosition + "', '" + proDescription + "', " + proEstTime + ", " + proPrice + ", '" + empEmail + "')";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New insertion into the table
			stmt.executeUpdate(sql);
			
			//Print result
			System.out.println("\n[RESULT] Printing profile inserted successfully!");
			//Commit changes to the database
			conn.commit();
		} catch (SQLException e) { printException("SQL", e.toString()); }
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Gets the order id
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static ResultSet getOrder(int ordID) {
		ResultSet rs = null;
		
		try {
			String sql = ""
					+ "\nSELECT * "
					+ "\nFROM pendingorders "
					+ "\nWHERE ordID = " + ordID + ""
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			//New instance of the result set assigned in the result from an SQL query
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return rs;
	}
	/*
	 * Gets the max order id
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static int getMaxOrdID() {
		int result = 0;
		
		try {
			String sql = ""
					+ "\nSELECT MAX(ordID) "
					+ "\nFROM Orders "
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			//New instance of the result set assigned in the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) result = rs.getInt(1);
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return result;
	}
	/*
	 * get the pro price
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static float getProPrice(String proName) {
		float value = 0;
		
		try {
			String sql = ""
					+ "\nSELECT proPrice"
					+ "\nFROM printingProfiles"
					+ "\nWHERE proName LIKE '" + proName + "'"
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
			//Retrieve the value
			if(rs.next())  value = rs.getFloat(1);
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return value;
	}
	/*
	 * Gets the profiles estimated time
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static float getProTime(String proName) {
		float value = 0;
		
		try {
			String sql = ""
					+ "\nSELECT proEstTime"
					+ "\nFROM printingProfiles"
					+ "\nWHERE proName LIKE '" + proName + "'"
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
			//Retrieve the value
			if(rs.next())  value = rs.getFloat(1);
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return value;
	}
	/*
	 * Gets the tshirts price
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static float getTshirtPrice(String tshBrand, String tshName, String tshColor, String tshSize) {
		float value = 0;
		
		try {
			String sql = ""
					+ "\nSELECT tshPrice"
					+ "\nFROM tshirts"
					+ "\nWHERE tshBrand LIKE '" + tshBrand + "'"
					+ "\nAND tshName LIKE '" + tshName + "'"
					+ "\nAND tshColor LIKE '" + tshColor + "'"
					+ "\nAND tshSize LIKE '" + tshSize + "'"
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			
			//New instance of the result set assigned the result from an SQL query
			ResultSet rs = stmt.executeQuery(sql);				
			//Retrieve the value
			if(rs.next())  value = rs.getFloat(1);
			//Terminate the result set
			rs.close();
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return value;
	}
	/*
	 * get the profiles list
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static ResultSet getProfilesList() {
		ResultSet rs = null;
		
		try {
			String sql = ""
					+ "\nSELECT * "
					+ "\nFROM profileslist "
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			//New instance of the result set assigned in the result from an SQL query
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return rs;
	}
	/*
	 * get the pending orders
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static ResultSet getPendingOrders() {
		ResultSet rs = null;
		
		try {
			String sql = ""
					+ "\nSELECT * "
					+ "\nFROM pendingorders "
					+ "\nORDER BY ordEstDeliveryDate"
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			//New instance of the result set assigned in the result from an SQL query
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return rs;
	}
	/*
	 * gets the customer products history
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static ResultSet getCustomerProductHistory(String cusEmail) {
		ResultSet rs = null;
		
		try {
			String sql = ""
					+ "\nSELECT * "
					+ "\nFROM CustomerProductHistory "
					+ "\nWHERE cusEmail LIKE '" + cusEmail + "'"
					+ "";
			if(DEBUG) /*[DEBUG LINE]*/ printSQL(sql);
			//New instance of the result set assigned in the result from an SQL query
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) { printException("SQL", e.toString()); }
		
		return rs;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * the the order on command line 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static void printOrder(int ordID) {
		System.out.println("[-->] Print order.");
		ResultSet rs = getOrder(ordID);
		
		String table = "";
		try {
			//Table cell size
			int cs = 30;
			String hLine = "";
			
			//Retrieve meta data from the result set
			ResultSetMetaData rsMetaData = rs.getMetaData();
			//Get the number of columns in the result set
			int colCnt = rsMetaData.getColumnCount();
			
		    //Generate table header
			for (int i = 1; i <= colCnt; i++) {
				//if(i != 1) {//Skipping the ordID from print
					hLine += String.format("+%-"+cs+"s", "").replace(' ', '=');
					table += String.format("|%-"+cs+"s", rsMetaData.getColumnName(i));   
				//}
		    }
			table += "|"; 
			hLine += "+";
			table = hLine + "\n" + table + "\n" + hLine;
			
			String prev_ordID = "";
			boolean firstrow = true;
			//Generate the table rows
		    while(rs.next()) {
		    	String line = "";
				String row = "";
		        for (int i = 1; i <= colCnt; i++) {
		        	if(i == 1) { //ordID
		        		if(!prev_ordID.equals(rs.getString(1))) {
			        		prev_ordID = rs.getString(i);
			        		line += String.format("+%-"+cs+"s", "").replace(' ', '-'); //Skipping the ordID from print
			        		row += String.format("|%-"+cs+"s", rs.getString(i)); //Skipping the ordID from print
			        		i++;
			        		line += String.format("+%-"+cs+"s", "").replace(' ', '-');
			        		row += String.format("|%-"+cs+"s", rs.getString(i));
			        	} else {
			        		line += String.format(".%-"+cs+"s", ""); //Skipping the ordID from print
			        		row += String.format("|%-"+cs+"s", ""); //Skipping the ordID from print
			        		i++;
			        		line += String.format(".%-"+cs+"s", "");
			        		row += String.format("|%-"+cs+"s", "");
			        	}
		        	} else {
		        		line += String.format("+%-"+cs+"s", "").replace(' ', '-');
		        		row += String.format("|%-"+cs+"s", rs.getString(i));
		        	}   
		        }
		        line += "+";
				row += "|";
				if(firstrow) {
					table += "\n" + row;
					firstrow = false;
				} else {
					table += "\n" + line + "\n" + row;
				}	
		    }
		    table += "\n" + hLine;
		    
		} catch (SQLException e) {
			System.out.println();
			System.out.println("[ERROR] Caught SQL Exception:\n" + e);
		}
				
		System.out.println("[RESULT]\n" + table);
		System.out.println("[<--] Print order.");
	}
	
	/*
	 * prints any exception message on the command line
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static void printException(String type, String e) {
		System.out.println();
		System.out.println("[ERROR] Caught " + type + " Exception:\n" + e);
	}
	/*
	 * prints all the pending orders on the command line 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static void printPendingOrders() {
		System.out.println("[-->] Print all the pending orders ordered by the estimated delivery date.");
		
		ResultSet rs = getPendingOrders();
		
		String table = "";
		try {
			//Table cell size
			int cs = 30;
			String hLine = "";
			
			//Retrieve meta data from the result set
			ResultSetMetaData rsMetaData = rs.getMetaData();
			//Get the number of columns in the result set
			int colCnt = rsMetaData.getColumnCount();
			
		    //Generate table header
			for (int i = 1; i <= colCnt; i++) {
				//if(i != 1) {//Skipping the ordID from print
					hLine += String.format("+%-"+cs+"s", "").replace(' ', '=');
					table += String.format("|%-"+cs+"s", rsMetaData.getColumnName(i));   
				//}
		    }
			table += "|"; 
			hLine += "+";
			table = hLine + "\n" + table + "\n" + hLine;
			
			String prev_ordID = "";
			boolean firstrow = true;
			//Generate the table rows
		    while(rs.next()) {
		    	String line = "";
				String row = "";
		        for (int i = 1; i <= colCnt; i++) {
		        	if(i == 1) { //ordID
		        		if(!prev_ordID.equals(rs.getString(1))) {
			        		prev_ordID = rs.getString(i);
			        		line += String.format("+%-"+cs+"s", "").replace(' ', '-'); //Skipping the ordID from print
			        		row += String.format("|%-"+cs+"s", rs.getString(i)); //Skipping the ordID from print
			        		i++;
			        		line += String.format("+%-"+cs+"s", "").replace(' ', '-');
			        		row += String.format("|%-"+cs+"s", rs.getString(i));
			        		i++;
			        		line += String.format("+%-"+cs+"s", "").replace(' ', '-');
			        		row += String.format("|%-"+cs+"s", rs.getString(i));
			        	} else {
			        		line += String.format(".%-"+cs+"s", ""); //Skipping the ordID from print
			        		row += String.format("|%-"+cs+"s", ""); //Skipping the ordID from print
			        		i++;
			        		line += String.format(".%-"+cs+"s", "");
			        		row += String.format("|%-"+cs+"s", "");
			        		i++;
			        		line += String.format(".%-"+cs+"s", "");
			        		row += String.format("|%-"+cs+"s", "");
			        	}
		        	} else {
		        		line += String.format("+%-"+cs+"s", "").replace(' ', '-');
		        		row += String.format("|%-"+cs+"s", rs.getString(i));
		        	}   
		        }
		        line += "+";
				row += "|";
				if(firstrow) {
					table += "\n" + row;
					firstrow = false;
				} else {
					table += "\n" + line + "\n" + row;
				}	
		    }
		    table += "\n" + hLine;
		    
		} catch (SQLException e) {
			System.out.println();
			System.out.println("[ERROR] Caught SQL Exception:\n" + e);
		}
				
		System.out.println("[RESULT]\n" + table);
		System.out.println("[<--] Print all the pending orders ordered by the estimated delivery date.");
	}
	/*
	 * prints all the profiles on the command line 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static void printProfilesList() {
		System.out.println("[-->] Print all the printing profiles.");
		System.out.println("[RESULT]\n" + formatToTable(getProfilesList()));
		System.out.println("[<--] Print all the printing profiles.");
	}
	/*
	 * prints all the customer products history on the command line 
	 * Author: Alex Jonathan Mvami Njeunje
	 * */
	private static void printCustomerProductHistory() {
		try {
			
			boolean instanceExists = false;
			String cusEmail = "";
			
			while(!instanceExists){
				//Get information from user					
				System.out.print("[INPUT] Enter the customer's email [ex. owhate5@yahoo.com]> ");
				cusEmail = keyboard.readLine();
				
				//Verify if the instance already exists
				if(cusExists(cusEmail)) instanceExists = true;
				else System.out.println("[ERROR] Couldn't find a matching instance in the database. Please, try again!");
			}
			
			System.out.println("[-->] Print all the customer service history.");
			System.out.println("[RESULT]\n" + formatToTable(getCustomerProductHistory(cusEmail)));
			System.out.println("[<--] Print all the customer service history.");
			
		} catch (IOException e) { printException("IO", e.toString()); }
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Author: Alex Jonathan Mvami Njeunje
	 * returns the result set in a table format
	 * */
	private static String formatToTable(ResultSet rs) {
		String table = "";
		try {
			//Table cell size
			int cs = 30;
			String hLine = "";
			
			//Retrieve meta data from the result set
			ResultSetMetaData rsMetaData = rs.getMetaData();
			//Get the number of columns in the result set
			int colCnt = rsMetaData.getColumnCount();
			
		    //Generate table header
			for (int i = 1; i <= colCnt; i++) {
				//if(i != 1) {//Skipping the ordID from print
					hLine += String.format("+%-"+cs+"s", "").replace(' ', '=');
					table += String.format("|%-"+cs+"s", rsMetaData.getColumnName(i));   
				//}
		    }
			table += "|"; 
			hLine += "+";
			table = hLine + "\n" + table + "\n" + hLine;
			
			boolean firstrow = true;
			//Generate the table rows
		    while(rs.next()) {
		    	String line = "";
				String row = "";
		        for (int i = 1; i <= colCnt; i++) {
	        		line += String.format("+%-"+cs+"s", "").replace(' ', '-');
	        		row += String.format("|%-"+cs+"s", rs.getString(i));
		        }
		        line += "+";
				row += "|";
				if(firstrow) {
					table += "\n" + row;
					firstrow = false;
				} else {
					table += "\n" + line + "\n" + row;
				}	
		    }
		    table += "\n" + hLine;
		    
		} catch (SQLException e) {
			System.out.println();
			System.out.println("[ERROR] Caught SQL Exception:\n" + e);
		}
		
		return table;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////	   
	
	//This method deletes all in-complete orders
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void deleteIncompleteOrders() {
	      try{
	         String validateOrder = "";
	         System.out.println("[INPUT] Are you sure you want to delete all incomplete orders? [Ex: yes/no]");
	         validateOrder = keyboard.readLine();
	         
	         if(validateOrder.equals("yes")){
	            Statement DelStmt= conn.createStatement();
	            String DeleteRecord = " DELETE FROM Orders WHERE ordStatus = 'incomplete' ";
	            DelStmt.executeUpdate(DeleteRecord);
	            System.out.println("[RESULT] Record(s) deleted");
	            System.out.println();
	         }
	         else{
	            System.out.println("[RESULT] Going to main menu..");
	            System.out.println();
	         }
	         
	      }
	      catch (Exception e){
	         System.out.println();
	         printException("Exception",e.toString());
	      }
	   }

	   //This method updates printing profiles of a customer
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void updatePrintingProfilePrice() {
	      try{
	            System.out.println("[INPUT] Enter Profile Name: [Ex: Profile-39024]");
	            String profileName = keyboard.readLine();
	            String ValidateQuery =     "SELECT "
	                                 +   " proName "
	                                 +   " FROM PrintingProfiles "
	                                 +   " WHERE proName = '" + profileName +"'";
	           
	            ResultSet GetProfileName = stmt.executeQuery(ValidateQuery);
	         
	            // If profile not found then it exits from the method
	            if (!GetProfileName.next()){
	               System.out.println("[RESULT] No Profile found..");
	               System.out.println();
	               return;
	            }
	            else{
	               System.out.println("[INPUT] Enter profile price to be updated: [Ex: 24.93]");
	               String profilePrice = keyboard.readLine();
	               
	               Statement UpdateStmt= conn.createStatement();
	               String UpdateRecord = " Update  PrintingProfiles SET proPrice = '"
	                                    + profilePrice + "' WHERE proName = '" 
	                                    + profileName + "'";
	      
	               UpdateStmt.executeUpdate(UpdateRecord);
	               System.out.println("[RESULT] Record Updated Successfully");
	               System.out.println();
	            }               
	      }
	      catch(Exception e){
	         System.out.println();
	         printException("Exception",e.toString());
	      }
	   }
	   
	   //This method will get the list of all available T-Shirts
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void getAvailableTShirts() {
	      try{
	         String AvailableTShirtsQuery =  " SELECT * " 
		                              
		                              + " FROM	availabletshirts ";
	                                 
	          ResultSet GetAvailableTShirts = stmt.executeQuery(AvailableTShirtsQuery);
	          System.out.println(formatToTable(GetAvailableTShirts));
	          GetAvailableTShirts.close();
	      }
	      catch(Exception e){
	         System.out.println();
	         printException("Exception",e.toString());
	      }
	   }

	   //This method gets all overdue orders
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void getOverDueOrders() {
	      try{
	            String OrderDueQuery =  " SELECT * " 
		                              
		                              + " FROM overdueorders ";
	                             
	           
	            ResultSet GetOverDueOrders = stmt.executeQuery(OrderDueQuery);
	            System.out.println(formatToTable(GetOverDueOrders)); 
	            GetOverDueOrders.close();
	            
	      }
	      catch(Exception e){
	         System.out.println();
	         printException("Exception",e.toString());
	      }
	   }

	   //This method gives all orders of a customer
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void getCustomerOrders() {
	      try{
	            System.out.println("Enter Customer Email: [Ex: aramirez0@slideshare.net]");
	            String cusEmail = keyboard.readLine();
	         
	            String OrderQuery =  " SELECT * " 
	                                 + " FROM	customerorders "
	                                 + " WHERE cusemail = '"+ cusEmail+"'";
	                             
	            ResultSet GetCusOrders = stmt.executeQuery(OrderQuery);
	            System.out.println(formatToTable(GetCusOrders)); 
	            GetCusOrders.close();
	         }
	         catch(Exception e){
	            System.out.println();
	            printException("Exception",e.toString());
	         }
	   }

	   //This method updates customer name provided by customer email
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void updateCustomerName() {
	      try{
	         System.out.println("[INPUT] Enter Email Address: [Ex: aramirez0@slideshare.net]");
	         String cusEmail = keyboard.readLine();
	         String ValidateQuery =     "SELECT "
	                             +   " cusName "
	                             +   " FROM Customers "
	                             +   " WHERE cusEmail = '" + cusEmail + "'";
	           
	         ResultSet GetCustomerName = stmt.executeQuery(ValidateQuery);
	         
	         // If there is no customer then it comes out of loop
	         if (!GetCustomerName.next()){
	            System.out.println("[RESULT] No Customer found..");
	            System.out.println();
	            return;
	         }
	         else{
	            System.out.println("[INPUT] Enter customer name to be updated: Ex: Aristotle Ramirez]");
	            String cusName = keyboard.readLine();
	            
	            Statement UpdateStmt= conn.createStatement();
	            String UpdateRecord = " Update  Customers SET cusName = '"
	                                 + cusName + "' WHERE cusEmail = '" 
	                                 + cusEmail + "'";
	      
	            UpdateStmt.executeUpdate(UpdateRecord);
	            System.out.println("[RESULT] Record Updated Successfully");
	            System.out.println();
	         }               
	      }
	      catch(Exception e){
	         System.out.println();
	         printException("Exception",e.toString());
	      }
	   }
	   
	   //This method deletes all cancelled orders
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void deleteCancelledOrders() {
	      try{
	         String validateOrder = "";
	         System.out.println("[INPUT] Are you sure you want to delete all cancelled orders? [Ex: yes/no]");
	         validateOrder = keyboard.readLine();
	         
	         if(validateOrder.equals("yes")){
	            Statement DelStmt= conn.createStatement();
	            String DeleteRecord = " DELETE FROM Orders WHERE ordStatus = 'cancelled' ";
	            DelStmt.executeUpdate(DeleteRecord);
	            System.out.println("[RESULT] Record(s) deleted");
	            System.out.println();
	         }
	         else{
	            System.out.println("[RESULT] Going to main menu..");
	            System.out.println();
	         }
	         
	      }
	      catch (Exception e){
	         System.out.println();
	         printException("Exception",e.toString());
	      }
	   }

	   //This method creates a art work
	   //Author: Sakala Lakshmi Venkata Maurya
	   private static void createArtWork(){
		   try{
			   try{
		            System.out.println("[INPUT] Enter Art work name: [EX: Art-29210]");
		            String artName = keyboard.readLine();
		      
		            String ValidateQuery =     "SELECT "
		                                 +   " artName "
		                                 +   " FROM artworks "
		                                 +   " WHERE artName = '" + artName +"'" ;
		           
		            ResultSet GetArtWorkName = stmt.executeQuery(ValidateQuery);
		         
		            // If art name found then it exits from the method
		            if (GetArtWorkName.next()){
		               System.out.println("[RESULT] Art Name found. Cannot be duplicated..");
		               System.out.println();
		               return;
		            }
		            else{
		               System.out.println("[INPUT] Enter Art Image: [EX: http://dummyimage.com/25x28.png/ff4444/ffffff]");
		               String artImage = keyboard.readLine();
		               
		               System.out.println("[INPUT] Enter customer email: [EX: aramirez0@slideshare.net]");
		               String cusEmail = keyboard.readLine();
		               
		               String ValidateCustomer =     "SELECT "
		                                          +   " cusName "
		                                          +   " FROM Customers "
		                                          +   " WHERE cusEmail = '" + cusEmail +"'";
		           
		               ResultSet GetCustomerName = stmt.executeQuery(ValidateCustomer);
		               
		               // If customer not found then it exits from the method
		               if (!GetCustomerName.next()){
		                  System.out.println("[RESULT] Customer not found..");
		                  System.out.println();
		                  return;
		               }
		               else{
		                  Statement InsStmt= conn.createStatement();
		                  String InsertRecord = " INSERT INTO ArtWorks VALUES( '"
		                                       + artName + "' , '" 
		                                       + artImage + "' , '" 
		                                       + cusEmail 
		                                       + "' )";
		      
		                  InsStmt.executeUpdate(InsertRecord);
		                  System.out.println("Record inserted successfully..");
		                  System.out.println();
		               }
		            }
			   }catch(IOException e){printException("IO", e.toString());}
		   }catch(SQLException e){printException("IO", e.toString());}
	   }
	
	 	   
}/*
*
* SPRING 2020 Validation Code (DO NOT DELETE):  mj59yFBezu74iHAm
*
*/


