package com.converage.controller.app;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.ResultUtils;
import com.converage.client.RedisClient;
import com.converage.client.netty.TradeMatchClient;
import com.converage.constance.RedisKeyConst;
import com.converage.service.common.AliOSSBusiness;
import com.converage.service.common.AppUpgradeService;
import com.converage.service.common.GlobalConfigService;
import com.converage.utils.IPUtils;
import com.converage.utils.ValueCheckUtils;
import com.converage.utils.VertifyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("app/common")
public class AppCommonController {

    @Autowired
    private AppUpgradeService appUpgradeService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private AliOSSBusiness aliOSSBusiness;

    @Autowired
    private TradeMatchClient tradeMatchClient;

    /**
     * 获取资源数据
     */
    @GetMapping("resource/{key}")
    public Result<?> resource(@PathVariable String key) {
        Map<String, Object> map = ImmutableMap.of(
                "url", globalConfigService.get(GlobalConfigService.Enum.REDIRECT_AFTER_REGISTER)
        );
        return ResultUtils.success(map);
    }

    /**
     * app 检查版本
     */
    @RequestMapping("checkVer")
    public Result<?> checkVersion(String device, String version) {
        if (StringUtils.isBlank(device) || StringUtils.isBlank(version)) {
            return ResultUtils.error("缺少必要参数");
        }
        return ResultUtils.success(appUpgradeService.checkVersion(device, version));
    }


    /**
     * 获取图形验证码
     *
     * @param request
     * @returnArticle
     */
    @GetMapping("vertifyPic")
    public Result<?> vertifyPic(HttpServletRequest request) {
        String redisKey = RedisKeyConst.VERTIFY_PICTURE + IPUtils.getClientIP(request);
        Object[] objs = VertifyUtils.createImage();
        String randomStr = (String) objs[0];
        redisClient.delete(redisKey);
        redisClient.setForTimeMIN(redisKey, randomStr, 5);
        Object o = new BASE64Encoder().encode((byte[]) objs[1]);
        return ResultUtils.success(o);
    }

    @RequestMapping("upload")
    public Result<?> upload(MultipartFile file) {
        ValueCheckUtils.notEmpty(file, "请选择文件");
        String url = aliOSSBusiness.uploadCommonPic(file);
        Map<String, Object> map = ImmutableMap.of("url", url);
        return ResultUtils.success(map);
    }


}
