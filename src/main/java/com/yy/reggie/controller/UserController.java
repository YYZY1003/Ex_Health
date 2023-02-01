package com.yy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yy.reggie.common.R;
import com.yy.reggie.entity.User;
import com.yy.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号码
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //验证码
            String code = phone.substring(7, 11);
            log.info("验证码：{}", code);
            //保存验证码
//            session.setAttribute(phone, code);

            //将生成的验证码保存到redis中一分钟
            redisTemplate.opsForValue().set(phone,code,1, TimeUnit.MINUTES);

            return R.success("手机验证码发送成功");
        }
        return R.error("手机短信发送失败");
    }

    /**
     * 移动用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("登录：{}", map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从redis中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        //从Session中获取验证码
        //Object codeInSession = session.getAttribute(phone);
        //验证码比对
        if (codeInSession != null && codeInSession.equals(code)) {
            //对比成功，自动登录
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            //判断是否为新用户，新用户自动注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            //登录成功，存入session
            session.setAttribute("user",user.getId());
            //删除redis中的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");
    }
}
