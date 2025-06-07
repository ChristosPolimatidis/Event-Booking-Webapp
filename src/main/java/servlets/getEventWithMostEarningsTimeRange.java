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
 * This Questions expects the 2 dates
 * The date that the user wants the search to start from
 * The date that the user wants the search to end from
 */
public class getEventWithMostEarningsTimeRange extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(getEventWithMostEarningsTimeRange.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            String StartDate_String = request.getParameter("start_date");
            String EndDate_String = request.getParameter("end_date");

            // Validate the type and value parameters
            if (StartDate_String == null || EndDate_String == null)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"Missing or invalid parameters\", \"errorCode\": \"INVALID_PARAMETERS\"}");
                }
                return;
            }

            int eventID = getEventWithMostEarnings(StartDate_String, EndDate_String);

            if(eventID > 0)
            {
                Event event = EditEventsTable.databaseToEvent(eventID);

                if (event == null)
                {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    try (PrintWriter out = response.getWriter()) {
                        out.write("{\"error\": \"Event not found\", \"errorCode\": \"EVENT_NOT_FOUND\"}");
                    }
                    return;
                }

                String mf_eventName = event.getEvent_name();

                // JSON Response
                String jsonResponse = String.format("{\"event_id\": %d, \"event_name\": \"%s\"}", eventID, mf_eventName);
                response.getWriter().write(jsonResponse);
            }
            else
            {
                String jsonResponse = String.format("{\"event_id\": %d, \"event_name\": \"No event found for the giver time period\"}", eventID);
                response.getWriter().write(jsonResponse);
            }
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            LOGGER.log(Level.SEVERE, "Database error: ", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter())
            {
                out.write("{\"error\": \"Internal server error\", \"errorCode\": \"DB_ERROR\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        LOGGER.log(Level.SEVERE, "Post method is not supported on Servlet getEventWithMostEarningsTimeRange");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public int getEventWithMostEarnings(String EventStartDate_String, String EventEndDate_String) throws SQLException, ClassNotFoundException
    {
        String query =  "SELECT event_id " +
                "FROM (" +
                "SELECT b.event_id, SUM(b.total_payment) AS total_money " +
                "FROM bookings b " +
                "WHERE b.event_id IN (" +
                "SELECT event_id " +
                "FROM events " +
                "WHERE booking_date BETWEEN ? AND ?" +
                ") " +
                "GROUP BY b.event_id " +
                "ORDER BY total_money DESC " +
                "LIMIT 1" +
                ") AS subquery";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query))
        {
            pst.setString(1, EventStartDate_String);
            pst.setString(2, EventEndDate_String);

            try (ResultSet rs = pst.executeQuery())
            {
                if (rs.next())
                    return rs.getInt("event_id");
                return 0; // In case no event is found
            }
        }
    }
}