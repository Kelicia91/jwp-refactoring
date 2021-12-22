package kitchenpos.ordertable.application;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.domain.OrderTableRepository;

@Service
public class TableService {

	private final OrderRepository orderRepository;
	private final OrderTableRepository orderTableRepository;

	public TableService(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository) {
		this.orderRepository = orderRepository;
		this.orderTableRepository = orderTableRepository;
	}

	@Transactional
	public OrderTable create(final OrderTable orderTable) {
		orderTable.setTableGroup(null);

		return orderTableRepository.save(orderTable);
	}

	public List<OrderTable> list() {
		return orderTableRepository.findAll();
	}

	@Transactional
	public OrderTable changeEmpty(final Long orderTableId, final OrderTable orderTable) {
		final OrderTable savedOrderTable = orderTableRepository.findById(orderTableId)
			.orElseThrow(IllegalArgumentException::new);

		if (Objects.nonNull(savedOrderTable.getTableGroupId())) {
			throw new IllegalArgumentException();
		}

		if (orderRepository.existsByOrderTable_IdAndOrderStatusIn(
			orderTableId, Arrays.asList(OrderStatus.COOKING, OrderStatus.MEAL))) {
			throw new IllegalArgumentException();
		}

		savedOrderTable.setEmpty(orderTable.isEmpty());

		return orderTableRepository.save(savedOrderTable);
	}

	@Transactional
	public OrderTable changeNumberOfGuests(final Long orderTableId, final OrderTable orderTable) {
		final int numberOfGuests = orderTable.getNumberOfGuests();

		if (numberOfGuests < 0) {
			throw new IllegalArgumentException();
		}

		final OrderTable savedOrderTable = orderTableRepository.findById(orderTableId)
			.orElseThrow(IllegalArgumentException::new);

		if (savedOrderTable.isEmpty()) {
			throw new IllegalArgumentException();
		}

		savedOrderTable.setNumberOfGuests(numberOfGuests);

		return orderTableRepository.save(savedOrderTable);
	}
}