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
public class getMostPopularEvent extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(getMostPopularEvent.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            // Fetch the number of tickets sold for this event
            int mf_eventID = getMostFamousEvent(); // Implement this method in EditEventsTable

            Event event = EditEventsTable.databaseToEvent(mf_eventID);

            // Check if the event got created correctly
            if (event == null)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"Missing or invalid parameters\", \"errorCode\": \"INVALID_PARAMETERS\"}");
                }
                return;
            }

            String mf_eventName = event.getEvent_name();

            // Assume mf_eventID and mf_eventName are variables holding the event ID and name
            String jsonResponse = String.format("{\"event_id\": %d, \"event_name\": \"%s\"}", mf_eventID, mf_eventName);
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
        LOGGER.log(Level.SEVERE, "Post method is not supported on Servlet getMostPopularEvent");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns event_id of the most popular event, based on bookings that have taken place.
     *
     * @param ---
     * @return The id of the most popular event, based on bookings that have taken place.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int getMostFamousEvent() throws SQLException, ClassNotFoundException {
        int mf_eventID = -1; // Default value in case no rows are found
        String query = "SELECT event_id " +
                "FROM (" +
                "    SELECT b.event_id, SUM(b.ticket_count) AS total_tickets " +
                "    FROM bookings b " +
                "    GROUP BY b.event_id " +
                "    ORDER BY total_tickets DESC " +
                "    LIMIT 1" +
                ") AS subquery";

        try (Connection conn = getConnection(); // Assuming a method to get DB connection
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) { // Check if there's a row in the result set
                mf_eventID = rs.getInt("event_id");
            } else {
                throw new SQLException("No rows returned from the query.");
            }
        }
        return mf_eventID;
    }
}