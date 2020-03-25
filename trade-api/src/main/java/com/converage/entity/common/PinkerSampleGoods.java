package com.converage.entity.common;

import com.converage.entity.shop.Beauty;
import lombok.Data;
import java.io.Serializable;

@Data
public class PinkerSampleGoods implements Serializable {

    private static final long serialVersionUID = 6015152465150569265L;

    private Beauty goods;
}
