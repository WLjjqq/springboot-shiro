package com.shiro.springbootshiro.shiro;

import com.shiro.springbootshiro.bean.User;
import com.shiro.springbootshiro.util.CodeAndMsgEnum;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * shiro 自定义filter 实现 并发登录控制
 */
public class KickoutSessionControlFilter extends AccessControlFilter {
    private final String kickOutKey = "kickout";
    private String kickoutPrefix;
    private RedisTemplate redisTemplate;
    private SessionManager sessionManager;
    /** 踢出后到的地址 */
    private String kickoutUrl;

    /**  同一个帐号最大会话数 默认1 */
    private int maxSession = 1;

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }


    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }
    public void setKickoutPrefix(String kickoutPrefix) {
        this.kickoutPrefix = kickoutPrefix;
    }
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 表示是否允许访问；mappedValue就是[urls]配置中拦截器参数部分，如果允许访问返回true，否则false；
     * @param servletRequest
     * @param servletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    /**
     * 表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理了，将直接返回即可。
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        //如果没有登录，不进行多出登录判断
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return true;
        }
        Session session = subject.getSession();
        User user = (User) subject.getPrincipal();
        String username = user.getUsername();
        Serializable sessionId = session.getId();
        //获取redis中数据
        ArrayList<Serializable> deque = (ArrayList<Serializable>) redisTemplate.opsForList().range(kickoutPrefix + username, 0, -1);
        if (deque == null || deque.size() == 0) {
            deque = new ArrayList<>();
        }
        //如果队列里没有此sessionId，且用户没有被踢出,当前session放入队列,redis缓存中
        if (!deque.contains(sessionId) && session.getAttribute(kickOutKey) == null) {
            deque.add(sessionId);
            redisTemplate.opsForList().leftPush(kickoutPrefix + username, sessionId);
        }
        //如果队列里的sessionId数大于1，开始踢人
        while (deque.size() > maxSession) {

            //获取第一个sessionId（arrayList方法有限转成LinkedList）
            Serializable kickoutSessionId = (Serializable) new LinkedList(deque).removeFirst();
            deque.remove(kickoutSessionId);
            redisTemplate.opsForList().remove(kickoutPrefix + username, 1, kickoutSessionId);
            try {
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                //设置会话的kickout属性表示踢出了
                if (kickoutSession != null) {
                    kickoutSession.setAttribute(kickOutKey, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //session包含kickout属性，T出  如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute(kickOutKey) != null) {
            try {
                subject.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*saveRequest(servletRequest);
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(HttpStatus.OK.value());
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.getWriter().write("{\"code\":" + CodeAndMsgEnum.UNAUTHENTIC.getcode() + ", \"msg\":\"" + "当前帐号在其他地方登录，您已被强制下载！" + "\"}");
*/
            WebUtils.issueRedirect(servletRequest,servletResponse,kickoutUrl);
            return false;
        }
        return true;
    }


}
