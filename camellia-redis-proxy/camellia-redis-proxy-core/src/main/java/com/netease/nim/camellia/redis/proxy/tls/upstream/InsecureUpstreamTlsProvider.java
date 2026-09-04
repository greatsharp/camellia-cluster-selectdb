package com.netease.nim.camellia.redis.proxy.tls.upstream;

import com.netease.nim.camellia.core.model.Resource;
import com.netease.nim.camellia.redis.proxy.conf.ProxyDynamicConf;
import com.netease.nim.camellia.redis.proxy.tls.SSLContextUtil;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ImmediateExecutor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A TLS provider that does not verify the backend redis server certificate (insecure mode).
 * Suitable for scenarios where the backend redis uses self-signed certificates and the client does not provide a CA certificate.
 *
 * Enable it with the following configuration:
 * upstream.tls.provider.class.name=com.netease.nim.camellia.redis.proxy.tls.upstream.InsecureUpstreamTlsProvider
 *
 * Created by terrytyzhang on 2023/8/9
 */
public class InsecureUpstreamTlsProvider implements UpstreamTlsProvider {

    private SSLContext sslContext;
    private boolean startTls = false;
    private Executor executor = ImmediateExecutor.INSTANCE;

    @Override
    public boolean init() {
        this.sslContext = SSLContextUtil.genInsecureSSLContext();
        this.startTls = ProxyDynamicConf.getBoolean("upstream.tls.startTls.enable", false);
        int poolSize = ProxyDynamicConf.getInt("upstream.tls.executor.pool.size", 0);
        int queueSize = ProxyDynamicConf.getInt("upstream.tls.executor.queue.size", 10240);
        if (poolSize <= 0) {
            this.executor = ImmediateExecutor.INSTANCE;
        } else {
            this.executor = new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(queueSize), new DefaultThreadFactory("upstream-tls-executor-insecure"),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
        return true;
    }

    @Override
    public SslHandler createSslHandler(Resource resource) {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(true);
        return new SslHandler(sslEngine, startTls, executor);
    }
}
