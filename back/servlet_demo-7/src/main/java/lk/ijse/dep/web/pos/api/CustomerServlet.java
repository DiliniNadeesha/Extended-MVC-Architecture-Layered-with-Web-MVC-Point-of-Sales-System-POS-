package lk.ijse.dep.web.pos.api;

import lk.ijse.dep.web.pos.business.custom.CustomerBO;
import lk.ijse.dep.web.pos.dto.CustomerDTO;
import lombok.SneakyThrows;
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
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet(name = "CustomerServlet", urlPatterns = "/customers")
public class CustomerServlet extends HttpServlet {

    private CustomerBO customerBO;

    private static String spilt(String qs, String name) throws UnsupportedEncodingException {
        if (qs == null || name == null || qs.trim().isEmpty() || name.trim().isEmpty()) {
            return null;
        }
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
        customerBO = ((AnnotationConfigApplicationContext) (getServletContext().getAttribute("ctx"))).getBean(CustomerBO.class);
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        try (PrintWriter out = resp.getWriter()) {
            if (id == null) {
                List<CustomerDTO> allCustomers = customerBO.getAllCustomers();

                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(allCustomers);
                out.println(json);

            } else {
                try {
                    CustomerDTO customer = customerBO.findCustomer(id);
                    out.println(customer);
                } catch (NoSuchElementException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }

            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String address = request.getParameter("address");

        if (id == null || name == null || address == null || !id.matches("C\\d{3}") || name.trim().length() < 3 || address.trim().length() < 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("text/plain");
        try (PrintWriter out = response.getWriter()) {
            if (customerBO.isCustomerExit(id)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            customerBO.saveCustomer(id, name, address);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.println("Customer saved successfully");

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String qs = req.getQueryString();
        String id = spilt(qs, "id");

        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        //String id = req.getParameter("id");
        //System.out.println("Customer updated :" + id);

        BufferedReader reader = req.getReader();
        String line = null;
        String requestBody = "";

        while ((line = reader.readLine()) != null) {
            requestBody += line;
        }
        //System.out.println(requestBody);
        String name = spilt(requestBody, "name");
        String address = spilt(requestBody, "address");

        if (id == null || name == null || address == null || !id.matches("C\\d{3}") || name.trim().length() < 3 || address.trim().length() < 3) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        resp.setContentType("text/plain");
        try (PrintWriter out = resp.getWriter()) {
            if (customerBO.isCustomerExit(id)) {
                customerBO.updateCustomer(name, address, id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                out.println("Customer updated successfully");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = spilt(req.getQueryString(), "id");
        try {
            if (id == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (customerBO.isCustomerExit(id)) {
                customerBO.deleteCustomer(id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                //  out.println("Customer deleted Successfully");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
