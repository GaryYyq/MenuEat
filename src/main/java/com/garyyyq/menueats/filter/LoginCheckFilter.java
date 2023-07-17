package com.garyyyq.menueats.filter;

import com.alibaba.fastjson.JSON;
import com.garyyyq.menueats.common.BaseContext;
import com.garyyyq.menueats.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = {"/*"})
@Slf4j
public class LoginCheckFilter implements Filter {

    //AntPathMatcher is a tool class for matching paths
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse  response = (HttpServletResponse) servletResponse;

        //Get request URI
        String requestURI = request.getRequestURI();

        log.info("Filtering request URI: {}", requestURI);

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/frontend/**",
                "/common/**",
                "/user/sendEmail",
                "/user/login"

        };

        //Check if the request URI is in the list
        boolean check = check(urls, requestURI);

        //If the request URI is in the list, skip the filter
        if(check){
            log.info("No need to filter {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //If the request URI is not in the list, check if the user is logged in
        if(request.getSession().getAttribute("employee") != null){
            log.info("User is logged in, user id: {}", request.getSession().getAttribute("employee"));

            Long empId  = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        //If the request URI is not in the list, check if the user is logged in
        if(request.getSession().getAttribute("user") != null){
            log.info("User is logged in, user id: {}", request.getSession().getAttribute("user"));

            Long userId  = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        //If the user is not logged in, return an error message
        log.info("User is not logged in");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    public boolean check(String[] urls, String requestURI){
        for(String url : urls){
            if(PATH_MATCHER.match(url, requestURI)){
                return true;
            }
        }
        return false;
    }
}
