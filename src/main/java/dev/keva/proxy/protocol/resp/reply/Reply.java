package dev.keva.proxy.protocol.resp.reply;

import dev.keva.proxy.protocol.resp.RedisReplyDecoder;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Reply<T> {
    byte[] CRLF = new byte[]{RedisReplyDecoder.CR, RedisReplyDecoder.LF};

    T data();

    void write(ByteBuf os) throws IOException;
}
