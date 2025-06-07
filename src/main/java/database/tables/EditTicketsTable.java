package database.tables;

import com.google.gson.Gson;
import database.DB_Connection;
import mainClasses.Ticket;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author TEAM 4
 */
public class EditTicketsTable
{
    public void addTicketFromJSON(String json) throws ClassNotFoundException
    {
        Ticket ticket = jsonToTicket(json);
        addNewTicket(ticket);
    }

    public Ticket jsonToTicket(String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json, Ticket.class);
    }

    public String ticketToJSON(Ticket ticket)
    {
        Gson gson = new Gson();
        return gson.toJson(ticket, Ticket.class);
    }

    /**
     * Deletes a specific ticket instance with Id = TicketId
     *
     * @param TicketId
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void deleteTicket(int TicketId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String deleteQuery = "DELETE FROM tickets WHERE ticket_id = '" + TicketId + "'";
        stmt.executeUpdate(deleteQuery);
        stmt.close();
        con.close();
    }

    /**
     * When called, this function returns an ArrayList, each element of which,
     * is a Ticket instance that exists in the tickets table in the database
     *
     * @return {ArrayList<Ticket>} (An ArrayList of all the Tickets)
     * @throws Exception
     */
    public ArrayList<Ticket> databaseToTickets() throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM tickets");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Ticket ticket = gson.fromJson(json, Ticket.class);
                tickets.add(ticket);
            }
            return tickets;
        }
        catch (Exception e)
        {
            Logger logger = Logger.getLogger(EditTicketsTable.class.getName());
            logger.log(Level.SEVERE, "Exception occurred while fetching tickets", e);
        }
        return null;
    }

    /**
     * Function that finds and returns a Ticket that it found from the database
     * with a specific Ticket id
     *
     * @param TicketId
     * @return {Ticket} (The Customer instance that it found from the database)
     * @throws Exception
     */
    public Ticket databaseToTicket(int TicketId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM tickets WHERE ticket_id = '" + TicketId + "'");
            if (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                return gson.fromJson(json, Ticket.class);
            }
            else
                return null; // or throw an exception if no ticket is found
        }
        catch (Exception e)
        {
            Logger logger = Logger.getLogger(EditTicketsTable.class.getName());
            logger.log(Level.SEVERE, "Exception occurred while fetching ticket", e);
        }
        return null;
    }

    /**
     * Function that returns all the tickets of a specific Event
     *
     * @param EventId
     * @return {ArrayList<Ticket>} (A List of all the tickets of a specific Event)
     * @throws Exception
     */
    public static ArrayList<Ticket> databaseToEventTickets(int EventId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM tickets WHERE event_id= '" + EventId + "'");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Ticket ticket = gson.fromJson(json, Ticket.class);
                tickets.add(ticket);
            }
            return tickets;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
        }
        return null;
    }

    /**
     * Function that returns all the tickets of a specific Booking
     *
     * @param BookingId
     * @return {ArrayList<Ticket>} (A List of all the tickets of a specific Booking)
     * @throws Exception
     */
    public static ArrayList<Ticket> databaseToBookingTickets(int BookingId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM tickets WHERE booking_id= '" + BookingId + "'");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Ticket ticket = gson.fromJson(json, Ticket.class);
                tickets.add(ticket);
            }
            return tickets;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
        }
        return null;
    }

    /**
     * Establish a database connection and add in the database.
     *
     * @throws ClassNotFoundException
     */
    public static void addNewTicket(Ticket ticket) throws ClassNotFoundException
    {
        try
        {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO "
                    + " tickets (booking_id, event_id, seat_type,ticket_price,ticket_availability,ticket_checked) "
                    + " VALUES ("
                    + "'" + ticket.getBooking_id() + "',"
                    + "'" + ticket.getEvent_id() + "',"
                    + "'" + ticket.getSeat_type() + "',"
                    + "'" + ticket.getTicket_price() + "',"
                    + "'" + ticket.getTicket_availability() + "',"
                    + "'" + ticket.getTicket_checked() + "'"
                    + ")";
            //stmt.execute(table);
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The ticket was successfully added in the database.");

            /* Get the member id from the database and set it to the member */
            stmt.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(EditTicketsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Updates the availability of the ticket
     * (True) if available, (False) if not available
     * @param TicketId
     * @param availability
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void updateTicketAvailability(int TicketId, String availability) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update="UPDATE tickets SET ticket_availability ='"+availability+"' WHERE ticket_id = '"+ TicketId +"'";
        stmt.executeUpdate(update);
        stmt.close();
        con.close();
    }

    /**
     * Updates the booking_id of the ticket
     *
     * @param TicketId
     * @param BookingID
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void updateTicketBookingID(int TicketId, int BookingID) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update="UPDATE tickets SET booking_id ='"+BookingID+"' WHERE ticket_id = '"+ TicketId +"'";
        System.out.println(update);
        stmt.executeUpdate(update);
        stmt.close();
        con.close();
    }

    /**
     * Function that returns all the tickets with a specific availability value
     *
     * @param availability
     * @return {ArrayList<Ticket>} (A List of all the available tickets)
     * @throws Exception
     */
    public static ArrayList<Ticket> databaseToAvailableTickets(String availability) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM tickets WHERE ticket_availability= '" + availability + "'");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Ticket ticket = gson.fromJson(json, Ticket.class);
                tickets.add(ticket);
            }
            return tickets;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
        }
        return null;
    }
}