package pk.temp.bm.security;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Created by Abubakar on 7/19/2017.
 */
public class RequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        MultiReadHttpServletRequest requestWrapper= new MultiReadHttpServletRequest(httpServletRequest);
        // Pass request back down the filter chain
        filterChain.doFilter(requestWrapper,httpServletResponse);
    }

    public void destroy( ){
 /* Called before the Filter instance is removed
 from service by the web container*/
    }

}
