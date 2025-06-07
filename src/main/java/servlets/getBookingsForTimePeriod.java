package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.DB_Connection.getConnection;

/**
 * @Author TEAM 4
 */
public class getBookingsForTimePeriod extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(getBookingsForTimePeriod.class.getName());

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

            // Take the booking-ids that are between start-date and end-date
            int [] bookings = getBookingsForSpecTime(StartDate_String, EndDate_String); // Implement this method in EditEventsTable

            // Create a JSON response
            StringBuilder jsonResponse = new StringBuilder("{");

            for (int i = 0; i < bookings.length; i++)
            {
                // Format the JSON string for each booking
                if (i + 1 < bookings.length)
                    jsonResponse.append(String.format("\"no. %d booking_id\": %d,", i + 1, bookings[i]));
                else
                    jsonResponse.append(String.format("\"no. %d booking_id\": %d", i + 1, bookings[i]));
            }

            jsonResponse.append("}");

            // Write the JSON response to the output
            response.getWriter().write(jsonResponse.toString());
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
     * Fetches the booking IDs for a given time period.
     *
     * @param EventStartDate_String the starting date
     * @param EventEndDate_String the ending date
     * @return An array of booking IDs
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int[] getBookingsForSpecTime(String EventStartDate_String, String EventEndDate_String) throws SQLException, ClassNotFoundException
    {
        List<Integer> dynamicList = new ArrayList<>();
        String query = "SELECT booking_id " +
                "FROM bookings " +
                "WHERE booking_date BETWEEN ? AND ?";  // Using prepared statement for date range

        try (Connection conn = getConnection(); // Assuming a method to get DB connection
             PreparedStatement pst = conn.prepareStatement(query))
        {
            pst.setString(1, EventStartDate_String);  // Set the start date
            pst.setString(2, EventEndDate_String);    // Set the end date

            try (ResultSet rs = pst.executeQuery())
            {
                // Iterate over the result set and add booking IDs to the list
                while (rs.next())
                {
                    dynamicList.add(rs.getInt("booking_id"));
                }

                // Convert list to an array
                int[] resultArray = new int[dynamicList.size()];
                for (int i = 0; i < dynamicList.size(); i++) {
                    resultArray[i] = dynamicList.get(i);
                }

                return resultArray;
            }
        }
    }
}