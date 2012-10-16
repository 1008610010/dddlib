package com.dayatang.spring.webapp;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dayatang.domain.InstanceFactory;
import com.dayatang.spring.factory.SpringProvider;

public class DayatangContextLoaderListener extends ContextLoaderListener {

	public void contextInitialized(ServletContextEvent  event) {
		super.contextInitialized(event);
		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
		SpringProvider springProvider = new SpringProvider(applicationContext);
		InstanceFactory.setInstanceProvider(springProvider);
	}

}
