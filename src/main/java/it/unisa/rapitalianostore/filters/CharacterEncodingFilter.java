package it.unisa.rapitalianostore.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {
  @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    req.setCharacterEncoding("UTF-8");
    res.setCharacterEncoding("UTF-8");
    if (res instanceof HttpServletResponse r) {
      r.setHeader("Content-Type", "text/html; charset=UTF-8");
    }
    chain.doFilter(req, res);
  }
}
