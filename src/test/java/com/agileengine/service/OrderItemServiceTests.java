package com.agileengine.service;

import com.agileengine.TestsUtils;
import com.agileengine.dto.*;
import com.agileengine.exception.ResourceNotFoundException;
import com.agileengine.model.Order;
import com.agileengine.model.OrderItem;
import com.agileengine.model.Product;
import com.agileengine.model.State;
import com.agileengine.repository.OrderItemRepository;
import com.agileengine.repository.OrderRepository;
import com.agileengine.repository.ProductRepository;
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
public class OrderItemServiceTests {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemToLongMapper orderItemToLongMapper;

    @Mock
    private OrderItemToOrderItemDtoMapper orderItemToOrderItemDtoMapper;

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
    void itShouldThrowExceptionWhenOrderNotFoundWhenTryingToCreate() {
        // given
        final long ORDER_ID = 1L;
        final long PRODUCT_ID = 1L;
        final int QUANTITY = 3;
        final var request = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1000);
        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.create(request));
        verify(orderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void itShouldThrowExceptionWhenProductNotFoundWhenTryingToCreate() {
        // given
        final long ORDER_ID = 1L;
        final long PRODUCT_ID = 1L;
        final int QUANTITY = 3;
        final var request = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1000);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.create(request));
        verify(orderRepository, never()).save(ArgumentMatchers.any());
        verify(orderRepository, never()).findById(ArgumentMatchers.any());
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
        final int PREVIOUS_QUANTITY = 2;
        final int NEW_QUANTITY = 3;

        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);
        final var orderItemFromDb = TestsUtils.orderItemOf(productFromDb, new Order(), PREVIOUS_QUANTITY);
        final Set<OrderItem> ORDER_ITEMS = Set.of(orderItemFromDb);

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
            NEW_QUANTITY);

        final var request = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, NEW_QUANTITY);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderFromDb));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));
        when(orderItemRepository.findById(ORDER_ITEM_ID)).thenReturn(Optional.of(orderItemFromDb));
        when(orderItemRepository.save(Mockito.any(OrderItem.class))).thenReturn(savedOrderItem);
        when(orderItemToLongMapper.apply(orderItemFromDb)).thenReturn(ORDER_ITEM_ID);

        // when
        OrderItemIdDto result = underTest.update(ORDER_ID, request);

        // then
        ArgumentCaptor<OrderItem> orderItemArgumentCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(orderItemArgumentCaptor.capture());
        OrderItem capturedOrderItem = orderItemArgumentCaptor.getValue();

        assertThat(result.orderItemId()).isEqualTo(ORDER_ITEM_ID);

        assertNotNull(capturedOrderItem.getOrder());
        assertNotNull(capturedOrderItem.getProduct());
        assertThat(capturedOrderItem.getQuantity()).isEqualTo(NEW_QUANTITY);
    }

    @Test
    void itShouldThrowExceptionWhenOrderNotFoundWhenTryingToUpdate() {
        // given
        final long ORDER_ITEM_ID = 1L;
        final long ORDER_ID = 1L;
        final long PRODUCT_ID = 1L;
        final int QUANTITY = 3;
        final var request = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1000);
        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.update(ORDER_ITEM_ID, request));
        verify(orderRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void itShouldThrowExceptionWhenProductNotFoundWhenTryingToUpdate() {
        // given
        final long ORDER_ITEM_ID = 1L;
        final long ORDER_ID = 1L;
        final long PRODUCT_ID = 1L;
        final int QUANTITY = 3;
        final var request = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.update(ORDER_ITEM_ID, request));
        verify(orderRepository, never()).save(ArgumentMatchers.any());
        verify(orderRepository, never()).findById(ArgumentMatchers.any());
    }

    @Test
    void itShouldThrowExceptionWhenOrderItemNotFoundWhenTryingToRemoveById() {
        // given
        final long ORDER_ID = 1L;

        when(orderItemRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.remove(ORDER_ID));
        verify(orderRepository, never()).deleteById(ArgumentMatchers.any());
    }

    @Test
    void itShouldRemoveOrderItem() {
        // given
        final Long ORDER_ITEM_ID = 1L;
        final Long ORDER_ID = 1L;
        final int QUANTITY = 3;
        final var orderItemFromDb = TestsUtils.orderItemOf(new Product(), new Order(), QUANTITY);
        when(orderItemRepository.findById(ORDER_ITEM_ID)).thenReturn(Optional.of(orderItemFromDb));

        // when
        underTest.remove(ORDER_ITEM_ID);

        // then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(orderItemRepository).deleteById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(ORDER_ID);
    }

    @Test
    void itShouldThrowExceptionWhenOrderItemNotFoundWhenTryingToGetById() {
        // given
        final long ORDER_ITEM_ID = 1L;

        when(orderItemRepository.findById(ORDER_ITEM_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.remove(ORDER_ITEM_ID));
        verify(orderItemToOrderItemDtoMapper, never()).apply(ArgumentMatchers.any());
    }

    @Test
    void itShouldGetOrderItemById() {
        // given
        final long PRODUCT_ID = 1L;
        final String PRODUCT_NAME = "laptop";
        final String PRODUCT_CODE = "1234";
        final String PRODUCT_DESCRIPTION = "xxx";
        final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1200);

        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String SHIPPING_ADDRESS = "some address";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(1200);
        final Set<OrderItem> ORDER_ITEMS = Set.of();
        final State STATE = State.ON_PROCESS;

        final Long ORDER_ITEM_ID = 1L;

        final int QUANTITY = 3;

        final var product = TestsUtils.productOf(
            PRODUCT_ID,
            PRODUCT_NAME,
            PRODUCT_CODE,
            PRODUCT_DESCRIPTION,
            PRODUCT_PRICE);

        final var order = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            STATE);

        final var orderItem = TestsUtils.orderItemOf(product, order, QUANTITY);
        final var orderItemDto = TestsUtils.orderItemDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);

        when(orderItemRepository.findById(ORDER_ITEM_ID)).thenReturn(Optional.of(orderItem));
        when(orderItemToOrderItemDtoMapper.apply(orderItem)).thenReturn(orderItemDto);

        // when
        underTest.getById(ORDER_ITEM_ID);

        // then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(orderItemRepository).findById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(ORDER_ITEM_ID);
    }

    @Test
    void itShouldGetOrderItems() {
        // given
        final long PRODUCT_ID = 1L;
        final String PRODUCT_NAME = "laptop";
        final String PRODUCT_CODE = "1234";
        final String PRODUCT_DESCRIPTION = "xxx";
        final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1200);

        final Long ORDER_ID = 1L;
        final LocalDateTime DATE_TIME = LocalDateTime.now();
        final String SHIPPING_ADDRESS = "some address";
        final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(1200);
        final Set<OrderItem> ORDER_ITEMS = Set.of();
        final State STATE = State.ON_PROCESS;

        final int QUANTITY = 3;

        final var product = TestsUtils.productOf(
            PRODUCT_ID,
            PRODUCT_NAME,
            PRODUCT_CODE,
            PRODUCT_DESCRIPTION,
            PRODUCT_PRICE);

        final var order = TestsUtils.orderOf(
            ORDER_ID,
            DATE_TIME,
            SHIPPING_ADDRESS,
            TOTAL_AMOUNT,
            ORDER_ITEMS,
            STATE);

        final var orderItem = TestsUtils.orderItemOf(product, order, QUANTITY);
        final var orderItemDto = TestsUtils.orderItemDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);

        List<OrderItem> orderItems = List.of(orderItem);
        Page<OrderItem> orderItemsPage = new PageImpl<>(orderItems);
        when(orderItemRepository.findAll(any(Pageable.class))).thenReturn(orderItemsPage);
        when(orderItemToOrderItemDtoMapper.apply(orderItem)).thenReturn(orderItemDto);

        // when
        underTest.getOrderItems(PageRequest.of(0, 30));

        // then
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderItemRepository).findAll(pageableArgumentCaptor.capture());
        Pageable captured = pageableArgumentCaptor.getValue();

        assertThat(captured).isNotNull();
    }
}
