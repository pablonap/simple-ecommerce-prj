package com.agileengine.service;

import com.agileengine.dto.*;
import com.agileengine.model.Order;
import com.agileengine.model.OrderItem;
import com.agileengine.model.State;
import com.agileengine.repository.OrderItemRepository;
import com.agileengine.repository.OrderRepository;
import com.agileengine.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTests {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemToLongMapper orderItemToLongMapper;

    @InjectMocks
    private OrderItemService underTest;

    @Test
    void itShouldCreateOrderItem() {
        // given
        final long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1000);

        final long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String PREVIOUS_SHIPPING_ADDRESS = "123 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State ON_PROCESS_STATE = State.ON_PROCESS;

        final long ORDER_ITEM_ID = 1L;
        final int QUANTITY = 3;

        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);
        final var ORDER_ITEM = TestsUtils.orderItemOf(productFromDb, new Order(), 3);
        final Set<OrderItem> ORDER_ITEMS = Set.of(ORDER_ITEM);

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            PREVIOUS_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            ON_PROCESS_STATE);

        final var savedOrderItem = TestsUtils.orderItemOf(
            ORDER_ITEM_ID,
            productFromDb,
            orderFromDb,
            QUANTITY);

        final var request = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));
        when(orderItemRepository.save(Mockito.any(OrderItem.class))).thenReturn(savedOrderItem);
        when(orderItemToLongMapper.apply(savedOrderItem)).thenReturn(savedOrderItem.getId());

        // when
        OrderItemIdDto result = underTest.create(request);

        // then
        ArgumentCaptor<OrderItem> orderItemArgumentCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(orderItemArgumentCaptor.capture());
        OrderItem capturedOrderItem = orderItemArgumentCaptor.getValue();

        assertThat(result.orderItemId()).isEqualTo(ORDER_ITEM_ID);

        assertNotNull(capturedOrderItem.getOrder());
        assertNotNull(capturedOrderItem.getProduct());
        assertThat(capturedOrderItem.getQuantity()).isEqualTo(QUANTITY);
    }

    @Test
    void itShouldUpdateOrderItem() {
        // given
        final long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1000);

        final long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String PREVIOUS_SHIPPING_ADDRESS = "123 street";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(0);
        final State ON_PROCESS_STATE = State.ON_PROCESS;

        final long ORDER_ITEM_ID = 1L;
        final int QUANTITY = 3;

        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);
        final var orderItem = TestsUtils.orderItemOf(productFromDb, new Order(), 3);
        final Set<OrderItem> ORDER_ITEMS = Set.of(orderItem);

        final var orderFromDb = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            PREVIOUS_SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            ON_PROCESS_STATE);

        final var savedOrderItem = TestsUtils.orderItemOf(
            ORDER_ITEM_ID,
            productFromDb,
            orderFromDb,
            QUANTITY);

        final var request = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));
        when(orderItemRepository.findById(ORDER_ITEM_ID)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(Mockito.any(OrderItem.class))).thenReturn(savedOrderItem);
        when(orderItemToLongMapper.apply(savedOrderItem)).thenReturn(ORDER_ITEM_ID);

        // when
        OrderItemIdDto result = underTest.update(ORDER_ID, request);

        // then
        ArgumentCaptor<OrderItem> orderItemArgumentCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(orderItemArgumentCaptor.capture());
        OrderItem capturedOrderItem = orderItemArgumentCaptor.getValue();

        assertThat(result.orderItemId()).isEqualTo(ORDER_ITEM_ID);

        assertNotNull(capturedOrderItem.getOrder());
        assertNotNull(capturedOrderItem.getProduct());
        assertThat(capturedOrderItem.getQuantity()).isEqualTo(QUANTITY);
    }


    /*
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

    */
}
