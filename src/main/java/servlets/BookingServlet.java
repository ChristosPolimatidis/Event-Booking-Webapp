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

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.json.JSONObject;

public class BookingServlet extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(BookingServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try
        {
            EditBookingsTable bookingsTable = new EditBookingsTable();
            ArrayList<Booking> bookings = bookingsTable.databaseToBookings();

            Gson gson = new Gson();
            String json = gson.toJson(bookings);
            System.out.println(json);
            response.getWriter().write(json);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Error fetching events\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        /* Set response content type */
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /* Read JSON from request body */
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader())
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                jsonBuilder.append(line);
            }
        }
        String jsonInput = jsonBuilder.toString();

        System.out.println("Received JSON: " + jsonInput);

        try
        {
            // Parse the original JSON
            JSONObject originalJson = new JSONObject(jsonInput);

            // Create a copy of the JSON to modify
            JSONObject modifiedJson = new JSONObject(originalJson.toString());

            // Remove the unwanted key
            modifiedJson.remove("vip_seats");
            modifiedJson.remove("general_seats");
            modifiedJson.remove("student_seats");
            modifiedJson.remove("child_seats");

            System.out.println(modifiedJson.toString());
            // Add booking logic (if required)
            EditBookingsTable.addBookingFromJSON(modifiedJson.toString());

            ArrayList<Booking> bookings = EditBookingsTable.databaseToCustomerBookings(modifiedJson.getInt("customer_id"));

            assert bookings != null;
            Booking lastBooking = bookings.get(bookings.size() - 1);

            // Parse JSON input
            Gson gson = new Gson();
            Map<String, Object> ticketDetails = gson.fromJson(jsonInput, Map.class);

            // Extract parameters from JSON
            float vipSeats = Float.parseFloat(ticketDetails.get("vip_seats").toString());
            float generalSeats = Float.parseFloat(ticketDetails.get("general_seats").toString());
            float studentSeats = Float.parseFloat(ticketDetails.get("student_seats").toString());
            float childSeats = Float.parseFloat(ticketDetails.get("child_seats").toString());

            // Log extracted parameters
            System.out.println("VIP Seats: " + vipSeats);
            System.out.println("General Seats: " + generalSeats);
            System.out.println("Student Seats: " + studentSeats);
            System.out.println("Child Seats: " + childSeats);

            // Example logic using the seat parameters
            ArrayList<Ticket> availableTickets = EditTicketsTable.databaseToAvailableTickets("1");

            if (availableTickets == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No tickets found for the provided booking_id\", \"errorCode\": \"NO_TICKETS_FOUND\"}");
                }
                return;
            }

            availableTickets.removeIf(ticket -> ticket.getEvent_id() != lastBooking.getEvent_id());

            for (Ticket ticket : availableTickets)
            {
                if(ticket.getSeat_type().equals("VIP") && vipSeats > 0)
                {
                    ticket.setBooking_id(lastBooking.getBooking_id());
                    ticket.setTicket_availability("0");
                    vipSeats--;
                }
                if(ticket.getSeat_type().equals("GENERAL_ADMISSION") && generalSeats > 0)
                {
                    ticket.setBooking_id(lastBooking.getBooking_id());
                    ticket.setTicket_availability("0");
                    generalSeats--;
                }
                if(ticket.getSeat_type().equals("STUDENT") && studentSeats > 0)
                {
                    ticket.setBooking_id(lastBooking.getBooking_id());
                    ticket.setTicket_availability("0");
                    studentSeats--;
                }
                if(ticket.getSeat_type().equals("CHILD") && childSeats > 0)
                {
                    ticket.setBooking_id(lastBooking.getBooking_id());
                    ticket.setTicket_availability("0");
                    childSeats--;
                }
            }
            Event bookingEvent = EditEventsTable.databaseToEvent(lastBooking.getEvent_id());

            if (bookingEvent == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No event found for the provided event_id\", \"errorCode\": \"NO_EVENT_FOUND\"}");
                }
                return;
            }

            int newCapacity = bookingEvent.getCapacity() - availableTickets.size();
            EditEventsTable.updateEventCapacity(bookingEvent.getEvent_id(), newCapacity);

            for(Ticket ticket : availableTickets)
            {
                EditTicketsTable.updateTicketAvailability(ticket.getTicket_id(), "0");
                EditTicketsTable.updateTicketBookingID(ticket.getTicket_id(), lastBooking.getBooking_id());
            }
        }
        catch (SQLException | ClassNotFoundException | NullPointerException e)
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Failed to process the booking!\"}");
            return;
        }

        /* Send success response back to frontend */
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Booking added successfully!\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");

        try
        {
            String BookingID_String = request.getParameter("booking_id");

            // Validate the type and value parameters
            if (BookingID_String == null)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                try (PrintWriter out = response.getWriter())
                {
                    out.write("{\"error\": \"Missing or invalid parameters\", \"errorCode\": \"INVALID_PARAMETERS\"}");
                }
                return;
            }

            int BookingID = Integer.parseInt(BookingID_String);

            Booking bookingToDelete = EditBookingsTable.databaseToBooking(BookingID);

            // Check if booking exist for the given booking id
            if (bookingToDelete == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No booking found for the provided booking\", \"errorCode\": \"NO_BOOKING_FOUND\"}");
                }
                return;
            }

            Event event = EditEventsTable.databaseToEvent(bookingToDelete.getEvent_id());

            // Check if booking exist for the given booking id
            if (event == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No event found for the provided booking_id\", \"errorCode\": \"NO_EVENT_FOUND\"}");
                }
                return;
            }

            ArrayList<Ticket> bookingTickets = EditTicketsTable.databaseToBookingTickets(BookingID);

            if(bookingTickets == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No tickets found for the provided booking_id\", \"errorCode\": \"NO_TICKETS_FOUND\"}");
                }
                return;
            }

            for (Ticket ticket : bookingTickets)
                EditTicketsTable.updateTicketAvailability(ticket.getTicket_id(), "1");

            int ticketsDeleted = Integer.parseInt(bookingToDelete.getTicket_count());
            int currentCapacity = event.getCapacity();
            int newCapacity = currentCapacity + ticketsDeleted;

            EditEventsTable.updateEventCapacity(event.getEvent_id(), newCapacity);

            // Get the event date (assuming it's stored as a string in the format "yyyy-MM-dd")
            String eventDateString = event.getEvent_date(); // Event date from the database
            LocalDate eventDate = LocalDate.parse(eventDateString); // Convert it to LocalDate

            // Get the current date
            LocalDate currentDate = LocalDate.now(); // Get today's date

            // Calculate the difference between the event date and the current date
            long daysUntilEvent = ChronoUnit.DAYS.between(currentDate, eventDate);

            if (daysUntilEvent > 1) // Full Refund
            {
                Customer customer = EditCustomersTable.databaseToCustomerID(bookingToDelete.getCustomer_id());

                if (customer == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    try (PrintWriter out = response.getWriter()) {
                        out.write("{\"error\": \"No customer found for the provided customer_id\", \"errorCode\": \"NO_CUSTOMER_FOUND\"}");
                    }
                    return;
                }

                int OldMoneyRefunded = Integer.parseInt(customer.getMoney_refunded());
                float bookingTotalPayment = Float.parseFloat(bookingToDelete.getTotal_payment());
                int totalMoneyRefunded = OldMoneyRefunded + (int)bookingTotalPayment;
                String NewMoneyRefunded = String.valueOf(totalMoneyRefunded);

                EditCustomersTable.updateCustomer(customer.getCustomer_id(), "money_refunded", NewMoneyRefunded);
            }
            else if (daysUntilEvent == 1) // 50% money refund
            {
                Customer customer = EditCustomersTable.databaseToCustomerID(bookingToDelete.getCustomer_id());

                if (customer == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    try (PrintWriter out = response.getWriter()) {
                        out.write("{\"error\": \"No customer found for the provided customer_id\", \"errorCode\": \"NO_CUSTOMER_FOUND\"}");
                    }
                    return;
                }

                int OldMoneyRefunded = Integer.parseInt(customer.getMoney_refunded());
                float bookingTotalPayment = Float.parseFloat(bookingToDelete.getTotal_payment()) / 2;
                int totalMoneyRefunded = OldMoneyRefunded + (int)bookingTotalPayment;
                String NewMoneyRefunded = String.valueOf(totalMoneyRefunded);

                EditCustomersTable.updateCustomer(customer.getCustomer_id(), "money_refunded", NewMoneyRefunded);
            }

            EditBookingsTable.deleteBooking(bookingToDelete.getBooking_id());
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