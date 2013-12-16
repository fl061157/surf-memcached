package com.cplatform.surf;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import com.cplatform.surf.protocol.MemcachedClientHandler;
import com.cplatform.surf.protocol.MemcachedProtocol;


/**
 * 
 * @author fangliang
 * @mail fl061157@gmail.com 
 */
public class MemClientInitializer extends ChannelInitializer<SocketChannel> {
    private static final ByteArrayEncoder ENCODER = new ByteArrayEncoder();
    private final MemcachedClientHandler clientHandler = new MemcachedClientHandler()  ;
    private final MemcachedProtocol protocol ; 
    
    public MemClientInitializer( MemcachedProtocol protocol ) {
    	this.protocol = protocol  ;
    }
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder" , protocol ) ; 
        pipeline.addLast("encoder", ENCODER);
        pipeline.addLast("handler", clientHandler); 
    }
}
