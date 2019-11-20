package cn.tianyu20.pre;

import akka.actor.*;
import akka.remote.RemoteScope;
import com.typesafe.config.ConfigFactory;

/**
 * system : 2553
 * 创建两个简单的远程服务    重要：请不要修改本文，下面的注释是固定了代码行数的，如果要做修改请copy一份对照修改
 */
public class Remote1 {

    /**
     * 创建一个可提供远程注入的 Actor ， 这个节点将在2554被调用
     */
    static class remoteActor extends AbstractActor{

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg->{
                System.out.println(msg + "  ----  message");
            }).build();
        }

    }

    //系统1启动并且注册一个Actor
    //但是现在要求把Actor1发送到Remote2的系统上，所以要先启动一个空壳系统 127.0.0.1:2554
    //当前系统是 127.0.0.1:2553
    // 2553 要给 2554的空壳系统注册一个Actor，然后在给注册到的Actor发送消息
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("remote1.conf"));
        ActorRef actor = system.actorOf(Props.create(remoteActor.class), "system1-actor");
        //构建2554的系统 , 这里只是创建2554的地址并没有建立连接
        Address address = new Address("akka.tcp","sys","127.0.0.1",2554);
        //部署到远程 2554
        ActorRef ref = system.actorOf(Props.create(remoteActor.class).withDeploy(new Deploy(new RemoteScope(address))), "remote");
        //使用对象的方式发送的数据实际上是
        //ref.tell("hello 2554",ActorRef.noSender());
        //部署到2554 之后我们就着在在这里调用一下看看（如果有兴趣可以单独创建一个系统调用）

        //下面的代码和41行注释都错了，如果使用下面的代码就会导致发送失败 ， 原因是虽然部署到了另外一台机器上了但是地址并没有变为另外一台的地址
        system.actorSelection("akka.tcp://sys@127.0.0.1:2554/user/remote").tell("hello 2554",ActorRef.noSender());
        //要注意这里的remote需要调用的是38行我们自己指定的Actor名称
    }
}
