package kitchenpos.tablegroup.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.springframework.util.CollectionUtils;

import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.tablegroup.exception.InvalidOrderTablesException;
import kitchenpos.tablegroup.exception.NotFoundOrderTablesException;

@Embeddable
public class OrderTables {

	public static final int MIN_SIZE_INCLUSIVE = 2;

	@OneToMany(mappedBy = "tableGroup")
	private List<OrderTable> orderTables = new ArrayList<>();

	protected OrderTables() {
	}

	private OrderTables(TableGroup tableGroup, List<OrderTable> orderTables) {
		validate(orderTables);
		addAll(tableGroup, orderTables);
	}

	private void validate(List<OrderTable> orderTables) {
		if (CollectionUtils.isEmpty(orderTables)) {
			throw new NotFoundOrderTablesException();
		}
		if (orderTables.size() < MIN_SIZE_INCLUSIVE) {
			throw new InvalidOrderTablesException();
		}
	}

	public static OrderTables of(TableGroup tableGroup, List<OrderTable> orderTables) {
		return new OrderTables(tableGroup, orderTables);
	}

	private void addAll(TableGroup tableGroup, List<OrderTable> orderTables) {
		this.orderTables.addAll(orderTables);
		this.orderTables.forEach(orderTable -> orderTable.group(tableGroup));
	}

	public void ungroup() {
		orderTables.forEach(OrderTable::ungroup);
	}

	public List<OrderTable> getOrderTables() {
		return Collections.unmodifiableList(orderTables);
	}
}