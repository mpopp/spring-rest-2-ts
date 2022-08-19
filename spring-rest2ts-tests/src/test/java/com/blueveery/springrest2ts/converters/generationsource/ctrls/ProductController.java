package com.blueveery.springrest2ts.converters.generationsource.ctrls;

import com.blueveery.springrest2ts.converters.generationsource.enums.ProductType;
import com.blueveery.springrest2ts.converters.generationsource.wrapper.SingleResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/product")
public class ProductController {

  /*
    This endpoint will make the test fail when the ModelClassesCondition was not set up or does not include the Enum type.
*/
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public SingleResult<ProductType> getProductType(String id) {
    return SingleResult.from(ProductType.ECONOMIC);
  }

/*
// With this endpoint, the test works even if no ModelClassesCondition was setup.
@RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ProductType getProductType(String id) {
    return ProductType.ECONOMIC;
  }
*/

}