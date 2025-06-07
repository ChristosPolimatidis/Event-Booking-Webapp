package servlets;

import com.google.gson.Gson;
import database.tables.EditBookingsTable;
import database.tables.EditCustomersTable;
import database.tables.EditEventsTable;
import database.tables.EditTicketsTable;
import mainClasses.Booking;
import mainClasses.Customer;
import mainClasses.Event;
import mainClasses.Ticket;

import java.io.BufferedReader;
import java.time.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EventServlet.class.getName());
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            EditEventsTable eventsTable = new EditEventsTable();
            ArrayList<Event> events = eventsTable.databaseToEvents();

            Gson gson = new Gson();
            String json = gson.toJson(events);
            System.out.println(json);
            response.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Error fetching events\"}");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* Set response content type */
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /* Read JSON from request body */
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        String jsonInput = jsonBuilder.toString();

        System.out.println(jsonInput);

        /* Process the JSON data */
        EditEventsTable eut = new EditEventsTable();
        try {
            eut.addEventFromJSON(jsonInput);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Failed to init the event!\"}");
            return;
        }

        /* Send success response back to frontend */
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Event added successfully!\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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

            Event eventToDelete = EditEventsTable.databaseToEvent(EventID);

            // Check if event exist for the given event id
            if (eventToDelete == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter())
                {
                    out.write("{\"error\": \"No event found for the provided event_id\", \"errorCode\": \"NO_EVENT_FOUND\"}");
                }
                return;
            }

            ArrayList<Ticket> eventTickets = EditTicketsTable.databaseToEventTickets(EventID);

            if (eventTickets == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No tickets found for the provided event_id\", \"errorCode\": \"NO_TICKETS_FOUND\"}");
                }
                return;
            }

            for (Ticket ticket : eventTickets)
                EditTicketsTable.deleteTicket(ticket.getTicket_id());

            ArrayList<Booking> eventBookings = EditBookingsTable.databaseToEventBookings(EventID);

            if (eventBookings == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No bookings found for the provided event_id\", \"errorCode\": \"NO_BOOKINGS_FOUND\"}");
                }
                return;
            }

            // Get the event date (assuming it's stored as a string in the format "yyyy-MM-dd")
            String eventDateString = eventToDelete.getEvent_date(); // Event date from the database
            LocalDate eventDate = LocalDate.parse(eventDateString); // Convert it to LocalDate

            for (Booking booking : eventBookings)
            {
                // Get the current date
                LocalDate currentDate; // Get today's date
                currentDate = LocalDate.now();

                if(!currentDate.isAfter(eventDate))
                {
                    Customer customer = EditCustomersTable.databaseToCustomerID(booking.getCustomer_id());

                    if (customer == null)
                    {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        try (PrintWriter out = response.getWriter()) {
                            out.write("{\"error\": \"No customer found for the provided CustomerId\", \"errorCode\": \"NO_CUSTOMER_FOUND\"}");
                        }
                        return;
                    }

                    int OldMoneyRefunded = Integer.parseInt(customer.getMoney_refunded());
                    float bookingTotalPayment = Float.parseFloat(booking.getTotal_payment());
                    int totalMoneyRefunded = OldMoneyRefunded + (int)(bookingTotalPayment);
                    String NewMoneyRefunded = String.valueOf(totalMoneyRefunded);

                    EditCustomersTable.updateCustomer(customer.getCustomer_id(), "money_refunded", NewMoneyRefunded);
                }

                EditBookingsTable.deleteBooking(booking.getBooking_id());
            }

            EditEventsTable.deleteEvent(eventToDelete.getEvent_id());
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

}