package servlets;

import com.google.gson.JsonObject;
import database.tables.EditBookingsTable;
import database.tables.EditEventsTable;
import mainClasses.Booking;
import mainClasses.Event;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class getCustomerBookings extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(getCustomerBookings.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");

        try {
            String CustomerId_String = request.getParameter("customer_id");
            JsonObject responseJson = new JsonObject();

            // Validate the type and value parameters
            if (CustomerId_String == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                System.out.println("{\"error\": \"Missing or invalid parameters\", \"errorCode\": \"INVALID_PARAMETERS\"}");
                return;
            }

            int CustomerId = Integer.parseInt(CustomerId_String);

            ArrayList<Booking> bookings = EditBookingsTable.databaseToCustomerBookings(CustomerId);

            if (bookings == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No bookings found for the provided customer_id\", \"errorCode\": \"NO_BOOKINGS_FOUND\"}");
                }
                return;
            }

            // Start the JSON response string
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("[");

            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                Event event = EditEventsTable.databaseToEvent(booking.getEvent_id());

                if (event == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    try (PrintWriter out = response.getWriter()) {
                        out.write("{\"error\": \"No event found for the provided booking_id\", \"errorCode\": \"NO_EVENT_FOUND\"}");
                    }
                    return;
                }

                String EventName = event.getEvent_name();

                // Start a new JSON object for each booking
                jsonResponse.append("{");
                jsonResponse.append("\"booking_id\": ").append(booking.getBooking_id()).append(",");
                jsonResponse.append("\"event_name\": \"").append(EventName).append("\",");
                jsonResponse.append("\"ticket_count\": ").append(booking.getTicket_count()).append(",");
                jsonResponse.append("\"booking_date\": \"").append(booking.getBooking_date()).append("\",");
                jsonResponse.append("\"total_payment\": ").append(booking.getTotal_payment());

                // End the current booking's JSON object
                jsonResponse.append("}");

                // Add a comma unless it's the last booking
                if (i < bookings.size() - 1)
                    jsonResponse.append(",");
            }
            jsonResponse.append("]");

            try (PrintWriter out = response.getWriter()) {
                out.write(jsonResponse.toString());
            }
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
        LOGGER.log(Level.SEVERE, "Post method is not supported on Servlet getCustomerBookings");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}