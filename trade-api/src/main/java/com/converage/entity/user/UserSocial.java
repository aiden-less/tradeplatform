package com.converage.entity.user;

import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@Data
public class UserSocial {
    private String userAccount;
    private String headPic;
    private String registerTime;
    private Integer socialNum;
    private Integer packageNum;
    private BigDecimal socialAchievement = BigDecimal.ZERO;

    public UserSocial() {
    }

    public UserSocial(User user, Integer socialNum, Integer packageNum, BigDecimal socialAchievement) {
        this.userAccount = user.getUserAccount();
        this.headPic = user.getHeadPictureUrl();
        this.registerTime = new SimpleDateFormat("yyyy-MM-dd").format(user.getCreateTime().getTime());
        this.socialNum = socialNum;
        this.packageNum = packageNum;
        this.socialAchievement = socialAchievement;
    }

}
