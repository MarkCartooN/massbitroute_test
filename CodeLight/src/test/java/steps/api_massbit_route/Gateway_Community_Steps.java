package steps.api_massbit_route;

import com.google.gson.JsonObject;
import constants.Massbit_Route_Endpoint;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Step;
import org.junit.Assert;
import steps.UtilSteps;
import utilities.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Gateway_Community_Steps {

    public static String mbrid = "";
    public static String sid = "";
    public static JsonObject gateway_info;

    public Response hello(){
        Response response = SerenityRest.rest()
                .given()
                .contentType(ContentType.JSON.withCharset("UTF-8"))
                .header("mbrid", 1)
                .when()
                .post(UtilSteps.getAPIURL()+ Massbit_Route_Endpoint.HELLO);

        return response;
    }

    @Step
    public Gateway_Community_Steps should_be_able_to_say_hello(){

        Response response = hello();
        String response_body = response.getBody().asString();
        Log.info("response of hello is " + response_body);
        Assert.assertTrue(response.getStatusCode() == 200);
        Assert.assertFalse(response_body.isEmpty());

        mbrid = response_body;

        return this;
    }

    @Step
    public Gateway_Community_Steps should_be_able_to_login() throws IOException, InterruptedException {

        String body = "\n{\n" +
                "\t\"username\":\"massbit\",\n" +
                "\t\"password\":\"massbit123\"\n" +
                "}";

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().header("Content-Type", "application/json")
                .header("mbrid", mbrid)
                .uri(URI.create(UtilSteps.getAPIURL()+ Massbit_Route_Endpoint.LOGIN))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertTrue(response.statusCode()==200);
        Assert.assertFalse(response.body().toString().isEmpty());

        String res = response.body().toString();

        sid = JsonPath.from(res).getString("data.sid");

        Log.highlight("sid :" + sid);

        return this;
    }

    public Response add_new_gateway(String name, String blockchain, String zone, String network){
        String body = "{\n" +
                "    \"name\":\"" + name + "\",\n" +
                "    \"blockchain\":\"" + blockchain + "\",\n" +
                "    \"zone\":\"" + zone + "\",\n" +
                "    \"network\":\"" + network + "\",\n" +
                "    \"sid\":\"" + sid + "\"\n" +
                "}";

        Log.info(body);
        Log.info("url: " + UtilSteps.getAPIURL()+ Massbit_Route_Endpoint.CREATE_GATEWAY);

        Response response = SerenityRest.rest()
                .given()
                .header("Content-Type", "application/json").config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .header("mbrid", mbrid)
                .when()
                .body(body)
                .post(UtilSteps.getAPIURL()+ Massbit_Route_Endpoint.CREATE_GATEWAY);

        return response;
    }

    @Step
    public Gateway_Community_Steps should_be_able_to_add_new_gateway(String name, String blockchain, String zone, String network){

        Response response = add_new_gateway(name, blockchain, zone, network);
        String response_body = response.getBody().asString();
        Log.info("response of create api of " + blockchain + " is: " + response_body);

        Assert.assertTrue(response.getStatusCode() == 200);
        Assert.assertTrue(JsonPath.from(response_body).getBoolean("result"));
        Assert.assertFalse(JsonPath.from(response_body).getString("data.id").isEmpty());

        gateway_info = JsonPath.from(response_body).getObject("data", JsonObject.class);

        return this;
    }

    public String get_install_gateway_script(){

        String cmd_start = "bash -c \"$(curl -sSfL '";
        String url = "https://dapi.massbit.io/api/v1/gateway_install?";
        String id = "id=" + JsonPath.from(gateway_info.toString()).getString("id");
        String user_id = "&user_id=" + JsonPath.from(gateway_info.toString()).getString("user_id");
        String blockchain = "&blockchain=" + JsonPath.from(gateway_info.toString()).getString("blockchain");
        String network = "&network=" + JsonPath.from(gateway_info.toString()).getString("network");
        String zone = "&zone=" + JsonPath.from(gateway_info.toString()).getString("zone");
        String cmd_end = "')\"";

        String install_script = cmd_start + url + id + user_id + blockchain + network + zone + cmd_end;


        return install_script;
    }

}
