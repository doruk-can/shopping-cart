package com.doruksorg.tycase.util;

import java.util.List;

public class Constants {
    public static class CategoryIds {
        public static final int VAS_ITEM_CATEGORY_ID = 3242;
        public static final int DIGITAL_ITEM_CATEGORY_ID = 7889;
        public static final int DISCOUNTED_CATEGORY_ID = 3003;
        public static final int FURNITURE_ID = 1001;
        public static final int ELECTRONICS_ID = 3004;

    }

    public static class SellerIds {
        public static final int VAS_ITEM_SELLER_ID = 5003;
    }

    public static class PromotionIds {
        public static final int SAME_SELLER_PROMOTION_ID = 9909;
        public static final int CATEGORY_PROMOTION_ID = 5676;
        public static final int TOTAL_PRICE_PROMOTION_ID = 1232;
    }

    public static class CartValidations {
        public static final int MAX_QUANTITY_PER_DIGITAL_ITEM = 5;
        public static final int MAX_VAS_ITEMS_IN_CART = 3;
        public static final int MAX_UNIQUE_ITEMS_IN_CART = 10;
        public static final int MAX_TOTAL_QUANTITY_IN_CART = 30;
        public static final int MAX_TOTAL_CART_PRICE = 500000;
        public static final List<Integer> VAS_ITEM_COMPATIBLE_CATEGORY_ID_LIST = List.of(Constants.CategoryIds.FURNITURE_ID,
                Constants.CategoryIds.ELECTRONICS_ID);
    }
}

