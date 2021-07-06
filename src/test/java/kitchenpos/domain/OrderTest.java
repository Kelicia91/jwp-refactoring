package kitchenpos.domain;

import static java.time.LocalDateTime.*;
import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

	@DisplayName("주문을 생성할 수 있다.")
	@Test
	void createTest() {
		//given
		OrderLineItem orderLineItem = new OrderLineItem(1L, 1L);
		OrderTable orderTable = new OrderTable(1, false);
		LocalDateTime orderedTime = now();

		// when
		Order order = Order.create(asList(orderLineItem), orderTable, orderedTime);

		// than
		assertThat(order.getOrderedTime()).isEqualTo(orderedTime);
		assertThat(orderLineItem.getOrder()).isEqualTo(order);
		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING);
	}

	@DisplayName("주문 항목이 없으면 주문을 생성할 수 있다.")
	@Test
	void createOrderWithoutOrderLineItems() {
		// given
		OrderTable orderTable = new OrderTable(1, true);
		LocalDateTime orderedTime = now();

		// when
		// than
		assertThatThrownBy(() ->  Order.create(Collections.emptyList(), orderTable, orderedTime))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("주문 항목이 없습니다.");
	}

	@DisplayName("빈 주문테이블에서 주문할 수 없다.")
	@Test
	void createOrderWithEmptyOrderTableTest() {
		// given
		OrderLineItem orderLineItem = new OrderLineItem(1L, 1L);
		OrderTable orderTable = new OrderTable(1, true);

		// when
		// than
		assertThatThrownBy(() ->  Order.create(asList(orderLineItem), orderTable, now()))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("빈테이블에서 주문할 수 없습니다.");
	}

	@DisplayName("계산완료상태의 주문은 상태변경이 불가능하다.")
	@Test
	void changeCompletedOrderTest() {
		// given
		OrderLineItem orderLineItem = new OrderLineItem(1L, 1L);
		OrderTable orderTable = new OrderTable(1, false);
		Order completedOrder = Order.create(asList(orderLineItem), orderTable, now());
		completedOrder.complete();

		// when
		// than
		assertThatThrownBy(() -> completedOrder.startMeal())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("계산 완료 주문은 상태를 변경할 수 없습니다.");
	}

	@DisplayName("주문의 상태를 변경 가능하다.")
	@Test
	void changeOrderTest() {
		// given
		OrderLineItem orderLineItem = new OrderLineItem(1L, 1L);
		OrderTable orderTable = new OrderTable(1, false);
		Order order = Order.create(asList(orderLineItem), orderTable, now());

		// when
		order.startMeal();
		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.MEAL);

		// than
		order.complete();
		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETION);
	}
}