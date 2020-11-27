package lk.ijse.dep.web.pos.business.custom;

import lk.ijse.dep.web.pos.business.SuperBO;
import lk.ijse.dep.web.pos.dto.CustomerDTO;

import java.util.List;

public interface CustomerBO extends SuperBO {

    public List<CustomerDTO> getAllCustomers() throws Exception;

    public void saveCustomer(String id, String name, String address)throws Exception;

    public void deleteCustomer(String customerId)throws Exception;

    public void updateCustomer(String name, String address, String customerId)throws Exception;

    public String getNewCustomerId()throws Exception;

    public CustomerDTO findCustomer(String id)throws Exception;

    public boolean isCustomerExit(String id) throws Exception;
}
