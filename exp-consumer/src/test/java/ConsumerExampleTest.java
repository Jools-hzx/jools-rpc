import com.jools.rpc.core.config.RpcConfig;
import com.jools.rpc.core.utils.ConfigUtils;
import org.junit.Test;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 12:12
 * @description: TODO
 */
public class ConsumerExampleTest {

    @Test
    public void testConsumerYmlConfig() {
        RpcConfig rpcConfig = ConfigUtils.loadConfigYaml(RpcConfig.class, ".yml");
        System.out.println(rpcConfig.getName());
        System.out.println(rpcConfig.getVersion());
        System.out.println(rpcConfig.getServerHost());
        System.out.println(rpcConfig.getServerPort());
    }

    @Test
    public void testConsumerConfig() {

        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");

        System.out.println(rpc.getName());
        System.out.println(rpc.getVersion());
        System.out.println(rpc.getServerHost());
        System.out.println(rpc.getServerPort());
    }
}
