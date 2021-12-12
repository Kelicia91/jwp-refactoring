package kitchenpos.dao;

import kitchenpos.domain.TableGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTableGroupRepository extends TableGroupDao, JpaRepository<TableGroup, Long> {
}
