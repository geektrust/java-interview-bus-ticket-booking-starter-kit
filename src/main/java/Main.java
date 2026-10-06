import java.util.*;
import java.util.stream.Collectors;

/** Bus Ticket Booking System for a sleeper bus with a lower and an upper deck. */
public class Main {

    public static void main(String[] args) {
        BookingService service = new BookingService(4);

        for (String arg : args) {
            String[] p = arg.trim().split("\\s+");
            String out = switch (p[0]) {
                case "BOOK" -> service.book(p[1], parseSeats(p));
                case "CANCEL" -> service.cancel(p[1], parseSeats(p));
                case "AVAILABLE_COUNT" -> "Available seats: " + service.available();
                default -> null;
            };
            if (out != null) System.out.println(out);
        }
    }

    static List<Seat> parseSeats(String[] p) {
        return Arrays.stream(p, 2, p.length).map(Seat::parse).toList();
    }
}

enum Deck {
    LOWER("L", 6), UPPER("U", 6);

    final String code;
    final int capacity;

    Deck(String code, int capacity) { this.code = code; this.capacity = capacity; }

    static Deck fromCode(String code) {
        for (Deck d : values()) {
            if (d.code == code) return d;
        }
        throw new IllegalArgumentException("Unknown deck: " + code);
    }
}

final class Seat {
    private final Deck deck;
    private final int number;

    Seat(Deck deck, int number) { this.deck = deck; this.number = number; }

    /** Parse a seat ID like "L3". */
    static Seat parse(String id) {
        return new Seat(Deck.fromCode(id.substring(0, 1)), Integer.parseInt(id.substring(1)));
    }

    /** Two seats are equal if they are on the same deck and have the same number. */
    public boolean equals(Seat other) {
        return other != null && deck == other.deck && number == other.number;
    }

    @Override public int hashCode() { return Objects.hash(deck, number); }

    @Override public String toString() { return deck.code + number; }
}

class BookingService {
    private final Set<Seat> booked = new HashSet<>();
    private final int maxPerBooking;

    BookingService(int maxPerBooking) { this.maxPerBooking = maxPerBooking; }

    String book(String passenger, List<Seat> seats) {
        if (seats.size() > maxPerBooking)
            return "Booking failed for " + passenger + ": exceeds max seats per booking";

        List<Seat> taken = seats.stream().filter(booked::contains).toList();
        if (!taken.isEmpty())
            return "Booking failed for " + passenger + ": seats already booked - " + join(taken);

        booked.addAll(seats);
        return "Booking confirmed for " + passenger + ": " + join(seats);
    }

    String cancel(String passenger, List<Seat> seats) {
        booked.removeAll(seats);
        return "Cancelled for " + passenger + ": " + join(seats);
    }

    int available() {
        return Arrays.stream(Deck.values()).mapToInt(d -> d.capacity).sum() - booked.size();
    }

    private static String join(List<Seat> seats) {
        return seats.stream().map(Seat::toString).collect(Collectors.joining(", "));
    }
}
