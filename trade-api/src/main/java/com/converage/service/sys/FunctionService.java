package com.converage.service.sys;

import com.converage.architecture.service.BaseService;
import com.converage.utils.ValueCheckUtils;
import com.converage.entity.sys.FuncTreeNode;
import com.converage.entity.sys.Function;
import com.converage.mapper.sys.FunctionMapper;
import com.converage.utils.TreeNodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class FunctionService extends BaseService{

    @Autowired
    private FunctionMapper functionMapper;

    public FuncTreeNode queryFuncTree(String subscriberId) {
        List<FuncTreeNode> functionList = functionMapper.selectFuncList(subscriberId);
        FuncTreeNode funcTreeNode = TreeNodeUtils.generateFucTreeNode("0",functionList);
        return funcTreeNode;
    }


    public void deleteFuc(String fucId){
        Function function = selectOneById(fucId,Function.class);
        ValueCheckUtils.notEmpty(function,"未找到记录");
        delete(function);
    }
}
