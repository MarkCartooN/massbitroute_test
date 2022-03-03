package simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import steps.api_massbit_route.Community_Nodes_Steps;
import steps.api_massbit_route.Decentralized_API_Steps;
import steps.api_massbit_route.Gateway_Community_Steps;
import utilities.Log;

@RunWith(SerenityRunner.class)
public class TrafficSimulator {

	public static List<NodeInfo>  listGateways = new ArrayList<>(); ;
    public static List<NodeInfo> listNodes = new ArrayList<>(); ;

    @Steps
    Gateway_Community_Steps gateway_community_steps;

    @Steps
    Decentralized_API_Steps decentralized_api_steps;

    @Steps
    Community_Nodes_Steps community_nodes_steps;

    @Test
    public void simulate_traffic() throws IOException, InterruptedException {
        decentralized_api_steps.should_be_able_to_say_hello();
        decentralized_api_steps.should_be_able_to_login();
        long cycle = 0;
        Random random = new Random(123123);
        while(true) {
        	//Reload node lists and gateway list every 10 cycles
        	if (cycle % 10 == 0) {
        		listNodes = getAvailableNodes();
        		listGateways = getAvailableGateways();
        	}
            for (NodeInfo node : listNodes) {  
            	//decentralized_api_steps.should_be_able_to_create_api("traffic_node_test", blockchain, "mainnet");
                //decentralized_api_steps.should_be_able_to_add_entrypoint("MASSBIT");
                decentralized_api_steps.send_api_request_direct_to_node(node.getBlockchain(), node.getId(), node.getApiKey());
            }
            for (NodeInfo gw : listGateways) {
                //decentralized_api_steps.should_be_able_to_create_api("traffic_gateway_test", blockchain, "mainnet");
                //decentralized_api_steps.should_be_able_to_add_entrypoint("MASSBIT");
            	decentralized_api_steps.send_api_request_direct_to_gateway(gw.getBlockchain(), gw.getIp());
            }
            //Sleep for maximize 60s
            long sleepTime = random.nextLong() % 60000;
            Log.info(String.format("Sleep for %d s...", sleepTime));
            Thread.sleep(sleepTime);
        }
    }
    private List<NodeInfo> getAvailableNodes() {
    	List<NodeInfo> listNodes = new ArrayList<NodeInfo>();
    	List<List<String>> list_nodes = community_nodes_steps.get_all_node_in_massbit();
    	if (list_nodes != null && list_nodes.size() > 0) {
        	for (List<String> list : list_nodes) {
        		String blockchain = list.get(2);
                String id = list.get(0);
                String x_api_key = list.get(7);
                int status = Integer.parseInt(list.get(8));
                int approved = Integer.parseInt(list.get(9));
                if (status == 1 && approved == 1) {
                	listNodes.add(new NodeInfo(blockchain, id, x_api_key));
                }
        	}
        }
    	Log.info(String.format("Avaiable nodes: %d, Active nodes: %d", list_nodes.size(), listNodes.size()));
    	return listNodes;
    }
    private List<NodeInfo> getAvailableGateways() {
    	List<List<String>> list_gateway = gateway_community_steps.get_all_gateway_in_massbit();
        List<NodeInfo> listGateWays = new ArrayList<NodeInfo>();
        if (list_gateway != null && list_gateway.size() > 0) {
        	for (List<String> list : list_gateway) {
        		String blockchain = list.get(2);
                String ip = list.get(4);
                int status = Integer.parseInt(list.get(8));
                int approved = Integer.parseInt(list.get(9));
                if (status == 1 && approved == 1) {
                	listGateWays.add(new NodeInfo(blockchain, ip));
                }
        	}
        }
        Log.info(String.format("Avaiable gateways: %d, active ones: %d", list_gateway.size(), listGateWays.size()));
        return listGateWays;
    }
    class NodeInfo {
    	String blockchain;
    	String id;
    	String ip;
    	String apiKey;
    	
		public NodeInfo(String blockchain, String ip) {
			super();
			this.blockchain = blockchain;
			this.ip = ip;
		}
		
		public NodeInfo(String blockchain, String id, String apiKey) {
			super();
			this.blockchain = blockchain;
			this.id = id;
			this.apiKey = apiKey;
		}

		public String getBlockchain() {
			return blockchain;
		}
		public void setBlockchain(String blockchain) {
			this.blockchain = blockchain;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getApiKey() {
			return apiKey;
		}
		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}
    	
    }
}
