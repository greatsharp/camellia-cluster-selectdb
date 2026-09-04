package com.netease.nim.camellia.redis.proxy.command;

import com.netease.nim.camellia.redis.proxy.netty.ChannelInfo;
import org.junit.Assert;
import org.junit.Test;

public class CommandsRouterTest {

    @Test
    public void shouldRejectDbChangeDuringTransaction() {
        ChannelInfo channelInfo = new ChannelInfo();
        channelInfo.setDb(1);
        channelInfo.setInTransaction(true);

        Assert.assertFalse(CommandsRouter.isDbChangeNotAllowed(channelInfo, 1));
        Assert.assertTrue(CommandsRouter.isDbChangeNotAllowed(channelInfo, 2));
    }
}
