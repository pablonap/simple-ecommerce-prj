package com.agileengine.controller;

import com.agileengine.TestsUtils;
import com.agileengine.dto.OrderItemCreateOrUpdateDto;
import com.agileengine.dto.OrderItemDto;
import com.agileengine.dto.OrderItemIdDto;
import com.agileengine.service.OrderItemService;
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

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderItemControllerTests {
    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderItemController underTest;

    @Test
    public void itShouldCreateOrderItemWithStatusOk() {
        // given
        final long PRODUCT_ID = 1;
        final long ORDER_ID = 1;
        final int QUANTITY = 3;

        final var REQUEST = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);
        OrderItemIdDto orderItemIdDto = new OrderItemIdDto(ORDER_ID);

        when(orderItemService.create(any(OrderItemCreateOrUpdateDto.class))).thenReturn(orderItemIdDto);

        // when
        ResponseEntity<OrderItemIdDto> response = underTest.create(REQUEST);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ORDER_ID, Objects.requireNonNull(response.getBody()).orderItemId());
    }

    @Test
    public void itShouldUpdateOrderItemWithStatusOk() {
        // given
        final long PRODUCT_ID = 1;
        final long ORDER_ID = 1;
        final int QUANTITY = 3;

        final var REQUEST = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);
        OrderItemIdDto orderItemIdDto = new OrderItemIdDto(ORDER_ID);

        when(orderItemService.update(anyLong(), any(OrderItemCreateOrUpdateDto.class))).thenReturn(orderItemIdDto);

        // when
        ResponseEntity<OrderItemIdDto> response = underTest.update(ORDER_ID, REQUEST);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void itShouldFindAllOrderItems() {
        // given
        int page = 0;
        int size = 30;
        PageRequest pageRequest = PageRequest.of(page, size);

        final long PRODUCT_ID = 1;
        final long ORDER_ID = 1;
        final int QUANTITY = 3;

        List<OrderItemDto> orderItemDtos = List.of(
            TestsUtils.orderItemDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY));

        Page<OrderItemDto> orderDtoPage =
            new PageImpl<>(orderItemDtos, pageRequest, orderItemDtos.size());

        when(orderItemService.getOrderItems(pageRequest)).thenReturn(orderDtoPage);

        // when
        ResponseEntity<Page<OrderItemDto>> response = underTest.findAll(page, size);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDtoPage, response.getBody());
    }

    @Test
    public void itShouldFindOrderById() {
        // given
        final long PRODUCT_ID = 1;
        final long ORDER_ID = 1;
        final int QUANTITY = 3;
        final long ORDER_ITEM_ID = 1;

        OrderItemDto orderItemDto = TestsUtils.orderItemDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);

        when(orderItemService.getById(anyLong())).thenReturn(orderItemDto);

        // when
        ResponseEntity<OrderItemDto> response = underTest.findById(ORDER_ITEM_ID);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderItemDto, response.getBody());
    }

    @Test
    public void itShouldRemoveOrderById() {
        // given
        long ORDER_ITEM_ID = 1L;

        // when
        ResponseEntity<Void> response = underTest.remove(ORDER_ITEM_ID);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderItemService).remove(ORDER_ITEM_ID);
    }
}
