package lk.ijse.dep.web.pos.entity;

import java.sql.Date;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


//Joined query
@NamedQuery(name ="Order.getAllOrderDetails2",query = "SELECT o.id AS orderId,o.date AS orderDate,c.id AS customerId,c.name AS customerName,SUM(od.unitPrice*od.qty) AS total FROM Order o INNER JOIN o.customerId c INNER JOIN o.orderDetailList od " +
        "GROUP BY o.id")

/*@NamedNativeQuery(name = "Order.getAllOrderDetails",
        query = "SELECT o.id AS orderId,o.date AS orderDate,c.id AS customerId,c.name AS customerName,SUM(od.qty*od.unitPrice) AS total FROM `Order` o " +
                "INNER JOIN Customer c ON o.customerId = c.id " +
                "INNER JOIN OrderDetail od on o.id = od.orderId " +
                "GROUP BY o.id"
        )*/

@Entity
//@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "orderDetailList")
@Table(name = "`Order`")
public class Order implements SuperEntity {

  @Id
  private String id;
  private Date date;
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "customerId", referencedColumnName = "id", nullable = false)
  private Customer customerId;
  //by directional
  @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
  private List<OrderDetail> orderDetailList;

  public Order(String id, Date date, Customer customerId) {
    this.id = id;
    this.date = date;
    this.customerId = customerId;
  }

  public Order(String id, Date date, Customer customerId, List<OrderDetail> orderDetailList) {
    this.id = id;
    this.date = date;
    this.customerId = customerId;
    for (OrderDetail orderDetail : orderDetailList) {
      orderDetail.setOrder(this);
    }
    this.orderDetailList = orderDetailList;
  }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
    for (OrderDetail orderDetail : orderDetailList) {
      orderDetail.setOrder(this);
    }
    this.orderDetailList = orderDetailList;
  }

  public void addOrderDetail(OrderDetail orderDetail) {
    orderDetail.setOrder(this);
    this.getOrderDetailList().add(orderDetail);
  }

}
