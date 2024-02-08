package com.agileengine.service;

import com.agileengine.TestsUtils;
import com.agileengine.dto.OrderIdDto;
import com.agileengine.dto.OrderToLongMapper;
import com.agileengine.dto.OrderToOrderDtoMapper;
import com.agileengine.exception.InconsistentDataException;
import com.agileengine.exception.RequestValidationException;
import com.agileengine.exception.ResourceNotFoundException;
import com.agileengine.model.Order;
import com.agileengine.model.OrderItem;
import com.agileengine.model.Product;
import com.agileengine.model.State;
import com.agileengine.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderToLongMapper orderToLongMapper;

    @Mock
    private OrderToOrderDtoMapper orderToOrderDtoMapper;

    @InjectMocks
    private OrderService underTest;

    @Test
    void itShouldCreateOrder() {
        // given
        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String SHIPPING_ADDRESS = "street 123";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final Set<OrderItem> ORDER_ITEMS = Set.of();
        final State STATE = State.ON_PROCESS;

        final var orderCreateDto = TestsUtils.orderCreateDtoOf(SHIPPING_ADDRESS);
        final var savedOrder = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            STATE);
        final var orderIdDtoMapped = TestsUtils.orderIdDtoOf(ORDER_ID);

        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(savedOrder);
        when(orderToLongMapper.apply(savedOrder)).thenReturn(orderIdDtoMapped.orderId());

        // when
        OrderIdDto result = underTest.create(orderCreateDto);

        // then
        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order capturedOrder = orderArgumentCaptor.getValue();

        assertThat(result.orderId()).isEqualTo(ORDER_ID);

        assertNotNull(capturedOrder.getCreateAt());
        assertThat(capturedOrder.getShippingAddress()).isEqualTo(SHIPPING_ADDRESS);
        assertThat(capturedOrder.getState()).isEqualTo(STATE);
    }

    @Test
    void itShouldUpdateOrder() {
        // given
        final Long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1000);

        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String PREVIOUS_SHIPPING_ADDRESS = "123 street";
        final String NEW_SHIPPING_ADDRESS = "321 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State FINISHED_STATE = State.FINISHED;
        final State ON_PROCESS_STATE = State.ON_PROCESS;
        final BigDecimal CALCULATED_TOTAL_AMOUNT = BigDecimal.valueOf(3000);

        final var PRODUCT = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);
        final var ORDER_ITEM = TestsUtils.orderItemOf(PRODUCT, new Order(), 3);
        final Set<OrderItem> ORDER_ITEMS = Set.of(ORDER_ITEM);

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            PREVIOUS_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            ON_PROCESS_STATE);
        final var orderUpdateDto = TestsUtils.orderUpdateDtoOf(NEW_SHIPPING_ADDRESS, FINISHED_STATE);
        final var updatedOrder = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            NEW_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            FINISHED_STATE);
        final var orderIdDtoMapped = TestsUtils.orderIdDtoOf(ORDER_ID);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(updatedOrder);
        when(orderToLongMapper.apply(updatedOrder)).thenReturn(orderIdDtoMapped.orderId());

        // when
        OrderIdDto result = underTest.update(ORDER_ID, orderUpdateDto);

        // then
        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order capturedOrder = orderArgumentCaptor.getValue();

        assertThat(result.orderId()).isEqualTo(ORDER_ID);

        assertThat(capturedOrder.getId()).isEqualTo(ORDER_ID);
        assertNotNull(capturedOrder.getCreateAt());
        assertThat(capturedOrder.getShippingAddress()).isEqualTo(NEW_SHIPPING_ADDRESS);
        assertThat(capturedOrder.getState()).isEqualTo(FINISHED_STATE);
        assertThat(capturedOrder.getTotalAmount()).isEqualTo(CALCULATED_TOTAL_AMOUNT);
    }

    @Test
    void itShouldThrowExceptionWhenThereAreNoOrderItemsWhenTryingToUpdate() {
        // given
        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String PREVIOUS_SHIPPING_ADDRESS = "123 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State ON_PROCESS_STATE = State.ON_PROCESS;
        final State FINISHED_STATE = State.FINISHED;
        final String NEW_SHIPPING_ADDRESS = "321 street";

        final Set<OrderItem> ORDER_ITEMS = Set.of();
        final var orderUpdateDto = TestsUtils.orderUpdateDtoOf(NEW_SHIPPING_ADDRESS, FINISHED_STATE);

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            PREVIOUS_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            ON_PROCESS_STATE);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));

        // when & then
        assertThrows(InconsistentDataException.class, () -> underTest.update(ORDER_ID, orderUpdateDto));
        verify(orderRepository, never()).deleteById(ArgumentMatchers.any());
    }

    @Test
    void itShouldThrowExceptionWhenOrderStateIsFinishedWhenTryingToUpdate() {
        // given
        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String PREVIOUS_SHIPPING_ADDRESS = "123 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State FINISHED_STATE = State.FINISHED;
        final String NEW_SHIPPING_ADDRESS = "321 street";

        final var ORDER_ITEM = TestsUtils.orderItemOf(new Product(), new Order(), 3);
        final Set<OrderItem> ORDER_ITEMS = Set.of(ORDER_ITEM);
        final var orderUpdateDto = TestsUtils.orderUpdateDtoOf(NEW_SHIPPING_ADDRESS, FINISHED_STATE);

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            PREVIOUS_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            FINISHED_STATE);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));

        // when & then
        assertThrows(RequestValidationException.class, () -> underTest.update(ORDER_ID, orderUpdateDto));
        verify(orderRepository, never()).deleteById(ArgumentMatchers.any());
    }

    @Test
    void itShouldThrowExceptionWhenOrderNotFoundWhenTryingToUpdate() {
        // given
        final String SHIPPING_ADDRESS = "321 street";
        final State STATE = State.ON_PROCESS;
        final long ORDER_ID = 1L;
        final var orderUpdateDto = TestsUtils.orderUpdateDtoOf(SHIPPING_ADDRESS, STATE);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.update(ORDER_ID, orderUpdateDto));
        verify(orderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void itShouldThrowExceptionWhenOrderNotFoundWhenTryingToRemoveById() {
        // given
        final long ORDER_ID = 1L;

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.remove(ORDER_ID));
        verify(orderRepository, never()).deleteById(ArgumentMatchers.any());
    }

    @Test
    void itShouldRemoveOrder() {
        // given
        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String PREVIOUS_SHIPPING_ADDRESS = "123 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State ON_PROCESS_STATE = State.ON_PROCESS;

        final Set<OrderItem> ORDER_ITEMS = Set.of();

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            PREVIOUS_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            ON_PROCESS_STATE);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));

        // when
        underTest.remove(ORDER_ID);

        // then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(orderRepository).deleteById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(ORDER_ID);
    }

    @Test
    void itShouldThrowExceptionWhenOrderNotFoundWhenTryingToGetById() {
        // given
        final long ORDER_ID = 1L;

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.remove(ORDER_ID));
        verify(orderRepository, never()).deleteById(ArgumentMatchers.any());
    }

    @Test
    void itShouldGetOrderById() {
        // given
        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String PREVIOUS_SHIPPING_ADDRESS = "123 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State ON_PROCESS_STATE = State.ON_PROCESS;

        final Set<OrderItem> ORDER_ITEMS = Set.of();

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            PREVIOUS_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            ON_PROCESS_STATE);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));

        // when
        underTest.getById(ORDER_ID);

        // then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(orderRepository).findById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(ORDER_ID);
    }

    @Test
    void itShouldGetOrders() {
        // given
        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String SHIPPING_ADDRESS = "123 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State ON_PROCESS_STATE = State.ON_PROCESS;

        final Set<OrderItem> ORDER_ITEMS = Set.of();
        final Set<Long> IDS_ORDER_ITEMS = Set.of();

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            ON_PROCESS_STATE);

        final var orderDto = TestsUtils.orderDtoOf(
            ORDER_ID,
            DATE_TIME,
            SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            IDS_ORDER_ITEMS,
            ON_PROCESS_STATE);


        List<Order> orders = List.of(orderFromDb);
        Page<Order> ordersPage = new PageImpl<>(orders);
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(ordersPage);
        when(orderToOrderDtoMapper.apply(orderFromDb)).thenReturn(orderDto);

        // when
        underTest.getOrders(PageRequest.of(0, 30));

        // then
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).findAll(pageableArgumentCaptor.capture());
        Pageable captured = pageableArgumentCaptor.getValue();

        assertThat(captured).isNotNull();
    }
}
