package lk.ijse.dep.web.pos.business.custom;

import lk.ijse.dep.web.pos.business.SuperBO;
import lk.ijse.dep.web.pos.dto.ItemDTO;

import java.util.List;

public interface ItemBO extends SuperBO {

    public List<ItemDTO> getAllItems()throws Exception;

    public void saveItem(String code, String description, int qtyOnHand, double unitPrice)throws Exception;

    public void deleteItem(String itemCode)throws Exception;

    public void updateItem(String description, int qtyOnHand, double unitPrice, String itemCode) throws Exception;

    public String getNewItemCode()throws Exception;

    public ItemDTO getItem(String code) throws Exception;

    public boolean isItemExit(String code) throws Exception;
}
