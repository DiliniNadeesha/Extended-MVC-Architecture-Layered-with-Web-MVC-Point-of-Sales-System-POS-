package lk.ijse.dep.web.pos.repository;

import lk.ijse.dep.web.pos.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, String> {

    Item getFirstLastItemCodeByOrderByCodeDesc() throws Exception;

}
