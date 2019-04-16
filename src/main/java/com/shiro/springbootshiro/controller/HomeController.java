package com.shiro.springbootshiro.controller;

import com.shiro.springbootshiro.bean.*;
import com.shiro.springbootshiro.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {

   /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping(value={"/login"},method= RequestMethod.GET)
    public String login(){
            return "login";
        }

    /**
     * 用户登录
     * @param request
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value="/login",method=RequestMethod.POST)
    public String login(HttpServletRequest request, User user, Model model){
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            request.setAttribute("msg", "用户名或密码不能为空！");
            return "login";
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUsername(),user.getPassword());
        try {
            subject.login(token);
            return "redirect:usersPage";
        }catch (LockedAccountException lae) {
            token.clear();
            request.setAttribute("msg", "用户已经被锁定不能登录，请与管理员联系！");
            return "login";
        } catch (AuthenticationException e) {
            token.clear();
            request.setAttribute("msg", "用户或密码不正确！");
            return "login";
        }
    }
    @RequestMapping(value="/usersPage")
    public String usersPage(){
        return "user/users";
    }

    @RequestMapping("/rolesPage")
    public String rolesPage(){
        return "role/roles";
    }

    @RequestMapping("/resourcesPage")
    public String resourcesPage(){
        return "resources/resources";
    }

    @GetMapping("/403")
    public String forbidden(){
        return "403";
    }

    @RequestMapping("/goodsPage")
    public String findAllGoods(Model model){
    //    List<Goods> list = goodsService.findAll();
      //  model.addAttribute("goodsList", list);
        return "good/goodslist";
    }

    @Autowired
    private IGoodsTypeService goodsTypeService;
    @Autowired
    private IBannerService bannerService;
    @RequestMapping("/goodsIndex")
    public String goodsIndex(ModelMap map){
        List<GoodsType> list = goodsTypeService.findAllType();
        map.put("goodsTypeList",list);

        List<Banner> bannerList = bannerService.findAllShowBanner();
        map.put("bannerList",bannerList);
        return "good/goodsIndex";
    }
    /**
     * @GetMapping是一个组合注解 是@RequestMapping(method = RequestMethod.GET)的缩写
     *  @PostMapping是一个组合注解 是@RequestMapping(method = RequestMethod.POST)的缩写
     * @return
     */
    @GetMapping(value = "login?kickout=1")
    public String kickOut(){
        return "login";
    }

    @Autowired
    IGoodsService goodsService;
    @RequestMapping("goods/detail")
    public String findGoodsDetail(Integer goodsId,ModelMap model){
        Goods goods = goodsService.findById(goodsId);
        model.put("goods",goods);
        model.put("evaList", goods.getEvaList());
        return "good/userview/product_detail";
    }

    @RequestMapping("/cart")
    public String toCart(ModelMap model){

        return "good/userview/shopping_cart";
    }

    @Autowired
    ICartService cartService;
    @Autowired
    IAddressService addressService;
    /*跳转到订单页面*/
    @RequestMapping("/cart/preOrder")
    public String preOrder(Integer[] goodslist, ModelMap model){
        List<Cart> cartList=new ArrayList<Cart>();
        for (Integer i : goodslist) {
            Cart cart = cartService.findCartById(i);
            cartList.add(cart);
        }
        model.put("cartList",cartList);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        List<Address> addrList = addressService.findAddressByUserId(user.getId());
        model.put("addrList", addrList);
        return "good/userview/confirm_order";
    }

    @Autowired
    IOrderService orderService;
    /*跳转到支付页面*/
    @RequestMapping("/order/takeOrder")
    public String takeOrder(Integer[] goodslist,Integer addr,ModelMap model){
        List<Cart> cartList=new ArrayList<Cart>();
        List<OrderDetail> detailList=new ArrayList<OrderDetail>();
        Double totalPrice=0D;
        for (Integer i : goodslist) {
            Cart cart = cartService.findCartById(i);
            totalPrice+=cart.getCartNum()*cart.getCartGoods().getGoodsPrice();
            cartList.add(cart);
            OrderDetail detail=new OrderDetail(cart.getCartGoods(), cart.getCartGoods().getGoodsPrice()*cart.getCartNum(), cart.getCartNum());
            detailList.add(detail);
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Date orderDate=new Date();
        Address address = addressService.findAddresById(addr);
        String a=address.getAddrProvince()+address.getAddrCity()+address.getAddrArea()+address.getAddrDetail();
        Order order=new Order(user, orderDate, totalPrice, 1, address.getAddrNickname(), address.getAddrPhone(), a);
        order.setDetailList(detailList);
        Order takeOrder = orderService.takeOrder(order);
        for (Cart c : cartList) {
            Goods goods = goodsService.findById(c.getCartGoods().getGoodsId());
            goods.setGoodsNum(goods.getGoodsNum()-c.getCartNum());
            goods.setGoodsVolume(goods.getGoodsVolume()+c.getCartNum());
            goodsService.update(goods);
            cartService.deleteCart(c.getCartId());
        }
        model.put("order",takeOrder);
        return "good/userview/takeorder";
    }
}
