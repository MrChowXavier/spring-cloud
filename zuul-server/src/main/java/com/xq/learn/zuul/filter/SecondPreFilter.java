package com.xq.learn.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @author xiaoqiang
 * @date 2019/10/16 1:38
 */
@Component
public class SecondPreFilter extends ZuulFilter
{
    @Override
    public String filterType()
    {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder()
    {
        return 1;
    }

    @Override
    public boolean shouldFilter()
    {
        RequestContext context = RequestContext.getCurrentContext();
        // 获取前一个过滤器的状态，如果为false就不需要执行该过滤器
        return context.sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException
    {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        // 逻辑代码
        String name = request.getParameter("name");
        if (null == name)
        {
            context.setSendZuulResponse(false);
            context.put("isAuth", false);
        }
        return null;
    }
}
