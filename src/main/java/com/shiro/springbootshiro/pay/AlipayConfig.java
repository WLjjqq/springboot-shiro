package com.shiro.springbootshiro.pay;
import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016091300504379";

    // 商户私钥，您的PKCS8格式RSA2私钥    （自己生成的私钥，记得去除空格，换行符）
    public static String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC1OQBO5rgfy+gGLHXfG3o68ZWGqRUhFAgtu0IJwTLk0DBH5tF2kzBJIe4shGW90eb0KUeiSkTKiaflzmj3DrxIuDODtYDijdsyQWJUU60HoiebMTkujDR19OWBZ8M8wOQY6LvSsxmh2Y6A27rUtgHhyAR5zapamEzFPipEemUudEQ3pJbBoD5HPgPbfF2XJfKnlKQZs20vWqn1NoKlVgFUjBGJUAgblX6GimDvDySoTuHotutekii54pWAFoFIspWJWE7m/V97V+MWOaftMrVQJFt5RoGawJ7xyD+wvL9A7PwV+C3yTTraGV4bqc1Tjf9DUC5GGtvKFKs8J53ksDNBAgMBAAECggEBAKQsJm5UL5uGkwT8xC/BacL6VrZueNjFl/8t9E53+s41GHgaz8l24Dhwh59GthD3lh29Q8rvM1C00iirDIY8kC/kx65bAI69akUl3Jl+UHNo4C6EskPL+j6eBEhuIv3n1PwH4xem7uKj/6gW5zOKSzwqgnuB6QE3ldzeS1ZL91vTqm1hpA13OIzwsCYQC9tldGqJ5zFXg5w8Eo80xDp7KZkfz9jLuGGZc34uKxXyW4FXdzPGS/s9NFuwEvcYygfI14ndB8wrQscV8k69EBl57sHcpBP9fMO6J3AAd0A5gN7FRayQNcx0ATxcl+gz0qYqCIubRlVmcDWr7y1q+nE9NB0CgYEA7O0tXEy6y1wx2azZhhdNEE5M7tCDWcTsrlPziqZhMlyHuY+jX3IvkhErPt8tQhrXHwLz2mTx9WYw5Gx1kLuekbW0YTNdLacxkh9Ue2wa1vB3pGPoKhMBaOpsw9cOBLODrbVpqYz3zoLou7MFQZCH96vvcO6cc/y5IaKn62Vay9sCgYEAw8/SrC+f+vT6CqaKK2x4/kopTDV5pkQjJIf3fe0DTfyTwJMGDT9kPMF116+k0ph+TwcliZsgROGKMTHLqljZW/Q355uug7xI38vlSWICQsfi8zs4UQxw1ET1B0NVQLGG5qJh2DqI8D5PrKVhg6a3rg+r06cdCVmm+WwPRkS+1hMCgYEAmFXfZotHR14eB1GmAxuURzmxKZQUAHIno+cCnlFgCVuJQPxkFQh8IbS8U453sRtE2gGx/OgO0rREF3rNFKQtzo5ATocSEDqCGuveDAV0NGMk6iP6sKLLs0OXb0wlDUzHC7erGoMzCisNrTHr3T4qzkpUiA5Dtif2ePP2d9oRSSUCgYEAvoFmNRGMsys+TbhjuwWo3bYnYbaxKRsnmbYTCtfaHDi9Q2GHRMJE8ntB/Fstn5qvYJHSaoObLIjF20DYJl6U8kqzTUmAyzgXKm0EIZYSHwi7++rEys2wxERmo+9VdUCCv8aCLU4dxqbI+25XZi+Aiv9CLARtUph/xDDm13WwuTMCgYAseLlZ6arMkwbE+tWlHeVB02jRhPKdXkZ/XdeZqZWq9cyWKggH/L7mKpY0h8NSEVv8tLcZ01p0JzZnBfAHIiqsC6HoH1Tz9Qqmzlg6TStnI8QiiNToJThQSzqWj4sm81j+0sR4oiUKcHGg762Q/g+8p7iJTh3bH2qUUpK55JJG/Q==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp2vE/iQ01uIH+IyML07iARpglpWuJUIsQZAesj8vh0MXxTBzOSrMUCndrA8lrSoY61Jni921K3jH8gH0p9iGZlFbm8uSAsXnHH1jhLWgSAxzBa2+wiAJ3A0v2CIypONSmzrdtgUBD6A4PMwJOBN1H73U7GUVQpctobM3sVJ9AtWnuxMls/9Naox/I3ANGzZlTPxFvxxQjBwUSXI8nY8rjJOXjdb3CFurMMP/dV7I23tyaFFUQeKICJNWMKzDILJ7xntvkwM20zY8dwSiYISc1waU4BAIHJcDtvQsY/d3zaWviLCk+pFy7j9qhM2N4rr+M7He4Cbd7nqaz1O2F86adQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/alipay/notify_url";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/order/return_url";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "E:\\pojie\\log";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
