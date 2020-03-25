package com.converage.mapper.information;


import com.converage.architecture.dto.Pagination;
import com.converage.entity.information.Investigation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestigationMapper {

    List<Investigation> selectPageWithGoods(Pagination<Investigation> pagination);

    List<Investigation> selectPageWithUser(Pagination<Investigation> pagination);

}