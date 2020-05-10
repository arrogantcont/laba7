package server.DB;

import commons.User;
import commons.model.*;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class DataBaseConnection {
    Connection conn;

    public DataBaseConnection() {

        // https://docs.oracle.com/javase/8/docs/api/java/sql/package-summary.html#package.description
        // auto java.sql.Driver discovery -- no longer need to load a java.sql.Driver class via Class.forName

        // register JDBC driver, optional, since java 1.6
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://pg:5432/studs", "s286562", "ovl881");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        // auto close connection
        try {
            if (conn != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to make connection!");
            }

////            ResultSet rs = stmt.executeQuery(" SELECT * FROM x");
//            while (rs.next()){
//                System.out.println(rs.getInt(2));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> loadAllUsers() throws SQLException {
        ArrayList<User> Users = new ArrayList<>();
        String sql = "select * from Users;";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            User user = new User(rs.getString(1), rs.getString(2));
            Users.add(user);
        }
        return Users;
    }

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (Username, Passhash) Values (?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        int rows = preparedStatement.executeUpdate();

        System.out.printf("%d user added", rows);
    }

    public void updateTicket(Ticket ticket) throws SQLException {
        long eventId = 0;
        if (ticket.getEvent() != null) {
            String sql = "INSERT INTO event (eventname, ticketsCount, EventType) Values (?, ?, ?) ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, ticket.getEvent().getName());
            preparedStatement.setInt(2, ticket.getEvent().getTicketsCount());
            preparedStatement.setString(3, ticket.getEvent().getEventType().toString());
            int rows = preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                eventId = rs.getInt(1);
                ticket.getEvent().setEventId(eventId);
            }
        }

        String sql = "UPDATE ticket SET (ticketName , x, y, creationdate, price, comment, refundable, ticketType, event, username ) = (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) where ticketId = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, ticket.getName());
        preparedStatement.setInt(2, ticket.getCoordinates().getX());
        preparedStatement.setInt(3, ticket.getCoordinates().getY());
        preparedStatement.setObject(4, ticket.getCreationDate().toInstant()
                .atZone(ZoneId.of("Africa/Tunis"))
                .toLocalDate());
        preparedStatement.setInt(5, ticket.getPrice());
        preparedStatement.setString(6, ticket.getComment());
        preparedStatement.setBoolean(7, ticket.isRefundable());
        if (ticket.getType() != null) preparedStatement.setString(8, ticket.getType().toString());
        else preparedStatement.setObject(8, null);
        if (eventId != 0) preparedStatement.setLong(9, eventId);
        else preparedStatement.setObject(9, null);
        preparedStatement.setString(10, ticket.getUser().getUsername());
        preparedStatement.setInt(11, ticket.getTicketId());
        int rows = preparedStatement.executeUpdate();
    }

    public void remove_by_if_from_DB(int id) throws SQLException {
        String sql = "Delete from ticket where ticketid = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, id);
        int rows = preparedStatement.executeUpdate();
    }

    public void clearDB(String username) throws SQLException {
        String sql = "Delete from ticket where username = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, username);
        int rows = preparedStatement.executeUpdate();
    }

    public void addTicket(Ticket ticket) throws SQLException {
        long eventId = 0;
        if (ticket.getEvent() != null) {
            String sql = "INSERT INTO event (eventname, ticketsCount, EventType) Values (?, ?, ?) ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, ticket.getEvent().getName());
            preparedStatement.setInt(2, ticket.getEvent().getTicketsCount());
            preparedStatement.setString(3, ticket.getEvent().getEventType().toString());
            int rows = preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                eventId = rs.getInt(1);
                ticket.getEvent().setEventId(eventId);
            }
        }
        String sql = "INSERT INTO ticket (ticketName , x, y, creationdate, price, comment, refundable, ticketType, event, username ) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, ticket.getName());
        preparedStatement.setInt(2, ticket.getCoordinates().getX());
        preparedStatement.setInt(3, ticket.getCoordinates().getY());
        preparedStatement.setObject(4, ticket.getCreationDate().toInstant()
                .atZone(ZoneId.of("Africa/Tunis"))
                .toLocalDate());
        preparedStatement.setInt(5, ticket.getPrice());
        preparedStatement.setString(6, ticket.getComment());
        preparedStatement.setBoolean(7, ticket.isRefundable());
        if (ticket.getType() != null) preparedStatement.setString(8, ticket.getType().toString());
        else preparedStatement.setObject(8, null);
        if (eventId != 0) preparedStatement.setLong(9, eventId);
        else preparedStatement.setObject(9, null);
        preparedStatement.setString(10, ticket.getUser().getUsername());
        int rows = preparedStatement.executeUpdate();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) {
            ticket.setTicketId(rs.getInt(1));
        }
    }

    public ArrayDeque<Ticket> loadAllTickets() throws SQLException {
        ArrayDeque<Ticket> tickets = new ArrayDeque<>();
        String sql = "select * from ticket;";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            String sq2 = "select * from Event where eventId = ?";
            PreparedStatement preparedStatementEvent = conn.prepareStatement(sq2);
            preparedStatementEvent.setLong(1, rs.getLong("event"));
            ResultSet rsEvent = preparedStatementEvent.executeQuery();
            Event event = null;
            if (rsEvent.next()) {
                event = new Event(rsEvent.getLong(1), rsEvent.getString(2), rsEvent.getInt(3), EventType.valueOf(rsEvent.getString(4)));
            }
            String sq3 = "select * from users where username = ?";
            PreparedStatement preparedStatementUser = conn.prepareStatement(sq3);
            preparedStatementUser.setString(1, rs.getString("username"));
            ResultSet rsUser = preparedStatementUser.executeQuery();
            User user = null;
            if (rsUser.next()) {
                user = new User(rsUser.getString(1), rsUser.getString(2));
            }
            if (rs.getString("tickettype") != null) {
                Ticket ticket = Ticket.builder()
                        .comment(rs.getString("comment"))
                        .coordinates(new Coordinates(rs.getInt("x"), rs.getInt("y")))
                        .creationDate(rs.getDate("creationdate"))
                        .event(event)
                        .ticketId(rs.getInt("ticketid"))
                        .name(rs.getString("ticketname"))
                        .price(rs.getInt("price"))
                        .refundable(rs.getBoolean("refundable"))
                        .type(TicketType.valueOf(rs.getString("tickettype")))
                        .user(user)
                        .build();
                tickets.add(ticket);
            } else {
                Ticket ticket = Ticket.builder()
                        .comment(rs.getString("comment"))
                        .coordinates(new Coordinates(rs.getInt("x"), rs.getInt("y")))
                        .creationDate(rs.getDate("creationdate"))
                        .event(event)
                        .ticketId(rs.getInt("ticketid"))
                        .name(rs.getString("ticketname"))
                        .price(rs.getInt("price"))
                        .refundable(rs.getBoolean("refundable"))
                        .type(null)
                        .user(user)
                        .build();
                tickets.add(ticket);
            }

        }
        return tickets;
    }
}
