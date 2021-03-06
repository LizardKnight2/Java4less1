package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufToStringInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        log.debug("Client accepted");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.debug("Client disconnected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        StringBuilder s = new StringBuilder();
        ByteBuf buf = (ByteBuf) msg;
        log.debug("received: {}", buf);
        while (buf.isReadable()){
            byte b = buf.readByte();
            s.append((char) b);
        }
        log.debug("processed message: {}", s);
        ctx.fireChannelRead(s.toString());
    }
}
