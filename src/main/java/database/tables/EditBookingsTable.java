package database.tables;

import com.google.gson.Gson;
import database.DB_Connection;
import mainClasses.Booking;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author Team 4
 */
public class EditBookingsTable
{
    /**
     * Adds a new booking from a JSON
     *
     * @param json
     * @throws ClassNotFoundException
     */
    public static void addBookingFromJSON(String json) throws ClassNotFoundException
    {
        Booking booking = jsonToBooking(json);
        addNewBooking(booking);
    }

    /**
     * Makes a json file into a booking and returns it
     *
     * @param json
     * @return {Booking instance} (The booking instance created from the JSON)
     */
    public static Booking jsonToBooking(String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json, Booking.class);
    }

    /**
     * Takes a booking instance and returns it as a JSON File
     * @param booking
     * @return {String} (The JSON File)
     */
    public String bookingToJSON(Booking booking)
    {
        Gson gson = new Gson();
        return gson.toJson(booking, Booking.class);
    }

    /**
     * When called, this function returns an ArrayList, each element of which,
     * is a Book instance that exists in the bookings table in the database
     *
     * @return {ArrayList<Booking>} (An ArrayList of all the bookings)
     * @throws Exception
     */
    public ArrayList<Booking> databaseToBookings() throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM bookings");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Booking booking = gson.fromJson(json, Booking.class);
                bookings.add(booking);
            }
            return bookings;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Function that finds and returns a Booking that it found from the database
     * With a specific Booking id
     *
     * @param BookingId
     * @return {Booking} (The Booking instance that it found from the database)
     * @throws Exception
     */
    public static Booking databaseToBooking(int BookingId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM bookings WHERE booking_id= '" + BookingId + "'");
            rs.next();
            String json=DB_Connection.getResultsToJSON(rs);
            Gson gson = new Gson();
            return gson.fromJson(json, Booking.class);
        }
        catch (Exception e)
        {
            Logger.getLogger(EditBookingsTable.class.getName()).log(Level.SEVERE, "Error in databaseToBooking", e);
        }
        return null;
    }

    /**
     * Function that returns all the bookings of a specific Customer
     *
     * @param CustomerId
     * @return {ArrayList<Booking>} (A List of all the bookings of a specific Customer)
     * @throws Exception
     */
    public static ArrayList<Booking> databaseToCustomerBookings(int CustomerId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM bookings WHERE customer_id= '" + CustomerId + "'");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Booking booking = gson.fromJson(json, Booking.class);
                bookings.add(booking);
            }
            return bookings;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
        }
        return null;
    }

    /**
     * Function that returns all the bookings of a specific Event
     *
     * @param EventId
     * @return {ArrayList<Booking>} (A List of all the bookings of a specific Event)
     * @throws Exception
     */
    public static ArrayList<Booking> databaseToEventBookings(int EventId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM bookings WHERE event_id= '" + EventId + "'");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Booking booking = gson.fromJson(json, Booking.class);
                bookings.add(booking);
            }
            return bookings;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
        }
        return null;
    }

    /**
     * Function that updates a booking
     *
     * @param BookingId
     * @param updates
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void updateBooking(int BookingId, HashMap<String, String> updates) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        for (String key : updates.keySet())
        {
            String update = "UPDATE bookings SET " + key + "='" + updates.get(key) + "'" + "WHERE booking_id = '" + BookingId + "'";
            stmt.executeUpdate(update);
        }
        stmt.close();
        con.close();
    }

    /**
     * Deletes a specific booking instance with Id = BookingId
     *
     * @param BookingId
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void deleteBooking(int BookingId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String deleteQuery = "DELETE FROM bookings WHERE booking_id = '" + BookingId + "'";
        stmt.executeUpdate(deleteQuery);
        stmt.close();
        con.close();
    }

    /**
     * Adds a new booking instance on the database
     *
     * @param booking
     * @throws ClassNotFoundException
     */
    public static void addNewBooking(Booking booking) throws ClassNotFoundException
    {
        try
        {
            Connection con = DB_Connection.getConnection();

            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO "
                    + " bookings (customer_id, event_id, ticket_count, booking_date, total_payment) "
                    + " VALUES ("
                    + "'" + booking.getCustomer_id() + "',"
                    + "'" + booking.getEvent_id() + "',"
                    + "'" + booking.getTicket_count() + "',"
                    + "'" + booking.getBooking_date() + "',"
                    + "'" + booking.getTotal_payment() + "'"
                    + ")";
            //stmt.execute(table);
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The booking was successfully added in the database.");

            stmt.close();

        }
        catch (SQLException ex)
        {
            Logger.getLogger(EditBookingsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
