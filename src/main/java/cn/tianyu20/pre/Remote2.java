package cn.tianyu20.pre;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

/**
 * system : 2554
 * 这边这个system不需要创建任何Actor，应为其他的远程系统可以帮我们这个节点注册
 */
public class Remote2 {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("remote2.conf"));
    }
}
