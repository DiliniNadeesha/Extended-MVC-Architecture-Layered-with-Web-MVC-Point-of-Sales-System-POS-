package lk.ijse.dep.web.pos.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Item implements SuperEntity {

    @Id
    private String code;
    private String description;
    private BigDecimal unitPrice;
    private int qtyOnHand;


}
