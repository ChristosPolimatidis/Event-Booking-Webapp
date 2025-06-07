package database.tables;

import com.google.gson.Gson;
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
 * @Author TEAM 4
 */
public class EditEventsTable
{
    public void addEventFromJSON(String json) throws ClassNotFoundException
    {
         Event event = jsonToEvent(json);
         addNewEvent(event);
    }
    
    public Event jsonToEvent(String json)
    {
         Gson gson = new Gson();
         return gson.fromJson(json, Event.class);
    }
    
    public String eventToJSON(Event event)
    {
         Gson gson = new Gson();
        return gson.toJson(event, Event.class);
    }

    public void updateEvent(int EventId, String key, String value) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update="UPDATE events SET "+key+"='"+value+"' WHERE event_id = '"+ EventId +"'";
        stmt.executeUpdate(update);
        stmt.close();
        con.close();
    }

    public static void updateEventCapacity(int EventId, int Capacity) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update="UPDATE events SET capacity ='"+Capacity+"' WHERE event_id = '"+ EventId +"'";
        stmt.executeUpdate(update);
        stmt.close();
        con.close();
    }

    /**
     * Deletes a specific event instance with ID = EventId
     *
     * @param EventId
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void deleteEvent(int EventId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String deleteQuery = "DELETE FROM events WHERE event_id = '" + EventId + "'";
        stmt.executeUpdate(deleteQuery);
        stmt.close();
        con.close();
    }
    
    public static Event databaseToEvent(int EventId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM events WHERE event_id = '" + EventId + "'");
            rs.next();
            String json = DB_Connection.getResultsToJSON(rs);
            Gson gson = new Gson();
            return gson.fromJson(json, Event.class);
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
     * is an Event instance that exists in the events table in the database
     *
     * @return {ArrayList<Event>} (An ArrayList of all the events)
     * @throws Exception
     */
    public String databaseEventToJSON(int EventId) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM events WHERE event_id = '" + EventId + "'");
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
     * is an Event instance that exists in the events table in the database
     *
     * @return {ArrayList<Event>} (An ArrayList of all the events)
     * @throws Exception
     */
    public ArrayList<Event> databaseToEvents() throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Event> events = new ArrayList<Event>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM events");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Event event = gson.fromJson(json, Event.class);
                events.add(event);
            }
            return events;
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
     * is an Event instance that exists in the events table in the database
     * and has a type of "String Type"
     *
     * @return {ArrayList<Event>} (An ArrayList of all the events with a specific type)
     * @throws Exception
     */
    public ArrayList<Event> databaseToEventsType(String Type) throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Event> events = new ArrayList<Event>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM events WHERE type_event = '" + Type + "'");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Event event = gson.fromJson(json, Event.class);
                events.add(event);
            }
            return events;
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
     * is an Event instance that exists in the events table in the database
     * and has capacity > 0
     *
     * @return {ArrayList<Event>} (An ArrayList of all the events with capacity > 0)
     * @throws Exception
     */
    public static ArrayList<Event> databaseToAvailableEvents() throws SQLException, ClassNotFoundException
    {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Event> events = new ArrayList<Event>();
        ResultSet rs;
        try
        {
            rs = stmt.executeQuery("SELECT * FROM events WHERE capacity > 0");
            while (rs.next())
            {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Event event = gson.fromJson(json, Event.class);
                events.add(event);
            }
            return events;
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Establish a database connection and add in the database.
     *
     * @throws ClassNotFoundException
     */
    public void addNewEvent(Event event) throws ClassNotFoundException
    {
        try
        {
            Connection con = DB_Connection.getConnection();
            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO "
                    + " events (type_event, event_name, event_date, start_time, capacity) "
                    + " VALUES ("
                    + "'" + event.getType_event() + "',"
                    + "'" + event.getEvent_name() + "',"
                    + "'" + event.getEvent_date() + "',"
                    + "'" + event.getStart_time() + "',"
                    + "'" + event.getCapacity() + "'"
                    + ")";
            //stmt.execute(table);
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The event was successfully added in the database.");

            /* Get the member id from the database and set it to the member */
            stmt.close();

        }
        catch (SQLException ex)
        {
            Logger.getLogger(EditEventsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}