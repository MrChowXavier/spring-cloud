package com.xq.learn.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 网关过滤器，有四大类型：pre, rout, post, error
 * 过滤器的执行顺序：
 * pre-->rout-->post, 这三个过滤器执行过程中出现异常，都会执行error过滤器
 * filter组件必须注册到spring的IOC容器中
 * 利用shouldFilter方法灵活控制过滤器是否启用，否则就算第一个过滤器执行失败，后面的过滤器也会执行
 * 只有shouldFilter返回false时，过滤器不会执行
 * @author xiaoqiang
 * @date 2019/10/16 1:18
 */
@Component
public class PreFilter extends ZuulFilter
{
    /**
     * 返回过滤器的类型
     * @return
     */
    @Override
    public String filterType()
    {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 同类型过滤器的顺序，不管怎么调整都不会影响pre-->rout-->post的执行顺序
     * @return 返回值越小，优先级越高
     */
    @Override
    public int filterOrder()
    {
        return 0;
    }

    /**
     * 是否启动该过滤器, 可以通过RequestContext共享数据，后面的过滤器可以拿到数据，进而
     * 灵活的控制过滤器要不要启用。
     * @return true表示起作用
     */
    @Override
    public boolean shouldFilter()
    {
        return true;
    }

    /**
     * 过滤的核心代码，返回值是一个预留值，没什么作用
     * @return 预留值
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException
    {
        // 获取请求的上下文信息
        RequestContext context = RequestContext.getCurrentContext();
        // 从上下文信息中获取request信息
        HttpServletRequest request = context.getRequest();
        String token = request.getHeader("X-Auth-Token");
        if (null == token || token.isEmpty())
        {
            // 如果没有token信息，拦截该次请求，设置状态和响应信息
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            context.setResponseBody("{\"is_success\": false, \"error_msg\": 401}");
        }
        return "认证失败";
    }
}
