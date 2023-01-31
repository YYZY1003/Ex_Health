package com.yy.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.yy.reggie.common.BaseContext;
import com.yy.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();   //路径匹配器

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //1、获取本次请求的uri
        String requestURI = request.getRequestURI();

        //定义不需要请求的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3、如果不需要处理
        if (check){
            filterChain.doFilter(request,response);
            return;
        }

        //4、判断登录状态，如果已经登录，直接放行
        if (request.getSession().getAttribute("employee")!=null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrenId(empId);

            filterChain.doFilter(request,response);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);
            return;
        }
        if (request.getSession().getAttribute("user")!=null){
            Long empId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrenId(empId);

            filterChain.doFilter(request,response);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);
            return;
        }

        //5、如果没有登录，通过输出流的方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("拦截到请求：{}", request.getRequestURI());
        return;
    }

    /**
     * 路径匹配，本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[]urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match==true){
                return true;
            }
        }
        return false;
    }
}
