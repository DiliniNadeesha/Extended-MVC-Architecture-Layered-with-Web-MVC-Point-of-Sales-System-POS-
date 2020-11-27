package lk.ijse.dep.web.pos.api;

import lk.ijse.dep.web.pos.business.custom.OrderBO;
import lk.ijse.dep.web.pos.dto.OrderDTO;
import lk.ijse.dep.web.pos.dto.OrderDetailDTO;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "OrderServlet", urlPatterns = "/orders")
public class OrderServlet extends HttpServlet {

    private OrderBO orderBo;

    @Override
    public void init() throws ServletException {
        orderBo = ((AnnotationConfigApplicationContext) (getServletContext().getAttribute("ctx"))).getBean(OrderBO.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<OrderDetailDTO> orderDetailList = new ArrayList<>();

        String orderId = request.getParameter("order_id");
        String orderDate = request.getParameter("order_date");
        String customerId = request.getParameter("customer_id");

        String[] code = request.getParameterValues("item_code");
        String[] qty = request.getParameterValues("item_qty");
        String[] price = request.getParameterValues("item_price");

        if (orderId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            if (!orderBo.isExitOrder(orderId)) {

                for (int i = 0; i < code.length; i++) {
                    // System.out.println("code:" + code[i] + "qty:" + qty[i] + "price:" + price[i]);
                    orderDetailList.add(new OrderDetailDTO(code[i], Integer.parseInt(qty[i]), new BigDecimal(price[i])));
                }

                orderBo.placeOrder(new OrderDTO(orderId, Date.valueOf(orderDate), customerId),
                        orderDetailList);
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println("Order placed successfully");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("order-id");
        try (PrintWriter out = resp.getWriter()) {
            if (id == null) {
            List<OrderDTO> allOrders = orderBo.getAllOrders();
                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(allOrders);
                out.println(json);
                resp.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
