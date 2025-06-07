package database.tables;

import com.google.gson.Gson;
import mainClasses.Customer;
import database.DB_Connection;
import mainClasses.Event;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TEAM 4
 */
public class EditCustomersTable
{
    /**
     * Makes a JSON file into a customer instances and creates him in the customers table
     *
     * @param json
     * @throws ClassNotFoundException
     */
    public void addCustomerFromJSON(String json) throws ClassNotFoundException
    {
         Customer customer=jsonToCustomer(json);
         addNewCustomer(customer);
    }

    /**
     * Returns a customer instance created from a JSON File
     *
     * @param json
     * @return {Customer} (An instance of a customer)
     */
     public Customer jsonToCustomer(String json)
     {
         Gson gson = new Gson();

         Customer customer = gson.fromJson(json, Customer.class);
         return customer;
    }

    /**
     * Creates a JSON with a particular customer instance's values
     *
     * @param customer
     * @return {String} (A Stringified JSON File)
     */
    public String CustomerToJSON(Customer customer)
    {
         Gson gson = new Gson();
        return gson.toJson(customer, Customer.class);
    }

    public static void updateCustomer(int CustomerId, String key, String value) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update="UPDATE customers SET "+key+"='"+value+"' WHERE customer_id = '"+CustomerId+"'";
        stmt.executeUpdate(update);
        stmt.close();
        con.close();
    }

    /**
     * Function that finds and returns a Customer that it found from the database
     *
     * @param CustomerId
     * @return {Customer} (The Customer instance that it found from the database)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Customer databaseToCustomerID(int CustomerId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM customers WHERE customer_id = '" + CustomerId + "'");
            rs.next();
            String json=DB_Connection.getResultsToJSON(rs);
            Gson gson = new Gson();
            return gson.fromJson(json, Customer.class);
        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Function that finds and returns a Customer that it found from the database
     *
     * @param customerUsername
     * @param customerPassword
     * @return {Customer} (The Customer instance that it found from the database)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Customer databaseToCustomer(String customerUsername, String customerPassword) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM customers WHERE customer_username = '" + customerUsername + "'" + " AND customer_password = '" + customerPassword + "'");
            rs.next();
            String json=DB_Connection.getResultsToJSON(rs);
            Gson gson = new Gson();
            return gson.fromJson(json, Customer.class);
        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Sends a Stringified JSON that contains all the information
     * of a specific customer with his username = customerUsername
     * and password = customerPassword
     *
     * @param customerUsername
     * @return {String} (A Stringified JSON that contains information of a specific Customer)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String databaseCustomerToJSON(String customerUsername, String customerPassword) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try
        {
            rs=stmt.executeQuery("SELECT * FROM customers WHERE customer_username = '" + customerUsername + "'" + " AND customer_password = '" + customerPassword + "'");
            rs.next();
            return DB_Connection.getResultsToJSON(rs);
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * When called, this function returns an ArrayList, each element of which,
     * is a Customer instance that exists in the customers table in the database
     *
     * @return {ArrayList<Customer>} (An ArrayList of all the customers)
     * @throws Exception
     */
    public ArrayList<Customer> databaseToCustomers() throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Customer> customers = new ArrayList<Customer>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM customers");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Customer customer = gson.fromJson(json, Customer.class);
                customers.add(customer);
            }
            return customers;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Establish a database connection and add a Customer in the database.
     *
     * @throws ClassNotFoundException
     */
    public void addNewCustomer(Customer customer) throws ClassNotFoundException
    {
        try
        {
            Connection con = DB_Connection.getConnection();

            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO "
                    + " customers (money_refunded, customer_username, customer_password, customer_firstname, customer_lastname, customer_email, customer_card_number, customer_card_owner, customer_card_expiration, customer_card_cvv) "
                    + " VALUES ("
                    + "'" + customer.getMoney_refunded() + "',"
                    + "'" + customer.getCustomer_username() + "',"
                    + "'" + customer.getCustomer_password() + "',"
                    + "'" + customer.getCustomer_firstname() + "',"
                    + "'" + customer.getCustomer_lastname() + "',"
                    + "'" + customer.getCustomer_email() + "',"
                    + "'" + customer.getCustomer_card_number() + "',"
                    + "'" + customer.getCustomer_card_owner() + "',"
                    + "'" + customer.getCustomer_card_expiration() + "',"
                    + "'" + customer.getCustomer_card_cvv() + "'"
                    + ")";
            //stmt.execute(table);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The customer was successfully added in the database.");

            /* Get the member id from the database and set it to the member */
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(EditCustomersTable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
