package com.netease.nim.camellia.redis.base.resource;

import com.netease.nim.camellia.core.model.Resource;

import java.util.List;

/**
 * 格式如下：
 * 1、没有密码
 * rediss-cluster-slaves://@host:port,host:port,host:port?withMaster=false
 * 2、有密码
 * rediss-cluster-slaves://password@host:port,host:port,host:port?withMaster=false
 * 3、有密码且有账号
 * rediss-cluster-slaves://username:password@host:port,host:port,host:port?withMaster=false
 */
public class RedissClusterSlavesResource extends Resource {

    private final List<RedisClusterResource.Node> nodes;
    private final String password;
    private final boolean withMaster;
    private final String userName;
    private final int db;

    public RedissClusterSlavesResource(List<RedisClusterResource.Node> nodes, String userName, String password, boolean withMaster, int db) {
        this.nodes = nodes;
        this.password = password;
        this.withMaster = withMaster;
        this.userName = userName;
        this.db = db;
        StringBuilder url = new StringBuilder();
        url.append(RedisType.RedissClusterSlaves.getPrefix());
        if (userName != null && password != null) {
            url.append(userName).append(":").append(password);
        } else if (userName == null && password != null) {
            url.append(password);
        }
        url.append("@");
        for (RedisClusterResource.Node node : nodes) {
            url.append(node.getHost()).append(":").append(node.getPort());
            url.append(",");
        }
        url.deleteCharAt(url.length() - 1);
        url.append("?withMaster=").append(withMaster);
        if (db > 0) {
            url.append("&db=").append(db);
        }
        this.setUrl(url.toString());
    }

    public RedissClusterSlavesResource(List<RedisClusterResource.Node> nodes, String userName, String password, boolean withMaster) {
        this(nodes, userName, password, withMaster, 0);
    }

    public List<RedisClusterResource.Node> getNodes() {
        return nodes;
    }

    public String getPassword() {
        return password;
    }

    public boolean isWithMaster() {
        return withMaster;
    }

    public String getUserName() {
        return userName;
    }

    public int getDb() {
        return db;
    }
}
