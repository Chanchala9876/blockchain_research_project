package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FabricConfig {
    
    @Value("${fabric.network.name:mychannel}")
    private String channelName;
    
    @Value("${fabric.chaincode.name:paperchain}")
    private String chaincodeName;
    
    @Value("${fabric.network.config.path:src/main/resources/fabric/network-config.yaml}")
    private String networkConfigPath;
    
    @Value("${fabric.wallet.path:wallet}")
    private String walletPath;
    
    @Value("${fabric.user.name:appUser}")
    private String userName;
    
    @Value("${fabric.msp.id:Org1MSP}")
    private String mspId;
    
    @Value("${fabric.ca.url:http://localhost:7054}")
    private String caUrl;
    
    public String getChannelName() { return channelName; }
    public String getChaincodeName() { return chaincodeName; }
    public String getNetworkConfigPath() { return networkConfigPath; }
    public String getWalletPath() { return walletPath; }
    public String getUserName() { return userName; }
    public String getMspId() { return mspId; }
    public String getCaUrl() { return caUrl; }
    
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public void setChaincodeName(String chaincodeName) { this.chaincodeName = chaincodeName; }
    public void setNetworkConfigPath(String networkConfigPath) { this.networkConfigPath = networkConfigPath; }
    public void setWalletPath(String walletPath) { this.walletPath = walletPath; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setMspId(String mspId) { this.mspId = mspId; }
    public void setCaUrl(String caUrl) { this.caUrl = caUrl; }
}