package kitchenpos.ordertable.application;

import static kitchenpos.common.DomainFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.order.domain.OrderRepository;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.domain.OrderTableRepository;
import kitchenpos.tablegroup.domain.TableGroup;

@ExtendWith(MockitoExtension.class)
public class TableServiceTest {

	@InjectMocks
	private TableService tableService;

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OrderTableRepository orderTableRepository;

	@Test
	void create() {
		given(orderTableRepository.save(any())).willReturn(orderTable(1L, null, 3, true));

		final OrderTable createdOrderTable = tableService.create(orderTable(null, null, 3, true));

		assertThat(createdOrderTable.getId()).isNotNull();
		assertThat(createdOrderTable.getTableGroupId()).isNull();
	}

	@Test
	void list() {
		final OrderTable orderTable1 = orderTable(1L, null, 4, false);
		final OrderTable orderTable2 = orderTable(2L, null, 3, true);
		given(orderTableRepository.findAll()).willReturn(Arrays.asList(orderTable1, orderTable2));

		assertThat(tableService.list()).containsExactly(orderTable1, orderTable2);
	}

	@Test
	void changeEmpty() {
		final OrderTable 비어있지않은_테이블 = orderTable(1L, null, 2, false);
		given(orderTableRepository.findById(any())).willReturn(Optional.of(비어있지않은_테이블));
		given(orderRepository.existsByOrderTable_IdAndOrderStatusIn(any(), anyList())).willReturn(false);

		final OrderTable 빈_테이블 = orderTable(비어있지않은_테이블.getId(), null, 2, true);
		given(orderTableRepository.save(any())).willReturn(빈_테이블);

		final OrderTable changedOrderTable = tableService.changeEmpty(비어있지않은_테이블.getId(), 빈_테이블);

		assertThat(changedOrderTable.isEmpty()).isTrue();
	}

	@Test
	void changeEmpty_not_found_order_table() {
		final Long orderTableId = 1L;
		given(orderTableRepository.findById(any())).willReturn(Optional.empty());

		assertThatIllegalArgumentException()
			.isThrownBy(() -> tableService.changeEmpty(orderTableId,
				orderTable(orderTableId, null, 2, false)
			));
	}

	@Test
	void changeEmpty_order_table_having_table_group_id() {
		final TableGroup tableGroup = tableGroup(2L, null);
		final OrderTable 빈_테이블 = orderTable(1L, tableGroup, 2, true);
		given(orderTableRepository.findById(any())).willReturn(Optional.of(빈_테이블));

		assertThatIllegalArgumentException()
			.isThrownBy(() -> tableService.changeEmpty(빈_테이블.getId(),
				orderTable(빈_테이블.getId(), tableGroup, 2, false)
			));
	}

	@Test
	void changeEmpty_order_table_status_cooking_or_meal() {
		final OrderTable 만석_테이블 = orderTable(1L, null, 2, false);
		given(orderTableRepository.findById(any())).willReturn(Optional.of(만석_테이블));
		given(orderRepository.existsByOrderTable_IdAndOrderStatusIn(any(), anyList())).willReturn(true);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> tableService.changeEmpty(만석_테이블.getId(),
				orderTable(만석_테이블.getId(), null, 0, false)
			));
	}

	@Test
	void changeNumberOfGuests() {
		final Long orderTableId = 1L;
		final OrderTable 손님_1명_테이블 = orderTable(orderTableId, null, 1, false);
		given(orderTableRepository.findById(any())).willReturn(Optional.of(손님_1명_테이블));
		final OrderTable 손님_2명_테이블 = orderTable(orderTableId, null, 2, false);
		given(orderTableRepository.save(any())).willReturn(손님_2명_테이블);

		final OrderTable changedOrderTable = tableService.changeNumberOfGuests(orderTableId, 손님_2명_테이블);

		assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(2);
	}

	@Test
	void changeNumberOfGuests_minus_number_of_guests() {
		final Long orderTableId = 1L;

		assertThatIllegalArgumentException()
			.isThrownBy(() -> tableService.changeNumberOfGuests(orderTableId,
				orderTable(orderTableId, null, -1, false)
			));
	}

	@Test
	void changeNumberOfGuests_not_found_order_table() {
		final Long orderTableId = 1L;
		given(orderTableRepository.findById(any())).willReturn(Optional.empty());

		assertThatIllegalArgumentException()
			.isThrownBy(() -> tableService.changeNumberOfGuests(orderTableId,
				orderTable(orderTableId, null, 3, false)
			));
	}

	@Test
	void changeNumberOfGuests_empty_order_table() {
		final OrderTable 빈_테이블 = orderTable(1L, null, 2, true);
		given(orderTableRepository.findById(any())).willReturn(Optional.of(빈_테이블));

		assertThatIllegalArgumentException()
			.isThrownBy(() -> tableService.changeNumberOfGuests(빈_테이블.getId(),
				orderTable(빈_테이블.getId(), null, 3, true)
			));
	}
}