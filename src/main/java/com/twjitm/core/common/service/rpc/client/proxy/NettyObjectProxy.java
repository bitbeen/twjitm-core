package com.twjitm.core.common.service.rpc.client.proxy;import com.twjitm.core.common.netstack.entity.rpc.NettyRpcRequestMessage;import com.twjitm.core.common.service.rpc.client.AbstractNettyRpcConnectManager;import com.twjitm.core.common.service.rpc.client.NettyRPCFuture;import com.twjitm.core.common.service.rpc.client.NettyRpcContextHolder;import com.twjitm.core.common.service.rpc.client.NettyRpcContextHolderObject;import com.twjitm.core.common.service.rpc.network.NettyRpcClient;import com.twjitm.core.common.service.rpc.service.NettyRpcClientConnectService;import com.twjitm.core.spring.SpringServiceManager;import com.twjitm.core.utils.logs.LoggerUtils;import org.apache.log4j.Logger;import java.lang.reflect.InvocationHandler;import java.lang.reflect.Method;import java.util.UUID;import java.util.concurrent.TimeUnit;/** * @author EGLS0807 - [Created on 2018-08-20 12:15] * @company http://www.g2us.com/ * @jdk java version "1.8.0_77" * 代理对象 */public class NettyObjectProxy<T> implements InvocationHandler {    private Logger logger=LoggerUtils.getLogger(NettyObjectProxy.class);    private Class<T> clazz;    private int timeOut;    public NettyObjectProxy(Class<T> clazz, int timeOut) {        this.clazz = clazz;        this.timeOut = timeOut;    }    @Override    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {        NettyRpcRequestMessage request = new NettyRpcRequestMessage();        request.setRequestId(UUID.randomUUID().toString());        request.setClassName(method.getDeclaringClass().getName());        request.setMethodName(method.getName());        request.setParameterTypes(method.getParameterTypes());        request.setParameters(args);        if(logger.isInfoEnabled()) {            logger.debug(method.getName());            logger.debug(method.getDeclaringClass().getName());            for (int i = 0; i < method.getParameterTypes().length; ++i) {                logger.debug(method.getParameterTypes()[i].getName());            }            for (int i = 0; i < args.length; ++i) {                logger.debug(args[i].toString());            }        }        NettyRpcContextHolderObject rpcContextHolderObject = NettyRpcContextHolder.getContext();        NettyRpcClientConnectService rpcClientConnectService = SpringServiceManager.getSpringLoadService().getNettyRpcClientConnectService();        AbstractNettyRpcConnectManager abstractRpcConnectManager = rpcClientConnectService.getNettyRpcConnectManager(rpcContextHolderObject.getNettyGameTypeEnum());        NettyRpcClient rpcClient = abstractRpcConnectManager.chooseNettyRpcClient(rpcContextHolderObject.getServiceId());        NettyRPCFuture rpcFuture = rpcClient.sendRequest(request);        if(timeOut > 0){            return rpcFuture.get(timeOut, TimeUnit.MILLISECONDS);        }        return rpcFuture.get();    }}