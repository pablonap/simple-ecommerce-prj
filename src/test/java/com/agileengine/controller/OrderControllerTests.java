package com.agileengine.controller;

import com.agileengine.TestsUtils;
import com.agileengine.dto.*;
import com.agileengine.model.State;
import com.agileengine.service.OrderService;
import com.agileengine.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTests {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController underTest;

    @Test
    public void itShouldCreateOrderWithStatusOk() {
        // given
        final String SHIPPING_ADDRESS = "x street";
        final long ORDER_ID = 1;

        final var REQUEST = TestsUtils.orderCreateDtoOf(SHIPPING_ADDRESS);
        OrderIdDto orderIdDto = TestsUtils.orderIdDtoOf(ORDER_ID);

        when(orderService.create(any(OrderCreateDto.class))).thenReturn(orderIdDto);

        // when
        ResponseEntity<OrderIdDto> response = underTest.create(REQUEST);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ORDER_ID, Objects.requireNonNull(response.getBody()).orderId());
    }

    @Test
    public void itShouldUpdateOrderWithStatusOk() {
        // given
        final String SHIPPING_ADDRESS = "x street";
        final State STATE = State.ON_PROCESS;
        final long ORDER_ID = 1;

        final var REQUEST = TestsUtils.orderUpdateDtoOf(SHIPPING_ADDRESS, STATE);
        OrderIdDto orderIdDto = TestsUtils.orderIdDtoOf(ORDER_ID);

        when(orderService.update(anyLong(), any(OrderUpdateDto.class))).thenReturn(orderIdDto);

        // when
        ResponseEntity<OrderIdDto> response = underTest.update(ORDER_ID, REQUEST);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void itShouldFindAllOrders() {
        // given
        int page = 0;
        int size = 30;
        PageRequest pageRequest = PageRequest.of(page, size);

        final long ORDER_ID = 1;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        String SHIPPING_ADDRESS = "x street";
        BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        Set<Long> IDS_ORDER_ITEMS = Set.of();
        State STATE = State.ON_PROCESS;

        List<OrderDto> orderDtos = List.of(
            TestsUtils.orderDtoOf(ORDER_ID,
                DATE_TIME,
                SHIPPING_ADDRESS,
                TOTAL_AMOUNT,
                IDS_ORDER_ITEMS,
                STATE));

        Page<OrderDto> orderDtoPage =
            new PageImpl<>(orderDtos, pageRequest, orderDtos.size());

        when(orderService.getOrders(pageRequest)).thenReturn(orderDtoPage);

        // when
        ResponseEntity<Page<OrderDto>> response = underTest.findAll(page, size);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDtoPage, response.getBody());
    }

    @Test
    public void itShouldFindOrderById() {
        // given
        final long ORDER_ID = 1;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        String SHIPPING_ADDRESS = "x street";
        BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        Set<Long> IDS_ORDER_ITEMS = Set.of();
        State STATE = State.ON_PROCESS;

        OrderDto orderDto = TestsUtils.orderDtoOf(ORDER_ID,
            DATE_TIME,
            SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            IDS_ORDER_ITEMS,
            STATE);

        when(orderService.getById(anyLong())).thenReturn(orderDto);

        // when
        ResponseEntity<OrderDto> response = underTest.findById(ORDER_ID);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDto, response.getBody());
    }

    @Test
    public void itShouldRemoveOrderById() {
        // given
        long ORDER_ID = 1L;

        // when
        ResponseEntity<Void> response = underTest.remove(ORDER_ID);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService).remove(ORDER_ID);
    }
}
