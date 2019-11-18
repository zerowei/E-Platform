package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.CartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartListVO;
import com.mmall.vo.CartVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("CartService")
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    public ReturnResponse<CartListVO> addCart(Integer userId, Integer productId, Integer count) {

        if (productId == null || userId == null) return ReturnResponse.ReturnError(StatusCode.ILLEGAL_PARAMETERS.getCode(), StatusCode.ILLEGAL_PARAMETERS.getName());
        Cart cart = cartMapper.checkCartByUserProductId(userId, productId);
        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setProductId(productId);
            newCart.setUserId(userId);
            newCart.setChecked(Const.CHECKED);
            newCart.setQuantity(count);
            cartMapper.insertSelective(newCart);
        } else {
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return searchCarts(userId);
    }

    public ReturnResponse<CartListVO> updateCart(Integer userId, Integer productId, Integer count) {

        if (productId == null || userId == null) return ReturnResponse.ReturnError(StatusCode.ILLEGAL_PARAMETERS.getCode(), StatusCode.ILLEGAL_PARAMETERS.getName());
        Cart cart = cartMapper.checkCartByUserProductId(userId, productId);
        if (cart == null) {
            return ReturnResponse.ReturnErrorByMessage("查询此商品相关购物车失败");
        } else {
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return searchCarts(userId);
    }

    public ReturnResponse<CartListVO> deleteCart(Integer userId, String productIds) {

        if (StringUtils.isBlank(productIds) || userId == null) return ReturnResponse.ReturnError(StatusCode.ILLEGAL_PARAMETERS.getCode(), StatusCode.ILLEGAL_PARAMETERS.getName());
        String[] products = productIds.split(",");
        List<String> temp = new ArrayList<>(Arrays.asList(products));
        List<Integer> productIdList = temp.stream().map(Integer::parseInt).collect(Collectors.toList());
        int count = cartMapper.deleteByProductIdList(userId, productIdList);
        if (count == 0) return ReturnResponse.ReturnErrorByMessage("删除购物车中商品失败");
        return searchCarts(userId);
    }

    public ReturnResponse<CartListVO> searchCarts(Integer userId) {
        CartListVO cartListVO = getCartListVO(userId);
        return ReturnResponse.ReturnSuccessByData(cartListVO);
    }

    public ReturnResponse<CartListVO> allSelects(Integer userId, Integer productId, Integer checked) {
        int count = cartMapper.allSelects(userId, productId, checked);
        if (count == 0) return ReturnResponse.ReturnErrorByMessage("更新选择失败");
        return searchCarts(userId);
    }

    public ReturnResponse<Integer> productQuantities(Integer userId) {
        int count = cartMapper.productQuantities(userId);
        return ReturnResponse.ReturnSuccessByData(count);
    }

    private CartListVO getCartListVO(Integer userId) {
        CartListVO cartListVO = new CartListVO();
        List<CartVO> cartVOs = new ArrayList<>();
        BigDecimal totalPrice = new BigDecimal("0");

        List<Cart> carts = cartMapper.getCartsByUserId(userId);
        if (carts.size() != 0) {
            for (Cart cart : carts) {
                CartVO cartVO = new CartVO();
                cartVO.setId(cart.getId());
                cartVO.setProductId(cart.getProductId());
                cartVO.setUserId(cart.getUserId());
                cartVO.setChecked(cart.getChecked());

                Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
                cartVO.setProductName(product.getName());
                cartVO.setProductMainImage(product.getMainImage());
                cartVO.setProductPrice(product.getPrice());
                cartVO.setProductStatus(product.getStatus());
                cartVO.setProductStock(product.getStock());
                cartVO.setProductSubtitle(product.getSubtitle());

                int finalQuantity;
                if (product.getStock() >= cart.getQuantity()) {
                    finalQuantity = cart.getQuantity();
                    cartVO.setOverStock(Const.UnderLIMIT);
                } else {
                    Cart temp = new Cart();
                    temp.setId(cart.getId());
                    temp.setQuantity(product.getStock());
                    cartMapper.updateByPrimaryKeySelective(temp);
                    cartVO.setOverStock(Const.OVERLIMIT);
                    finalQuantity = product.getStock();
                }
                cartVO.setQuantity(finalQuantity);
                cartVO.setProductTotalPrice(BigDecimalUtil.multiply(cartVO.getProductPrice().doubleValue(), cartVO.getQuantity()));

                if (cart.getChecked().equals(Const.CHECKED)) {
                    totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), cartVO.getProductTotalPrice().doubleValue());
                }
                cartVOs.add(cartVO);
            }
        }

        cartListVO.setCartListVO(cartVOs);
        cartListVO.setTotalPrice(totalPrice);
        if (isAllChecked(userId)) cartListVO.setAllChecked(true);
        else cartListVO.setAllChecked(false);
        cartListVO.setImagePrefix(PropertiesUtil.getProperties("ftp.server.http.prefix"));
        return cartListVO;

    }

    private boolean isAllChecked(Integer userId) {
        int count = cartMapper.isAllChecked(userId);
        return count == 0;
    }
}
