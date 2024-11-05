package org.yzc.property1.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 智能合约交互工具类
 */

@Component
public class HttpUtils {
    private static String contractAddress;
    private static String contractName;
    private static String contractAbi;
    private static String transUrl;
    public static String ownerAddress;

    @Value("${system.contract.address}")
    private void setContractAddress(String _address){
        contractAddress = _address;
    }

    @Value("${system.contract.name}")
    private void setContractName(String _name){
        contractName = _name;
    }

    @Value("${system.contract.abi}")
    private void setContractAbi(String _abi){
        contractAbi = _abi;
    }

    @Value("${system.fisco.trans.url}")
    private void setTransUrl(String _url){
        transUrl = _url;
    }

    @Value("${system.contract.owner.address}")
    private void setOwnerAddress(String _addr){
        ownerAddress = _addr;
    }

    private static OkHttpClient client = new OkHttpClient();

    private static String commonReq(String funcName, List funcParam) {
        return HttpUtils.commonReq(ownerAddress,funcName,funcParam);
    }
    /**
     * 基础数据请求方法
     *
     * @param userAddress
     * @param funcName
     * @param funcParam
     * @return cn.hutool.core.lang.Dict
     * @author huangyu@ivinsight.com
     * @date 2022/7/1
     */
    static private String commonReq(String userAddress, String funcName, List funcParam) {
        JSONArray abiJSON = JSON.parseArray(contractAbi);
        JSONObject data = new JSONObject();
        data.put("groupId", "1");
        data.put("user", userAddress);
        data.put("contractName", contractName);
        data.put("version", "");
        data.put("funcName", funcName);
        data.put("funcParam", funcParam);
        data.put("contractAddress", contractAddress);
        data.put("contractAbi", abiJSON);
        data.put("useAes", false);
        data.put("useCns", false);
        data.put("cnsName", "");

        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toJSONString());
            Request request = new Request.Builder()
                    .url(transUrl)
                    .post(requestBody) //post请求
                    .build();
            final Call call = client.newCall(request);
            Response response = call.execute();
            return response.body().string();
        }catch (IOException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }

    /**
     * 申请并获取webase上的测试用户地址
     * @param funcParam
     * @return
     */
    static public String commonReq(String funcParam) {
        try{
            Request request = new Request.Builder()
                    .get()
                    .url("http://172.16.0.13:5002/WeBASE-Front/privateKey?useAes=false&type=0&userName="+funcParam)
                    .build();
            final Call call = client.newCall(request);
            Response response = call.execute();
            String result=response.body().string();
            JSONObject jsonObject = JSON.parseObject(result);
            Map<String, String> stringStringMap = JSONObject.parseObject(jsonObject.toJSONString(), new TypeReference<Map<String, String>>() {
            });
           String address=(stringStringMap.get("address"));
            System.out.println(address);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;

    }
    /**
     * 写合约
     * @param userAddress 用户地址
     * @param funcName 合约方法名
     * @param funcParam 合约方法参数列表
     * @return 合约回执
     */
    public static JSONObject writeContract(String userAddress,String funcName,List funcParam) {
        String result = HttpUtils.commonReq(userAddress,funcName, funcParam);
        JSONObject _obj = JSON.parseObject(result);
        if (_obj.getIntValue("code") > 0 || !_obj.get("status").equals("0x0")) {
            System.out.println(_obj);
            return null;
        }
        return _obj;
    }

    /**
     * 写合约（合约管理者）
     * @param funcName 合约方法名
     * @param funcParam 合约方法参数列表
     * @return 合约回执
     */
    public static JSONObject writeContract(String funcName,List funcParam) {
        return writeContract(ownerAddress,funcName,funcParam);
    }

    /**
     * 读合约
     * @param contractAddress 合约地址
     * @param funcName 合约方法名
     * @return JSON格式数组
     */
    public static JSONArray readContract(String contractAddress,String funcName){
        String result = HttpUtils.commonReq(contractAddress,funcName, new ArrayList());
        JSONArray obj = JSON.parseArray(result);
        return obj;
    }

    /**
     * 读合约（管理员）
     * @param funcName 合约名
     * @return JSON格式数组
     */
    public static JSONArray readContract(String funcName){
        String result = HttpUtils.commonReq(ownerAddress,funcName, new ArrayList());
        System.out.println(result);
        JSONArray _obj = JSON.parseArray(result);
        return _obj;
    }

    /**
     * 读合约
     * @param funcName
     * @param funcparam 合约参数
     * @return JSON格式数组
     */
    public static JSONArray readContract(String funcName,List funcparam){
        String result =HttpUtils.commonReq(ownerAddress,funcName,funcparam);
        System.out.println(result);
        JSONArray _obj = JSON.parseArray(result);
        return _obj;
    }
}
