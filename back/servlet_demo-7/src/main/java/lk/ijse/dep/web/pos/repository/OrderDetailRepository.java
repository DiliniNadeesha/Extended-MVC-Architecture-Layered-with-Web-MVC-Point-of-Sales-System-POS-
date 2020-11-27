package lk.ijse.dep.web.pos.repository;

import lk.ijse.dep.web.pos.entity.OrderDetail;
import lk.ijse.dep.web.pos.entity.OrderDetailPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailPK> {

}
