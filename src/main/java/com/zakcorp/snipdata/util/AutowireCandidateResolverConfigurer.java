/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class AutowireCandidateResolverConfigurer implements BeanFactoryPostProcessor {
  private static class EnvAwareQualifierAnnotationAutowireCandidateResolver extends
    QualifierAnnotationAutowireCandidateResolver {

    private static class ResolvedQualifier implements Qualifier {

      private final String value;
      ResolvedQualifier(String value) {
        this.value = value;
      }
      @Override
      public String value() {
        return this.value;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return Qualifier.class;
      }
    }

    @Override
    protected boolean checkQualifier(BeanDefinitionHolder bdHolder, Annotation annotation, TypeConverter typeConverter) {
      if (annotation instanceof Qualifier) {
        Qualifier qualifier = (Qualifier) annotation;
        if (qualifier.value().startsWith("${") && qualifier.value().endsWith("}")) {
          DefaultListableBeanFactory bf = (DefaultListableBeanFactory) this.getBeanFactory();
          ResolvedQualifier resolvedQualifier = new ResolvedQualifier(bf.resolveEmbeddedValue(qualifier.value()));
          return super.checkQualifier(bdHolder, resolvedQualifier, typeConverter);
        }
      }
      return super.checkQualifier(bdHolder, annotation, typeConverter);
    }

  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    DefaultListableBeanFactory bf = (DefaultListableBeanFactory) beanFactory;
    bf.setAutowireCandidateResolver(new EnvAwareQualifierAnnotationAutowireCandidateResolver());

  }
}
