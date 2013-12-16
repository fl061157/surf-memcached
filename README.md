surf-memcached
==============

基于Netty以及Zookeeper封装的Memcached客户端，性能较Xmemcached有一定的提升


使用方式见test package

不使用 zookeeper 下的使用方式 
<code>
<p>
MemcachedClient client = new MemcachedClient("127.0.0.1", 12000)
</P>
<p>
.build("127.0.0.1", 12001).build("127.0.0.1", 12002) ;
</p>
</code>
<code>
<p>
boolean b1 = client.set(key1, value1 ) ;
</p>
<p>
String value = (String)client.get( key ) ;
</p>
</code>
<code>
<p>
boolean b2 = client.set(key2, new User( 1002, "fangliang_1002" )  )  ;
</p>
<p>
User valueUser = (User)client.get( key2 ) ;
</p>
</code>
<p>
详见 test包
</P>
使用 zookeeper 情形下 
<p>
 ZkMemcachedClient client = new ZkMemcachedClient("zk1.com:2181,zk2.com:2181,zk2.com:2182,zk3.com:2181,zk3.com:2182"
				, "/zk/memcached", "wap") ; 
</P>				
其中  zk1.com:2181,zk2.com:2181,zk2.com:2182,zk3.com:2181,zk3.com:2182 为 zookeeper server 的地址 

请确保 zookeeper 下有上述路径 /zk/memcached/wap

可以根据不同的子空间实力化不同的 ZkMemcachedClient 比如 下属路径</br>
<p>/zk/memcached</p>
<p>--/zk/memcached/wap></p>
<p>----/zk/memcached/wap/127.0.0.1:12000<br></p>
<p>----/zk/memcached/wap/127.0.0.1:12001<br></p>
<p>----/zk/memcached/wap/127.0.0.1:12002<br></p>
<p>--/zk/memcached/www<br></p>
<p>----/zk/memcached/www/127.0.0.1:xxxxx<br></p>
<p>----/zk/memcached/www/127.0.0.1:xxxxx<br></p>
<p>--/zk/memcached/ishare<br></p>
<p>--/zk/memcached/xxxx<br></p>
那么相应的  ZkMemcachedClient 分别为 ZkMemcachedClient client = new ZkMemcachedClient("zk1.com:2181,zk2.com:2181,zk2.com:2182,zk3.com:2181,zk3.com:2182"
				, "/zk/memcached", "wap") ;  
				
				ZkMemcachedClient client = new ZkMemcachedClient("zk1.com:2181,zk2.com:2181,zk2.com:2182,zk3.com:2181,zk3.com:2182"
				, "/zk/memcached", "www") ;
				
				ZkMemcachedClient client = new ZkMemcachedClient("zk1.com:2181,zk2.com:2181,zk2.com:2182,zk3.com:2181,zk3.com:2182"
				, "/zk/memcached", "ishare") ;
				
				ZkMemcachedClient client = new ZkMemcachedClient("zk1.com:2181,zk2.com:2181,zk2.com:2182,zk3.com:2181,zk3.com:2182"
				, "/zk/memcached", "xxxx") ;
				
				
				
				后面的调用方式与未使用 zookeeper 情形下想同。
				

系统实现动态添加 memcache 节点 ， key 通过一致性hash 分布在对应的server 上面 


增加 zookeeper 节点 
  create /zk/memcached/wap/127.0.0.1:12003 127.0.0.1:12003
删除 zookeeper 节点
  delete /zk/memcached/wap/127.0.0.1:12003 


            















