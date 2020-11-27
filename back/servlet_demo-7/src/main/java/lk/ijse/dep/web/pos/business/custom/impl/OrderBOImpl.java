package lk.ijse.dep.web.pos.business.custom.impl;

import lk.ijse.dep.web.pos.business.custom.OrderBO;
import lk.ijse.dep.web.pos.dto.OrderDTO;
import lk.ijse.dep.web.pos.dto.OrderDetailDTO;
import lk.ijse.dep.web.pos.entity.CustomEntity;
import lk.ijse.dep.web.pos.entity.Item;
import lk.ijse.dep.web.pos.entity.Order;
import lk.ijse.dep.web.pos.entity.OrderDetail;
import lk.ijse.dep.web.pos.repository.CustomerRepository;
import lk.ijse.dep.web.pos.repository.ItemRepository;
import lk.ijse.dep.web.pos.repository.OrderDetailRepository;
import lk.ijse.dep.web.pos.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class OrderBOImpl implements OrderBO {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public String getNewOrderId() throws Exception {
        String lastOrderId = orderRepository.getFirstLastOrderIdByOrderByIdDesc().getId();
        if (lastOrderId == null) {
            return "OD001";
        } else {
            int maxId = Integer.parseInt(lastOrderId.replace("OD", ""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "OD00" + maxId;
            } else if (maxId < 100) {
                id = "OD0" + maxId;
            } else {
                id = "OD" + maxId;
            }
            return id;
        }
    }

    public void placeOrder(OrderDTO order, List<OrderDetailDTO> orderDetails) throws Exception {
        orderRepository.save(new Order(order.getOrderId(), order.getOrderDate(),
                customerRepository.findById(order.getCustomerId()).get()));

        for (OrderDetailDTO orderDetail : orderDetails) {
            orderDetailRepository.save(new OrderDetail(
                    order.getOrderId(), orderDetail.getItemCode(),
                    orderDetail.getItemQuantity(), orderDetail.getUnitPrice()
            ));
            Item item = itemRepository.findById(orderDetail.getItemCode()).get();
            item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getItemQuantity());
            itemRepository.save(item);

        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> getAllOrders() throws Exception {
        List<CustomEntity> allOrderDetails = orderRepository.getAllOrderDetails2();
        List<OrderDTO> orderDetailsList = new ArrayList<>();
        for (CustomEntity orderDetails : allOrderDetails) {
            BigDecimal total = orderDetails.getTotal();
            orderDetailsList.add(new OrderDTO(orderDetails.getOrderId(), orderDetails.getOrderDate(), orderDetails.getCustomerId(),
                    orderDetails.getCustomerName(), Double.parseDouble(total.toString())));
        }
        return orderDetailsList;
    }

    @Override
    public boolean isExitOrder(String id) throws Exception {
        return orderRepository.findById(id).isPresent();
    }
}
