package lk.ijse.dep.web.pos.api;

import lk.ijse.dep.web.pos.business.custom.ItemBO;
import lk.ijse.dep.web.pos.dto.ItemDTO;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "ItemServlet", urlPatterns = "/items")
public class ItemServlet extends HttpServlet {

    private ItemBO itemBO;

    private static String parameter(String qs, String name) throws UnsupportedEncodingException {
        String[] qp = qs.split("&");
        for (String parameters : qp) {
            if (parameters.contains("=") & parameters.startsWith(name)) {
                return URLDecoder.decode(parameters.split("=")[1],"UTF-8");
            }
        }
        return null;
    }

    @Override
    public void init() throws ServletException {
        itemBO = ((AnnotationConfigApplicationContext) (getServletContext().getAttribute("ctx"))).getBean(ItemBO.class);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        try (PrintWriter out = response.getWriter()) {
            if (code == null) {
                List<ItemDTO> allItems = itemBO.getAllItems();

                response.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(allItems);
                out.println(json);
                return;
            }
            if (itemBO.isItemExit(code)) {
                ItemDTO item = itemBO.getItem(code);
                out.println(item);
                response.setStatus(200);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String code = request.getParameter("code");
        String description = request.getParameter("description");
        String quantity = request.getParameter("quantity");
        String unitPrice = request.getParameter("unit-price");

        if (code == null || description == null || quantity == null || unitPrice == null ||
                !code.matches("I\\d{3}") || description.trim().length() < 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            if (!itemBO.isItemExit(code)) {
                itemBO.saveItem(code, description, Integer.parseInt(quantity), Double.parseDouble(unitPrice));
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println("Item saved successfully");
            } else {
                response.sendError(400);
            }
        } catch (Exception e) {
            response.sendError(500);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String qs = req.getQueryString();
        if (qs == null) {
            resp.sendError(400);
            return;
        }
        String code = parameter(qs, "code");
        if (code == null) {
            resp.sendError(400);
            return;
        }

        BufferedReader reader = req.getReader();
        String line = null;
        String requestBody = "";

        while ((line = reader.readLine()) != null) {
            requestBody += line;
        }

        //System.out.println(requestBody);
        String description = parameter(requestBody, "description");
        String quantity = parameter(requestBody, "quantity");
        String unitPrice = parameter(requestBody, "unit-price");

        try (PrintWriter out = resp.getWriter()) {
            if (itemBO.isItemExit(code)) {
                itemBO.updateItem(description, Integer.parseInt(quantity), Double.parseDouble(unitPrice), code);
                resp.setStatus(204);
                out.println("Item updated successfully");
            } else {
                resp.sendError(400);
            }
        } catch (Exception e) {
            resp.sendError(500);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String qs = req.getQueryString();
        if (qs == null) {
            resp.sendError(400);
            return;
        }
        String code = parameter(qs, "code");
        if (code == null) {
            resp.sendError(400);
            return;
        }
        try {
            if (itemBO.isItemExit(code)) {
                itemBO.deleteItem(code);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
