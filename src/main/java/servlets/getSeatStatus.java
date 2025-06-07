package servlets;

import database.tables.EditEventsTable;
import mainClasses.Event;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.DB_Connection.getConnection;

/**
 * @Author TEAM 4
 */
public class getSeatStatus extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(getSeatStatus.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            String EventID_String = request.getParameter("event_id");

            // Validate the type and value parameters
            if (EventID_String == null)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"Missing or invalid parameters\", \"errorCode\": \"INVALID_PARAMETERS\"}");
                }
                return;
            }

            int EventID = Integer.parseInt(EventID_String);

            Event event = EditEventsTable.databaseToEvent(EventID);

            // Check if the event got created correctly
            if (event == null)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"Missing or invalid parameters\", \"errorCode\": \"INVALID_PARAMETERS\"}");
                }
                return;
            }

            // Fetch the number of tickets sold for this event
            int[] av_unav_seats = getEventSeatStatus(EventID); // Implement this method in EditEventsTable

            // Create a JSON response
            String jsonResponse = String.format("{\"event_id\": %d, \"capacity\": %d, \"not_empty_seats\": %d}",
                    event.getEvent_id(),
                    av_unav_seats[0],
                    av_unav_seats[1]);
            response.getWriter().write(jsonResponse);
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            LOGGER.log(Level.SEVERE, "Database error: ", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            try (PrintWriter out = response.getWriter())
            {
                out.write("{\"error\": \"Internal server error\", \"errorCode\": \"DB_ERROR\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        LOGGER.log(Level.SEVERE, "Post method is not supported on Servlet getSeatStatus");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Fetches the capacity and the number of reserved seats for a given event.
     *
     * @param eventID The ID of the event.
     * @return An int array where:
     *         - [0] = capacity of the event
     *         - [1] = number of not available (reserved) seats
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int[] getEventSeatStatus(int eventID) throws SQLException, ClassNotFoundException
    {
        String query = "SELECT e.event_id, e.capacity, ( " +
                "         SELECT COUNT(t.ticket_id) " +
                "         FROM tickets t " +
                "         WHERE t.event_id = e.event_id AND t.ticket_availability = '0') AS not_empty_seats " +
                "FROM events e";

        try (Connection conn = getConnection(); // Assuming a method to get DB connection
             PreparedStatement pst = conn.prepareStatement(query))
        {
            try (ResultSet rs = pst.executeQuery())
            {
                // Iterate over the result set and filter for the eventID
                while (rs.next())
                {
                    int eventIDFromDB = rs.getInt("event_id"); // Get event_id from the result
                    if (eventIDFromDB == eventID)   // Check if the event matches the requested ID

                    {
                        int capacity = rs.getInt("capacity"); // Get event capacity
                        int notEmptySeats = rs.getInt("not_empty_seats"); // Get reserved seats count
                        return new int[] { capacity, notEmptySeats }; // Return capacity and reserved seats
                    }
                }
            }
        }
        return new int[] { 0, 0 }; // Return default values if no result is found
    }


}