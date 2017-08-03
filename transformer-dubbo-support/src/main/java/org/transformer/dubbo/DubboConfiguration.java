package org.transformer.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * dubbo配置.
 */
@Configuration
public class DubboConfiguration {

  /**
   * 应用名称.
   */
  @Value("${dubbo.application.name:transformer}")
  private String applicationName;

  /**
   * 注册中心协议.
   */
  @Value("${dubbo.registry.protocol:zookeeper}")
  private String registryProtocol;

  /**
   * 注册中心地址.
   */
  @Value("${dubbo.registry.address:127.0.0.1:2181}")
  private String registryAddress;

  /**
   * 监控中心协议.
   */
  @Value("${dubbo.monitor.protocol:registry}")
  private String monitorProtocol;

  /**
   * 对外开放服务端口号.默认获取大于20880的随机可以端口
   * 
   */
  @Value("${dubbo.protocol.port:-1}")
  private int protocolPort;

  /**
   * 消费者是否确认服务可用.
   */
  @Value("${dubbo.consumer.check:false}")
  private boolean consumerCheck;

  /**
   * 应用配置.
   * @return 应用配置BEAN
   */
  @Bean
  public ApplicationConfig application() {
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setName(applicationName);
    return applicationConfig;
  }

  /**
   * 注册中心配置.
   * @return 注册中心配置BEAN
   */
  @Bean
  public RegistryConfig registry() {
    RegistryConfig registryConfig = new RegistryConfig();
    registryConfig.setProtocol(registryProtocol);
    registryConfig.setAddress(registryAddress);
    return registryConfig;
  }

  /**
   * 监控配置.
   * @return 监控配置BEAN
   */
  @Bean
  public MonitorConfig monitor() {
    MonitorConfig mc = new MonitorConfig();
    mc.setProtocol(monitorProtocol);
    return mc;
  }

  /**
   * 协议配置.
   * 默认情况下，从20880开始依次获取可用的端口号
   * @return 传输协议配置BEAN
   */
  @Bean
  public ProtocolConfig protocol() {
    ProtocolConfig protocolConfig = new ProtocolConfig();
    if (protocolPort == -1) {
      protocolPort = 20880;
      while (protocolPort < 0xFFFF) {
        try (ServerSocket serverSocket = new ServerSocket(protocolPort)) {
          int port = serverSocket.getLocalPort();
          protocolConfig.setPort(port);
          break;
        } catch (@SuppressWarnings("unused") IOException exception) {
          //端口号不可用
          protocolPort++;
        }
      }
    } else {
      protocolConfig.setPort(protocolPort);
    }
    return protocolConfig;
  }

  /**
   * 服务提供方默认配置.
   * @return 服务提供方默认配置bean 
   */
  @Bean
  public ProviderConfig provider() {
    ProviderConfig providerConfig = new ProviderConfig();
    return providerConfig;
  }

  /**
   * 服务调用方默认配置.
   * @return 服务调用方默认配置bean
   */
  @Bean
  public ConsumerConfig consumer() {
    ConsumerConfig consumerConfig = new ConsumerConfig();
    consumerConfig.setCheck(consumerCheck);
    return consumerConfig;
  }

  /**
   * 注解注册配置.
   * @param scanPackage 扫描的包路径，多个以","隔开，默认org.transformer
   * @return 注解注册配置BEAN
   */
  @Bean
  @Value("${dubbo.scan.packages:org.transformer}")
  public static AnnotationBean annotation(String scanPackage) {
    AnnotationBean annotationBean = new AnnotationBean();
    annotationBean.setPackage(scanPackage);
    return annotationBean;
  }

}
