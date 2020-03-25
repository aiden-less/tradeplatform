package com.converage.utils;

import com.converage.entity.sys.FuncTreeNode;
import com.converage.entity.sys.UserTreeNode;
import com.converage.entity.user.User;
import com.converage.entity.user.UserSocial;
import com.converage.entity.work.ApiTreeNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TreeNodeUtils {


    public static FuncTreeNode generateFucTreeNode(String roleId, List<FuncTreeNode> list) {
        FuncTreeNode current_node = new FuncTreeNode();
        for (FuncTreeNode item : list) {
            if (roleId.equals(item.getId())) {
                current_node = item;
                break;
            }
        }

        List<FuncTreeNode> childrenFuncTreeNode = new ArrayList<>();
        for (FuncTreeNode item : list) {
            if (roleId.equals(item.getPid())) {
                childrenFuncTreeNode.add(item);
            }
        }

        for (FuncTreeNode item : childrenFuncTreeNode) {
            FuncTreeNode node = generateFucTreeNode(item.getId(), list);
            current_node.getChildren().add(node);
        }
        return current_node;
    }

    public static ApiTreeNode generateApiTreeNode(String roleId, List<ApiTreeNode> list) {
        ApiTreeNode current_node = new ApiTreeNode();
        for (ApiTreeNode item : list) {
            if (roleId.equals(item.getId())) {
                current_node = item;
                break;
            }
        }

        List<ApiTreeNode> childrenFuncTreeNode = new ArrayList<>();
        for (ApiTreeNode item : list) {
            if (roleId.equals(item.getPid())) {
                childrenFuncTreeNode.add(item);
            }
        }

        for (ApiTreeNode item : childrenFuncTreeNode) {
            ApiTreeNode node = generateApiTreeNode(item.getId(), list);
            current_node.getChildren().add(node);
        }

        return current_node;
    }

    public static UserTreeNode generateUserTreeNode(String userId, List<UserTreeNode> list, BigDecimal usdtTCNYPrice) {
        UserTreeNode current_node = new UserTreeNode();
        for (UserTreeNode item : list) {
            if (userId.equals(item.getId())) {
                BigDecimal socialAchievement = BigDecimal.ZERO;
                Integer socialNum = 0;
                if (!"1".equals(userId)) {

                    UserSocial userSocial = CacheUtils.getSocialMap(userId);
                    socialAchievement = userSocial.getSocialAchievement();
                    socialNum = userSocial.getSocialNum();
                }
                item.setLabel(item.getLabel() + " --- " + "业绩：" + socialAchievement);
                item.setLabel(item.getLabel() + " --- " + "社群：" + socialNum);
                current_node = item;
                break;
            }
        }

        List<UserTreeNode> childrenFuncTreeNode = new ArrayList<>();
        for (UserTreeNode item : list) {
            if (userId.equals(item.getInviteId())) {


                childrenFuncTreeNode.add(item);
            }
        }

        for (UserTreeNode item : childrenFuncTreeNode) {
            UserTreeNode node = generateUserTreeNode(item.getId(), list, usdtTCNYPrice);
            current_node.getChildren().add(node);
        }
        return current_node;

    }

    public static void filterUserDownTreeNode(String userId, List<User> users, List<User> userList) {
        for (User user : users) {
            if (userId.equals(user.getInviteId())) {
                userList.add(user);
                filterUserDownTreeNode(user.getId(), users, userList);
            }
        }
    }
}
