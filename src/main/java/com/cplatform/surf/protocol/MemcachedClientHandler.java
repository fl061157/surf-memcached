package com.cplatform.surf.protocol;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import com.cplatform.surf.Session;
import com.cplatform.surf.command.Command;
import com.cplatform.surf.response.Response;

@Sharable
public class MemcachedClientHandler extends
		ChannelInboundMessageHandlerAdapter<Command< ? extends Response >> { 

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Command< ? extends Response > response)
			throws Exception {
		response.setResponse() ;
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace() ;
 		ctx.close() ;
	}
	
	
}
