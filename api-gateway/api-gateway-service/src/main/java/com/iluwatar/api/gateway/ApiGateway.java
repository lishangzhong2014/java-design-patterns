/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.api.gateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * The ApiGateway aggregates calls to microservices based on the needs of the individual clients.
 */
@RestController
public class ApiGateway {

  @Resource
  private ImageClient imageClient;

  @Resource
  private PriceClient priceClient;

  /**
   * Retrieves product information that desktop clients need
   * @return Product information for clients on a desktop
   */
  @RequestMapping("/desktop")
  public DesktopProduct getProductDesktop() {
    // Start the clock
    long start = System.currentTimeMillis();

    DesktopProduct desktopProduct = new DesktopProduct();
    desktopProduct.setImagePath(imageClient.getImagePath());
    desktopProduct.setPrice(priceClient.getPrice());

    // Print results, including elapsed time
    System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
    return desktopProduct;
  }

  /**
   * use https://spring.io/guides/gs/async-method/
   * @return
   */
  @RequestMapping("/desktop-async")
  public DesktopProduct getProductDesktopAsync() {
    // Start the clock
    long start = System.currentTimeMillis();

    CompletableFuture<String> imagePath = imageClient.getImagePathAsync();
    CompletableFuture<String> price = priceClient.getPriceAsync();
    CompletableFuture.allOf(imagePath, price).join();

    DesktopProduct desktopProduct = new DesktopProduct();
    try {
      desktopProduct.setImagePath(imagePath.get());
      desktopProduct.setPrice(price.get());
    } catch (Exception ex){
      ex.printStackTrace();
    }
    // Print results, including elapsed time
    System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
    return desktopProduct;
  }

  /**
   * Retrieves product information that mobile clients need
   * @return Product information for clients on a mobile device
   */
  @RequestMapping("/mobile")
  public MobileProduct getProductMobile() {
    MobileProduct mobileProduct = new MobileProduct();
    mobileProduct.setPrice(priceClient.getPrice());
    return mobileProduct;
  }
}
