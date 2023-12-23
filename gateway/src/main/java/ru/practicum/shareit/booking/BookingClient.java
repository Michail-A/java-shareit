package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, int userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> setApprove(int userId, int bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved.toString(), userId);
    }

    public ResponseEntity<Object> getForOwnerOrBooker(int bookingId, int userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getForUser(int userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", (long) userId, parameters);
    }

    public ResponseEntity<Object> getBookingsForOwner(int userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", (long) userId, parameters);
    }
}