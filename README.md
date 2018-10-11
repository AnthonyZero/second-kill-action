# 简介
基于SpringBoot实现的高并发抢购秒杀案例

## 开发技术
后端: SpringBoot 1.5.8.RELEASE  
前端: Thymeleaf + Bootstrap + jQuery   
其它: Redis + RabbitMQ

## 秒杀优化方向
1.将请求尽量拦截在系统上游：传统秒杀系统之所以压力大，是因为请求都落到了数据库，数据读写锁冲突严重，
几乎所有请求都超时，流量虽大，下单成功的有效流量甚小，我们可以通过限流、降级等措施来最大化减少对数据库的访问，从而保护系统。

2.充分利用缓存：秒杀商品是一个典型的读多写少的应用场景，充分利用缓存将大大提高并发量

3.异步：不影响用户秒杀结果的尽量异步解耦，提高系统TPS，比如异步创建订单、写系统日志等

## 特征&功能

- [x] 统一响应结果封装、异常处理
- [x] JSR-303参数校验
- [x] 用户信息ThreadLocal线程绑定
- [x] MQ异步下单 + 客户端轮询结果
- [x] 接口限流防刷
- [x] 分布式Session
- [x] 对秒杀地址进行隐藏（地址随时变化）
- [x] 页面缓存 + 对象缓存
- [x] 页面静态化（前后端分离）
- [x] 数学公式验证码
- [x] 解决超卖和重复秒杀
- [x] 本地标记 + Redis预减库存

## 目录结构

```shell
├── second-kill-action                                
│   ├── src/main
│   ├── ├──java/com/anthonyzero/seckill            // 具体代码
│   ├── ├──├──common                               // common
│   ├── ├──├──├──annotation                        // 注解定义
│   ├── ├──├──├──core                              // 核心定义
│   ├── ├──├──├──enums                             // 系统常量枚举
│   ├── ├──├──├──excetion                          // 统一全局异常
│   ├── ├──├──├──rabbitmq                          // RabbitMQ集成
│   ├── ├──├──├──redis                             // Redis集成
│   ├── ├──├──├──utils                             // 工具类
│   ├── ├──├──├──validator                         // 校验
│   ├── ├──├──config                               // WEB配置
│   ├── ├──├──controller                           // 控制器
│   ├── ├──├──dao                                  // DAO层
│   ├── ├──├──domain                               // 实体类
│   ├── ├──├──service                              // 业务逻辑
│   ├── ├──├──vo                                   // VO
│   ├── ├──├──├──Application                       // SpringBoot启动类
│   ├── ├──resources                               // 资源文件
│   ├── ├──├──static                               // 静态资源
│   ├── ├──├──templates                            // 页面
|   ├── ├──├──application.yml                      // SpringBoot配置文件
├── dbscript                                       // sql脚本
├── .gitignore                                     // git忽略项
├── pom.xml                                        // 父pom
├── LICENSE               
├── README.md               

```
## 开始&使用 
* 将项目导入开发环境中,创建数据库seckill 执行项目dbscript目录下的SQL脚本  
* 根据自己本机情况修改application.yml相关配置:数据库连接、Redis、RabbitMQ 
* 启动项目访问http://localhost:8080/login/loin  用帐号15800000000密码123456进行登录 开始体验  

