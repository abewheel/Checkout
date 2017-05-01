package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "checkout-mobilehub-1017081688-Inventory")

public class InventoryDO {
    private String _productID;
    private String _amount;
    private Double _price;

    @DynamoDBHashKey(attributeName = "productID")
    @DynamoDBAttribute(attributeName = "productID")
    public String getProductID() {
        return _productID;
    }

    public void setProductID(final String _productID) {
        this._productID = _productID;
    }
    @DynamoDBAttribute(attributeName = "Amount")
    public String getAmount() {
        return _amount;
    }

    public void setAmount(final String _amount) {
        this._amount = _amount;
    }
    @DynamoDBAttribute(attributeName = "Price")
    public Double getPrice() {
        return _price;
    }

    public void setPrice(final Double _price) {
        this._price = _price;
    }

}
