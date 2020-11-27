package lk.ijse.dep.web.pos.api.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getMethod().equals("OPTIONS")) {
            res.setHeader("Access-Control-Allow-Headers","Content-Type");
            res.setHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE");
        }
        res.setHeader("Access-Control-Allow-Origin","*");
        res.setHeader("Access-Control-Expose-Headers","Content-Type");
        super.doFilter(req, res, chain);
    }
}
