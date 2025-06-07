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
public class getEventEarnings extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(getEventEarnings.class.getName());

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
            int earnings = getEarningsForEvent(EventID); // Implement this method in EditEventsTable

            // Create JSON response
            String jsonResponse = String.format("{\"event_id\": %d, \"earnings\": %d}", EventID, earnings);
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
        LOGGER.log(Level.SEVERE, "Post method is not supported on Servlet getEventEarnings");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Fetches the earnings for a given event.
     *
     * @param eventID The ID of the event.
     * @return An int that represents the total earnings from that event
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int getEarningsForEvent(int eventID) throws SQLException, ClassNotFoundException
    {
        int earnings;

        String query = "SELECT e.event_id, COALESCE(SUM(b.total_payment), 0) AS total_payment " +
                "       FROM events e " +
                "       LEFT JOIN bookings b ON e.event_id = b.event_id " +
                "       GROUP BY e.event_id ";

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
                        earnings = rs.getInt("total_payment"); // Get event capacity
                        return earnings; // Returns earnings from that event
                    }
                }
            }
        }
        return 0; // Return default value if no result is found
    }
}