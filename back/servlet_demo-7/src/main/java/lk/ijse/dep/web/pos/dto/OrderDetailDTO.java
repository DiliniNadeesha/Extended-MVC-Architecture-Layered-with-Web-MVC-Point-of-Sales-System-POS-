package lk.ijse.dep.web.pos.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetailDTO {

    private String itemCode;
    private int itemQuantity;
    private BigDecimal unitPrice;
}
