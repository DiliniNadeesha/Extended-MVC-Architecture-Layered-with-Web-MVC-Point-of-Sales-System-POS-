package lk.ijse.dep.web.pos.dto;

import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {

    private String orderId;
    private Date orderDate;
    private String customerId;
    private String customerName;
    private double orderTotal;

    public OrderDTO(String orderId, Date orderDate, String customerId) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerId = customerId;
    }
}
