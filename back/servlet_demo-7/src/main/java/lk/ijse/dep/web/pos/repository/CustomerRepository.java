package lk.ijse.dep.web.pos.repository;

import lk.ijse.dep.web.pos.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,String> {

    Customer getFirstLastCustomerIdByOrderByIdDesc() throws Exception;


}
