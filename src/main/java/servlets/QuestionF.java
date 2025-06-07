package servlets;

import database.DB_Connection;

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

public class QuestionF extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(QuestionF.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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

        int eventID = Integer.parseInt(EventID_String);

        String seatType_String = request.getParameter("seat_type");

        // Validate the type and value parameters
        if (seatType_String == null)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            try (PrintWriter out = response.getWriter()) {
                out.write("{\"error\": \"Missing or invalid parameters\", \"errorCode\": \"INVALID_PARAMETERS\"}");
            }
            return;
        }

        int earnings = 0;

        if(eventID <= 0)
        {
            try
            {
                earnings = getTotalEarningsNoEvent(seatType_String);
            }
            catch (SQLException | ClassNotFoundException e)
            {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\": \"Error fetching earnings\"}");
            }
        }
        else
        {
            try
            {
                earnings = getTotalEarningsEvent(eventID, seatType_String);
            }
            catch (SQLException | ClassNotFoundException e)
            {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\": \"Error fetching earnings\"}");
            }

        }

        // Assume mf_eventID and mf_eventName are variables holding the event ID and name
        String jsonResponse = String.format("{\"event_id\": %d, \"earnings\": \"%s\"}", eventID, earnings);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        LOGGER.log(Level.SEVERE, "Post method is not supported on Servlet QuestionF");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Fetches the earnings for a given seat-type and a given event.
     *
     * @param eventID The ID of the event.
     * @param
     * @return An int that represents the total earnings from that event
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int getTotalEarningsEvent(int eventID, String seatType) throws SQLException, ClassNotFoundException {
        int earnings = 0;

        String query = "SELECT SUM(ticket_price) AS total_earnings " +
                "FROM tickets " +
                "WHERE seat_type = ? AND event_id = ? AND ticket_availability=0";

        try (Connection conn = DB_Connection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, seatType);
            pst.setInt(2, eventID);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    earnings = rs.getInt("total_earnings");
                }
            }
        }
        return earnings;
    }


    public int getTotalEarningsNoEvent(String seatType) throws SQLException, ClassNotFoundException {
        int earnings = 0;

        String query = "SELECT SUM(ticket_price) AS total_earnings " +
                "FROM tickets " +
                "WHERE seat_type = ? AND ticket_availability=0";

        try (Connection conn = DB_Connection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, seatType);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    earnings = rs.getInt("total_earnings");
                }
            }
        }
        return earnings;
    }

}