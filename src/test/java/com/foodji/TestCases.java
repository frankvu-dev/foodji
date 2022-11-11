package com.foodji;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class TestCases {
    @Test
    public void verifyEachMachineHasAtLeastAProduct() {
        // get machine IDs
        RestAssured.baseURI = "https://stage-2-master.foodji.io/machines/";
        RequestSpecification httpRequest = RestAssured.given();
        Response machineListResponse = httpRequest.get("list");
        Assert.assertEquals(machineListResponse.statusCode(), 200);

        JsonPath jsonPath = new JsonPath(machineListResponse.asString());

        ArrayList<String> machineID = jsonPath.get("data.id");

        // verify each machine has a product
        for (String id : machineID) {
            Response response = httpRequest.get(id);
            JsonPath jsp = new JsonPath(response.asString());
            Assert.assertTrue(jsp.getList("data.machineProducts.name").size() > 0);
        }
    }

    @Test
    public void verifyCategoryIDIsUnique() {
        RestAssured.baseURI = "https://stage-2-master.foodji.io/";
        RequestSpecification httpRequest = RestAssured.given();
        Response categoryResponse = httpRequest.get("categories");
        Assert.assertEquals(categoryResponse.statusCode(), 200);

        JsonPath jsonPath = new JsonPath(categoryResponse.asString());

        ArrayList<String> categoryID = jsonPath.get("data.id");
        Assert.assertTrue((verifyAllUnique(categoryID)));
    }

    private boolean verifyAllUnique(ArrayList<String> list) {
        for(String s : list) {
            if(!s.equals(list.get(0))) {
                return true;
            }
        }
        return false;
    }
}
