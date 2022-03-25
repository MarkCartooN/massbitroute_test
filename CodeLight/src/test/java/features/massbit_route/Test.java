package features.massbit_route;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.runner.RunWith;
import steps.UtilSteps;
import steps.api_massbit_route.Community_Nodes_Steps;
import steps.api_massbit_route.Decentralized_API_Steps;
import steps.api_massbit_route.Gateway_Community_Steps;
import utilities.Log;

import java.io.IOException;

@RunWith(SerenityRunner.class)
public class Test {

//    public static void main(String []args) throws IOException {
//        String a = "curl -s -H 'Content-Type: application/json' -H 'Authorization: Nitroute.dev/mbr/node/088d03ad-3125-4afe-a116-0a51f5e45304/verify\n";
//        Log.highlight(a);
//        UtilSteps.runCommand(a);
//    }

    @Steps
    Decentralized_API_Steps decentralized_api_steps;

    @Steps
    Community_Nodes_Steps community_nodes_steps;

    @Steps
    Gateway_Community_Steps gateway_community_steps;

    @org.junit.Test

    public void cleanup_dapi() throws IOException, InterruptedException {
        decentralized_api_steps.should_be_able_to_say_hello()
                               .should_be_able_to_login()
                               .cleanup_dapi();
    }

    @org.junit.Test
    public void cleanup_gateway() throws IOException, InterruptedException {
        gateway_community_steps.should_be_able_to_say_hello()
                               .should_be_able_to_login();

        gateway_community_steps.cleanup_gateway();
    }

    @org.junit.Test
    public void cleanup_node() throws IOException, InterruptedException {
        community_nodes_steps.should_be_able_to_say_hello()
                             .should_be_able_to_login();

        community_nodes_steps.cleanup_node();
    }



}
