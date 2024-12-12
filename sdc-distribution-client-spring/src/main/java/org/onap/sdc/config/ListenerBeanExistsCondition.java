package org.onap.sdc.config;

import org.onap.sdc.consumer.NotificationListener;
import org.onap.sdc.consumer.StatusListener;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition that evaluates to true, if either a NotificationListener or StatusListener bean
 * exists in the spring context.
 */
public class ListenerBeanExistsCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    boolean notificationListenerBeanExists = beanExists(context, NotificationListener.class);
    boolean statusListenerBeanExists = beanExists(context, StatusListener.class);
    return notificationListenerBeanExists || statusListenerBeanExists;
  }

  private boolean beanExists(ConditionContext context, Class<?> requiredType) {
    boolean beanExists;
    try {
      context.getBeanFactory().getBean(requiredType);
      beanExists = true;
    } catch (NoUniqueBeanDefinitionException e) {
      beanExists = true;
    } catch (Exception e) {
      beanExists = false;
    }
    return beanExists;
  }

}
