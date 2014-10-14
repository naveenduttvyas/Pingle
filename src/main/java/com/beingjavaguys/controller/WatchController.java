package com.beingjavaguys.controller;

import com.beingjavaguys.model.Brand;
import com.beingjavaguys.model.PWatch;
import com.beingjavaguys.services.DataServices;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nvyas
 * Date: 9/26/14
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/watches")
public class WatchController {

    @Autowired
    DataServices dataServices;

    static final Logger logger = Logger.getLogger(WatchController.class);

    @RequestMapping(value = "/watchbrands", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAllWatchesBrands() {
        String result = "";
        List<String> brandList = new ArrayList<String>();
        SortedSet<String> finalBrandlist = null;
        try {
            List<PWatch> mobileBrandList = dataServices.getAllWatchBrandList();
            for (PWatch product : mobileBrandList) {
                if (product.getBrand() != null && !product.getBrand().equalsIgnoreCase("") && product.getProductName().toLowerCase().contains("men") && !product.getProductName().toLowerCase().contains("women")) {
                    brandList.add(product.getBrand());
                }
            }
            if (brandList.size() > 0) {
                Collections.sort(brandList, new Comparator<String>() {
                    @Override
                    public int compare(final String object1, final String object2) {
                        return object1.compareTo(object2);
                    }
                });
            }

            finalBrandlist = new TreeSet<String>(brandList);

            List<Brand> brnBrand = new ArrayList<Brand>();
            for (String brand1 : finalBrandlist) {
                Brand brand11 = new Brand();
                brand11.setBrand(brand1);
                brnBrand.add(brand11);

            }

            Gson gson = new Gson();
            result = gson.toJson(brnBrand);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    @RequestMapping(value = "/menwatches", method = RequestMethod.GET)
    public
    @ResponseBody
    List<PWatch> getAllMenWatches() {

        List<PWatch> pWatchList = null;
        try {
            pWatchList = dataServices.getAllWatchList();

            for (Iterator<PWatch> iter = pWatchList.iterator(); iter.hasNext(); ) {
                PWatch p = iter.next();

                if (!p.getProductName().toLowerCase().contains("men")) {
                    iter.remove();
                } else if (p.getProductName().toLowerCase().contains("women")) {
                    iter.remove();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pWatchList;
    }

    @RequestMapping(value = "filterwatches/{brands}/{color}/{price}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String mobileBrands(@PathVariable("brands") String brandNames, @PathVariable("color") String color,
                               @PathVariable("price") String price) throws Exception {

        System.out.println("mobileBrands for brandNames- " + brandNames.toString() + " - color - " + color + " - price - " + price);
        String result = "";

        JSONArray obj = new JSONArray(brandNames);
        JSONArray obj1 = new JSONArray(color);
        JSONArray obj2 = new JSONArray(price);

        List<PWatch> brnBrand = dataServices.getWatchQueryResult(obj, obj1, obj2);

        for (Iterator<PWatch> iter = brnBrand.iterator(); iter.hasNext(); ) {
            PWatch p = iter.next();

            if (!p.getProductName().toLowerCase().contains("men")) {
                iter.remove();
            } else if (p.getProductName().toLowerCase().contains("women")) {
                iter.remove();
            }

        }

        Gson gson = new Gson();
        result = gson.toJson(brnBrand);

        return result;
    }

    @RequestMapping(value = "compare/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String compareProductPrice(@PathVariable("key") String key) throws Exception {

        System.out.println("compareProductPrice for - " + key);
        String result = "";
        List<PWatch> getOMGPrice = null;
        try {
            getOMGPrice = dataServices.compareWatchProductPrice(key.replaceAll("[^a-zA-Z0-9 ]", ""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        result = gson.toJson(getOMGPrice.subList(0, 6));

        return result;
    }
}
