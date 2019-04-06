# springboot-shiro

# 一：maven加入jar包

```Java
        <!--添加shiro-->
        <!-- https://mvnrepository.com/artifact/org.apache.shiro/shiro-spring -->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.4.0</version>
        </dependency>
        <!--thymeleaf使用shiro标签-->
        <!-- https://mvnrepository.com/artifact/com.github.theborakompanioni/thymeleaf-extras-shiro -->
        <dependency>
            <groupId>com.github.theborakompanioni</groupId>
            <artifactId>thymeleaf-extras-shiro</artifactId>
            <version>2.0.0</version>
        </dependency>
        <!--分页-->
        <!-- https://mvnrepository.com/artifact/com.github.pagehelper/pagehelper-spring-boot-starter -->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.10</version>
        </dependency>
        <!--通用Mapper-->
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>2.0.0</version>
        </dependency>
```
# 二：自定义Realm
```Java
package com.shiro.springbootshiro.shiro;

import com.shiro.springbootshiro.bean.Resources;
import com.shiro.springbootshiro.bean.User;
import com.shiro.springbootshiro.service.ResourcesService;
import com.shiro.springbootshiro.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作用：自定义Realm
 */
public class UserRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Resource
    private ResourcesService resourcesService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user= (User) SecurityUtils.getSubject().getPrincipal();//User{id=1, username='admin', password='3ef7164d1f6167cb9f2658c07d3c2f0a', enable=1}
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userid",user.getId());
        List<Resources> resourcesList = resourcesService.loadUserResources(map);
        // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for(Resources resources: resourcesList){
            info.addStringPermission(resources.getResurl());
        }
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取用户的输入的账号.
        String username = (String)authenticationToken.getPrincipal();
        User user = userService.selectByUsername(username);
        if(user==null) throw new UnknownAccountException();
        if (0==user.getEnable()) {
            throw new LockedAccountException(); // 帐号锁定
        }
        // 认证信息token里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名
        // 盐也放进去
        // 这样通过配置中的 HashedCredentialsMatcher 进行自动校验
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, //用户
                user.getPassword(), //密码
                ByteSource.Util.bytes(username),
                getName()  //realm name
        );
        // 当验证都通过后，把用户信息放在session里
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userSession", user);
        session.setAttribute("userSessionId", user.getId());
        return authenticationInfo;
    }
}
```

# 三：写ShiroConfig
```Java
package com.shiro.springbootshiro.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.github.pagehelper.util.StringUtil;
import com.shiro.springbootshiro.bean.Resources;
import com.shiro.springbootshiro.service.ResourcesService;
import com.shiro.springbootshiro.shiro.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shiro的配置类
 */
@Configuration
public class ShiroConfig {

    @Autowired(required = false)  //ResourcesService不用必须存在
    private ResourcesService resourcesService;

    /**
     * Shiro过滤器工程类，具体的实现类是：ShiroFilterFactoryBean
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shirFilter(@Qualifier("securityManager") DefaultWebSecurityManager  securityManager){
        System.out.println("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean  = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/usersPage");
        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();

        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/css/**","anon");
        filterChainDefinitionMap.put("/js/**","anon");
        filterChainDefinitionMap.put("/img/**","anon");
        filterChainDefinitionMap.put("/font-awesome/**","anon");
        //<!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        //自定义加载权限资源关系
        List<Resources> resourcesList = resourcesService.queryAll();
        for(Resources resources:resourcesList){

            if (StringUtil.isNotEmpty(resources.getResurl())) {
                String permission = "perms[" + resources.getResurl()+ "]";
                filterChainDefinitionMap.put(resources.getResurl(),permission);
            }
        }
        filterChainDefinitionMap.put("/**", "authc");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    /**
     * Shiro的安全管理，主要是身份认证的管理，缓存管理，cookie管理，
     * 所以在实际开发中我们主要是和SecurityManager进行打交道的。
     * 创建DefaultWebSecurityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 关联realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 用于身份信息权限信息的验证。开发时集成AuthorizingRealm，重写两个方法:
     * doGetAuthenticationInfo(获取即将需要认真的信息)、
     * doGetAuthorizationInfo(获取通过认证后的权限信息)。
     * 创建Realm
     */
    @Bean(name = "userRealm")
    public UserRealm getRealm(){

        UserRealm userRealm = new UserRealm();
        //告诉realm,使用credentialsMatcher加密算法类来验证密文
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return userRealm;
    }

    /**
     * 配置ShiroDialect，用于thymeleaf和shiro标签配合使用
     */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *  所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));

        return hashedCredentialsMatcher;
    }


    /**
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager  securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
```

# 四：shiro中使用thymelea标签。添加页面，设置没有权限的数据不显示出来
```Java
<!--thymelea标签添加页面   inclide=”文件路径::局部代码片段名称”-->
<div th:include="common/top :: top"></div>
<div th:include="common/menu :: menu"></div>

<!--没有权限的不显示出来-->
                    <form class="form-inline">
                        <button type="button" shiro:hasPermission="/roles/add" onclick="$('#addRole').modal();" class="btn btn-info" style="float: right; margin-right: 1px;">新增</button>
                        <button type="button" shiro:hasPermission="/roles/delete" onclick="delById();" class="btn btn-info" style="float: right; margin-right: 1px;">删除</button>
                        <button type="button" shiro:hasPermission="/roles/saveRoleResources" onclick="allotResources();" class="btn btn-info" style="float: right; margin-right: 1px;">分配权限</button>
                    </form>
```
    
