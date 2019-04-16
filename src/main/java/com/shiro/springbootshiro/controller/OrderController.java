package com.shiro.springbootshiro.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.shiro.springbootshiro.bean.Order;
import com.shiro.springbootshiro.bean.OrderDetail;
import com.shiro.springbootshiro.pay.AlipayConfig;
import com.shiro.springbootshiro.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 作用：支付宝的支付
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    IOrderService orderService;
    @RequestMapping("toPay")
    public String toPayFor(String orderId, HttpServletResponse response){
        //支付宝的一些参数信息
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        Order order = orderService.findOrderById(orderId);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no=null;
        //付款金额，必填
        String total_amount=null;
        //订单名称，必填
        String subject=null;
        try {
            out_trade_no = new String(orderId.getBytes("ISO-8859-1"),"UTF-8");
            total_amount = new String((order.getOrderPrice()+"").getBytes("ISO-8859-1"),"UTF-8");
            subject = new String("思语缘贸易有限公司");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        //商品描述，可空
        List<OrderDetail> detailList = order.getDetailList();
        String body = null;
        for (OrderDetail o : detailList) {
            body=body+o.getDetailGoods().getGoodsName()+",";
        }
        body=body.substring(0, body.length()-1)+"等商品";

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求
        try {
            String result = alipayClient.pageExecute(alipayRequest).getBody();
            //输出
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print(result);
        } catch (AlipayApiException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @RequestMapping("/notify_url")
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response){
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put(name, valueStr);
        }

        boolean signVerified=false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e1) {
            e1.printStackTrace();
        } //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——

		/* 实际验证过程建议商户务必添加以下校验：
		1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
		3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		4、验证app_id是否为该商户本身。
		*/
        PrintWriter out=null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(signVerified) {//验证成功
            //商户订单号
            String out_trade_no="";
            //交易状态
            String trade_status="";
            try {
                out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //支付宝交易号
                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

                trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(trade_status.equals("TRADE_FINISHED")||trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                Integer rs = orderService.payForOrder(out_trade_no);
                if(rs>0){
                    System.out.println("异步通知支付成功");
                }
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
            }

            out.println("success");

        }else {//验证失败
            out.println("fail");

            //调试用，写文本函数记录程序运行情况是否正常
            //String sWord = AlipaySignature.getSignCheckContentV1(params);
            //AlipayConfig.logResult(sWord);
        }
    }
    @RequestMapping("/return_url")
    public String returnUrl(String out_trade_no){
        Integer rs = orderService.payForOrder(out_trade_no);
        if(rs>0){
            System.out.println("同步通知支付成功");
        }
        return "/alipay/return_url";
    }
}
