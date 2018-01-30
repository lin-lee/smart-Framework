package org.smart4j.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smart4j.framework.bean.Data;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.View;
import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ConfigHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.helper.RequestHelper;
import org.smart4j.framework.helper.UploadHelper;
import org.smart4j.framework.util.JsonUtil;
import org.smart4j.framework.util.ReflectionUtil;
import org.smart4j.framework.util.StringUtil;

/**
 * 请求转发器
 * @author lin
 *
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispacherServlet extends HttpServlet {

	private static final long serialVersionUID = -6467905678431405368L;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException{
		HelperLoader.init();
		ServletContext servletContext = servletConfig.getServletContext();
		//注册处理JSP的Servlet
		ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
		jspServlet.addMapping(ConfigHelper.getAppJspPath()+"*");
		//注册处理静态资源的Servlet
		ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
		defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");
		
		UploadHelper.init(servletContext);
		
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		String requestMethod = request.getMethod().toLowerCase();
		String requestPath = request.getPathInfo();
		if(requestPath.equals("/favicon.ico")){
			return;
		}
		//获取Action处理器
		Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
		if(handler != null){
			//获取ControllerClass类及其Bean实例
			Class<?> controllerClass = handler.getControllerClass();
			Object controllerBean = BeanHelper.getBean(controllerClass);
			//创建请求参数对象
			Param param;
			if(UploadHelper.isMultipart(request)){
				param  = UploadHelper.createParam(request);
			}else{
				param = RequestHelper.createParam(request);
			}
			
			//调用Action方法
			Object result;
			Method actionMethod = handler.getActionMethod();
			if(param.isEmpty()){
				result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
			}else{
				result = ReflectionUtil.invokeMethod(controllerBean, 
						actionMethod, param);
			}
			//处理Action方法返回值
			if(result instanceof View){
				this.handleViewResult((View)result, request, response);
			}else if(result instanceof Data){
				this.handleDataResult((Data)result, request, response);
			}
		}
	}
	
	private void handleViewResult(View view, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException{
		//返回JSP页面
		String path = view.getPath();
		if(StringUtil.isNotEmpty(path)){
			if(path.startsWith("/")){
				response.sendRedirect(request.getContextPath()+path);
			}else{
				Map<String, Object> model = view.getModel();
				for(Map.Entry<String, Object> entry : model.entrySet()){
					request.setAttribute(entry.getKey(), entry.getValue());
				}
				request.getRequestDispatcher(ConfigHelper.getAppJspPath()+path)
					.forward(request, response);
			}
		}
	}
	
	private void handleDataResult(Data data, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException{
		Object model = data.getModel();
		if(model != null){
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			String json = JsonUtil.toJson(model);
			writer.write(json);
			writer.flush();
			writer.close();
		}
	}
	
	

}
