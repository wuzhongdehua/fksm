package com.fksm.log;

import com.typesafe.config.Config;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class SimulationDataUtil {

    private static Logger logger = LoggerFactory.getLogger(SimulationDataUtil.class);

    private static Config config = ConfigUtil.getConfig();

    private static String filePath = config.getString("data.file.filePath");
    private static String serversFileName = config.getString("data.file.serversFileName");
    private static String serviceFileName = config.getString("data.file.serviceFileName");
    private static String serverServiceFileName = config.getString("data.file.serverServiceFileName");
    private static String serviceServerFileName = config.getString("data.file.serviceServerFileName");

    private static String[] server_ip;
    private static Map<String, ArrayList<String>> server_services;
    private static Map<String, String> service_server;
    private static String[] services;
    private static String[] caller_ip;

    private static Integer[] response_code = new Integer[]{100,200,300,400,500};

    static {
        logger.debug("start load server->service file....");
        File serverFile = new File(filePath + File.separator + serversFileName);
        File serviceFile = new File(filePath + File.separator + serviceFileName);
        File serverServiceFile = new File(filePath + File.separator + serverServiceFileName);
        File serviceServerFile = new File(filePath + File.separator + serviceServerFileName);
        if (serverFile.exists() && serverServiceFile.exists() && serviceFile.exists() && serviceServerFile.exists()){
            try {
                logger.debug("load server file.");
                List<String> lines = FileUtils.readLines(serverFile);
                server_ip = new String[lines.size()];
                lines.toArray(server_ip);
                logger.debug("load service file.");
                lines = FileUtils.readLines(serviceFile);
                services = new String[lines.size()];
                for (int i=0;i<lines.size();i++){
                    services[i]=lines.get(i);
                }
                logger.debug("load server->service file.");
                lines = FileUtils.readLines(serverServiceFile);
                server_services = new HashMap<>();
                for(String line:lines){
                    String[] server_service = line.split("\t");
                    String server_ip = server_service[0];
                    String service_id = server_service[1];
                    ArrayList<String> services = server_services.get(server_ip);
                    if(services == null){
                        ArrayList<String> n = new ArrayList<>();
                        n.add(service_id);
                        server_services.put(server_ip, services);
                    }else{
                        services.add(service_id);
                    }
                }
                logger.debug("load service->server file.");
                lines = FileUtils.readLines(serviceServerFile);
                service_server = new HashMap<>(lines.size());
                for(String line:lines) {
                    String[] service_server_array = line.split("\t");
                    service_server.put(service_server_array[0],service_server_array[1]);
                }
            } catch (IOException e) {
                logger.error("error read file : {}", e);
            }
        }else {
            logger.error("some file is not exists and create it auto...");
            try {
                createServerServiceFile();
            } catch (IOException e) {
                logger.error("error create file : {}", e);
            }
        }
        logger.debug("done load server->service file.");

        logger.debug("start load caller_ips ...");
        Integer callerCount = getRandomCount(30);
        caller_ip = new String[callerCount];
        for (int i = 0;i < callerCount;i++){
            caller_ip[i] = LogUtil.getRandomIp();
        }
        logger.debug("done load caller_ips .");
    }

    protected static void createServerServiceFile() throws IOException {
        File path = new File(filePath);
        if (!path.exists()){
            path.mkdirs();
        }
        Integer serverCount = getRandomCount(config.getInt("data.server.maxServerCount"));
        logger.info("serverCount : {}",serverCount);
        List<String> ips = new ArrayList<>(serverCount);
        List<String> services = new ArrayList<>();
        List<String> server_services = new ArrayList<>();
        List<String> service_server = new ArrayList<>();
        for (int i=0;i<serverCount;i++){
            String ip = LogUtil.getRandomIp();
            logger.info("server ip : {}",ip);
            ips.add(ip);
            Integer serviceCount = getRandomCount(config.getInt("data.service.maxPerServerCount"));
            for (int j=0;j<serviceCount;j++){
                String service =  getRandomUUID();
                services.add(service);
                logger.info("ip : {}, service : {}", ip, service);
                server_services.add(ip + "\t" + service);
                service_server.add(service + "\t" + ip);
            }
        }
        //write servers
        String serverFile = filePath + File.separator + serversFileName;
        FileUtils.writeLines(new File(serverFile), ips);
        //write services
        String serviceFile = filePath + File.separator + serviceFileName;
        FileUtils.writeLines(new File(serviceFile), services);
        //write server_services
        String serverServiceFile = filePath + File.separator + serverServiceFileName;
        FileUtils.writeLines(new File(serverServiceFile), server_services);
        //write service_server
        String serviceServerFile = filePath + File.separator + serviceServerFileName;
        FileUtils.writeLines(new File(serviceServerFile), service_server);
        logger.info("done!");
    }

    public static String getRandomUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private static Integer getRandomCount(int max){
        Random r=new Random();
        return r.nextInt(max) + 1;
    }

    public static Integer getRandomResponseCode(){
        Random r=new Random();
        int index = r.nextInt(response_code.length);
        return response_code[index];
    }

    private static Long getRandomRequestId(Integer serviceId){
        Random r=new Random();
        int index = r.nextInt(response_code.length);
        return 1L;
    }

    public static String[] getServer_ip(){
        return  server_ip;
    }

    public static String[] getCaller_ip(){
        return caller_ip;
    }

    public static String[] getRandomCallerIps(int maxSize){
        Random r=new Random();
        Integer size = r.nextInt(maxSize) + 1;
        String[] res = new String[size];
        //res index
        int in = 0;
        Integer caller_size = caller_ip.length;
        for(int i=0;i<size;i++){
            Integer index = r.nextInt(caller_size);
            res[in] = caller_ip[index];
            in++;
        }
        return res;
    }

    public static String[] getServices(){
        return services;
    }

    public static String[] getRandomServices(int maxSize){
        Random r=new Random();
        Integer size = r.nextInt(maxSize) + 1;
        String[] res = new String[size];
        //res index
        int in = 0;
        Integer caller_size = services.length;
        for(int i=0;i<size;i++){
            Integer index = r.nextInt(caller_size);
            res[in] = services[index];
            in++;
        }
        return res;
    }

    public static Map<String, ArrayList<String>> getServer_services(){
        return server_services;
    }

    public static Map<String, String> getService_server(){
        return service_server;
    }

    public static void main(String[] args){
        try {
            createServerServiceFile();
        } catch (IOException e) {
            logger.error("error init server_service file : {}",e);
        }
    }
}
