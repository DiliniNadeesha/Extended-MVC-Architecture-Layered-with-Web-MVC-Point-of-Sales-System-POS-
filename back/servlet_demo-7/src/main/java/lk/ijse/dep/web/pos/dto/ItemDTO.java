package lk.ijse.dep.web.pos.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemDTO {

    private String code;
    private String description;
    private int quantity;
    private BigDecimal unitPrice;
}
